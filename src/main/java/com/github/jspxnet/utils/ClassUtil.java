/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.annotation.NullClass;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.interceptor.InterceptorSupport;
import com.github.jspxnet.txweb.support.ActionSupport;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLClassLoader;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.io.*;
import java.net.URL;


/**
 * 涉及类的方法等的工具类。
 *
 * @since 0.4
 */
@Slf4j
public class ClassUtil {

    public static final String METHOD_NAME_SET = "set";
    public static final String METHOD_NAME_GET = "get";
    public static final String METHOD_NAME_IS = "is";
    public static final String[] BASE_NUMBER_TYPE = new String[]{"short","int","long","float","double"};

    public static final String[] NO_CHECK_IS_PROXY = new String[]{"com.seeyon.ctp.common.po.BasePO","org.apache.logging.log4j","org.apache.commons"};




    /**
     * 私有构造方法，防止类的实例化，因为工具类不需要实例化。
     */
    private ClassUtil() {
    }

    public static boolean isNumberType(Type cla) {
        return cla.equals(long.class) || cla.equals(Long.class) || cla.equals(int.class) || cla.equals(Integer.class) || cla.equals(Short.class) ||
                cla.equals(float.class) || cla.equals(Float.class) || cla.equals(double.class) || cla.equals(Double.class) ||
                cla.equals(BigDecimal.class);
    }

    public static boolean isNumberType(String typeName) {
        return ArrayUtil.inArray(new String[]{"int","Integer","long","float","double","BigDecimal","Short"},typeName,true);
    }

    public static boolean isStandardType(Type clazz) {
        return isNumberType(clazz) || clazz.equals(Byte.class) || clazz.equals(Character.class)
                || clazz.equals(String.class) || clazz.equals(char.class) || clazz.equals(boolean.class) || clazz.equals(Boolean.class) ||
                clazz.equals(Date.class) || clazz.equals(Timestamp.class)  || clazz.equals(Time.class) || clazz.equals(java.util.Locale.class);
    }

    public static boolean isStandardProperty(Class<?> clazz) {
        return clazz.isPrimitive() || isNumberProperty(clazz) || clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(Character.class)
                || clazz.isAssignableFrom(String.class) || clazz.isAssignableFrom(char.class) || clazz.isAssignableFrom(Boolean.class) ||
                clazz.isAssignableFrom(Date.class) || clazz.isAssignableFrom(Timestamp.class) ||
                clazz.isAssignableFrom(Time.class)|| clazz.isAssignableFrom(Date.class) || clazz.isAssignableFrom(String.class)
                || clazz.isAssignableFrom(java.util.Locale.class)

                ;
    }


    /**
     * @param clazz 类对象
     * @return 判断是否为数字对象
     */
    public static boolean isNumberProperty(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(short.class) ||
                clazz.isAssignableFrom(Short.class) ||
                clazz.isAssignableFrom(int.class) ||
                clazz.isAssignableFrom(Integer.class) ||
                clazz.isAssignableFrom(long.class) ||
                clazz.isAssignableFrom(Long.class) ||
                clazz.isAssignableFrom(float.class) ||
                clazz.isAssignableFrom(Float.class) ||
                clazz.isAssignableFrom(double.class) ||
                clazz.isAssignableFrom(Double.class) ||
                clazz.isAssignableFrom(BigInteger.class) ||
                clazz.isAssignableFrom(BigDecimal.class);
    }
    public static boolean isBaseNumberType(Type type) {
        if (type == null) {
            return false;
        }
        return ArrayUtil.inArray(BASE_NUMBER_TYPE,type.getTypeName(),false);
    }
    private static boolean isJacMethod(String methodName) {
        return methodName != null && methodName.startsWith("_");
    }

