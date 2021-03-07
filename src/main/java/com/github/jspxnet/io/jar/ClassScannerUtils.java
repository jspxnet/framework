package com.github.jspxnet.io.jar;


import java.util.Set;
import java.util.function.Predicate;


public class ClassScannerUtils {

    public static Set<Class<?>> searchClasses(String packageName,String defaultPath) {
        return searchClasses(packageName, null,defaultPath);
    }

    public static Set<Class<?>> searchClasses(String packageName, Predicate predicate,String defaultPath) {
        ScanExecutor scanExecutor = ScanExecutor.getInstance();
        return scanExecutor.search(packageName, predicate,defaultPath);
    }
}
