package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_field_enum",caption = "字段枚举关系")
public class SoberFieldEnum extends OperateTable{
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "表名称", length = 100)
    private String tableName;

    @Column(caption = "字段名", length = 100)
    private String fieldName;

    //中文的表明描述，可以作为关键字识别
    @Column(caption = "字典表分组", length = 100)
    private String groupCode = StringUtil.empty;

    //如果没用配置将变成动态对象
    @Column(caption = "命名空间", length = 200)
    private String namespace;
}
