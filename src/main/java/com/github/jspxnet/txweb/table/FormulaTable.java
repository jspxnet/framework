package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/12/20 20:53
 * description: thermo-model
 **/
@Data
@Table(name = "jspx_formula_table",caption = "公式表")
public class FormulaTable implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "编号",length = 32, notNull = true)
    private String code;

    @Column(caption = "名称", length = 50,notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "描述", length = 200)
    private String des = StringUtil.empty;

    @Column(caption = "单位", length = 30)
    private String unit = StringUtil.empty;

    @Column(caption = "计算方式编号",length = 30)
    private String calcCode= "default";

    //default:默认从 cache表起数据,acsColSum:热网的一列合计;sql:是用sql起数据
    @Nexus(mapping = MappingType.OneToOne, field = "calcCode", targetField = "code", targetEntity = FormulaCalcType.class)
    private FormulaCalcType formulaCalcType = new FormulaCalcType();

    @Column(caption = "公式", length = 1000,notNull = true)
    private String content = StringUtil.empty;

    @Column(caption = "公式分组", length = 32)
    private String groupType = StringUtil.empty;

    @Column(caption = "返回类型", length = 200)
    private String returnClass = StringUtil.empty;

    @Column(caption = "日期", notNull = true)
    private Date createDate = new Date();


}
