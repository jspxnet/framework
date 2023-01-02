package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
@Table(name = "jspx_control_property", caption = "Web控件属性")
public class ControlProperty implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "控件名称", length = 50, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "分组", length = 100, notNull = true)
    private String groupName = StringUtil.empty;

    @Column(caption = "属性名称", length = 100, notNull = true)
    private String propertyName = StringUtil.empty;

    @Column(caption = "值域", length = 100, notNull = true)
    private String propertyRange = StringUtil.empty;

    @Column(caption = "默认值", length = 100)
    private String propertyDef = StringUtil.empty;

    @Column(caption = "说明", length = 250)
    private String desc = StringUtil.empty;

}
