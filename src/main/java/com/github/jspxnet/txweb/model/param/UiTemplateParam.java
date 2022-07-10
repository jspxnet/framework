package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class UiTemplateParam implements Serializable {

    @Param(caption = "ID")
    private long id = 0;

    @Param(caption = "name", max = 250,level = SafetyEnumType.HEIGHT)
    private String name = StringUtil.empty;

    @Param(caption = "正文", max = 20000,level = SafetyEnumType.NONE)
    private String content = StringUtil.empty;
}
