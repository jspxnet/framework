package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.HttpMethodEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 保存需要调用的API接口信息,
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_call_api_conf", caption = "API调用配置")
public class CallApiConf extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    //外部通过这个来匹配
    @Column(caption = "名称", length = 250, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "调用名称", length = 250, notNull = true)
    private String caption = StringUtil.empty;

    @Column(caption = "调用路径", length = 250, notNull = true)
    private String url = StringUtil.empty;

    @Column(caption = "调用方式", length = 20, notNull = true)
    private String MethodType = HttpMethodEnumType.POST.getName();

    //提交参数
    @Column(caption = "参数", length = 4000)
    private String postData = StringUtil.empty;

}
