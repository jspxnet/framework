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
 * 错别字检查
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_wrongly_word", caption = "错别字")
public class WrongWord extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "错误词组", length = 100, notNull = true)
    private String wrong = StringUtil.empty;

    @Column(caption = "正确词组", length = 100, notNull = true)
    private String word = StringUtil.empty;

    @Column(caption = "识别次数", notNull = true)
    private int times = 0;


}
