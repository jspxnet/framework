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

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-15
 * Time: 14:09:44
 * 宏调用
 * <pre>{@code
 *  <#macro name=button>
 *   <input type="button" name="${name}" value="${value}"/>
 *  </#macro>
 *    <@button name="bname" value="myvalue"/>
 *    }</pre>
 */
public class CallBlock extends TagNode {
    public String getCallName() {
        String s = getSource();
        int i = s.indexOf(macroCallTag);
        if (i == -1) {
            return null;
        }
        int iEnd = s.indexOf(' ', i);
        if (iEnd == -1) {
            return null;
        }
        return s.substring(i + 1, iEnd);
    }

}