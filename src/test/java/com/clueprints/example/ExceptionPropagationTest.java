package com.clueprints.example;

import com.clueprints.CanRunInAHeapOf;
import com.clueprints.JmemRunner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

public class ExceptionPropagationTest {    
    @Test
    public void test() throws Exception {
        RunNotifier notifier = new RunNotifier();
        Result result = new Result();
        notifier.addListener(result.createListener());
        JmemRunner runner = new JmemRunner(FixtureTest.class);
        runner.setForkAllowed(false);
        runner.run(notifier);
        
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertSame(FixtureTest.EXPECTED_EXCEPTION, result.getFailures().get(0).getException());
    }

    @Ignore
    public static class FixtureTest {
        public static final RuntimeException EXPECTED_EXCEPTION = new RuntimeException();

        @CanRunInAHeapOf(megabytes=20)
        @Test
        public void test() {
            throw EXPECTED_EXCEPTION;
        }
    }
}
