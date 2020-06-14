/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.io.Serializable;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:09:02
 */
public interface Phrase extends Serializable {
    /**
     * 1:break 2:continue
     * @param env 运行环境
     * @param tagNode 节点
     * @param out 输出
     * @return 0:正常返回; 1:break 2:continue
     * @throws ScriptRunException 异常
     */
    int getRun(EnvRunner env, TagNode tagNode, Writer out) throws ScriptRunException;


}