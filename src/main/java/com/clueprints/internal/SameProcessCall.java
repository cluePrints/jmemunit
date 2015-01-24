package com.clueprints.internal;

import com.clueprints.JmemRunner;
import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class SameProcessCall implements ExecutionInterface {
    public Result runTest(final Class<?> klass, final FrameworkMethod method) {
        try {
            JmemRunner runner = new JmemRunner(klass);
            runner.setForkAllowed(false);
            runner.filter(new Filter() {            
                @Override
                public boolean shouldRun(Description paramDescription) {
                    return paramDescription.getMethodName().equals(method.getName());
                }
                
                @Override
                public String describe() {
                    return null;
                }
            });
            RunNotifier notifier = new RunNotifier();
            Result result = new Result();
            notifier.addListener(result.createListener());
            runner.run(notifier);
            Assert.assertTrue(result.getFailureCount() + result.getRunCount() > 0);
            return result;           
        } catch (NoTestsRemainException ex) {
            throw new RuntimeException(ex);
        } catch (InitializationError ex) {
            throw new RuntimeException(ex);
        }
    }
}
