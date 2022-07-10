package com.github.jspxnet.io.jar;


import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;


public class ClassScannerUtils {

    public static Set<Class<?>> searchClasses(String packageName,String defaultPath) {
        return searchClasses(packageName, null,defaultPath);
    }

    public static Set<Class<?>> searchClasses(String packageName, Predicate<Class<?>> predicate,String defaultPath) {
        return ScanExecutor.getInstance().search(packageName, predicate,defaultPath);
    }

    public static Set<Class<?>> searchAnnotation(String packageName,String defaultPath,Class<?> annotationClass) {
        return searchAnnotation(packageName, null,defaultPath,annotationClass);
    }

    public static Set<Class<?>> searchAnnotation(String packageName, Predicate<Class<?>> predicate,String defaultPath,Class<?> annotationClass) {
        return ScanExecutor.getInstance().search(packageName, predicate,defaultPath,annotationClass);
    }


}
