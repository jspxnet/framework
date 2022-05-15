package com.github.jspxnet.component.formula.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/12/20 21:22
 * description:calcClass 为java执行类
 **/
@Data
@Table(name = "jspx_formula_calc_type",caption = "计算方式表")
public class FormulaCalcType implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "编号",length = 32, notNull = true)
    private String code;

    @Column(caption = "计算类",length = 100, notNull = true)
    private String calcClass;

}
