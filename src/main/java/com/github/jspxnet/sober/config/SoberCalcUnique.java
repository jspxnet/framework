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

import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2025-12-27
 * Time: 21:41:04
 * 分两种情况
 * 1.是直接sql查询一个值放入进来
 * 2.代理其他的表
 */
@Data
@Table(name = "jspx_sober_calc",caption = "计算关联关系")
public class SoberCalcUnique implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "表名称",length = 100)
    private String tableName;

    @Column(caption = "字段名称", length = 100)
    private String field;

    @Column(caption = "字段名称", length = 100)
    private String caption;

    //自己的变量
    @Column(caption = "SQL", length = 1000)
    private String sql;

    //如果上边的sql是完整的下边就可以不要了
    @Column(caption = "实体对象数组", length = 1000)
    private String[] entity;

    @Column(caption = "返回数据类型", length = 200)
    private String type = TypeUtil.TYPE_STRING;

    //父对象字段 中的值作为参数 放入sql中查询
    @Column(caption = "父对象字段", length = 200)
    private String[] params = null;

}