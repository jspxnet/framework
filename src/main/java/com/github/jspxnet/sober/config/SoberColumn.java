/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.8+  x86/amd64
 *
 */
package com.github.jspxnet.sober.config;

import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.enums.MappingEnumType;
import com.github.jspxnet.sober.table.SoberFieldEnum;
import com.github.jspxnet.sober.table.meta.FormControl;
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
@Table(name = "jspx_sober_column",caption = "字段关系",idx = "jspx_sober_column_idx(databaseName,name,field,version)")
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
    private String field = StringUtil.empty;


    @Column(caption = "实体名称",length = 100)
    private String name = StringUtil.empty;

    //类型
    @JsonIgnore
    @Column(caption = "类对象",length = 100)
    private Class<?> classType = String.class;

    @Column(caption = "是否空",length = 10)
    private boolean noNull = false;

    @Column(caption = "配置默认值",length = 1000)
    private String defaultValue = StringUtil.empty;

    @Column(caption = "字段初始值",length = 200)
    private Object initValue = null;

    @Column(caption = "描述",length = 200)
    private String caption = StringUtil.empty;

    @Column(caption = "枚举类型",length = 200)
    private String enumType = StringUtil.empty;

    //option 是oracle 关键字, 以后少用，尽量使用 SoberFieldEnum
    @Column(field = "opt", caption = "选项",length = 200)
    private String option = StringUtil.empty;

    //以前是验证类型，现在取消改为数据库的数据类型
    @Column(caption = "数据类型",length = 1000)
    private String dataType = StringUtil.empty;

    //和WebComponent 名称对应
    @Column(caption = "输入框",length = 100)
    private String input = "text";

    //添加的时候使用
    @JsonIgnore
    @Column(caption = "前一个字段",length = 100)
    private String oldColumn = StringUtil.empty;

    @Column(field = "fieldLength", caption = "长度")
    private int length = 0;

    //true 的时候导出屏蔽
    @Column(caption = "隐藏")
    private boolean hidden = false;

    @Column(caption = "不允许搜索")
    private boolean searchHidden = false;

    @Column(caption = "配置的枚举", length = 250)
    private SoberFieldEnum fieldEnum = null;

    @Column(caption = "自动ID")
    private boolean autoincrement = false;


    @Column(caption = "是否显示枚举")
    private boolean showEnum = false;

    @Nexus(mapping = MappingEnumType.OneToOne, field = "field", targetField = "field",term = "tableName:eq[${tableName}];version:eq[${version}]",  targetEntity = SoberNexus.class, chain = true,save = true,update = true, delete = true)
    private SoberNexus nexus = null;

    @Nexus(mapping = MappingEnumType.OneToOne, field = "field", targetField = "field",term = "tableName:eq[${tableName}];version:eq[${version}]", targetEntity = SoberCalcUnique.class, chain = true,save = true,update = true, delete = true)
    private SoberCalcUnique calcUnique = null;


/*
    @Nexus(mapping = MappingEnumType.OneToOne, field = "field", targetField = "field",targetEntity = FormControl.class, chain = true,save = true,update = true, delete = true)
    private FormControl control = null;

*/

    @Column(caption = "排序")
    private int sortType = 0;

    @Column(caption = "版本号")
    private int version = 0;

    @JsonField
    public String getTypeString() {
        if (classType!=null)
        {
            return TypeUtil.getTypeString(classType);
        }
        return  "String";
    }

    public String getField() {
        if (StringUtil.isEmpty(field))
        {
            return name;
        }
        return field;
    }

    @JsonField
    public String getBeanField(boolean camel) {

        StringBuilder sb = new StringBuilder();
        String typeStr = getTypeString();
        if ("string".equalsIgnoreCase(typeStr))
        {
            if (length==0)
            {
                if (noNull)
                {
                    if (!StringUtil.isNull(option))
                    {
                        sb.append("@Column(caption = \"").append(caption).append("\",").append("noNull=").append(noNull).append(",option=\"").append(option).append("\"").append(",enumTypes=false").append(")").append("\r\n");
                    } else {
                        sb.append("@Column(caption = \"").append(caption).append("\",").append("noNull=").append(noNull).append(")").append("\r\n");
                    }
                } else
                {
                    if (!StringUtil.isNull(option))
                    {
                        sb.append("@Column(caption = \"").append(caption).append("\"").append(",option=\"").append(option).append("\"").append(")").append("\r\n");
                    } else {
                        sb.append("@Column(caption = \"").append(caption).append("\"").append(")").append("\r\n");
                    }
                }
            }
            else
            {
                if (noNull)
                {
                    if (!StringUtil.isNull(option))
                    {
                        sb.append("@Column(caption = \"").append(caption).append("\", length=").append(length).append(",noNull=").append(noNull).append(",option=\"").append(option).append("\"").append(")").append("\r\n");
                    } else {
                        sb.append("@Column(caption = \"").append(caption).append("\", length=").append(length).append(",noNull=").append(noNull).append(")").append("\r\n");
                    }

                } else {
                    sb.append("@Column(caption = \"").append(caption).append("\", length=").append(length).append(")").append("\r\n");
                }
            }

        } else {
            if (noNull)
            {
                if (!StringUtil.isNull(option))
                {
                    sb.append("@Column(caption = \"").append(caption).append("\",").append("noNull=").append(noNull).append(",option=\"").append(option).append("\"").append(",enumTypes=false").append(")").append("\r\n");
                } else {
                    sb.append("@Column(caption = \"").append(caption).append("\",").append("noNull=").append(noNull).append(")").append("\r\n");
                }
            } else {
                if (!StringUtil.isNull(option))
                {
                    sb.append("@Column(caption = \"").append(caption).append("\"").append(",option=\"").append(option).append("\"").append(")").append("\r\n");
                } else {
                    sb.append("@Column(caption = \"").append(caption).append("\"").append(")").append("\r\n");
                }

            }
        }


        String fieldName = camel?StringUtil.underlineToCamel(name):name;
        String typeString = TypeUtil.CODE_TYPE_MAP.get(typeStr);
        if (StringUtil.isNull(typeString))
        {
            typeString = typeStr;
        }
        if (camel&&StringUtil.isChinese(fieldName))
        {
            fieldName = StringUtil.uncapitalize(ChineseUtil.fullSpell(fieldName,StringUtil.empty));
        }
        if (ClassUtil.isNumberType(typeString)&&!"BigDecimal".equalsIgnoreCase(typeString))
        {
            sb.append("private ").append(typeString).append(" ").append(fieldName).append(" = 0;");
        } else
        if ("BigDecimal".equalsIgnoreCase(typeString))
        {
            typeString = "double";
            sb.append("private ").append(typeString).append(" ").append(fieldName).append(" = 0;");
        } else
        if (typeString.equals(Date.class.getName()) || typeString.equals(Date.class.getSimpleName()) )
        {
            if (noNull)
            {
                sb.append("private ").append(typeString).append(" ").append(fieldName).append(" = new Date();");
            } else {
                sb.append("private ").append(typeString).append(" ").append(fieldName).append(" = null;");
            }
        }
        else
        {
            typeString = "String";
            sb.append("private ").append(typeString).append(" ").append(fieldName).append(" = StringUtil.empty;");
        }
        return sb.toString();
    }

}