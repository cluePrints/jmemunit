package com.clueprints.example;

import com.clueprints.CanRunInAHeapOf;
import com.clueprints.ChildProcessMainThreadException;
import com.clueprints.JmemRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JmemRunner.class)
public class MemoryCapTest {
    private static List<Object> hoggedMemory = new ArrayList<Object>();
    
    @Test
    @CanRunInAHeapOf(megabytes=16)
    public void testBasicOkCaseIsPossible() {}
    
    @Test(expected=ChildProcessMainThreadException.class)
    @CanRunInAHeapOf(megabytes=16)
    public void testBasicFailureCaseIsPossible() {
        hogMemory(16);
        
        Assert.fail("The test should have failed earlier");
    }
    
    // TODO: stacktrace should be useful
    
    @Test
    @CanRunInAHeapOf(megabytes=20)
    public void testThatAddingMoreMemoryToAKnowHogFixesIt() {
        hogMemory(16);
    }

    private static boolean beforeClassRan;
    private boolean beforeMethodRan;
    private boolean afterMethodRan;
    
    @BeforeClass
    public static void beforeClass() {
        beforeClassRan = true;
    }
    
    @Before
    public void before() {
        beforeMethodRan = true;
    }
    
    @After
    public void after() {
        afterMethodRan = true;
    }

    @Test
    @CanRunInAHeapOf(megabytes=17)
    public void testThatBeforeClassIsTriggered() {
        Assert.assertTrue(beforeClassRan);
    }

    @Test
    @CanRunInAHeapOf(megabytes=17)
    public void testThatBeforeMethodIsTriggered() {
        Assert.assertTrue(beforeMethodRan);
    }
    
    @Test
    @CanRunInAHeapOf(megabytes=17)
    public void testThatAfterMethodIsNotTriggeredBeforeMethodRunFinished() {
        Assert.assertTrue(afterMethodRan);
    }
    
    @Test(expected=MyException.class)
    @CanRunInAHeapOf(megabytes=17)
    public void testExpectExceptionsWork() {
        throw new MyException();
    }

    private void hogMemory(int megabytes) {
        for (int i=0; i<megabytes * 1024; i++) {
            byte[] arr = new byte[1024];
            hoggedMemory.add(arr);
        }
    }
    
    private static class MyException extends RuntimeException {
        
    }
}
