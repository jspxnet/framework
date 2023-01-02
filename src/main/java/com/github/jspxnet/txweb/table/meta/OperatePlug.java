package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 二开插件支持功能
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_operate_plug", caption = "单据动作插件")
public class OperatePlug extends OperateTable {
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "表名称", length = 100, notNull = true)
    private String tableName;

    //注入的名称
    @Column(caption = "资源名称", length = 100, notNull = true)
    private String refName;

    //允许为空
    @Column(caption = "命名空间", length = 100)
    private String namespace;

    @Column(caption = "描述", length = 100)
    private String desc = StringUtil.empty;

    @Column(caption = "执行顺序", notNull = true)
    private int sortType = 0;

}
