package com.clueprints;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used with {@link JmemRunner}. 
 * 
 * Specify how much heap you'd like to constraint your app with and give it a go.
 * It's highly recommended to put ballpark numbers here rather than precies ones:
 * 1) Behavior can differ in between JVM version  
 * 2) The runner itself needs some memory which may change in between library versions 
 *  
 * @author ivan.sobolev
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD })
@Inherited
public @interface CanRunInAHeapOf {
    public int megabytes();
}