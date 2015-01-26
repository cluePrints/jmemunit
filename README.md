# JMemUnit
Unit testing suite for the memory usage.

Verify your assumptions about memory usage / GC footprint of your components automatically and regularly.

If you're a geek(or working in a mission-critical environment), you'll be using this from the beginning.
Slackers like me will use it for TDD-style fixes to memory leaks - regressions no more!

## Example 1: verifying memory consumption of a code fragment in isolation

    @RunWith(JmemRunner.class)
    public class MemoryCapTest {
        private static List<Object> hoggedMemory = new ArrayList<Object>();
        
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

## Example 2: Asserting object is subject to GC

    WeakReference<T> referenceToYourObject = ...;
    MemAssert.assertGc(referenceToYourObject);

# Future plans
* Assert amount of garbage generated(wink HPC folks)
* Assert a particular object is subject to GC(yay TDD style fixes of memory leaks)
* Automate comparing heapdumps with baseline ones

# Other
* Feature requests are gladly accepted in the form of PR's(please include test coverage)
