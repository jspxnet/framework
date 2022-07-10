package com.github.jspxnet.io.jar;

import com.github.jspxnet.io.ScanJar;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;


public class ScanExecutor implements ScanJar {

    private volatile static ScanExecutor instance;


    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate, String defaultPath) {
        return search( packageName,predicate,  defaultPath,null);
    }

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate, String defaultPath,Class<?> annotationClass) {
        ScanJar fileSc = new FileScanner();
        Set<Class<?>> fileSearch = fileSc.search(packageName, predicate,defaultPath,annotationClass);
        ScanJar jarScanner = new JarScanner();
        Set<Class<?>> jarSearch = jarScanner.search(packageName, predicate,defaultPath,annotationClass);
        fileSearch.addAll(jarSearch);
        return fileSearch;
    }

    private ScanExecutor() {
    }

    public static ScanExecutor getInstance() {
        if (instance == null) {
            synchronized (ScanExecutor.class) {
                if (instance == null) {
                    instance = new ScanExecutor();
                }
            }
        }
        return instance;
    }

}
