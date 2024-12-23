package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 只是作为映射补充
 * com.github.jspxnet.sober.table.SoberTableModel
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_table_model",caption = "实体映射关系")
public class SoberTableModel extends OperateTable{
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "表名称", length = 100)
    private String tableName;

    /**
     * 中文的表明描述，可以作为关键字识别
     */
    @Column(caption = "表名描述", length = 100)
    private String caption = StringUtil.empty;

    /**
     * 如果没用配置将变成动态对象
     */
    @Column(caption = "实体对象", length = 200)
    private String entityClass;

    @Column(caption = "缓存")
    private boolean useCache = true;
}
