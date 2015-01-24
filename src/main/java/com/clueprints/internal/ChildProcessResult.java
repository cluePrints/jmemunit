package com.clueprints.internal;

import java.io.Serializable;

public class ChildProcessResult implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Throwable mainThreadException;
    
    public Throwable getMainThreadException() {
        return mainThreadException;
    }
    
    public void setMainThreadException(Throwable mainThreadException) {
        this.mainThreadException = mainThreadException;
    }
}
