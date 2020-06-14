/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * Created by IntelliJ IDEA.
 * User: chenyuan
 * date: 12-5-4
 * Time: 下午11:49
 * 这里目前只着例子
 * 动态编译
 */
public class DynamicCompile {
    public class JavaStringObject extends SimpleJavaFileObject {
        private String code;

        public JavaStringObject(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return code;
        }
    }

    /**
     * 动态生成类
     *
     * @param path     生成路径
     * @param fileName 文件名
     * @param code     java代码
     * @return 是否生成成功
     */
    public boolean compile(String path, String fileName, String code) {
        // 开始编译
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject fileObject = new JavaStringObject(fileName, code);
        CompilationTask task = javaCompiler.getTask(null, null, null, Arrays.asList("-d", path, "-encoding", Environment.defaultEncode), null, Arrays.asList(fileObject));
        return task.call();
    }

    /**
     * 调用生成的代码,静态方法 这个目录必须是程序执行目录
     *
     * @param className 类名
     * @param method    方法
     * @param args      参数
     * @return 返回值
     * @throws Exception 异常
     */
    public Object invokeStatic(String className, String method, Object[] args) throws Exception {
        Class<?> class1 = ClassUtil.loadClass(className);
        Method callMethod = class1.getDeclaredMethod(method, String[].class);
        return callMethod.invoke(class1.newInstance(), args);
    }


    public static void main(String[] args) throws Exception {

        //这个目录必须是程序执行目录
        String path = "D:\\website\\webapps\\root\\WEB-INF\\classes\\";
        System.out.println("path=" + path);

        StringBuilder code = new StringBuilder();
        code.append("package testaio.hello;");
        code.append("public class Hello{");
        code.append("public String getText(){");
        code.append("    return \"xxxx\";");
        code.append("}");
        code.append("public static void main(String[] args){");
        code.append("   System.out.println(\"helloworld!\");");
        code.append("}");
        code.append("}");

        DynamicCompile dc = new DynamicCompile();
        if (dc.compile(path, "Hello", code.toString())) {
            System.out.println("编译成功");
            dc.invokeStatic("testaio.hello.Hello", "main", new String[]{null});
            //  System.out.println(BeanUtil.invoke();dc..invoke(path, "testaio.hello.Hello", "Text", null));
        }

    }
}