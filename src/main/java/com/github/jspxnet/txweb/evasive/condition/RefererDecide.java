/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.evasive.condition;


import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;

/**
 * 外连判断
 * //Reject
 */
public class RefererDecide extends AbstractDecide {
    @Override
    public boolean execute() {
        if (RequestUtil.isPirated(request)) {
            String serverName = URLUtil.getTopDomain(request.getServerName());
            return !ArrayUtil.inArray(StringUtil.split(content, "|"), serverName, true);
        }
        return false;
    }
}