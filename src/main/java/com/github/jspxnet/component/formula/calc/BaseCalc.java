package com.github.jspxnet.component.formula.calc;

import com.github.jspxnet.component.formula.table.FormulaTable;
import com.github.jspxnet.sober.SoberSupport;


public interface BaseCalc {

    void  setDao(SoberSupport dao);
    void  setFormulaTable(FormulaTable formula);
    Object getFormulaResult();

}
