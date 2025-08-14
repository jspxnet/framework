package com.github.jspxnet.component.formula.service.impl;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.component.formula.calc.BaseCalc;
import com.github.jspxnet.component.formula.dao.FormulaDAO;
import com.github.jspxnet.component.formula.service.FormulaService;
import com.github.jspxnet.component.formula.table.FormulaTable;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/22 16:59
 * description: 公式计算引起
 **/
@Slf4j
public class FormulaServiceImpl implements FormulaService {

    //复合方式计算
    static final private String CALC_TYPE_FORMULA = "formula";

    //sql
    static final private String CALC_TYPE_SQL = "sql";
    //表达式
    static final private String CALC_TYPE_DEFAULT = "default";

    static final private String CALC_TYPE_NONE = "none";

    @Ref
    private FormulaDAO formulaDAO;

    /**
     *
     * @param valueMap 变量表
     * @param code 公式枚举编号
     * @return 计算结果
     */
    @Override
    public double getFormulaResult(Map<String, Object> valueMap, String code)
    {
        FormulaTable formula = formulaDAO.getByCode(code);
        if (formula==null)
        {
            return 0;
        }
        String content = formula.getContent();
        if (StringUtil.isEmpty(content))
        {
            return 0;
        }

        if (CALC_TYPE_NONE.equalsIgnoreCase(formula.getCalcCode()))
        {
            return StringUtil.toDouble(content);
        }

        //sql查询
        if (CALC_TYPE_SQL.equalsIgnoreCase(formula.getCalcType().getCode()))
        {
            return ObjectUtil.toDouble(formulaDAO.getUniqueResult(content));
        }

        if (CALC_TYPE_DEFAULT.equalsIgnoreCase(formula.getCalcType().getCode()))
        {
            try {
                String out =  EnvFactory.getPlaceholder().processTemplate(valueMap,"${" + content + "}");
                return ObjectUtil.toDouble(out);
            } catch (Exception e)
            {
                log.info("CALC_TYPE_DEFAULT 表达式计算异常:{}",content);
                return 0;
            }
        }

        BaseCalc baseCalc = (BaseCalc) EnvFactory.getBeanFactory().getBean(formula.getCalcType().getCalcClass());
        if (baseCalc==null)
        {
            return 0;
        }

        baseCalc.setFormulaTable(formula);
        baseCalc.setDao(formulaDAO);
        return ObjectUtil.toDouble(baseCalc.getFormulaResult());


    }

}
