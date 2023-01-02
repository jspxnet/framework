package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.txweb.annotation.Operate;
import lombok.Data;

@Data
public class BillEvent implements java.io.Serializable {

    protected OperationResult operationResult = null;

    //执行的动作
    protected transient  Operate operate;
    //数据元
    protected transient Class<?> tableMeta;

    //参数
    protected transient Object  param;
}
