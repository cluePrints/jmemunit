package com.clueprints.internal;

import org.junit.runner.Result;

import java.io.Serializable;

public class ChildProcessResult implements Serializable {
    private static final long serialVersionUID = 2L;
    
    private Result result;
    private Throwable mainThreadException;
    
    public Result getJunitResult() {
        return result;
    }
    
    public void setJunitResult(Result result) {
        this.result = result;
    }
    
    public Throwable getMainThreadException() {
        return mainThreadException;
    }
    
    public void setMainThreadException(Throwable mainThreadException) {
        this.mainThreadException = mainThreadException;
    }
}
