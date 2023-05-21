package com.github.jspxnet.io.jar;


import com.github.jspxnet.io.ScanJar;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


@Slf4j
public class FileScanner implements ScanJar {


    private static final String CLASS_SUFFIX = ".class";
    private static final String[] JUMP_CLASS_LIST = {"Big5", "GB2Big5","com\\sun\\syndication","com\\github\\jspxnet\\component\\jxls\\MergeCell","org\\apache\\rocketmq"};

    public FileScanner() {

    }

    public static boolean useRocketMq = ClassUtil.hasClass("org.apache.rocketmq.client.producer.SendCallback");


    private static boolean isJumpClass(String name) {
        if (StringUtil.isNull(name)) {
            return true;
        }
        for (String className : JUMP_CLASS_LIST) {

            if (name.toLowerCase().contains(className.toLowerCase())) {
                return true;
            }
            if (!useRocketMq&&name.toLowerCase().contains("com\\github\\jspxnet\\txweb\\interceptor\\ActionLogRocketInterceptor".toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    private static class ClassSearcher {
        final private Set<Class<?>> classPaths = new HashSet<>();

        private Set<Class<?>> doPath(File file, String packageName, Predicate<Class<?>> predicate, boolean flag,Class<?> annotationClass) {

            if (file.isDirectory()) {
                //文件夹我们就递归
                File[] files = file.listFiles();
                if (!flag) {
                    packageName = packageName + StringUtil.DOT + file.getName();
                }

                assert files != null;
                for (File f1 : files) {
                    doPath(f1, packageName, predicate, false,annotationClass);
                }
            } else {//标准文件
                //标准文件我们就判断是否是class文件

                if (file.getName().endsWith(CLASS_SUFFIX) && !isJumpClass(file.getPath())) {
                    //如果是class文件我们就放入我们的集合中。
                    String className = null;
                    try {
                        className = packageName + StringUtil.DOT + file.getName().substring(0, file.getName().lastIndexOf(StringUtil.DOT));
                        Class<?> clazz = Class.forName(className);
                        if (predicate == null || predicate.test(clazz)) {
                            if (annotationClass==null)
                            {
                                classPaths.add(clazz);
                            } else
                            {
                                Class<Annotation> annotation = ( Class<Annotation>)annotationClass;
                                if (clazz.getAnnotation(annotation)!=null)
                                {
                                    classPaths.add(clazz);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        log.info("not class :{}",className,e);
                        e.printStackTrace();
                    }
                }
            }
            return classPaths;
        }
    }

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate, String defaultPath) {
        //先把包名转换为路径,首先得到项目的classpath
        //然后把我们的包名basPack转换为路径名
        return search( packageName,  predicate,  defaultPath,null);
    }

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate, String defaultPath,Class<?> annotationClass) {
        //先把包名转换为路径,首先得到项目的classpath
        //然后把我们的包名basPack转换为路径名
        String basePackPath = packageName.replace(StringUtil.DOT, File.separator);
        return new ClassSearcher().doPath(new File(defaultPath,basePackPath), packageName, predicate, true,annotationClass);
    }

}
