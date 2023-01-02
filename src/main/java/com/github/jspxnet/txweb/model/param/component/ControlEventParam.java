package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class ControlEventParam implements Serializable {

    @Param(caption = "控件名称",max = 50)
    private String name = StringUtil.empty;

    @Param(caption = "事件名称", max = 50)
    private String eventName = StringUtil.empty;

    @Param(caption = "描述", max = 100)
    private String eventCaption = StringUtil.empty;

    //function  save(e) {  //...     }  这种样式
    @Param(caption = "事件模版", max = 1000)
    private String template = StringUtil.empty;

    @Param(caption = "说明", max = 250)
    private String desc = StringUtil.empty;
}