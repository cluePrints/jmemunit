package com.clueprints.jmemunit;

import com.clueprints.jmemunit.internal.CallAdapter;
import com.clueprints.jmemunit.internal.ChildProcessCall;
import com.clueprints.jmemunit.internal.ExecutionInterface;
import com.clueprints.jmemunit.internal.SameProcessCall;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class JmemRunner extends BlockJUnit4ClassRunner {
    // TODO: this looks like a sign of doing too much stuff with a single class
    private boolean forkAllowed = true;
    
    public JmemRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
    
    @Override
    protected Statement methodInvoker(final FrameworkMethod method, Object test) {
        final Statement original = super.methodInvoker(method, test);
        if (!forkAllowed) {
            return original;
        }
        
        // I'll call it enterprise ready the day this piece will be refactored into a factory
        ExecutionInterface callType = new ChildProcessCall();
        if (isDebug()) {
            // TODO: allow to override this
            System.out.println("Debug detected. Forking(and mem-limited testing) thus won't happen.");
            callType = new SameProcessCall();
        }
        
        return new CallAdapter(callType, getTestClass().getJavaClass(), method);        
    }
    
    protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next) {
        Test annotation = (Test) method.getAnnotation(Test.class);
        if (!expectsException(annotation)) {
            return next;
        }
        
        Class<? extends Throwable> expectedException = getExpectedException(annotation);
        if (expectedException == ChildProcessMainThreadException.class) {
            return new ExpectException(next, getExpectedException(annotation));
        }
        
        // exceptions gonna be caught on forks, so master(forkAllowed=true) should not have concept of expected ones
        if (forkAllowed) {
            return next;
        }
        
        return new ExpectException(next, getExpectedException(annotation));
    }
    
    private boolean expectsException(Test annotation) {
        return (getExpectedException(annotation) != null);
    }
    
    private Class<? extends Throwable> getExpectedException(Test annotation) {
        if ((annotation == null) || (annotation.expected() == Test.None.class)) {
            return null;
        }
        return annotation.expected();
    }
    
    private boolean isDebug() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    }
    
    public void setForkAllowed(boolean forkAllowed) {
        this.forkAllowed = forkAllowed;
    }
}
