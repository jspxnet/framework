package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_child_table_meta",cache = false)
public class ChildTableMeta extends OperateTable{

    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "主表ID", notNull = true)
    private long masterId = 0;

    @Column(caption = "表名称")
    private String tableName;

    //中文的表明描述，可以作为关键字识别
    @Column(caption = "表名描述", length = 100)
    private String caption = StringUtil.empty;

    @Column(caption = "关键字名")
    private String primary = StringUtil.empty;

    @Nexus(mapping = MappingType.OneToMany, field = "tableName", targetField = "tableName", targetEntity = SoberColumn.class)
    private List<SoberColumn> columns = new LinkedList<>();
}
