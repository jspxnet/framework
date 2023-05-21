package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class SoberColumnDto implements Serializable {

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

    @JsonField
    public String getTypeString() {
        if (classType!=null)
        {
            return TypeUtil.getTypeString(classType);
        }
        return  "String";
    }


    @JsonField
    public String getBeanField() {

        StringBuilder sb = new StringBuilder();
        sb.append("@Column(caption = \"").append(caption).append("\", length = ").append(length).append(",notNull=").append(notNull).append(")").append("\r\n");
        //StringUtil.empty
        String typeStr = getTypeString();

        String typeString = TypeUtil.CODE_TYPE_MAP.get(typeStr);
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

}