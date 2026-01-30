package com.github.jspxnet.sober.table;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.AuditEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_safe_word", caption = "安全字符串映射")
public class SafeWordTable extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "字符", length = 200, notNull = true)
    private String safeWord = StringUtil.empty;

    @Column(caption = "显示", length = 100, notNull = true)
    private String caption = StringUtil.empty;

    //system 的时候起系统默认配置
    @Column(caption = "钥匙", length = 100, notNull = true)
    private String key = Environment.SYSTEM_NAME;

    @Column(caption = "审核")
    private int auditType =  AuditEnumType.OK.getValue();

    //默认替换方式
    @Column(caption = "策略", length = 100, notNull = true)
    private String strategy = "ReplaceIgnoreCase";

    @Column(caption = "备注说明", length = 200)
    private String remark = StringUtil.empty;

}
