package com.clueprints.internal;

import java.util.ArrayList;
import java.util.List;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
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
        Result result = call.runTest(klass, method);
        List<Throwable> throwables = new ArrayList<Throwable>();
        for (Failure fail : result.getFailures()) {
            throwables.add(fail.getException());
        }
        
        MultipleFailureException.assertEmpty(throwables);
    }
}
