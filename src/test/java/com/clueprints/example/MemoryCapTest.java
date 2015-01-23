package com.clueprints.example;

import com.clueprints.CanRunInAHeapOf;
import com.clueprints.JmemRunner;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JmemRunner.class)
public class MemoryCapTest {
    private static List<Object> hoggedMemory = new ArrayList<Object>();
    
    @Test
    @CanRunInAHeapOf(megabytes=16)
    public void testBasicOkCaseIsPossible() {}
    
    @Test(expected=Throwable.class)
    @CanRunInAHeapOf(megabytes=16)
    public void testBasicFailureCaseIsPossible() {
        hogMemory(16);
        
        Assert.fail("The test should have failed earlier");
    }
    
    @Test
    @CanRunInAHeapOf(megabytes=17)
    public void testThatAddingMoreMemoryToAKnowHogFixesIt() {
        hogMemory(16);
    }

    private void hogMemory(int megabytes) {
        for (int i=0; i<megabytes * 1024; i++) {
            byte[] arr = new byte[1024];
            hoggedMemory.add(arr);
        }
    }
}
