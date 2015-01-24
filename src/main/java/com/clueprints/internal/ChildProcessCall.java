package com.clueprints.internal;

import com.clueprints.CanRunInAHeapOf;

import com.clueprints.ChildProcessMainThreadException;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;

// Although Eclipse guys did something like this, the instance there is reused making things faster but no suitable for isolation guarantees I want here
// https://eclipse.googlesource.com/jdt/eclipse.jdt.ui/+/Branch21/org.eclipse.jdt.junit/src/org/eclipse/jdt/internal/junit/runner/RemoteTestRunner.java 
public class ChildProcessCall implements ExecutionInterface {
    private static SameProcessCall sameProcessCall = new SameProcessCall();
    
    public Result runTest(Class<?> testClass, FrameworkMethod method) {
        try {
            String classpathParam = System.getProperty("java.class.path");
            System.out.println("Starting with classpath: " + classpathParam);
            
            File tempFile = File.createTempFile(testClass.getName(), method.getName());
            
            int heapMegabytes = method.getAnnotation(CanRunInAHeapOf.class).megabytes();
            List<String> javaParams = new ArrayList<String>();
            String memoryParam = "-Xmx" + heapMegabytes + "m";
            String mainClassParam = ChildProcessCall.class.getName();
            
            javaParams.addAll(Arrays.asList("java", "-cp", classpathParam, memoryParam, mainClassParam));
            javaParams.addAll(Arrays.asList(testClass.getName(), method.getName(), tempFile.getAbsolutePath()));
            
            ProcessBuilder builder = new ProcessBuilder(javaParams.toArray(new String[0]));
            builder.redirectError(Redirect.INHERIT);
            builder.redirectOutput(Redirect.INHERIT);
            Process p = builder.start();
            
            // TODO: timeout
            p.waitFor();
            ChildProcessResult result = readResult(tempFile);
            if (result.getJunitResult() != null) {
                return result.getJunitResult();
            }
            if (result.getMainThreadException() != null) {
                throw new ChildProcessMainThreadException(result.getMainThreadException());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalStateException("This should not happen. Contact devs");
    }

    private ChildProcessResult readResult(File tempFile) throws IOException {
        try {
            ObjectInputStream resultStream = new ObjectInputStream(new FileInputStream(tempFile));
            ChildProcessResult result = (ChildProcessResult) resultStream.readObject();
            resultStream.close();
            return result; 
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("This should not happen. Contact devs", ex);
        } catch (EOFException ex) {
            throw new RuntimeException("Seems our code in child process was not able to write the debug info. Sorry!", ex);
        }
    }
    
    public static void main(String... args) throws Exception {
        String className = args[0];
        String methodName = args[1];
        String resultFilePath = args[2];
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resultFilePath));
        
        Class<?> klass = Class.forName(className);
        Method method = klass.getMethod(methodName);
        try {
            ChildProcessResult res = runAndCaptureResult(klass, method);
            oos.writeObject(res);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            // ideally we should not happen to be here
            ex.printStackTrace();
            System.exit(75656);
        }
    }

    private static ChildProcessResult runAndCaptureResult(Class<?> klass, Method method) {
        // Stolen from the gamedev. The trick is to allocate some memory in the beginning of a project,
        // so when it's necessary to reduce the consumption in the end, you just drop the buffer.
        @SuppressWarnings("unused")
        byte[] tidyUpBuffer = new byte[1024*1024];
        
        ChildProcessResult res = new ChildProcessResult();
        try {
            Result junitResult = sameProcessCall.runTest(klass, new FrameworkMethod(method));
            res.setJunitResult(junitResult);
        } catch (Throwable ex) {
            ex.printStackTrace();
            res.setMainThreadException(ex);
        }
        return res;
    }
}
