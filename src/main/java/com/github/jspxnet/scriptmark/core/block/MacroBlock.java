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
 * date: 2008-11-14
 * Time: 17:29:40
 * <pre>{@code
 *  <#macro name=button>
 *   <input type="button" name="${name}" value="${value}"/>
 *  </#macro>
 *  <@button name="bname" value="myvalue"/>
 * 宏定义
 * }</pre>
 */

public class MacroBlock extends TagNode {
    final private static String name = "name";

    public String getMacroName() {
        return getStringAttribute(name);
    }
}