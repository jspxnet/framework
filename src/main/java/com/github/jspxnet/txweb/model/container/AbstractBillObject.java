package com.github.jspxnet.txweb.model.container;


import com.github.jspxnet.enums.BoolEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.model.container.AbstractObjectValue;
import com.github.jspxnet.txweb.enums.DocumentStatusEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * 单据对象,放入单据的标准字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractBillObject extends AbstractObjectValue {

    @Column(caption = "单据编号", length = 70, notNull = true)
    protected String billNo = StringUtil.empty;

    @Column(caption = "单据状态", length = 2,enumType = DocumentStatusEnumType.class,notNull = true)
    protected int documentStatus = DocumentStatusEnumType.A.getValue();

    @Column(caption = "审核人ID")
    protected long approverId = 0;

    @Column(caption = "审核日期")
    protected Date approveDate;

    @Column(caption = "修改人")
    protected long modifierId = 0;

    @Column(caption = "修改日期")
    protected Date modifyDate;

    @Column(caption = "作废人")
    protected long canceler = 0;

    @Column(caption = "作废日期")
    protected Date cancelDate;

    @Column(caption = "作废状态",enumType = BoolEnumType.class,length = 2,notNull = true)
    protected int cancelStatus = BoolEnumType.NO.getValue();

    //和数据库建表对应
    @Column(caption = "单据类型ID")
    protected long billTypeId;

}
