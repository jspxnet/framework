package com.github.jspxnet.txweb.evasive.condition;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public class ParameterDecide extends AbstractDecide {
    @Override
    public boolean execute() {
        //代码已经在外部执行过了
        return StringUtil.toBoolean(StringUtil.trim(request.getParameter(content)));
    }
}
