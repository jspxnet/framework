package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.DocumentFormatType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_page_code_maker", caption = "页面代码表")
public class PageCodeMaker extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;


    @Column(caption = "访问id", length = 32, notNull = true)
    private String urlId = StringUtil.empty;

    @Column(caption = "名称", length = 250, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "文档类型",enumType = DocumentFormatType.class,defaultValue = "0")
    private int docType = DocumentFormatType.HTML.getValue();

    @Column(caption = "页面模型", length = 200)
    private String templateName = StringUtil.empty;

    @Column(caption = "类名", length = 512, notNull = true)
    private String className = StringUtil.empty;

    @Column(caption = "实体ID", length = 60, notNull = true)
    private String modelId = StringUtil.empty;

    @Column(caption = "正文", length = 20000, dataType = "isLengthBetween(1,20000)")
    private String content = StringUtil.empty;

    @Column(caption = "跳过字段", length = 250)
    private String jumpFields = StringUtil.empty;

    @Column(caption = "调试参数", length = 250)
    private String debugParam = StringUtil.empty;

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Column(caption = "版本号",defaultValue = "1")
    private int version = 1;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;


}
