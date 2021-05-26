/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.config;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.txweb.annotation.Param;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-6
 * Time: 9:20:43
 * 映射关系 manytoone onetomany  onttoone
 */
public class SoberNexus implements Serializable {
    public SoberNexus() {

    }

    //映射关系
    private String mapping;
    //字段名称
    private String field;
    //触发字段
    private String targetField;
    //触发实体
    @JsonIgnore
    private Class<?> targetEntity;
    //条件
    private String term;
    //排序
    private String orderBy;
    //关联删除
    private boolean delete;
    //关联保存
    private boolean save;
    //关联更新
    private boolean update;
    //关联更新
    private boolean chain;
    //关联更新
    private String where;
    //数据个数
    private String length;

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }


    public Class<?> getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Class<?> targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getTerm() {
        return term;
    }

    @Param(caption = "条件", max = 50)
    public void setTerm(String term) {
        this.term = term;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isChain() {
        return chain;
    }

    public void setChain(boolean chain) {
        this.chain = chain;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}