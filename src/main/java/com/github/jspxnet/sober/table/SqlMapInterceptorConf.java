package com.github.jspxnet.sober.table;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author chenYuan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_sql_interceptor", caption = "sqlmap拦截器")
public class SqlMapInterceptorConf extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "名称", length = 250, dataType = "isLengthBetween(1,200)", notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "描述", length = 250)
    private String caption = StringUtil.empty;

    @Column(caption = "条件",length = 250)
    private String term = StringUtil.empty;

    @Column(caption = "排序", notNull = true, defaultValue = "0",hidden = true)
    private int sortType = 0;

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Column(caption = "开启",enumType = YesNoEnumType.class,defaultValue = "1",input = "radio")
    private int enable = YesNoEnumType.YES.getValue();

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Column(caption = "版本号",defaultValue = "1")
    private int version = 1;


    @Column(caption = "命名空间", length = 50, dataType="isLengthBetween(1,50)")
    public String namespace = StringUtil.empty;
}
