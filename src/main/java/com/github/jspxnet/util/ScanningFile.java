package com.github.jspxnet.util;

import com.github.jspxnet.utils.ListUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanningFile {


    private final Class<?> superStrategy = String.class;//接口类class 用于过滤 可以不要

    private final List<Class<? extends String>> eleStrategyList = new ArrayList<>();

    private final ClassLoader classLoader = ScanningFile.class.getClassLoader();//默认使用的类加载器

    //扫描的结果
    private final List<String> result = new ArrayList<>();


    public static void main(String[] args) {
        ScanningFile s = new ScanningFile();
        List<String> fileList = s.scanClass("com.fasterxml");
        System.out.println(ListUtil.toString(fileList, "\r\n"));
    }

    /**
     * 获取包下所有实现了superStrategy的类并加入list
     */
    private List<String> scanClass(String path) {
        URL url = classLoader.getResource(path.replace(".", "/"));
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            // 本地自己可见的代码
            findClassLocal(path);
        } else if ("jar".equals(protocol)) {
            // 引用jar包的代码
            findClassJar(path);
        }
        return result;
    }

    /**
     * 本地查找
     *
     * @param packName 包名
     */
    private void findClassLocal(final String packName) {
        URI url = null;
        try {
            url = classLoader.getResource(packName.replace(".", "/")).toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }

        File file = new File(url);
        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File chiFile) {
                if (chiFile.isDirectory()) {
                    findClassLocal(packName + "." + chiFile.getName());
                }
                if (chiFile.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packName + "." + chiFile.getName().replace(".class", StringUtil.empty));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    result.add(chiFile.getPath());
                    if (superStrategy.isAssignableFrom(clazz)) {
                        eleStrategyList.add((Class<? extends String>) clazz);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * jar包查找
     *
     * @param packName 包名
     */
    private void findClassJar(final String packName) {

        String pathName = packName.replace(".", "/");
        JarFile jarFile = null;
        try {
            URL url = classLoader.getResource(pathName);
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if (jarEntryName.contains(pathName) && !jarEntryName.equals(pathName + "/")) {
                //递归遍历子目录
                if (jarEntry.isDirectory()) {
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findClassJar(prefix);
                }
                if (jarEntry.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", StringUtil.empty));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (superStrategy.isAssignableFrom(clazz)) {
                        eleStrategyList.add((Class<? extends String>) clazz);
                    }
                }
            }
        }
    }
}