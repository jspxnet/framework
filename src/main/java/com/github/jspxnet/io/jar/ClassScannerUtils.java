package com.github.jspxnet.io.jar;


import java.util.Set;
import java.util.function.Predicate;


public class ClassScannerUtils {

    public static Set<Class<?>> searchClasses(String packageName) {
        return searchClasses(packageName, null);
    }

    public static Set<Class<?>> searchClasses(String packageName, Predicate predicate) {
        return ScanExecutor.getInstance().search(packageName, predicate);
    }
}
