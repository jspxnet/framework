package com.github.jspxnet.sioc.tag;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BeanModel extends BeanElement {

    public BeanModel() {

    }

    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //真实所在的命名空间,在扫描后才载入,因为存在继承关系，所以要在这里标明，不然会出现两个单引用对象
    private String namespace = Sioc.global;

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private String create = StringUtil.empty;

    @Override
    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    private boolean singleton = false;

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    private String className = StringUtil.empty;

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private String injection = StringUtil.empty;

    @Override
    public String getInjection() {
        return injection;
    }

    public void setInjection(String injection) {
        this.injection = injection;
    }

    @Override
    public List<PropertyElement> getPropertyElements() {
        return new ArrayList<PropertyElement>();
    }

    @Override
    public List<TagNode> getMapElements() {
        return new ArrayList<TagNode>();
    }

    @Override
    public List<TagNode> getListElements() {
        return new ArrayList<TagNode>();
    }

    @Override
    public List<TagNode> getArrayElements() {
        return new ArrayList<TagNode>();
    }

    @Override
    public String getSource() {
        StringBuilder sb = new StringBuilder();
        sb.append("<bean id=\"" + getId() + "\" class=\"" + className + "\" singleton=\"" + singleton + "\" create=\"" + create + "\" />");
        return sb.toString();
    }
}
