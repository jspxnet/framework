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

import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sioc.util.TypeUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 22:17:10
 * 字段属性
 */
@Data
@Table(name = "jspx_sober_column",caption = "字段关系",cache = false)
public class SoberColumn implements Serializable {

    //同时也是关联关系
    @Column(caption = "表名称",length = 100)
    private String tableName;

    @Column(caption = "字段名称",length = 100)
    private String name = StringUtil.empty;

    //类型
    @JsonIgnore
    //Column(caption = "类对象")
    private Class<?> classType;


    @Column(caption = "是否空")
    private boolean notNull = false;

    @Column(caption = "默认值")
    private String defaultValue = StringUtil.empty;

    @Column(caption = "描述")
    private String caption = StringUtil.empty;

    @Column(caption = "选项")
    private String option = StringUtil.empty;

    @Column(caption = "验证")
    private String dataType = StringUtil.empty;

    @Column(caption = "输入框")
    private String input = "text";

    @Column(caption = "长度")
    private int length = 0;

    //true 的时候导出屏蔽
    @Column(caption = "隐藏")
    private boolean hidden = false;

    @Column(caption = "java类型")
    private String javaType = StringUtil.empty;

    @JsonField
    public String getTypeString() {
        if (classType!=null)
        {
            return TypeUtil.getTypeString(classType);
        }
        return  javaType;
    }


    @JsonField
    public String getBeanField() {
        StringBuilder sb = new StringBuilder();
        sb.append("@Column(caption = \"").append(caption).append("\", length = ").append(length).append(",notNull=").append(notNull).append(")").append("\r\n");
        //StringUtil.empty
        String typeString = getTypeString();
        if (ClassUtil.isNumberType(typeString))
        {
            sb.append("private ").append(getTypeString()).append(" ").append(name).append(" = 0;");
        } else
        if (typeString.equals(Date.class.getName()) || typeString.equals(Date.class.getSimpleName()) )
        {
            sb.append("private ").append(getTypeString()).append(" ").append(name).append(" = new Date();");
        }
        else
        {
            sb.append("private ").append(getTypeString()).append(" ").append(name).append(" = StringUtil.empty;");
        }
        return sb.toString();
    }
}