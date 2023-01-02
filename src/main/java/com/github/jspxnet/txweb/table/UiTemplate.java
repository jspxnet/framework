package com.github.jspxnet.txweb.table;


import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_ui_template", caption = "页面模版")
public class UiTemplate extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "name", length = 512, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "正文", length = 20000, hidden = true,dataType = "isLengthBetween(1,20000)",input = "textarea")
    private String content = StringUtil.empty;

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Column(caption = "版本号",defaultValue = "1")
    private int version = 1;

}
