package com.github.jspxnet.io.jar;

import com.github.jspxnet.io.ScanJar;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarScanner implements ScanJar {
    public static final String[] NO_SEARCH_CLASS = new String[]{"com.seeyon.ctp.common.po.BasePO","org.junit","com.github.jspxnet.component.jxls","org.apache","org.jxls","net.sf.cglib","com.aliyuncs"};

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate, String defaultPath) {
        return search( packageName, predicate,  defaultPath,null) ;
    }

    @Override
    public Set<Class<?>> search(String packageName, Predicate<Class<?>> predicate, String defaultPath,Class<?> annotationClass) {

        Set<Class<?>> classes = new HashSet<>();
        try {
            //通过当前线程得到类加载器从而得到URL的枚举
            Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(StringUtil.DOT, StringUtil.BACKSLASH));
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();//得到的结果大概是：jar:file:/C:/Users/ibm/.m2/repository/junit/junit/4.12/junit-4.12.jar!/org/junit
                String protocol = url.getProtocol();//大概是jar
                if ("jar".equalsIgnoreCase(protocol)) {
                    //转换为JarURLConnection
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    if (connection != null) {
                        JarFile jarFile = connection.getJarFile();
                        if (jarFile != null) {
                            //得到该jar文件下面的类实体
                            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                            while (jarEntryEnumeration.hasMoreElements()) {
                                    /*entry的结果大概是这样：
                                    org/
                                    org/junit/
                                    org/junit/rules/
                                    org/junit/runners/*/
                                JarEntry entry = jarEntryEnumeration.nextElement();
                                if (entry==null)
                                {
                                    continue;
                                }
                                String jarEntryName = entry.getName();
                                //这里我们需要过滤不是class文件和不在basePack包名下的类
                                String tempName = jarEntryName.replaceAll("/", StringUtil.DOT);
                                if (tempName.endsWith(StringUtil.DOT))
                                {
                                    tempName = tempName.substring(0,tempName.length()-1);
                                }
                                if (tempName.contains(".class") && tempName.startsWith(packageName)&&!ArrayUtil.containsChildIgnore(NO_SEARCH_CLASS,packageName))
                                {
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(StringUtil.DOT)).replace("/", StringUtil.DOT);
                                    Class<?> cls = ClassUtil.loadClass(className);
                                    if (predicate == null || predicate.test(cls)) {
                                        if (annotationClass==null)
                                        {
                                            classes.add(cls);
                                        } else
                                        {
                                            Class<Annotation>  annotation = (Class<Annotation>)annotationClass;
                                            if (cls.getAnnotation(annotation)!=null)
                                            {
                                                classes.add(cls);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if ("file".equalsIgnoreCase(protocol)) {
                    FileScanner fileScanner = new FileScanner();
                    classes.addAll(fileScanner.search(packageName, predicate, url.getPath().replace(packageName.replace(StringUtil.DOT, "/"), "")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }



}
