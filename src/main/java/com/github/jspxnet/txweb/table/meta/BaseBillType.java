package com.github.jspxnet.txweb.table.meta;

import com.github.jspxnet.enums.BoolEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.txweb.enums.BillPrintEnumType;
import com.github.jspxnet.txweb.enums.DocumentStatusEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单据数据基本信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_base_bill_type",caption = "基础单据类型",cache = false)
public class BaseBillType extends OperateTable {
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "表名称", length = 100, notNull = true)
    private String tableName;

    //如果有多个的时候起默认这个
    @Column(caption = "默认", length = 2, notNull = true)
    private int defType = BoolEnumType.YES.getValue();

    //可以摆放为树结构
    @Column(caption = "父ID", length = 100, notNull = true)
    private String parentNodeId = StringUtil.empty;

    //对应表 Sequences 的 name
    @Column(caption = "单据编号规则,对应表Sequences", length = 100)
    private String billNoRuleName = StringUtil.empty;

    @Column(caption = "最大打印次数")
    private int maxPrintCount = 100;

    @Column(caption = "审核后才允许打印",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int printAfterAudit = YesNoEnumType.NO.getValue();

    @Column(caption = "控制打印次数",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int controlPrintCount = YesNoEnumType.NO.getValue();

    @Column(caption = "打印方式",length = 2,enumType = YesNoEnumType.class, notNull = true)
    private int printType = BillPrintEnumType.NONE.getValue();

    @Column(caption = "打印模版", length = 250)
    private String printTemplate = StringUtil.empty;

    //系统默认,还是扩展的
    @Column(caption = "系统预设",length = 2,enumType = YesNoEnumType.class)
    private int sysPreSet = YesNoEnumType.NO.getValue();

    @Column(caption = "禁用状态", length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int forbidStatus = YesNoEnumType.NO.getValue();

    @Column(caption = "开启审核流程",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int useWorkFlow = YesNoEnumType.NO.getValue();

    @Column(caption = "业务流程编号", length = 32)
    private String flowNo = StringUtil.empty;

    //是否运行自己创建自己审核,不是用工作流的情况
    @Column(caption = "允许自己审核",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int allowSelfAudit = YesNoEnumType.NO.getValue();

    @Column(caption = "返审核和审核人一致",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int useUnAuditAudit = YesNoEnumType.NO.getValue();

    //包括附件
    @Column(caption = "删除人和创建人一致",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int useDeleteIsCreator = YesNoEnumType.NO.getValue();

    @Column(caption = "修改人和创建人一致",length = 2,enumType = YesNoEnumType.class,notNull = true)
    private int useModifierIsCreator = YesNoEnumType.NO.getValue();

    @Column(caption = "分页条数",notNull = true,defaultValue = "20")
    private int pageCount = 20;

    @Column(caption = "导出每批数量",notNull = true,defaultValue = "5000")
    private int exportCount = 5000;


    @Column(caption = "单据默认状态", length = 2,enumType = DocumentStatusEnumType.class,notNull = true)
    protected int defDocumentStatus = DocumentStatusEnumType.A.getValue();

}
