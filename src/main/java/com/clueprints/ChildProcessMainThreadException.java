package com.clueprints;

public class ChildProcessMainThreadException extends RuntimeException {    
    private static final long serialVersionUID = 1L;

    public ChildProcessMainThreadException() {
        super();
    }

    public ChildProcessMainThreadException(String paramString, Throwable paramThrowable, boolean paramBoolean1,
            boolean paramBoolean2) {
        super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
    }

    public ChildProcessMainThreadException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }

    public ChildProcessMainThreadException(String paramString) {
        super(paramString);
    }

    public ChildProcessMainThreadException(Throwable paramThrowable) {
        super(paramThrowable);
    }
}
