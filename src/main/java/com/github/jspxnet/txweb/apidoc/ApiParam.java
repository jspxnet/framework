package com.github.jspxnet.txweb.apidoc;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

@Data
public class ApiParam implements Serializable {
    @Column(caption = "变量名")
    private String name;

    @Column(caption = "字段名")
    @JsonIgnore(isNull = true)
    private String filed;

    @Column(caption = "字段类型")
    @JsonIgnore(isNull = true)
    private String filedType;

    @JsonIgnore
    private Type filedClass;

    @Column(caption = "是否必须")
    private boolean required = false;

    @Column(caption = "描述名称")
    private String caption;

    @Column(caption = "格式")
    @JsonIgnore(isNull = true)
    private String format;

    @Column(caption = "安全")
    @JsonIgnore(isNull = true)
    private String safety;

    @Column(caption = "包含字段")
    private List<ApiParam> children = new LinkedList<>();

    @JsonIgnore(isNull = true)
    private JSONObject childJson;

    @Column(caption = "类参数")
    private boolean classParam = false;


}
