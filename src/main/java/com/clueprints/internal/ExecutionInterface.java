package com.clueprints.internal;

import org.junit.runners.model.FrameworkMethod;


public interface ExecutionInterface {
    void runTest(Class<?> testClass, FrameworkMethod method);
}
