package com.github.jspxnet.txweb.apidoc;

import com.github.jspxnet.json.JsonIgnore;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * author chenYuan
 */
@Data
public class ApiMethod implements Serializable {
    @JsonIgnore(isNull = true)
    private String name;

    //方法调用参数
    @JsonIgnore(isNull = true)
    private Map<String, ApiParam> params;

}
