package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by ChenYuan on 2017/6/2.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_counters", caption = "计数器")
public class Counters extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private int id;

    @Column(caption = "编码", notNull = true,length = 64)
    private String code = StringUtil.empty;

    @Column(caption = "名称", notNull = true,length = 64)
    private String name = StringUtil.empty;

    @Column(caption = "数据", notNull = true)
    private long num = 0;

}
