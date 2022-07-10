package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.DocumentFormatType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class HelpTipParam implements Serializable {

    @Param(caption = "ID")
    private long id = 0;

    @Param(caption = "title", max = 512,required = true)
    private String title = StringUtil.empty;

    @Param(caption = "正文",level = SafetyEnumType.NONE, max = 20000,required = true)
    private String content = StringUtil.empty;

    @Param(caption = "文档类型",enumType = DocumentFormatType.class,value = "0")
    private int docType = DocumentFormatType.HTML.getValue();
    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Param(caption = "版本号")
    private int version = 1;

    @Param(caption = "命名空间", max = 50)
    private String namespace = StringUtil.empty;
}
