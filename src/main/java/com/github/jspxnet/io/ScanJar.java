package com.github.jspxnet.io;

import java.util.Set;
import java.util.function.Predicate;

import java.util.Set;
import java.util.function.Predicate;

public interface ScanJar {


    Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate);

    default Set<Class<?>> search(String packageName) {
        return search(packageName, null);
    }

}
