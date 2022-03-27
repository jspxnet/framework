package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
public class ChildTableParam implements Serializable {


    @Param(caption = "表名称")
    private String tableName;

    //中文的表明描述，可以作为关键字识别
    @Param(caption = "表名描述",min = 0,max = 100)
    private String caption = StringUtil.empty;

    @Nexus(mapping = MappingType.OneToMany, field = "tableName", targetField = "tableName", targetEntity = SoberColumn.class)
    private List<SoberColumn> columns = new LinkedList<>();
}
