package com.clueprints.jmemunit.example;

import com.clueprints.jmemunit.JmemRunner;

import com.clueprints.jmemunit.CanRunInAHeapOf;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

public class TimeoutTest {
    @Test
    public void testTimeoutIsRespected() throws Exception {
        RunNotifier notifier = new RunNotifier();
        Result result = new Result();
        notifier.addListener(result.createListener());
        JmemRunner runner = new JmemRunner(FixtureTest.class);
        runner.setForkAllowed(true);
        runner.run(notifier);

        Assert.assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        Assert.assertThat(exception.getMessage(), Matchers.containsString("test timed out after"));
        
        Assert.assertThat(result.getRunTime(), Matchers.lessThan(1000L));
    }

    @Ignore
    public static class FixtureTest {
        public static final RuntimeException EXPECTED_EXCEPTION = new RuntimeException();

        @CanRunInAHeapOf(megabytes=20)
        @Test(timeout=10)
        public void test() throws Exception{
            Thread.sleep(20000);

            Assert.fail("This should not happen");
        }
    }
}
