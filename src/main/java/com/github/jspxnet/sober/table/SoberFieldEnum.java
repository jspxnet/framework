package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设计说明:
 *  这里只是保持了表中字段对应的字典表 分组
 *  通过分组 查询 OptionBundle 中得到真实的字典表数据
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_field_enum",caption = "字段枚举关系",create = true,cache = true)
public class SoberFieldEnum extends OperateTable{

    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "表名称", length = 100)
    private String tableName;

    @Column(caption = "字段名", length = 100)
    private String field;

    //中文的表明描述，可以作为关键字识别
    @Column(caption = "字典表分组", length = 100)
    private String code = StringUtil.empty;

    @Column(caption = "分组名称", length = 100)
    private String caption = StringUtil.empty;

    //和 OptionBundle 的 namespace 一致
    @Column(caption = "命名空间", length = 200)
    private String namespace = StringUtil.empty;
}
