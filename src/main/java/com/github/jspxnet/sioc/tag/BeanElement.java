/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.tag;

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.AnnotationUtil;

import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 1:10:34
 */
@Slf4j
public class BeanElement extends IocTagNode {

    public final static String TAG_NAME = "bean";
    private final static String KEY_CREATE = "create";
    private final static String KEY_SINGLETON = "singleton";
    //自动注入,当创建一个对象的时候,判断是否有这个方法,有就自动的注入进去
    //主要用于数据源,配置等注入
    /*
        <!--Sober  begin  injection 配置后,其他的bean会自动注入这个对象,数据库配置后,后边就可以自动载入数据库配置了 -->
       <bean id="jspxSoberFactory" class="com.github.jspxnet.sober.config.SoberMappingBean" singleton="true" inject="soberFactory">
     */
    private final static String KEY_INJECT = "inject";

    public BeanElement() {

    }

    @Override
    public String getId() {
        String id = super.getId();
        if (!StringUtil.isEmpty(id)) {
            return id;
        }
        String className = getClassName();
        if (StringUtil.isNull(className))
        {
            log.error("bean 配置错误 className:{}",className);
            return null;
        }
        log.debug("bean load className:{}",className);
        Class<?> cls;
        try {
            cls = ClassUtil.loadClass(getClassName());
        } catch (Exception e) {
            log.error("bean 配置错误 className:{},source:{},error:{}",className,getSource(),e.getMessage());
            e.printStackTrace();
            return null;
        }
        return AnnotationUtil.getBeanId(cls);
    }


    /**
     * 真实所在的命名空间,在扫描后才载入,因为存在继承关系，所以要在这里标明，不然会出现两个单引用对象
     * 如果没有配置ID，默认就使用接口路径
     */
    private String namespace = Sioc.global;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public String getCreate() {
        return XMLUtil.deleteQuote(getStringAttribute(KEY_CREATE));
    }

    public boolean isSingleton() {
        String singleton = XMLUtil.deleteQuote(getStringAttribute(KEY_SINGLETON));
        return StringUtil.isNull(singleton) || StringUtil.toBoolean(singleton);
    }

    public String getClassName() {
        return XMLUtil.deleteQuote(StringUtil.trim(getStringAttribute(Sioc.IocClass)));
    }

    public String getInjection() {
        return XMLUtil.deleteQuote(StringUtil.trim(getStringAttribute(KEY_INJECT)));
    }

    public List<PropertyElement> getPropertyElements() throws Exception {
        ReadProperty readProperty = new ReadProperty();
        if (XMLUtil.parseXmlString(readProperty, getSource())) {
            return readProperty.getPropertyElements();
        }
        return new ArrayList<PropertyElement>();
    }

    public List<TagNode> getMapElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(MapElement.TAG_NAME, MapElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

    public List<TagNode> getListElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(ListElement.TAG_NAME, ListElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

    public List<TagNode> getArrayElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(ArrayElement.TAG_NAME, ArrayElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }
}