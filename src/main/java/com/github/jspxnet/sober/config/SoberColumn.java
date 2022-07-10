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

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ReflectUtil;
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

    @Column(caption = "默认值",length = 1000)
    private String defaultValue = StringUtil.empty;

    @Column(caption = "描述",length = 200)
    private String caption = StringUtil.empty;

    @Column(caption = "选项",length = 1000)
    private String option = StringUtil.empty;

    @Column(caption = "验证",length = 1000)
    private String dataType = StringUtil.empty;

    @Column(caption = "输入框",length = 100)
    private String input = "text";

    @Column(caption = "长度")
    private int length = 0;

    //true 的时候导出屏蔽
    @Column(caption = "隐藏")
    private boolean hidden = false;

    @Column(caption = "java类型",length = 100)
    private String javaType = StringUtil.empty;

    @JsonField
    public String getTypeString() {
        if (classType!=null)
        {
            return TypeUtil.getTypeString(classType);
        }
        return  javaType;
    }

    private static String[] baseType = {"int", "integer", "BigInteger", "long", "bool", "boolean", "float",  "BigDecimal", "date", "double", "string", "ref", "map"};

    private static Map<String,String> codeTypeMap = new HashMap<>();
    static{
        codeTypeMap.put("int","int");
        codeTypeMap.put("integer","integer");
        codeTypeMap.put("BigInteger","BigInteger");
        codeTypeMap.put("long","long");
        codeTypeMap.put("bool","boolean");
        codeTypeMap.put("float","float");
        codeTypeMap.put("BigDecimal","BigDecimal");
        codeTypeMap.put("date","Date");
        codeTypeMap.put("double","double");
        codeTypeMap.put("string","String");
        codeTypeMap.put("map","Map");

    }

    @JsonField
    public String getBeanField() {

        StringBuilder sb = new StringBuilder();
        sb.append("@Column(caption = \"").append(caption).append("\", length = ").append(length).append(",notNull=").append(notNull).append(")").append("\r\n");
        //StringUtil.empty
        String typeStr = getTypeString();

        String typeString = codeTypeMap.get(typeStr);
        if (StringUtil.isNull(typeString))
        {
            typeString = typeStr;
        }
        if (ClassUtil.isNumberType(typeString))
        {
            sb.append("private ").append(typeString).append(" ").append(name).append(" = 0;");
        } else
        if (typeString.equals(Date.class.getName()) || typeString.equals(Date.class.getSimpleName()) )
        {
            sb.append("private ").append(typeString).append(" ").append(name).append(" = new Date();");
        }
        else
        {
            sb.append("private ").append(typeString).append(" ").append(name).append(" = StringUtil.empty;");
        }
        return sb.toString();
    }


    public List<Object> getOptionList()
    {
        if (StringUtil.isNull(option))
        {
            return new ArrayList<>(0);
        }
        List<Object> result = new ArrayList<>();
        if (StringUtil.isJsonArray(option))
        {
            JSONArray array = new JSONArray(option);
            for (int i=0;i<array.length();i++)
            {
                Object obj = array.get(i);
                if (obj instanceof Map)
                {
                    result.add(ReflectUtil.createDynamicBean((Map)obj));
                } else
                {
                    result.add(obj);
                }
            }
        } else
        {
            StringMap<String,String> stringMap = new StringMap<>();
            stringMap.setKeySplit(StringUtil.COLON);
            stringMap.setLineSplit(StringUtil.SEMICOLON);
            stringMap.setString(option);
            for (String key:stringMap.keySet())
            {
                JSONObject json = new JSONObject();
                json.put("value",key);
                json.put("name",stringMap.getString(key));
                result.add(ReflectUtil.createDynamicBean(json));
            }
        }
        return result;
    }
}