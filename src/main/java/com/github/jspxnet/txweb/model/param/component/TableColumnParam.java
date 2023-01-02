package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class TableColumnParam implements Serializable {
    //同时也是关联关系
    @Param(caption = "表名称",required = true,min = 1,max = 200,message = "表名不允许为空")
    private String tableName;

    @Param(caption = "字段列表")
    private List<SoberColumnParam> columns = new ArrayList<>();


}
