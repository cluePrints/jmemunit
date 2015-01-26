package com.clueprints.jmemunit;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import org.junit.Assert;

public class MemAssert {
    public static <T> void assertGc(WeakReference<T> reference) {
        Assert.assertFalse(isReachable(reference));
    }

    static <T> boolean isReachable(WeakReference<T> reference) {
        int totalGcRuns = countGcRuns();
        int tries = 0;
        do {
            System.out.println("Triggering a GC run");
            Runtime.getRuntime().gc();
            Assert.assertTrue("Oops. Seems GC can't be triggered from code. We've tried a few times. Contact devs", tries++ < 100);
        } while (totalGcRuns == countGcRuns());

        return reference.get() != null;
    }

    private static int countGcRuns() {
        int totalGcRuns = 0;
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            totalGcRuns += bean.getCollectionCount();
        }
        return totalGcRuns;
    }
}
