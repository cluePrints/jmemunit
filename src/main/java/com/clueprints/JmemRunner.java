package com.clueprints;

import java.lang.ProcessBuilder.Redirect;
import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class JmemRunner extends BlockJUnit4ClassRunner {
    private final boolean forked;
    
    public JmemRunner(Class<?> klass) throws InitializationError {
        this(klass, false);
    }
    
    private JmemRunner(Class<?> klass, boolean forked) throws InitializationError {
        super(klass);
        this.forked = forked;
    }
    
    @Override
    protected Statement methodInvoker(final FrameworkMethod method, Object test) {
        final Statement original = super.methodInvoker(method, test);
        if (forked) {
            return original;
        }
        
        if (isDebug()) {
            // TODO: allow to override this
            System.out.println("Debug detected. Forking(and mem-limited testing) thus won't happen.");
            return new Statement() {                
                @Override
                public void evaluate() throws Throwable {
                    main(getTestClass().getName(), method.getName());
                }
            };
        }
        
        return new Statement() {            
            @Override
            public void evaluate() throws Throwable {
                String cp = System.getProperty("java.class.path");
                System.out.println("Starting with classpath: " + cp);
                
                int heapMegabytes = method.getAnnotation(CanRunInAHeapOf.class).megabytes();
                ProcessBuilder builder = new ProcessBuilder("java", "-cp", cp, "-Xmx" + heapMegabytes + "m", JmemRunner.class.getName(),
                        getTestClass().getName(), method.getName());
                builder.redirectError(Redirect.INHERIT);
                builder.redirectOutput(Redirect.INHERIT);
                Process p = builder.start();
                
                // TODO: timeout
                Assert.assertEquals(0, p.waitFor());
            }
        };
    }
    
    private boolean isDebug() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    }
    
    public static void main(String... args) throws Exception {
        String className = args[0];
        final String method = args[1];
        
        Class<?> klass = Class.forName(className);
        boolean forked = true;
        JmemRunner runner = new JmemRunner(klass, forked);
        runner.filter(new Filter() {            
            @Override
            public boolean shouldRun(Description paramDescription) {
                return paramDescription.getMethodName().equals(method);
            }
            
            @Override
            public String describe() {
                return null;
            }
        });
        runner.run(new RunNotifier());        
    }
}
