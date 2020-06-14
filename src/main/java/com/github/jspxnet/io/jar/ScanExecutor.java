package com.github.jspxnet.io.jar;

import com.github.jspxnet.io.ScanJar;

import java.util.Set;
import java.util.function.Predicate;


public class ScanExecutor implements ScanJar {

    private volatile static ScanExecutor instance;

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate) {
        ScanJar fileSc = new FileScanner();
        Set<Class<?>> fileSearch = fileSc.search(packageName, predicate);
        ScanJar jarScanner = new JarScanner();
        Set<Class<?>> jarSearch = jarScanner.search(packageName, predicate);
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
