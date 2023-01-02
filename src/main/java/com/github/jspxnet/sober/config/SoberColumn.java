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
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sioc.util.TypeUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 22:17:10
 * 字段属性
 */
@Data
@Table(name = "jspx_sober_column",caption = "字段关系")
public class SoberColumn implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "数据库名",length = 100)
    private String databaseName;

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

    @Column(caption = "默认值",length = 1000)
    private String defaultValue = StringUtil.empty;

    @Column(caption = "描述",length = 200)
    private String caption = StringUtil.empty;

    @Column(caption = "选项",length = 1000)
    private String option = StringUtil.empty;

    @Column(caption = "验证",length = 1000)
    private String dataType = StringUtil.empty;

    //和WebComponent 名称对应
    @Column(caption = "输入框",length = 100)
    private String input = "text";

    //添加的时候使用
    @JsonIgnore
    @Column(caption = "前一个字段",length = 100)
    private String oldColumn = "";

    @Column(caption = "长度")
    private int length = 0;

    //true 的时候导出屏蔽
    @Column(caption = "隐藏")
    private boolean hidden = false;

    @Column(caption = "自动ID")
    private boolean autoincrement = false;


    @JsonField
    public String getTypeString() {
        if (classType!=null)
        {
            return TypeUtil.getTypeString(classType);
        }
        return  "String";
    }

    public List<Object> getOptionList()
    {
        return TypeUtil.getOptionList(option);
    }


}