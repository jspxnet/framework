/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.factory;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.network.rpc.client.proxy.NettyRpcProxy;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.annotation.*;
import com.github.jspxnet.sioc.interceptor.GlobalMethodInterceptor;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;
import com.github.jspxnet.sioc.tag.*;
import com.github.jspxnet.sioc.util.AnnotationUtil;
import com.github.jspxnet.sioc.util.Empty;
import com.github.jspxnet.txweb.enums.RpcProtocolEnumType;
import com.github.jspxnet.txweb.service.HessianClient;
import com.github.jspxnet.txweb.service.client.HessianClientFactory;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.TypeUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 15:59:47
 * ioc 对象工厂
 */
@Slf4j
public final class EntryFactory implements BeanFactory {

    private final static Lifecycle LIFE_CYCLE = new LifecycleManager();
    private final static Map<String, BeanElement> INJECT_OBJECTS = new HashMap<>();
    private static IocContext iocContext;

    @Override
    public IocContext getIocContext() {
        return iocContext;
    }

    @Override
    public void setIocContext(IocContext iocContext) {
        EntryFactory.iocContext = iocContext;
    }

    /**
     * 创建生命周期对象
     *
     * @param beanElement 配置
     * @param namespace   命名空间
     * @return 返回生命对象
     * @throws Exception 异常 错误
     *                   解析错误
     */
    private LifecycleObject createLifecycleObject(BeanElement beanElement, final String namespace) throws Exception {
        Map<String, Object> paraMap = new HashMap<>(TypeUtil.getPropertyValue(beanElement.getPropertyElements(), namespace));

        //////////////array begin
        for (TagNode element : beanElement.getArrayElements()) {
            ArrayElement array = (ArrayElement) element;
            paraMap.put(array.getId(), TypeUtil.getArrayValue(array, namespace, this));
        }
        //////////////array end

        //////////////list begin
        for (TagNode element : beanElement.getListElements()) {
            ListElement list = (ListElement) element;
            paraMap.put(list.getId(), TypeUtil.getListValue(list, namespace, this));
        }
        //////////////list end

        //////////////map begin
        for (TagNode element : beanElement.getMapElements()) {
            MapElement map = (MapElement) element;
            paraMap.put(map.getId(), TypeUtil.getMapValue(map, namespace, this));
        }
        //////////////map end

        LifecycleObject lifecycleObject = new LifecycleObject();
        lifecycleObject.setSingleton(beanElement.isSingleton());
        lifecycleObject.setClassName(beanElement.getClassName());
        lifecycleObject.setCreate(beanElement.getCreate());
        lifecycleObject.setNamespace(namespace);
        lifecycleObject.setParamMap(paraMap);
        if (lifecycleObject.isSingleton()) {
            //创建最底层的对象
            BeanElement beanElementTmp = iocContext.getBeanElement(lifecycleObject.getName(), lifecycleObject.getNamespace());
            if (beanElementTmp != null) {
                lifecycleObject.setNamespace(beanElementTmp.getNamespace());
            }
            lifecycleObject.setObject(createEntry(lifecycleObject));

        }
        return lifecycleObject;
    }

