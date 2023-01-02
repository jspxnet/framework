package com.github.jspxnet.component.formula.dao.impl;


import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.component.formula.dao.FormulaDAO;

import com.github.jspxnet.component.formula.table.FormulaTable;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/22 16:54
 * description: thermo-model
 **/
@Slf4j
public class FormulaDAOImpl extends JdbcOperations implements FormulaDAO {

    @Override
    public FormulaTable getByCode(String code)
    {
        return get(FormulaTable.class,"code",code,false);
    }

}
