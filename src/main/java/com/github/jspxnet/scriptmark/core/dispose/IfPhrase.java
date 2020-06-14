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
import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.block.BaseIfBlock;
import com.github.jspxnet.scriptmark.core.block.ElseBlock;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.ObjectUtil;
import java.io.Writer;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:08:00
 * if 执行
 */
public class IfPhrase implements Phrase {

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
        ScriptRunner scriptEngine = env.getScriptRunner();
        BaseIfBlock ifBlock = (BaseIfBlock) tagNode;

        String w = ifBlock.getWhere();
        boolean bw = false;
        try {
            if (!ObjectUtil.isEmpty(w)) {
                Object resultStr = scriptEngine.eval(w, tagNode.getLineNumber());
                bw = ObjectUtil.toBoolean(resultStr);
            }
        } catch (ScriptException e) {
            throw new ScriptRunException(tagNode, tagNode.getSource());
        }
        if (bw) {
            ////if true begin
            List<TagNode> ifList = ifBlock.getTrueNode();
            int bc;
            for (TagNode ifn : ifList) {
                bc = env.runBlock(ifn, out);
                if (bc != 0) {
                    return bc;
                }
            }
            ifList.clear();
            ////if true end
        } else {
            ///////if false transfer do else begin
            List<TagNode> elseList = ifBlock.getElseBlock();
            ElseBlock defaultBlock = null;
            boolean isRun = false;
            if (elseList != null) {
                for (TagNode elNode : elseList) {
                    ElseBlock elseBlock = (ElseBlock) elNode;
                    w = elseBlock.getWhere();
                    if (w == null) {
                        if (defaultBlock != null) {
                            throw new ScriptRunException(elseBlock, elseBlock.getSource());
                        }
                        defaultBlock = elseBlock;
                        continue;
                    }
                    try {
                        bw = ObjectUtil.toBoolean(scriptEngine.eval(w, tagNode.getLineNumber()));
                    } catch (Exception e) {
                        throw new ScriptRunException(tagNode, tagNode.getSource());
                    }
                    if (bw) {
                        isRun = true;
                        List<TagNode> elseNode = elseBlock.getChildList();
                        if (elseNode != null) {
                            int bc;
                            for (TagNode eNode : elseNode) {
                                bc = env.runBlock(eNode, out);
                                if (bc != 0) {
                                    return bc;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if (!isRun && defaultBlock != null) {
                env.runBlock(defaultBlock, out);
            }
            ///////if false transfer do else end
        }
        return 0;
    }
}