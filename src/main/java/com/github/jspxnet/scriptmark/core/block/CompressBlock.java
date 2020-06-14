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

import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.core.TagNode;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 13:09:38
 * 压缩，等同Freemarker 删除里边的空格,保留\r\n
 * <pre>{@code
 * <#compress>
 * 1 2  3   4    5
 * </#compress>
 * 12345
 * }</pre>
 */
public class CompressBlock extends TagNode {
    public CompressBlock() {

    }

}