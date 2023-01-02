package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.DocumentFormatType;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

//com.github.jspxnet.txweb.table.HelpTip
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_help_tip", caption = "帮助提示")
public class HelpTip extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "文档类型",enumType = DocumentFormatType.class,defaultValue = "0",input = "select")
    private int docType = DocumentFormatType.HTML.getValue();

    @Column(caption = "标题", length = 512, notNull = true)
    private String title = StringUtil.empty;

    @Column(caption = "正文", length = 20000, hidden = true,dataType = "isLengthBetween(1,20000)",input = "textarea")
    private String content = StringUtil.empty;

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Column(caption = "版本号",defaultValue = "1")
    private int version = 1;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonField(name = "docTypeCaption")
    public String getDocTypeName() {
        return DocumentFormatType.find(docType).getName();
    }
}
