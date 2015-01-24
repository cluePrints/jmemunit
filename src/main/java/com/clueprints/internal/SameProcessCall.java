package com.clueprints.internal;

import com.clueprints.JmemRunner;
import java.util.List;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class SameProcessCall implements ExecutionInterface {
    public void runTest(final Class<?> klass, final FrameworkMethod method) {
        try {
            JmemRunner runner = new JmemRunner(klass);
            runner.setChild(true);
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
            runner.run(notifier);
            Result result = new Result();
            notifier.addListener(result.createListener());
            List<Failure> failures = result.getFailures();
            for (Failure failure : failures) {
                throw new RuntimeException(failure.getException());
            }
        } catch (NoTestsRemainException ex) {
            throw new RuntimeException(ex);
        } catch (InitializationError ex) {
            throw new RuntimeException(ex);
        }
    }
}
