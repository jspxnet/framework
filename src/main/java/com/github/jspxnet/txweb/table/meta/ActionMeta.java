package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 只是作为是用帮助,可以辅助开发提示信息
 */
@Data
@Table(name = "jspx_com_action", caption = "Web控件事件")
public class ActionMeta implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "事件名称", length = 50, notNull = true)
    private String name = StringUtil.empty;

    //事件的中文说明,比如 更新,保存
    @Column(caption = "描述", length = 100, notNull = true)
    private String caption = StringUtil.empty;

    //function  save(e) {  //...     }  这种样式
    @Column(caption = "事件模版", length = 1000, notNull = true)
    private String template = StringUtil.empty;

    //是用说明
    @Column(caption = "说明", length = 250)
    private String desc = StringUtil.empty;
}
