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
 * date: 2008-11-16
 * Time: 12:22:38
 *
 * <pre>
 * {@code
 * <#script>
 *    function avg(x,y)
 *    {
 *        return (x + y) / 2;
 *    }
 *    function mul(x,y)
 *    {
 *        return x*y;
 *    }
 *
 * </#script>
 * ${avg(10, 20) * mul(2,5)}
 */

/**
 * 脚本块
 */
public class ScriptBlock extends TagNode {
    public ScriptBlock() {
        repair = true;
    }
}