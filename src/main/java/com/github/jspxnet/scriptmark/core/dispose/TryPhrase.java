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
import com.github.jspxnet.scriptmark.core.block.BaseTryBlock;
import com.github.jspxnet.scriptmark.core.block.template.TryBlock;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.io.Writer;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:24:35
 * try 块
 */
public class TryPhrase implements Phrase {
    public TryPhrase() {

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
        BaseTryBlock tryBlock = (BaseTryBlock) tagNode;
        int bc = 0;
        try {
            List<TagNode> tryList = tryBlock.getBodyList();
            for (TagNode node : tryList) {
                env.runBlock(node, out);
            }
            tryList.clear();
        } catch (Exception e) {
            List<TagNode> catchList = tryBlock.getCatchBodyList();
            for (TagNode node : catchList) {
                bc = env.runBlock(node, out);
                if (bc != 0) {
                    return bc;
                }
            }
            catchList.clear();
        } finally {
            List<TagNode> finList = tryBlock.getFinallyBodyList();
            for (TagNode node : finList) {
                bc = env.runBlock(node, out);
                if (bc != 0) {
                    break;
                }
            }
            finList.clear();
        }
        return bc;
    }
}