package com.github.jspxnet.txweb.apidoc;


import com.github.jspxnet.json.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiField implements Serializable {
    //字段名称
    private String name;
    //字段类型
    private String type;
    //显示名称
    private String caption;
    //子对象
    @JsonIgnore(isNull = true)
    private List<ApiField> child;
}

