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
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-6
 * Time: 9:20:43
 * 映射关系 manytoone onetomany  onttoone
 */

@Data
@Table(name = "jspx_sober_nexus",caption = "关联关系",cache = false)
public class SoberNexus implements Serializable {
    public SoberNexus() {

    }
    //同时也是关联关系
    @Column(caption = "表名称",length = 100)
    private String tableName;

    @Column(caption = "映射关系", length = 100)
    private String mapping;

    @Column(caption = "字段名称", length = 100)
    private String field;

    @Column(caption = "触发字段", length = 100)
    private String targetField;
    //触发实体

    @JsonIgnore
    private Class<?> targetEntity;

    @Column(caption = "实体对象", length = 100)
    private String entityClass;

    @Column(caption = "条件", length = 100)
    private String term;

    @Column(caption = "排序", length = 100)
    private String orderBy;

    @Column(caption = "关联删除")
    private boolean delete;

    @Column(caption = "关联保存")
    private boolean save;

    @Column(caption = "关联更新")
    private boolean update;

    @Column(caption = "关联更新")
    private boolean chain;

    @Column(caption = "查询条件")
    private String where;

    @Column(caption = "长度")
    private String length;


    @Param(caption = "条件", max = 200)
    public void setTerm(String term) {
        this.term = term;
    }

    public void setTargetEntity(Class<?> targetEntity) {
        if (targetEntity==null)
        {
            return;
        }
        this.targetEntity = targetEntity;
        this.entityClass = targetEntity.getName();
    }
}