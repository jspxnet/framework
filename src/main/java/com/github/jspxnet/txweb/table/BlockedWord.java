package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by chenyuan on 15-6-10.
 * Blocked 表示不允许发布
 * 要屏蔽的关键字,关键字来源两个部分，本表和 屏蔽的主机名称
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_blocked_word", caption = "屏蔽关键字")
public class BlockedWord extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "名称", length = 120, notNull = true)
    private String word = StringUtil.empty;

    @Column(caption = "识别次数", notNull = true)
    private int times = 0;
}
