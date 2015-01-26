package com.clueprints;

import java.lang.ref.WeakReference;
import org.junit.Assert;
import org.junit.Test;

public class MemAssertTest {
    @Test
    public void shouldDetectReachableObjects() {
        Object referent = new Object();

        boolean reachable = MemAssert.isReachable(new WeakReference<Object>(referent));
        Assert.assertTrue(reachable);
    }

    @Test
    public void shouldDetectUnreachableObjects() {
        boolean reachable = MemAssert.isReachable(new WeakReference<Object>(new Object()));
        Assert.assertFalse(reachable);
    }
}
