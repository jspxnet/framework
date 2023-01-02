package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.LinkedList;
import java.util.List;

/**
 * 保存单据的的所有数据信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_table_meta", caption = "表单数据元")
public class TableMeta extends OperateTable {
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    //传入类名后 通过表 SoberTableModel 查询对应的表明
    @Column(caption = "表名称", length = 100, notNull = true)
    private String tableName;

    //0:作废,1:表示只有1级主表;2:表示主子表;3:足子孙表 , 一般就不要超过3级了
    //利用这个标识优化查询
    @Column(caption = "表类型")
    private int tableType = 1;

    @Column(caption = "父表", length = 100, notNull = true)
    private String parentTableName = StringUtil.empty;

    //中文的表明描述，可以作为关键字识别
    @Column(caption = "表名描述", length = 100)
    private String caption = StringUtil.empty;

    //如果没用配置将变成动态对象
    @Column(caption = "实体对象", length = 200)
    private String entityClass;

    //一般默认为id
    @Column(caption = "关键字名", length = 200)
    private String primary = StringUtil.empty;

    @Nexus(mapping = MappingType.OneToMany, field = "tableName", targetField = "tableName", targetEntity = SoberColumn.class)
    private List<SoberColumn> columns = new LinkedList<>();


    @Nexus(mapping = MappingType.OneToMany, field = "tableName", targetField = "tableName", targetEntity = OperatePlug.class)
    private List<OperatePlug> operatePlugList = new LinkedList<>();

    //所有的界面功能脚本
    @Column(caption = "功能脚本", length = 20000)
    private String actionScript = StringUtil.empty;

    //所有的界面脚本放这里 比如:http://120.92.142.115:81/vform3pro/  上边导出的json
    @Column(caption = "布局脚本", length = 20000)
    private String viewScript = StringUtil.empty;

}
