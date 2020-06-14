package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by chenyuan on 15-6-18.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_sensitive_word", caption = "敏感词")
public class SensitiveWord extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "敏感词", length = 100, notNull = true)
    private String word = StringUtil.empty;

    @Column(caption = "识别次数", notNull = true)
    private int times = 0;


}