    /**
     * @param lifecycleObject 创建实体对象
     * @return 创建实体对象
     */
    @Override
    public Object createEntry(LifecycleObject lifecycleObject) throws Exception {
        if (lifecycleObject.isSingleton() && lifecycleObject.getObject() != null) {
            return lifecycleObject.getObject();
        }
        Object result = null;
        Class<?> cla = ClassUtil.loadClass(lifecycleObject.getClassName());
        if (Sioc.KEY_RPC_CLIENT.equalsIgnoreCase(lifecycleObject.getCreate())) {
            RpcClient rpcClient = cla.getAnnotation(RpcClient.class);
            if (rpcClient == null) {
                throw new Exception(lifecycleObject.getClassName() + "没有定义@RpcClient");
            }
            Class<?> classObj = rpcClient.bind().equals(Empty.class) ? cla : rpcClient.bind();
            if (RpcProtocolEnumType.TCP.equals(rpcClient.protocol())) {
                result = NettyRpcProxy.create(classObj, rpcClient.url(), rpcClient.groupName());
            }
            if (RpcProtocolEnumType.HTTP.equals(rpcClient.protocol())) {
                HessianClient hessianClient = HessianClientFactory.getInstance();
                //读取本地配置
                String hessianUrl = rpcClient.url();
                if (StringUtil.isNull(hessianUrl)) {
                    throw new Exception(cla.getName() + " RpcClient url is null,不允许为空");
                }
                if (!hessianUrl.startsWith("http") && hessianUrl.contains(StringUtil.DOT)) {
                    String beanName = StringUtil.substringBeforeLast(hessianUrl, StringUtil.DOT);
                    String beanField = StringUtil.substringAfterLast(hessianUrl, StringUtil.DOT);
                    Object urlConfigObj = getBean(beanName);
                    if (urlConfigObj == null) {
                        throw new Exception(cla.getName() + " RpcClient url not found,不能得到配置," + hessianUrl);
                    }
                    if (ClassUtil.isDeclaredField(urlConfigObj.getClass(), beanField)) {
                        hessianUrl = BeanUtil.getFieldValue(urlConfigObj, beanField);
                    } else {
                        hessianUrl = (String) BeanUtil.getProperty(urlConfigObj, beanField);
                    }
                }
                result = hessianClient.getInterface(classObj, hessianUrl);
            }
        } else {
            result = ClassUtil.newInstance(lifecycleObject.getClassName());
            if (AnnotationUtil.hasProxyMethod(cla)) {
                GlobalMethodInterceptor methodInterceptor = new GlobalMethodInterceptor();
                result = methodInterceptor.getProxyInstance(result);
            }

        }

        //载入要注册的bean   begin
        setImportIoc(cla, result);
        //载入要注册的bean   end

        ////////Property 配置 begin
        setProperty(cla, result);
        ////////Property 配置 end

        //已经设置过的方法就不在设置,只设置一次
        ////////Ref对象注入 begin
        String[] setRefField = setRef(cla, result, lifecycleObject.getRefNamespace());
        ////////Ref对象注入 end

        //////////////设置参数 begin
        Map<String, Object> paramMap = lifecycleObject.getParamMap();
        for (String name : paramMap.keySet()) {
            if (name == null) {
                continue;
            }
            Object pValue = paramMap.get(name);
            if (pValue instanceof String) {
                String tmp = (String) pValue;
                if (tmp.startsWith(Sioc.IocLoad)) {
                    tmp = tmp.substring(Sioc.IocLoad.length());
                    String beanName = tmp.substring(0, tmp.indexOf(Sioc.IocFen));
                    pValue = this.getBean(beanName, lifecycleObject.getNamespace());
                } else if (tmp.contains(Sioc.IocRootNamespace)) {
                    pValue = StringUtil.replace((String) pValue, Sioc.IocRootNamespace, TypeUtil.getRootNamespace(lifecycleObject.getNamespace()));
                } else if (tmp.contains(Sioc.IocNamespace)) {
                    pValue = StringUtil.replace((String) pValue, Sioc.IocNamespace, lifecycleObject.getNamespace());
                }
            }
            BeanUtil.setSimpleProperty(result, name, pValue);
            setRefField = ArrayUtil.add(setRefField,name);
        }

        ////DAO injection begin
        List<BeanElement> injectionBeanElements = iocContext.getInjectionBeanElements();
        if (!injectionBeanElements.isEmpty()) {
            for (BeanElement beanElement : injectionBeanElements) {
                if (StringUtil.isNull(beanElement.getInjection())) {
                    continue;
                }
                if (!INJECT_OBJECTS.containsKey(beanElement.getInjection())) {
                    INJECT_OBJECTS.put(beanElement.getInjection(), beanElement);
                }
            }

            Method[] methods = ClassUtil.getDeclaredSetMethods(cla);
            if (methods != null) {
                for (Method method : methods) {
                    if (ArrayUtil.contains(setRefField,method.toString())) {
                        //外部设置优先，如果外部设置过了，这里就不在自动注入
                        continue;
                    }
                    String setFiledName = ClassUtil.getMethodFiledName(method.getName());
                    if (paramMap.containsKey(setFiledName)) {
                        //已经设置过的变量
                        continue;
                    }
                    if (INJECT_OBJECTS.containsKey(setFiledName)) {
                        BeanElement beanElement = INJECT_OBJECTS.get(setFiledName);
                        Object inObj = getBean(beanElement.getId(), beanElement.getNamespace());
                        //log.debug("{} 自动注入对象 ioc namespace {},method {},inject {}", result, lifecycleObject.getNamespace(), setFiledName, inObj);
                        BeanUtil.setSimpleProperty(result, method.getName(), inObj);
                    }
                }
            }
        }
        ////DAO injection end

        //这里上配置方式的的初始化,配置优先
        AnnotationUtil.invokeInit(result);

        ////////代理创建对象 begin
        if (!StringUtil.isNull(lifecycleObject.getCreate()) && !Sioc.KEY_RPC_CLIENT.equalsIgnoreCase(lifecycleObject.getCreate())) {
            result = BeanUtil.getProperty(result, lifecycleObject.getCreate());
        }
        ////////代理创建对象 end

        return result;
    }

