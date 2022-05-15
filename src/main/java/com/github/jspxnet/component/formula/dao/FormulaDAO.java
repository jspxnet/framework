package com.github.jspxnet.component.formula.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.component.formula.table.FormulaTable;


/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/22 16:54
 * description: thermo-model
 **/
public interface FormulaDAO extends SoberSupport {
    FormulaTable getByCode(String code);
}
