package com.github.jspxnet.txweb.evasive.condition;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public class SessionDecide extends AbstractDecide {

    @Override
    public boolean execute() {
        if (StringUtil.isNull(content)) {
            return false;
        }
        String value = (String) request.getSession().getAttribute(content);
        return value != null && value.toLowerCase().contains(value.toLowerCase());
    }
}
