package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;

/**
 * Created by ChenYuan on 2017/6/2.
 */
@Table(name = "jspx_counters", caption = "计数器")
public class Counters extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private int id;

    @Column(caption = "数据", notNull = true)
    private long num = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