    /**
     * 设置资源后的对象
     *
     * @param o         对象
     * @param namespace 命名空间
     */
    @Override
    public String[] setRef(Class<?> superclass, Object o, final String namespace) throws Exception {
        if (o == null) {
            return null;
        }
        String[] setRefField = null;
        // Ref 支持字段方式，和设置方法两种
        Field[] fields = ClassUtil.getDeclaredFields(superclass);
        if (fields != null) {
            for (Field field : fields) {
                if (Modifier.isFinal(field.getModifiers()))
                {
                    continue;
                }
                Ref ref = field.getAnnotation(Ref.class);
                if (ref == null) {
                    continue;
                }
                if (ArrayUtil.contains(setRefField,field.getName()))
                {
                    continue;
                }
                String beanId = ref.name();
                if (StringUtil.isEmpty(beanId) && !ref.bind().equals(Empty.class)) {
                    beanId = ref.bind().getName();
                }
                if (StringUtil.isEmpty(beanId)) {
                    beanId = field.getType().getName();
                }
                String mNamespace = (StringUtil.isNull(ref.namespace()) || Sioc.global.equalsIgnoreCase(ref.namespace())) ? namespace : ref.namespace();
                Object obj = getBean(beanId, mNamespace);

                if (ref.test()) {
                    if (!containsBean(beanId, mNamespace)) {
                        log.info("sioc config error, no find name=" + ref.name() + " namespace=" + mNamespace + " ref=" + o.getClass().getName());
                    } else {
                        field.setAccessible(true);
                        field.set(o, obj);
                        setRefField = ArrayUtil.add(setRefField,o.hashCode()+ field.getName());
                    }
                } else {
                    field.setAccessible(true);
                    field.set(o, obj);
                    setRefField = ArrayUtil.add(setRefField,field.getName());
                }
            }
        }

        //字段
        Method[] methods = ClassUtil.getDeclaredMethods(superclass);
        if (methods != null) {
            for (Method method : methods) {
                Ref ref = method.getAnnotation(Ref.class);
                if (ref == null) {
                    continue;
                }

                if (ArrayUtil.contains(setRefField,method.toString()))
                {
                    continue;
                }
                String beanId = ref.name();
                if (!StringUtil.hasLength(beanId)) {
                    Class<?>[] classes = method.getParameterTypes();
                    if (!ArrayUtil.isEmpty(classes)) {
                        beanId = classes[0].getName();
                    }
                }

                String mNamespace = StringUtil.isNull(ref.namespace()) || Sioc.global.equalsIgnoreCase(ref.namespace()) ? namespace : ref.namespace();
                if (ref.test()) {
                    if (!containsBean(beanId, mNamespace)) {
                        log.info("jspx sioc config error, no find name=" + ref.name() + " namespace=" + mNamespace + " ref=" + o.getClass().getName());
                    } else {
                        method.invoke(o, getBean(beanId, mNamespace));
                        setRefField = ArrayUtil.add(setRefField, method.getName());
                    }
                } else if (!ref.test()) {
                    method.invoke(o, getBean(beanId, mNamespace));

                    setRefField = ArrayUtil.add(setRefField, method.toString());
                }
            }
        }
        return setRefField;
    }

