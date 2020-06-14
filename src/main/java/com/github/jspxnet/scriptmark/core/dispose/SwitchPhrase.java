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
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.block.BaseSwitchBlock;
import com.github.jspxnet.scriptmark.core.block.DefaultBlock;
import com.github.jspxnet.scriptmark.core.block.CaseBlock;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.ObjectUtil;
import java.io.Writer;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:23:52
 * Switch块
 */
public class SwitchPhrase implements Phrase {


    public SwitchPhrase() {

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
        ScriptRunner scriptEngine = env.getScriptRunner();
        boolean isRun = false;
        BaseSwitchBlock switchBlock = (BaseSwitchBlock) tagNode;
        DefaultBlock defaultBlock = null;
        String check = switchBlock.getVarName();
        List<TagNode> caseList = switchBlock.getCaseAndDefaultBlock();

        for (TagNode cNode : caseList) {
            if (cNode instanceof CaseBlock) {
                CaseBlock caseblock = (CaseBlock) cNode;
                try {
                    if (ObjectUtil.toBoolean(scriptEngine.eval(check + "==" + caseblock.getWhere(), tagNode.getLineNumber()))) {
                        isRun = true;
                        for (TagNode node : caseblock.getChildList()) {
                            env.runBlock(node, out);
                        }
                        break;
                    }
                } catch (ScriptException e) {
                    throw new ScriptRunException(caseblock, caseblock.getSource());
                }
            } else if (cNode instanceof DefaultBlock) {
                defaultBlock = (DefaultBlock) cNode;
            }
        }
        if (!isRun && defaultBlock != null) {
            for (TagNode dNode : defaultBlock.getChildList()) {
                env.runBlock(dNode, out);
            }
        }
        caseList.clear();
        return 0;
    }
}