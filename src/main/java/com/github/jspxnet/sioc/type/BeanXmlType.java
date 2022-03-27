/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.type;

import com.github.jspxnet.sioc.util.AnnotationUtil;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sioc.tag.*;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.core.TagNode;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-1
 * Time: 3:22:11
 * [bean id="classId" class="com.jspx.XXXXX" singleton="false" /]
 */
public class BeanXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return Object.class;
    }

    @Override
    public String getTypeString() {
        return "bean";
    }

    private String namespace = "global";

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private BeanFactory beanFactory = null;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getTypeObject() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("bean", BeanElement.class.getName());
        BeanElement beanElement = (BeanElement) xmlEngine.createTagNode((String) value);
        String className = beanElement.getClassName();
        if (!StringUtil.hasLength(className)) {
            return null;
        }
        Object result = ClassUtil.newInstance(className);
        Map<String, Object> paramMap = TypeUtil.getPropertyValue(beanElement.getPropertyElements(), namespace);
        //////////////list begin
        List<TagNode> listElements = beanElement.getListElements();
        for (TagNode element : listElements) {
            ListElement li = (ListElement) element;
            paramMap.put(li.getId(), TypeUtil.getListValue(li, namespace, beanFactory));
        }
        listElements.clear();
        //////////////list end

        //////////////map begin
        List<TagNode> mapElements = beanElement.getMapElements();
        for (TagNode element : mapElements) {
            MapElement amap = (MapElement) element;
            paramMap.put(amap.getId(), TypeUtil.getMapValue(amap, namespace, beanFactory));
        }
        mapElements.clear();
        //////////////map end

        //////////////array begin
        List<TagNode> arrayElements = beanElement.getArrayElements();
        for (TagNode element : arrayElements) {
            ArrayElement aArray = (ArrayElement) element;
            paramMap.put(aArray.getId(), TypeUtil.getArrayValue(aArray, namespace, beanFactory));
        }
        arrayElements.clear();
        //////////////array end

        //////////////设置参数 begin
        String[] setRefField = null;
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
                    if (beanFactory != null) {
                        pValue = beanFactory.getBean(beanName, namespace);
                    }
                }
            }
            BeanUtil.setSimpleProperty(result, name, pValue);
            setRefField = ArrayUtil.add(setRefField, name);
        }

        if (!StringUtil.isNull(beanElement.getCreate())) {
            result = BeanUtil.getProperty(result, beanElement.getCreate());
        }

        beanFactory.setRef(ClassUtil.loadClass(className),result, namespace);

        AnnotationUtil.invokeInit(result);
        return result;
    }

    @Override
    public String getXmlString() throws Exception {
        if (value == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<bean name=\"").append(name).append("\" class=\"").append(value.getClass().getName()).append("\">\r\n");
        Method[] methods = ClassUtil.getDeclaredReturnMethods(value.getClass(), 0);
        for (Method method : methods) {
            String methodName = ClassUtil.getCallMethodName(method);
            Object object = BeanUtil.getProperty(value, methodName);
            sb.append(TypeUtil.getTypeSerializer(methodName, object));
        }
        sb.append("</bean>");
        return sb.toString();
    }
}