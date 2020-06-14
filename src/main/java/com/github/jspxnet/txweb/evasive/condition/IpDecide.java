package com.github.jspxnet.txweb.evasive.condition;

import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.IpUtil;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public class IpDecide extends AbstractDecide {

    @Override
    public boolean execute() {
        return IpUtil.interiorly(content, RequestUtil.getRemoteAddr(request));
    }

}
