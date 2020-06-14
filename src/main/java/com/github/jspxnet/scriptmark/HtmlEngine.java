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
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-15
 * Time: 18:43:32
 */
public interface HtmlEngine extends Serializable {
    List<TagNode> getComputeTree() throws ScriptRunException;

    List<TagNode> getBlockTree(String source, Map<String, String> tagMap);

    List<TagNode> getBlockTree(int begin, int end, Map<String, String> tagMap) throws Exception;
}