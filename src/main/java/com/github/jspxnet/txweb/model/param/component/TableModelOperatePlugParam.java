package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
public class TableModelOperatePlugParam implements Serializable {

    @Param(caption = "表名称",max = 200,required = true)
    private String tableName;

    @Param(caption = "执行顺序",required = true)
    private List<OperatePlugParam> operatePlugList = new ArrayList<>();

}