    private void setImportIoc(Class<?> superclass, Object o) {
        if (o == null) {
            return;
        }
        ImportIoc importIoc = superclass.getAnnotation(ImportIoc.class);
        if (importIoc == null) {
            return;
        }
        Class<?>[] classes = importIoc.value();
        if (ArrayUtil.isEmpty(classes)) {
            return;
        }
        for (Class<?> cla : classes) {
            iocContext.registryIocBean(cla);
        }
    }

    /**
     * 设置配置属性
     *
     * @param superclass 类型,避免代理的时候错误
     * @param o          对象
     * @throws Exception 异常
     */
    public void setProperty(Class<?> superclass, Object o) throws Exception {
        if (o == null) {
            return;
        }

        Map<String, Object> valueMap;
        PropertySource propertySource = superclass.getAnnotation(PropertySource.class);
        if (propertySource != null) {
            File loadFile = null;
            String[] fileNames = propertySource.value();
            if (!ArrayUtil.isEmpty(fileNames)) {
                for (String fileName : fileNames) {
                    File f = EnvFactory.getFile(fileName);
                    if (f != null && f.isFile()) {
                        loadFile = f;
                    }
                }
            }

            if (loadFile == null) {
                String msg = o + " propertySource not find file:" + ArrayUtil.toString(propertySource.value(), ";");
                log.error(msg);
                if (!propertySource.ignore()) {
                    throw new Exception(msg);
                }
            }

            String cacheKey = propertySource.name();
            if (StringUtil.isNull(cacheKey)) {
                if (loadFile != null) {
                    cacheKey = loadFile.getAbsolutePath();
                }
            }
            if (cacheKey.length() > 32) {
                cacheKey = EncryptUtil.getMd5(cacheKey);
            }

            valueMap = (Map) JSCacheManager.get(DefaultCache.class, cacheKey);
            if (valueMap == null || valueMap.isEmpty()) {
                StringMap tempMap = new StringMap();
                tempMap.setKeySplit(StringUtil.EQUAL);
                tempMap.setLineSplit(StringUtil.CRLF);
                if (loadFile != null) {
                    tempMap.loadFile(loadFile.getAbsolutePath());
                }
                valueMap = tempMap;
                JSCacheManager.put(DefaultCache.class, cacheKey, valueMap);
            }
        } else {
            valueMap = EnvFactory.getEnvironmentTemplate().getVariableMap();
        }
        //装置valueMap

        //匹配方式
        PropPrefix propPrefix = superclass.getAnnotation(PropPrefix.class);
        if (propertySource != null) {
            Field[] fields = ClassUtil.getDeclaredFields(superclass);
            if (fields != null && propPrefix != null) {
                for (Field field : fields) {
                    if (Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())
                            || Modifier.isInterface(field.getModifiers())) {
                        continue;
                    }
                    Value value = field.getAnnotation(Value.class);
                    String key = null;
                    if (value != null) {
                        key = propPrefix.prefix() + StringUtil.DOT + value.value();
                    } else {
                        key = propPrefix.prefix() + StringUtil.DOT + field.getName();
                    }
                    String valueStr = (String) valueMap.get(key);
                    if (StringUtil.isEmpty(valueStr)) {
                        continue;
                    }
                    String valueObj = EnvFactory.getPlaceholder().processTemplate(valueMap, valueStr);
                    BeanUtil.setFieldValue(o, field.getName(), valueObj);
                }
            }
            String[] fieldNames = null;
            if (fields != null) {
                for (Field field : fields) {
                    Value val = field.getAnnotation(Value.class);
                    if (val != null) {
                        String valueStr = StringUtil.empty;
                        if (val.value().startsWith("$")) {
                            String[] varNames = StringUtil.getFreeMarkerVar(val.value());
                            valueStr = (String) valueMap.get(varNames[0]);
                        } else {
                            valueStr = (String) valueMap.get(val.value());
                        }
                        String valueObj = EnvFactory.getPlaceholder().processTemplate(valueMap, valueStr);
                        fieldNames = ArrayUtil.add(fieldNames, field.getName());
                        BeanUtil.setFieldValue(o, field.getName(), valueObj);
                    }
                }
            }
            //装置值
            Method[] methods = ClassUtil.getDeclaredSetMethods(superclass);
            if (methods != null) {
                for (Method method : methods) {
                    if (ArrayUtil.contains(fieldNames, ClassUtil.getMethodFiledName(method.getName()))) {
                        //已经设置过,不重复
                        continue;
                    }
                    Value val = method.getAnnotation(Value.class);
                    if (val != null) {
                        String valueStr = StringUtil.empty;
                        if (val.value().startsWith("$")) {
                            String[] varNames = StringUtil.getFreeMarkerVar(val.value());
                            valueStr = (String) valueMap.get(varNames[0]);
                        } else {
                            valueStr = (String) valueMap.get(val.value());
                        }
                        String valueObj = EnvFactory.getPlaceholder().processTemplate(valueMap, valueStr);
                        if (valueObj == null) {
                            continue;
                        }
                        BeanUtil.setSimpleProperty(o, method.getName(), valueObj);
                    }
                }
            }
        }
    }

    @Override
    public <T> T getBean(Class<T> classes) {
        return (T) getBean(classes.getName(), Sioc.global);
    }

    @Override
    public <T> T getBean(Class<T> classes, final String namespace) {
        return (T) getBean(classes.getName(), namespace);
    }

    /**
     * @param beanName bean id
     * @return 得到bean对象
     */
    @Override
    public Object getBean(String beanName) {
        String namespace = null;
        if (beanName.contains(StringUtil.AT)) {
            namespace = StringUtil.substringAfter(beanName, StringUtil.AT);
            beanName = StringUtil.substringBefore(beanName, StringUtil.AT);
        }
        if (StringUtil.isNull(namespace)) {
            namespace = Sioc.global;
        }
        try {
            return getBean(beanName, namespace);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param beanName  bean id
     * @param namespace 命名空间
     * @return 得到bean对象
     */
    @Override
    public Object getBean(String beanName, final String namespace) {
        if (!StringUtil.hasLength(beanName)) {
            return null;
        }
        String nameK = StringUtil.isNull(namespace) ? Sioc.global : namespace;
        if (beanName.contains(StringUtil.AT)) {
            beanName = StringUtil.substringBefore(beanName, StringUtil.AT);
            nameK = StringUtil.substringAfterLast(beanName, StringUtil.AT);
        }
        LifecycleObject lifecycleObject = null;
        try {
            lifecycleObject = getLifecycleObject(beanName, namespace);
            if (lifecycleObject != null && lifecycleObject.isSingleton()) {
                return lifecycleObject.getObject();
            }
        } catch (Exception e) {
            log.error("create object error " + beanName + StringUtil.AT + namespace, e);
        }

        try {
            //得到的所在命名空间 begin
            BeanElement beanElement = iocContext.getBeanElement(beanName, nameK);
            if (beanElement == null) {
                throw new Exception("不能发现配置:sioc no find bean BeanElement name:" + beanName + " namespace:" + namespace + " 或 " + nameK);
            }
            //得到的所在命名空间 end

            //得到所在空间对象 不使用synchronized多线程,单列在高压下不成功
            //判断是否有继承关系的单例对象
            lifecycleObject = createLifecycleObject(beanElement, beanElement.getNamespace());
            if (!beanElement.getNamespace().equalsIgnoreCase(namespace) && Sioc.global.equalsIgnoreCase(beanElement.getNamespace())) {
                lifecycleObject.setRefNamespace(nameK);
            }

            LIFE_CYCLE.put(beanName, lifecycleObject);
            //如果没有就创建
        } catch (Exception e) {
            log.error("bean name:" + beanName + "  namespace:" + namespace, e);
            e.printStackTrace();
        }
        if (lifecycleObject == null) {
            log.warn("not find bean name:" + beanName + "  namespace:" + namespace);
            return null;
        }
        if (lifecycleObject.isSingleton()) {
            return lifecycleObject.getObject();
        } else {
            try {
                return createEntry(lifecycleObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param beanName  bean id
     * @param namespace 命名空间
     * @return 按照继承关系，找到最底层的配置对象
     */
    @Override
    public LifecycleObject getLifecycleObject(final String beanName, final String namespace) throws Exception {
        String nameK = namespace;
        LifecycleObject lifecycleObject;

        Map<String, String> extendMap = iocContext.getExtendMap();
        while (!StringUtil.isNull(nameK) && !Sioc.global.equalsIgnoreCase(nameK)) {
            lifecycleObject = LIFE_CYCLE.get(beanName, nameK);
            if (lifecycleObject != null) {
                return lifecycleObject;
            }
            //判断是否有继承关系的单例对象 注意sync
            BeanElement beanElement = iocContext.getBeanElement(beanName, nameK);
            if (beanElement != null && beanElement.getNamespace().equalsIgnoreCase(nameK)) {
                lifecycleObject = createLifecycleObject(beanElement, nameK);
                LIFE_CYCLE.put(beanName, lifecycleObject);
                return lifecycleObject;
            }
            //如果没有就创建

            if (extendMap.containsKey(nameK)) {
                nameK = extendMap.get(nameK);
            } else {
                if (nameK.contains("/")) {
                    nameK = StringUtil.substringBeforeLast(nameK, "/");
                } else {
                    nameK = Sioc.global;
                }
            }
        }


        synchronized (LIFE_CYCLE)
        {
            // global 处理 begin
            lifecycleObject = LIFE_CYCLE.get(beanName, Sioc.global);
            if (lifecycleObject != null) {
                return lifecycleObject;
            }
            BeanElement beanElement = iocContext.getBeanElement(beanName, Sioc.global);
            if (beanElement != null && beanElement.getNamespace().equalsIgnoreCase(nameK)) {
                lifecycleObject = createLifecycleObject(beanElement, beanElement.getNamespace());
                LIFE_CYCLE.put(beanName, lifecycleObject);
                return lifecycleObject;
            }
        }
        return null;
    }


    @Override
    public boolean registerBean(BeanModel beanElement) {
        if (beanElement == null) {
            return false;
        }
        iocContext.registerBean(beanElement);
        return true;
    }

    /**
     * @param beanName bean id
     * @return 判断是否存在对象
     */
    @Override
    public boolean containsBean(String beanName) {
        return containsBean(beanName, null);
    }

    /**
     * @param beanName  bean id
     * @param namespace 命名空间
     * @return 判断是否存在对象
     */
    @Override
    public boolean containsBean(String beanName, final String namespace) {
        try {
            return iocContext.containsBean(beanName, namespace);
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * @param beanName  bean id
     * @param namespace 命名空间
     * @return 判断是否为单列
     */
    @Override
    public boolean isSingleton(String beanName, final String namespace) {
        try {
            BeanElement beanElement = iocContext.getBeanElement(beanName, namespace);
            return beanElement != null && beanElement.isSingleton();
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage());
        }
        return false;
    }


    /*
     * 得到sioc 中配置的所有应用
     * @return 目的为了方便判断当前开启应用的应用
     */
    @Override
    public Map<String, String> getApplicationMap() {
        return iocContext.getApplicationMap();
    }


    /**
     * 初始化定时任务
     */
    public void initScheduler() {

        //全局开关
        if (!ObjectUtil.toBoolean(EnvFactory.getEnvironmentTemplate().getString(Environment.USE_SCHEDULE, "true"))) {
            return;
        }

        SchedulerManager schedulerManager = SchedulerTaskManager.getInstance();
        //扫描得到的 begin
        Map<String, String> map = iocContext.getSchedulerMap();
        for (String name : map.keySet()) {
            Object o = getBean(name, map.get(name));
            if (o != null) {
                log.info("init Scheduler " + o.getClass());
                schedulerManager.add(o);
            }
        }
        map.clear();
        //扫描得到的 end

        //扫描注册的bean里边是否存在 begin
        List<BeanElement> elementList = iocContext.getElementList();
        for (BeanElement beanElement : elementList) {
            try {
                Class<?> cls = ClassUtil.loadClass(beanElement.getClassName());
                if (AnnotationUtil.hasScheduled(cls)) {
                    Object o = getBean(beanElement.getId(), beanElement.getNamespace());
                    schedulerManager.add(o);
                }
            } catch (ClassNotFoundException e) {
                log.error("init Scheduler " + beanElement.getSource(), e);
                e.printStackTrace();
            }
        }
        //烧苗注册的bean里边是否存在 end
    }


    /**
     * 关闭卸载
     */
    @Override
    public void shutdown() {
        LIFE_CYCLE.shutdown();
    }
}