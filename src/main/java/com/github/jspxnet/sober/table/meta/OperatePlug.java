package com.github.jspxnet.sober.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 二开插件支持功能
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_operate_plug", caption = "单据动作插件")
public class OperatePlug extends OperateTable {
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "表单Id", notNull = true)
    private long formId;

    @Column(caption = "描述", length = 100)
    private String desc = StringUtil.empty;

    @Column(caption = "执行顺序", notNull = true)
    private int sortType = 0;

    @Column(caption = "动作", notNull = true, length = 50)
    private String operate = "";

    //注入的名称
    @Param(caption = "资源名称",max = 100,required = true)
    private String refName;

    @Column(caption = "插件对象", notNull = true, length = 100)
    private String operateClass = "";

    //允许为空
    @Param(caption = "命名空间",max = 100)
    private String namespace;

}
