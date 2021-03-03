package com.github.jspxnet.io.jar;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.io.ScanJar;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


public class FileScanner implements ScanJar {

    private String defaultClassPath;
    private static final String CLASS_SUFFIX = ".class";
    private static final String[] JUMP_CLASS_LIST = {"Big5", "GB2Big5"};


    public String getDefaultClassPath() {
        return defaultClassPath;
    }

    public void setDefaultClassPath(String defaultClassPath) {
        this.defaultClassPath = defaultClassPath;
    }

    public FileScanner(String defaultClassPath) {
        this.defaultClassPath = defaultClassPath;
    }

    public FileScanner() {

        URL url = FileScanner.class.getResource("/");
        if (url!=null)
        {
            defaultClassPath =  url.getPath();
        } else
        {
            defaultClassPath = System.getProperty("user.dir");
        }
    }

    private static boolean isJumpClass(String name) {
        if (StringUtil.isNull(name)) {
            return true;
        }
        for (String className : JUMP_CLASS_LIST) {
            if (name.toLowerCase().contains(className.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static class ClassSearcher {
        final private Set<Class<?>> classPaths = new HashSet<>();

        private Set<Class<?>> doPath(File file, String packageName, Predicate<Class<?>> predicate, boolean flag) {

            if (file.isDirectory()) {
                //文件夹我们就递归
                File[] files = file.listFiles();
                if (!flag) {
                    packageName = packageName + "." + file.getName();
                }

                assert files != null;
                for (File f1 : files) {
                    doPath(f1, packageName, predicate, false);
                }
            } else {//标准文件
                //标准文件我们就判断是否是class文件
                if (file.getName().endsWith(CLASS_SUFFIX) && !isJumpClass(file.getName())) {
                    //如果是class文件我们就放入我们的集合中。

                    try {
                        Class<?> clazz = Class.forName(packageName + "." + file.getName().substring(0, file.getName().lastIndexOf(".")));
                        if (predicate == null || predicate.test(clazz)) {
                            classPaths.add(clazz);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return classPaths;
        }
    }

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate) {
        //先把包名转换为路径,首先得到项目的classpath
        String classpath = defaultClassPath;
        if (classpath==null)
        {
            classpath = StringUtil.empty;
        }
        //然后把我们的包名basPack转换为路径名
        String basePackPath = packageName.replace(".", File.separator);
        String searchPath = classpath + basePackPath;
        return new ClassSearcher().doPath(new File(searchPath), packageName, predicate, true);
    }

}
