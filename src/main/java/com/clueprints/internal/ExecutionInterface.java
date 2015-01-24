package com.clueprints.internal;

import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;


public interface ExecutionInterface {
    Result runTest(Class<?> testClass, FrameworkMethod method);
}
