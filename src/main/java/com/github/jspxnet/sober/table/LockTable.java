package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 锁定表写,防止两个人同时编辑一条记录
 * com.github.jspxnet.sober.table.LockTable
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_lock_table",caption = "锁定表",cache = false)
public class LockTable extends OperateTable {

    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    //锁定的表名
    @Column(caption = "表名称",length = 100,notNull = true)
    private String tableName;

    //锁定ID
    @Column(caption = "Id",length = 100,notNull = true)
    private String lockId;
}
