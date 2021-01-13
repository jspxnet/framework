package com.github.jspxnet.txweb.apidoc;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "action_doc_vo", caption = "API文档索引对象", create = false)
public class ApiDocument extends ApiAction {

    @JsonIgnore(isNull = true)
    private List<ApiOperate> operateList = new LinkedList<>();

    //全局参数
    @JsonIgnore(isNull = true)
    private Map<String, ApiParam> params = new LinkedHashMap<>();

    @Column(caption = "描述")
    private String describe;

}
