package com.clueprints.internal;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class CallAdapter extends Statement {
    private final ExecutionInterface call;
    private final Class<?> klass;
    private final FrameworkMethod method;
    
    public CallAdapter(ExecutionInterface call, Class<?> klass, FrameworkMethod method) {
        super();
        this.call = call;
        this.klass = klass;
        this.method = method;
    }

    @Override
    public void evaluate() throws Throwable {
        call.runTest(klass, method);
    }
}
