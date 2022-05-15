package com.github.jspxnet.component.formula.service;



import java.util.Map;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/22 16:59
 * description: thermo-model
 **/
public interface FormulaService {

    double getFormulaResult(Map<String, Object> valueMap, String code);

}
