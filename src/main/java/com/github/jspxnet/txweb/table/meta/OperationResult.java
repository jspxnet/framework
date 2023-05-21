package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.enums.YesNoEnumType;
import lombok.Data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件返回统一标准,操作事件链,后返回
 */
@Data
public class OperationResult implements Serializable {

    //成功,失败
    private int success = YesNoEnumType.YES.getValue();

    private Map<String,String> error = new HashMap<>();  //错误信息

    private String message = null; // 请求状态描述，调试用

}
