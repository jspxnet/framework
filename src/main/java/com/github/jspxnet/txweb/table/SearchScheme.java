package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.BoolEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author chenyuan
 * 保存搜索信息，原型参考星空搜索框
 * com.github.jspxnet.txweb.table.SearchScheme
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "jspx_search_scheme", caption = "保存搜索条件")
public class SearchScheme extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "搜索名称", length = 100, notNull = true)
    private String name = StringUtil.empty;

    //保存需要显示的字段
    @Column(caption = "显示字段", length = 1000)
    private String fields = StringUtil.empty;

    @Column(caption = "条件", length = 2000)
    private String term = StringUtil.empty;

    @Column(caption = "排序", length = 200)
    private String orderBy = StringUtil.empty;

    //用json保存
    @Column(caption = "快捷过滤", length = 2000)
    private String fastFilter = StringUtil.empty;


    @Column(caption = "共享")
    private int share = BoolEnumType.NO.getValue();

    //多个用分号分隔
    @Column(caption = "共享用户")
    private String shareUser = StringUtil.empty;
}
