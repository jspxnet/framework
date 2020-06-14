/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-26
 * Time: 16:53:06
 * switch 中的case块
 */
public class CaseBlock extends TagNode {
    final private static String WHERE = "where";
    final private static String W = "w";

    public CaseBlock() {

    }


    public String getWhere() {
        String s = getExpressionAttribute(WHERE);
        if (!StringUtil.hasLength(s)) {
            s = getExpressionAttribute(W);
        }
        if (!StringUtil.hasLength(s)) {
            return null;
        }
        return s.replaceAll(" lt ", "<").replaceAll(" le ", "<=").replaceAll(" gt ", ">").replaceAll(" ge ", ">=");
    }
}