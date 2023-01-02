package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OperatePlugParam extends OperateTable {

    @Param(caption = "表名称",max = 200,required = true)
    private String tableName;

    //注入的名称
    @Param(caption = "资源名称",max = 100,required = true)
    private String refName;

    @Param(caption = "描述",max = 200)
    private String desc;

    //允许为空
    @Param(caption = "命名空间",max = 100)
    private String namespace;

    @Param(caption = "执行顺序",min = 1,max = 1000,required = true,value = "0")
    private int sortType = 0;

}