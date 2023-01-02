package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.DocumentFormatType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
@Data
public class PageCodeMakerParam implements Serializable {
    @Param(caption = "ID")
    private long id;


    @Param(caption = "访问id", max = 32)
    private String urlId = StringUtil.empty;

    @Param(caption = "ID", max = 250)
    private String name = StringUtil.empty;

    @Param(caption = "文档类型",enumType = DocumentFormatType.class)
    private int docType = DocumentFormatType.HTML.getValue();

    @Param(caption = "页面模型",min = 1,max = 200)
    private String templateName = StringUtil.empty;

    @Param(caption = "模型ID", max = 200)
    private String modelId = StringUtil.empty;

    @Param(caption = "类名", max = 512)
    private String className = StringUtil.empty;

    @Param(caption = "正文", level = SafetyEnumType.NONE, max = 20000)
    private String content = StringUtil.empty;

    @Param(caption = "跳过字段", level = SafetyEnumType.LOW, max = 250)
    private String jumpFields = StringUtil.empty;

    @Param(caption = "调试参数", max = 250)
    private String debugParam = StringUtil.empty;
    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Param(caption = "版本号")
    private int version = 1;

    @Param(caption = "命名空间", max = 50)
    private String namespace = StringUtil.empty;

}
