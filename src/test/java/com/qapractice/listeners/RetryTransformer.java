package com.qapractice.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * Worked answer to part of task 4.4.
 *
 * <p>Attaches {@link RetryAnalyzer} to every test method, so the policy cannot
 * be forgotten on a new test and can be changed in one place.
 *
 * <p>The raw {@code Class} and {@code Constructor} parameters are not an
 * oversight. {@code IAnnotationTransformer} declares them raw; using
 * {@code Class<?>} produces a method with the same erasure that overrides
 * nothing, so this listener would register and silently do nothing. The
 * {@code @Override} annotation is the only thing that catches it.
 */
@SuppressWarnings("rawtypes")
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(
            ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