    /**
     * @param c    类
     * @param name 方法
     * @return 是否有此方法
     */
    public static boolean haveMethodsName(Class<?> c, String name) {
        try {
            Method[] methods = c.getMethods();
            for (Method method : methods) {
                if (!(Modifier.isStatic(method.getModifiers()) || isJacMethod(method.getName()))) {
                    if (method.getName().equals(name)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param c    存在方法
     * @param name 方法
     * @return 得到Set 方法的 key方法明  和 value 类型
     */
    public static Map<String, Class<?>> getMethodsNameAndType(Class<?> c, String name) {
        Map<String, Class<?>> result = new HashMap<>(c.getMethods().length);
        try {
            Method[] methods = c.getMethods();
            for (Method method : methods) {
                if (!(Modifier.isStatic(method.getModifiers()) || isJacMethod(method.getName()))) {
                    String mName = method.getName();
                    if (METHOD_NAME_SET.equals(name) && mName.startsWith(name)) {
                        result.put(StringUtil.uncapitalize(mName.substring(name.length())), method.getParameterTypes()[0]);
                    } else if (METHOD_NAME_GET.equals(name) && mName.startsWith(name)) {
                        result.put(StringUtil.uncapitalize(mName.substring(name.length())), method.getReturnType());
                    } else if (METHOD_NAME_IS.equals(name) && mName.startsWith(name)) {
                        result.put(StringUtil.uncapitalize(mName.substring(name.length())), method.getReturnType());
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 得到所有方法命
     *
     * @param cl 类
     * @return String[]  得到所有方法命
     */
    public static String[] getAllMethodsName(Class<?> cl) {
        String[] methodNames = null;
        List<String> tmp = new ArrayList<>();
        try {
            Method[] methods = cl.getMethods();
            for (Method method : methods) {
                if (!(Modifier.isStatic(method.getModifiers()) || isJacMethod(method.getName()))) {
                    tmp.add(method.getName());
                }
            }
            methodNames = new String[tmp.size()];
            for (int i = 0; i < tmp.size(); i++) {
                methodNames[i] = tmp.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methodNames;
    }


    /**
     * 支持 两种写法
     * boolean b = ClassUtil.isSetMethod(MatterView.class,"setRequest");
     * boolean b2 = ClassUtil.isSetMethod(MatterView.class,"request");
     *
     * @param c     类名称
     * @param mName 方法名称
     * @return 判断是否有set方法
     */
    public static Method getSetMethod(Class<?> c, String mName) {
        if (mName == null) {
            return null;
        }
        Method[] methods = getDeclaredMethods(c);
        for (Method method : methods) {

            if (method.getName().equals(mName) && method.getName().startsWith(METHOD_NAME_SET)) {
                return method;
            }
            if (StringUtil.uncapitalize(method.getName().substring(METHOD_NAME_SET.length())).equals(mName) && method.getName().startsWith(METHOD_NAME_SET)) {
                return method;
            }
        }
        return null;
    }

    /**
     * @param mName 字段名称
     * @return 得到方法的字段名称
     */
    public static String getMethodFiledName(String mName) {
        if (mName.startsWith(METHOD_NAME_GET)) {
            return StringUtil.uncapitalize(mName.substring(METHOD_NAME_GET.length()));
        }
        if (mName.startsWith(METHOD_NAME_SET)) {
            return StringUtil.uncapitalize(mName.substring(METHOD_NAME_SET.length()));
        }
        return mName;
    }

    /**
     * Create a new instance given a class name
     *
     * @param className A class name
     * @return A new instance
     * @throws Exception 错误
     */
    public static Object newInstance(String className) throws Exception {
        return Class.forName(StringUtil.trim(className), true, getClassLoader()).newInstance();
    }


    /**
     * @param className 类名
     * @param arg       参数
     * @return 带参数的创建对象
     * @throws Exception 异常
     */
    public static Object newInstance(String className, Object[] arg) throws Exception {
        if (arg == null) {
            return newInstance(className);
        }
        Class<?> c = loadClass(className);
        Class<?>[] classArray = new Class[arg.length];
        for (int i = 0; i < arg.length; i++) {
            classArray[i] = arg[i].getClass();
        }
        Constructor<?> c1 = c.getDeclaredConstructor(classArray);
        c1.setAccessible(true);
        return c1.newInstance(arg);
    }

    /**
     * Load a class given its name.
     * BL: We wan't transfer use a known ClassLoader--hopefully the heirarchy
     * is set correctly.
     *
     * @param className A class name
     * @return The class pointed transfer by [code]className [/code]
     * @throws ClassNotFoundException If a loading error occurs
     */
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(StringUtil.trim(className), true, getClassLoader());
    }

    /**
     * @param resource 载入资源
     * @return URL 路径
     */
    public static URL getResource(String resource) {
        return getClassLoader().getResource(resource);
    }

    /**
     * @param resource 载入资源
     * @return 流方式
     */
    public static InputStream getResourceAsStream(String resource) {
        return getClassLoader().getResourceAsStream(resource);
    }


    /**
     * Return the context classloader.
     * BL: if this is command line dbhand, the classloading issues
     * are more sane.  During upload execution, we explicitly set
     * the ClassLoader.
     *
     * @return The context classloader.
     */
    private static ClassLoader getClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            return System.class.getClassLoader();
        }
    }

    /**
     * Determine the last modification date for this
     * class file or its enclosing library
     *
     * @param aClass A class whose last modification date is queried
     * @return The time the given class was last modified
     * @throws IllegalArgumentException The class was not loaded from a file
     *                                  or directory
     */
    public static long lastModified(Class<?> aClass) throws IllegalArgumentException {
        URL url = aClass.getProtectionDomain().getCodeSource().getLocation();
        if (!"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("Class was not loaded from a file url");
        }

        File directory = new File(url.getFile());
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Class was not loaded from a directory");
        }
        String className = aClass.getName();
        String basename = className.substring(className.lastIndexOf(StringUtil.DOT) + 1);

        File file = new File(directory, basename + ".class");

        return file.lastModified();
    }

    /**
     * @param aClass Name of the class.
     * @return current classpath.
     */
    public static String which(Class<?> aClass) {
        String path = null;
        try {
            path = aClass.getProtectionDomain().getCodeSource().getLocation().toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return path;
    }

    static public String getClassFilePath(String className) {
        if (StringUtil.isNull(className)) {
            return null;
        }
        if (!className.startsWith(StringUtil.BACKSLASH)) {
            className = StringUtil.BACKSLASH + className;
        }
        className = className.replace(StringUtil.DOT, StringUtil.BACKSLASH);
        className = className + ".class";
        java.net.URL classUrl = ClassUtil.class.getResource(className);
        if (classUrl != null) {
            return URLUtil.getUrlDecoder(classUrl.getPath(), Environment.defaultEncode);
        }
        String classpath = System.getProperties().getProperty("java.class.path");
        String[] classPathList = classpath.split(StringUtil.SEMICOLON);
        String basePath;
        for (String path : classPathList) {
            if (!path.contains(".jar") || !path.contains(".zip") || !path.contains(".apk")) {
                basePath = FileUtil.mendPath(path);
                basePath = basePath.substring(0, basePath.length() - 1) + "!" + className;
                if (FileUtil.isFileExist(basePath)) {
                    return basePath;
                }
            }
        }
        return StringUtil.empty;
    }

    //------------------------------------------------------------------------------------------------------------------


    /**
     * @param f1 字段数组1
     * @param f2 字段数组2
     * @return 合并两个字段数组
     */
    public static Field[] addFieldArray(Field[] f1, Field[] f2) {
        if (f1 == null) {
            return f2;
        }
        if (f2 == null) {
            return f1;
        }
        Field[] result = new Field[f1.length + f2.length];
        System.arraycopy(f1, 0, result, 0, f1.length);
        System.arraycopy(f2, 0, result, f1.length, f2.length);
        return result;
    }

    /**
     * @param cls 类
     * @return cls的所有字段
     */
    public static Field[] getDeclaredFields(Class<?> cls) {
        Class<?> superclass =  ClassUtil.getClass(cls);
        Field[] result = null;
        while (!(superclass == null || superclass.equals(Object.class) || superclass.equals(Serializable.class)  || superclass.isInterface()
                || superclass.getName().contains("net.sf.cglib.empty.Object")  || superclass.getName().contains("com.seeyon.ctp.common.po.BasePO"))) {
            Field[] fields = superclass.getDeclaredFields();
            result = addFieldArray(result, fields);
            superclass = superclass.getSuperclass();
        }
        return result;
    }

    /**
     * @param cls 类对象
     * @return 字段列表
     */
    public static String[] getDeclaredFieldNames(Class<?> cls) {
        Field[] fields = ClassUtil.getDeclaredFields(cls);
        Set<String> set = new LinkedHashSet<>();
        for (Field field : fields) {
            set.add(field.getName());
        }
        return set.toArray(new String[0]);
    }

    /**
     *
     * @param cls 类对象
     * @return 字段列表
     */
    public static Map<Integer,String> getDeclaredFieldMap(Class<?> cls) {
        Field[] fields = ClassUtil.getDeclaredFields(cls);
        Map<Integer,String> result = new LinkedHashMap<>();
        int i = 0;
        for (Field field : fields) {
            result.put(i++,field.getName());
        }
        return result;
    }

    public static Field getDeclaredField(Class<?> cls, String fieldName) {
        if (fieldName == null) {
            return null;
        }
        Class<?> childClass = cls;
        while (childClass != null) {
            if (childClass.equals(Object.class) || childClass.equals(Serializable.class) || childClass.isInterface()) {
                break;
            }
            Field[] fields = childClass.getDeclaredFields();
            for (Field f : fields) {
                if (fieldName.equals(f.getName())) {
                    return f;
                }
            }
            childClass = childClass.getSuperclass();
        }
        return null;
    }

    /**
     * @param cls 类
     * @return cls 类的所有方法
     */
    public static Method[] getDeclaredMethods(Class<?> cls) {
        Class<?> childClass = ClassUtil.getClass(cls);
        Method[] result = null;
        while (childClass != null) {
            if (childClass.equals(Object.class) || childClass.equals(Serializable.class)) {
                break;
            }
            result = BeanUtil.joinMethodArray(result, childClass.getDeclaredMethods());
            childClass = childClass.getSuperclass();
        }
        return result;
    }

    /**
     * @param cls  类
     * @param name 方法名称
     * @return 判断是否为这个类的方法
     */
    public static boolean isDeclaredMethod(Class<?> cls, String name) {
        if (name == null) {
            return false;
        }
        Method[] methods = getDeclaredMethods(cls);
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param cls  类
     * @param name 字段名称
     * @return 判断一个字段名称是否存在
     */
    public static boolean isDeclaredField(Class<?> cls, String name) {
        if (name == null) {
            return false;
        }
        Field[] fields = getDeclaredFields(cls);
        return isDeclaredField(fields, name);
    }

    /**
     * 只为提高性能
     *
     * @param fields 字段列表
     * @param name   字段名称
     * @return 判断一个字段名称是否存在
     */
    public static boolean isDeclaredField(Field[] fields, String name) {
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param cls       类对象
     * @param theMethod 方法
     * @return 判断是否为这个类的方法
     */
    public static boolean isDeclaredMethod(Class<?> cls, Method theMethod) {
        if (theMethod == null) {
            return false;
        }
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (method.equals(theMethod)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param cls  类
     * @param name 方法名称
     * @return 根据方法名称返回方法对象
     */
    public static Method getDeclaredMethod(Class<?> cls, String name) {
        if (name == null) {
            return null;
        }
        Method[] methods = getDeclaredMethods(cls);
        for (Method method : methods) {
            if (method.getName().equals(name) || method.getName().equals(METHOD_NAME_GET + StringUtil.capitalize(name))
                    || method.getName().equals(METHOD_NAME_SET + StringUtil.capitalize(name))
                    || method.getName().equals(METHOD_NAME_IS + StringUtil.capitalize(name))) {

                return method;
            }
        }
        return null;
    }

    /**
     *
     * @param cls 类
     * @param name 名称
     * @param ignore 不分大小写
     * @return 得到同名的方法列表
     */
    public static Method[] getDeclaredMethodList(Class<?> cls, String name,boolean ignore) {
        if (name == null) {
            return null;
        }
        Method[] result = null;
        Method[] methods = getDeclaredMethods(cls);
        for (Method method : methods) {
            if (!ignore&&method.getName().equals(name) || ignore&&method.getName().equalsIgnoreCase(name)) {
                result = BeanUtil.appendMethodArray(result, method);
            }
        }
        return result;
    }

    /**
     * @param cls  类
     * @param name 名称
     * @return 得到同名的方法列表
     */
    public static Method[] getDeclaredMethodList(Class<?> cls, String name) {
        return getDeclaredMethodList(cls, name,false);
    }
    /**
     * @param cls  类
     * @param name 方法名称
     * @param pi   参数
     * @return 得到这个类根据方法名称，并且参数个数要相同
     */
    public static Method getDeclaredMethod(Class<?> cls, String name, int pi) {
        if (name == null) {
            return null;
        }
        Method[] methods = getDeclaredMethods(cls);
        for (Method method : methods) {
            if ((method.getName().equals(name))
                    && method.getParameterCount() == pi) {
                return method;
            }
        }
        return null;
    }

    /**
     * @param cls 类
     * @return 得到得到所有set的方法
     */
    public static Method[] getDeclaredSetMethods(Class<?> cls) {
        Method[] methods = getDeclaredMethods(cls);
        Method[] result = null;
        for (Method method : methods) {
            if (method.getName().startsWith(METHOD_NAME_SET)) {
                result = BeanUtil.appendMethodArray(result, method);
            }
        }
        return result;
    }


    /**
     * @param cls 类
     * @param pi  参数个数
     * @return 得到所有get方法
     */
    public static Method[] getDeclaredReturnMethods(Class<?> cls, int pi) {
        Method[] methods = getDeclaredMethods(cls);
        if (methods==null)
        {
            return null;
        }
        Method[] result = null;
        for (Method method : methods) {
            if (!method.getGenericReturnType().equals(Void.TYPE) && (pi != -1 && method.getParameterCount() == pi)) {
                result = BeanUtil.appendMethodArray(result, method);
            }
        }
        return result;
    }

    /**
     * @param method 方法
     * @return 得到方法参数
     */
    public static Map<String, Type> getParameterNames(Method method) {
        if (method == null || method.getParameterCount() <= 0) {
            return null;
        }
        Parameter[] parameters = method.getParameters();
        Type[] types = method.getGenericParameterTypes();
        Map<String, Type> map = new HashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; ++i) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                log.error("编译的时候必须加入编译参数 -parameters，否则不能正常使用此功能");
                return null;
            }
            map.put(param.getName(), types[i]);
        }
        return map;
    }

    /**
     * 通过方法得到对应的参数指
     * @param method 方法
     * @param varName 变量名称
     * @param values 变量数组
     * @return 值
     */
    public static Object getParameterValue(Method method,String varName,Object[] values ) {
        if (method == null || method.getParameterCount() <= 0 || ObjectUtil.isEmpty(values)) {
            return null;
        }
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; ++i) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                log.error("编译的时候必须加入编译参数 -parameters，否则不能正常使用此功能");
                return null;
            }
            if (param.getName().equals(varName)&&i<values.length)
            {
                return values[i];
            }
        }
        return null;
    }


    /**
     * @param method 方法  例如: setName  返回:name
     * @return 得到方法名称
     */
    public static String getCallMethodName(Method method) {
        if (method == null) {
            return null;
        }
        if (method.getName().startsWith(METHOD_NAME_GET) || method.getName().startsWith(METHOD_NAME_SET)) {
            return StringUtil.uncapitalize(method.getName().substring(3));
        }
        if (method.getName().startsWith(METHOD_NAME_IS)) {
            return StringUtil.uncapitalize(method.getName().substring(METHOD_NAME_IS.length()));
        }
        return method.getName();
    }

    public static boolean isArrayType(Type type) {
        if (type==null)
        {
            return false;
        }
        return type.equals(String[].class) || type.equals(int[].class) || type.equals(Integer[].class) ||
                type.equals(long[].class) || type.equals(Long[].class) ||
                type.equals(float[].class) || type.equals(Float[].class) ||
                type.equals(double[].class) || type.equals(Double[].class) ||
                type.equals(char[].class) || type.equals(Character[].class) ||
                type.equals(byte[].class) || type.equals(BigInteger[].class) ||
                type.equals(BigDecimal[].class) || type.equals(Object[].class);
    }

    public static boolean isCollection(Type o) {
        return o != null && (o.toString().contains(".List")||o.toString().contains(".Collection")||o.toString().contains(".Iterable") );
    }


    /**
     * @param o 对象
     * @return 判断是否对象为一个集合，列表 类型
     */
    public static boolean isCollection(Object o) {
        return o != null && (o.getClass().isArray() || o instanceof Collection );
    }


    /**
     * Calls a static method
     *
     * @param aClass  类
     * @param aMethod 方法
     * @param args    参数
     * @return an object
     */
    public static Object callStaticMethod(Class<?> aClass, String aMethod, Object... args) {
        if (aClass == null) {
            return null;
        }
        try {
            Class<?>[] argClasses = getClassArray(args);
            Method method;
            if (argClasses != null) {
                method = aClass.getMethod(aMethod, argClasses);
            } else {
                method = aClass.getMethod(aMethod);
            }
            return method.invoke(aClass, args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * @param className  类名
     * @param methodName 方法名
     * @param args       参数数组
     * @return 执行方法返回的结果
     * @throws Exception 异常 没有类 运行错误
     */
    public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception {
        Class<?> ownerClass = loadClass(className);
        if (args==null)
        {
            return ownerClass.getMethod(methodName).invoke(null);
        }

        Class<?>[] argsClass = new Class[args.length];
        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }
        return ownerClass.getMethod(methodName, argsClass).invoke(null, args);
    }

    /**
     * @param args 对象
     * @return 得到对象的类型数组
     */
    public static Class<?>[] getClassArray(Object[] args) {
        if (args == null) {
            return null;
        }
        Class<?>[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argClasses[i] = ClassUtil.getClass(args[i].getClass());
        }

        return argClasses;
    }

    /**
     * @param implClass 对象
     * @return hessian 查询调用接口方法
     */
    public static Class<?> findRemoteApi(Class<?> implClass) {
 		if (implClass == null) {
            return null;
        }
        if (implClass.isInterface())
        {
            return implClass;
        }
        implClass = ClassUtil.getClass(implClass);
        if (implClass == null || implClass.equals(com.caucho.services.server.GenericService.class)) {
            return null;
        }
        Class<?>[] interfaces = implClass.getInterfaces();
        if (interfaces.length == 1) {
            return interfaces[0];
        }
        return findRemoteApi(implClass.getSuperclass());
    }


    /**
     * IOC 得到接口，接口名称保存在IOC中
     * 为了唯一的识别,不会放入 SoberSupport.class Action.class 这种接口
     * @param implClass 类对象
     * @return IOC 得到接口
     */
    public static Class<?> getImplements(Class<?> implClass) {
        if (implClass == null || implClass.equals(com.caucho.services.server.GenericService.class) || ClassUtil.isStandardProperty(implClass)
                || implClass.equals(Serializable.class) || implClass.equals(Map.class) || implClass.equals(List.class) || implClass.equals(Runnable.class)
                || implClass.equals(SoberSupport.class) || implClass.equals(Action.class) || implClass.equals(ActionSupport.class)
                || implClass.equals(InterceptorSupport.class)
        ) {
            return null;
        }
        Class<?>[] interfaces = implClass.getInterfaces();
        if (interfaces.length == 1) {
            Class<?> result = interfaces[0];
            if (isIocInterfaces(result)) {
                return result;
            }
        }
        return getImplements(implClass.getSuperclass());
    }

    /**
     * @param implClass 接口对象
     * @return 得到ioc接口
     */
    public static boolean isIocInterfaces(Class<?> implClass) {
        return implClass != null && !implClass.equals(com.caucho.services.server.GenericService.class) && !ClassUtil.isStandardProperty(implClass)
                && !implClass.equals(Serializable.class) && !implClass.equals(Map.class) && !implClass.equals(List.class) && !implClass.equals(Runnable.class)
                && !implClass.equals(SoberSupport.class);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
     *
     * @param clazz The class transfer introspect
     * @return the first generic declaration, or [code]Object.class } if cannot be determined
     */
    public static Class<?> getSuperClassType(Class<?> clazz) {
        return getSuperClassType(clazz, 0);
    }


    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager
     *
     * @param clazz 类对象
     * @param index 参数
     * @return 类对象
     * @throws IndexOutOfBoundsException 异常
     */
    public static Class<?> getSuperClassType(Class<?> clazz, int index)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return params[index].getClass();
    }

    /**
     * 得到枚举类型的所有值列表
     *
     * @param cla       类
     * @param fieldName 字段名
     * @return 值对象

     */
    public static Object[] getEnumFieldValue(Class<?> cla, String fieldName) {
        if (cla==null||cla.equals(NullClass.class))
        {
            return new Object[0];
        }
        Object[] result = null;
        try {
            Field field = cla.getDeclaredField(fieldName);
            if (cla.isEnum()) {
                Object[] objects = cla.getEnumConstants();
                for (Object enu : objects) {
                    field.setAccessible(true);
                    result = ArrayUtil.add(result, field.get(enu));
                }
            }
        } catch (Exception e) {
            log.error("cla:{},fieldName:{},error:{}",cla,fieldName,e.getMessage());
            return null;
        }
        return result;
    }

    /**
     *
     * @param cla 枚举类
     * @param methodName 方法名称
     * @return 得到枚举列表
     */
    public static Object[] getEnumMethodValue(Class<?> cla, String methodName)  {
        if (cla==null||cla.equals(NullClass.class))
        {
            return new Object[0];
        }
        Object[] result = null;
        try {
            Method method = cla.getDeclaredMethod(methodName);
            if (cla.isEnum()) {
                Object[] objects = cla.getEnumConstants();
                for (Object enu : objects) {
                    result = ArrayUtil.add(result, method.invoke(enu));
                }
            }
        } catch (Exception e) {
            log.error("cla:{},fieldName:{},error:{}",cla,methodName,e.getMessage());
        }

        return result;
    }

    public static Map<Object,Object> getEnumMap(Class<?> cla, String fieldName,String name) {
        if (cla==null||cla.equals(NullClass.class))
        {
            return new HashMap<>(0);
        }
        Map<Object,Object> result = new HashMap<>(5);
        try {
            Field field = cla.getDeclaredField(fieldName);
            Field fieldDes = cla.getDeclaredField(name);
            if (cla.isEnum()) {
                Object[] objects = cla.getEnumConstants();
                for (Object enu : objects) {
                    field.setAccessible(true);
                    fieldDes.setAccessible(true);
                    result.put(field.get(enu),fieldDes.get(enu));
                }
            }
        } catch (Exception e) {
            log.error("cla:{},fieldName:{},{}, error:{}",cla,fieldName,name,e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * 动态的查询枚举对象
     * TalkEnumType oo = ClassUtil.invokeEnumMethod(TalkEnumType.class,"find",1);
     * @param cla 枚举类型
     * @param methodName 要执行的枚举静态方法
     * @param value 值
     * @param <T> 返回类型,还是枚举
     * @return  返回类型, 没有为空
     */
    public static <T> T invokeEnumMethod(Class<T> cla,String methodName,int value)
    {
        if (cla==null)
        {
            return null;
        }
        try {
            Method method = cla.getMethod(methodName,int.class);
            return (T)method.invoke(null,value);
        } catch (Exception e) {
            log.error("cla:{},fieldName:{},value:{},error:{}",cla,methodName,value,e.getMessage());
        }
        return null;
    }

    /**
     * 得到classname
     * @param className 排除代理
     * @return 返回类名称
     */
    public static String getClassName(String className)
    {
        if ("net.sf.cglib.empty.Object".equals(className))
        {
            return null;
        }
        if (className.contains("$$"))
        {
            return StringUtil.substringBefore(className,"$$");
        }
        return className;
    }


    public static boolean inNoCheckProxyClass(Class<?> type)
    {
        if (type==null)
        {
            return true;
        }
        for (String className:NO_CHECK_IS_PROXY)
        {

            if (type.getName().toLowerCase().startsWith(className.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }



    /**
     *
     * @param type 对象的类型
     * @return 判断是否为代理对象
     */
    public static boolean isProxy(Class<?> type) {
        if (type == null || inNoCheckProxyClass(type)) {
            return false;
        }
        if (ClassUtil.isStandardProperty(type))
        {
            return false;
        }
        try {
            return type.getName().contains("CGLIB$$") || Enhancer.isEnhanced(type);
        } catch (Exception e)
        {
            e.printStackTrace();
            log.info("判断代理异常:{}",type,e);
        }
        return false;
    }

    public static Class<?> getClass(Class<?> type)  {
        Class<?> cls = type;
        try {
            if (ClassUtil.isProxy(type))
            {
                cls = ClassUtil.loadClass(ClassUtil.getClassName(type.getName()));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cls;
    }


    /**
     * 通过泛型模版得到类
     * @param typeModel 泛型模版
     *  <pre>{@code
     *      com.github.jspxnet.txweb.result.RocResponse<java.util.List<jspx.jcommon.model.dto.FrameworkTemplateDto>>
     *  }</pre>
     * @return 类列表
     */
    public static List<Class<?>> getClassForTypeModel(String typeModel)  {

        List<String> classNameList = new ArrayList<>();
        String className = StringUtil.trim(StringUtil.substringBefore(typeModel,"<"));
        typeModel = StringUtil.substringOutBetween(typeModel,"<",">");
        if (!StringUtil.isEmpty(className))
        {
            classNameList.add(className);
        }
        while (!StringUtil.isEmpty(className)&&!StringUtil.isEmpty(typeModel))
        {
            className = StringUtil.trim(StringUtil.substringBefore(typeModel,"<"));
            typeModel = StringUtil.substringOutBetween(typeModel,"<",">");
            if (className.contains(StringUtil.COMMAS))
            {
                String[] classNames = StringUtil.split(className,StringUtil.COMMAS);
                for (String name:classNames)
                {
                    if (!StringUtil.isEmpty(name)&&!name.contains("[")&&!classNameList.contains(name))
                    {
                        classNameList.add(name);
                    }
                }
            } else
            if (!StringUtil.isEmpty(className)&&!className.contains("[")&&!classNameList.contains(className))
            {
                classNameList.add(className);
            }
        }

        List<Class<?>> resultList = new ArrayList<>();
        for (String name:classNameList)
        {
            if (name==null)
            {
                continue;
            }
            try {
                if ("string".equalsIgnoreCase(name))
                {
                    resultList.add(String.class);
                } else
                if ("float".equalsIgnoreCase(name))
                {
                    resultList.add(String.class);
                }else if ("double".equalsIgnoreCase(name))
                {
                    resultList.add(String.class);
                } else
                if ("int".equalsIgnoreCase(name))
                {
                    resultList.add(Integer.class);
                } else
                if ("list".equalsIgnoreCase(name))
                {
                    resultList.add(List.class);
                } else
                if ("map".equalsIgnoreCase(name))
                {
                    resultList.add(Map.class);
                }

                else
                {
                    if (name.contains(StringUtil.DOT)&&!name.endsWith("[]"))
                    {
                        resultList.add(ClassUtil.loadClass(name));
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("错误的类名:{}",name);
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     *
     * @return 得到当前系统运行的jar列表
     */
    public static List<File> getRunJarList()
    {
        List<File> result = new ArrayList<>();
        ClassLoader effectiveClassLoader = ClassUtil.class.getClassLoader();
        URL[] classPath = ((URLClassLoader) effectiveClassLoader).getURLs();
        for (URL url:classPath)
        {
            File file = new File(url.getFile());
            if (file.isFile())
            {
                result.add(file);
            }
        }
        return result;
    }

    public static List<File> getRunJarDir()
    {
        List<File> result = new ArrayList<>();
        ClassLoader effectiveClassLoader = ClassUtil.class.getClassLoader();
        URL[] classPath = ((URLClassLoader) effectiveClassLoader).getURLs();
        for (URL url:classPath)
        {
            File file = new File(url.getFile());
            if (file.isFile())
            {
                file = file.getParentFile();
                if (file.getPath().contains("repository"))
                {
                    continue;
                }
            }
            if (file.isDirectory()&&!result.contains(file))
            {
                result.add(file);
            }
        }
        File file = new File(System.getProperty("user.dir"));
        if (!result.contains(file))
        {
            result.add(file);
        }
        return result;
    }
}