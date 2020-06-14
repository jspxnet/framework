/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.dispose;

import com.github.jspxnet.scriptmark.Phrase;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.block.IncludeBlock;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

import java.io.Writer;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:21:03
 * 包含执行
 */
public class IncludePhrase implements Phrase {
    public IncludePhrase() {

    }
    /**
     * 1:break 2:continue
     * @param env 运行环境
     * @param tagNode 节点
     * @param out 输出
     * @return 0:正常返回; 1:break 2:continue
     * @throws ScriptRunException 异常
     */
    @Override
    public int getRun(EnvRunner env, TagNode tagNode, Writer out) throws ScriptRunException {
        IncludeBlock includeBlock = (IncludeBlock) tagNode;
        includeBlock.setCurrentPath(env.getCurrentPath());
        includeBlock.setRootDirectory(env.getRootDirectory());
        final String[] jumpName = new String[]{IncludeBlock.name, IncludeBlock.caption, IncludeBlock.caption, IncludeBlock.encoding, IncludeBlock.file};
        String[] attributeName = includeBlock.getAttributeName();
        if (!ArrayUtil.isEmpty(attributeName)) {
            for (String varName : attributeName) {
                if (StringUtil.isNull(varName) || ArrayUtil.inArray(jumpName, varName, true)) {
                    continue;
                }
                String value = XMLUtil.deleteQuote(includeBlock.getStringAttribute(varName));
                if (!StringUtil.hasLength(value)) {
                    continue;
                }
                try {
                    env.getScriptRunner().eval(varName + "=(" + value + ");", includeBlock.getLineNumber());
                } catch (ScriptException e) {
                    throw new ScriptRunException(includeBlock, value);
                }
            }
        }

        List<TagNode> list = includeBlock.getIncludeNodeList();
        int bc = 0;
        for (TagNode node : list) {
            bc = env.runBlock(node, out);
            if (bc != 0) {
                return bc;
            }
        }
        return bc;
    }
}