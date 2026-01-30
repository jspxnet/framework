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
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
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

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    //同时也是关联关系
    @Column(caption = "表名称",length = 100)
    private String tableName  = StringUtil.empty;

    @Column(caption = "描述", length = 200)
    private String caption = StringUtil.empty;

    @Column(caption = "映射关系", length = 100)
    private String mapping = StringUtil.empty;

    @Column(caption = "自己字段", length = 100)
    private String field = StringUtil.empty;

    @Column(caption = "变量名", length = 100)
    private String name = StringUtil.empty;

    //是数据库字段，不是实体名称
    @Column(caption = "对应字段", length = 100)
    private String targetField = StringUtil.empty;
    //触发实体

    @JsonIgnore
    @Column(caption = "触发对象类型", length = 200)
    private Class<?> targetEntity;

    @Column(caption = "实体对象", length = 200)
    private String entityClass;

    @Column(caption = "条件", length = 200)
    private String term = StringUtil.empty;

    @Column(caption = "排序", length = 200)
    private String orderBy = StringUtil.empty;

    @Column(caption = "关联删除")
    private boolean isDelete;

    @Column(caption = "关联保存",notNull = true)
    private boolean isSave = true;

    @Column(caption = "关联更新")
    private boolean isUpdate;

    @Column(caption = "关联更新")
    private boolean chain;

    @Column(field="filter", caption = "查询条件", length = 200)
    private String where = StringUtil.empty;

    //是脚本配置 变量方式的行数
    @Column(field = "lines", caption = "行数", length = 220)
    private String length = StringUtil.empty;

    public void setTargetEntity(Class<?> targetEntity) {
        if (targetEntity==null)
        {
            return;
        }
        this.targetEntity = targetEntity;
        this.entityClass = targetEntity.getName();
    }


}