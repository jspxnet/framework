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
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sioc.util.TypeUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 22:17:10
 * 字段属性
 */
@Data
public class SoberColumn implements Serializable {
    @Column(caption = "字段名称")
    private String name = StringUtil.empty;
    //类型
    @JsonIgnore
    @Column(caption = "类对象")
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
    @Column(caption = "屏蔽")
    private boolean hidden = false;

    @JsonField
    public String getType() {
        return TypeUtil.getTypeString(classType);
    }


    @JsonField
    public String getJavaType() {
        return classType.getName();
    }


    @JsonField
    public String getBeanField() {
        StringBuilder sb = new StringBuilder();
        sb.append("@Column(caption = \"").append(caption).append("\", length = ").append(length).append(",notNull = ").append(notNull).append(")").append("\r\n");
        //StringUtil.empty
        if (ClassUtil.isNumberType(classType))
        {
            sb.append("private ").append(classType.getSimpleName()).append(" ").append(name).append(" = 0;");
        } else
        {
            sb.append("private ").append(classType.getSimpleName()).append(" ").append(name).append(" = StringUtil.empty;");
        }
        return sb.toString();
    }



}