package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
@Table(name = "jspx_control_event", caption = "控件事件配置模版")
public class ControlEvent implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "控件名称", length = 50, notNull = true)
    private String name = StringUtil.empty;


    @Column(caption = "事件名称", length = 50, notNull = true)
    private String eventName = StringUtil.empty;

    @Column(caption = "描述", length = 100, notNull = true)
    private String eventCaption = StringUtil.empty;

    //function  save(e) {  //...     }  这种样式
    @Column(caption = "事件模版", length = 1000, notNull = true)
    private String template = StringUtil.empty;

    @Column(caption = "说明", length = 250)
    private String desc = StringUtil.empty;
}
