package com.github.jspxnet.txweb.evasive.condition;

import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public class UserAgentDecide extends AbstractDecide {
    @Override
    public boolean execute() {
        if (StringUtil.isNull(content)) {
            return false;
        }
        String userAgent = RequestUtil.getUserAgent(request);
        return userAgent != null && userAgent.toLowerCase().contains(content.toLowerCase());
    }

}
