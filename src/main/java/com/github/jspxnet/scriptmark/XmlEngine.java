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

import java.util.List;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-14
 * Time: 15:27:08
 */
public interface XmlEngine extends Serializable {
    void putTag(String tag, String className);

    String removeTag(String tag);

    List<TagNode> getTagNodes(String str) throws Exception;

    TagNode createTagNode(String str) throws Exception;
}