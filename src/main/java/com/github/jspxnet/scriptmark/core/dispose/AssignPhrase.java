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
import com.github.jspxnet.scriptmark.core.block.AssignBlock;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.StringUtil;
import org.mozilla.javascript.NativeObject;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:08:35
 * Phrase
 * 赋值
 */
public class AssignPhrase implements Phrase {
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

        AssignBlock assignBlock = (AssignBlock) tagNode;
        String type = assignBlock.getType();
        String in = assignBlock.getIn();
        List<TagNode> list = assignBlock.getValueList();
        Writer aw = new StringWriter();
        for (TagNode node : list) {
            if (AssignBlock.SCRIPT.equalsIgnoreCase(type)) {
                try {
                    env.runBlock(node, aw);
                } catch (ScriptRunException e) {
                    throw new ScriptRunException(node, node.getSource());
                }
            } else {
                try {
                    aw.write(node.getSource());
                } catch (IOException e) {
                    throw new ScriptRunException(node, node.getSource());
                }
            }
        }

        ScriptRunner scriptEngine = env.getScriptRunner();
        if (StringUtil.hasLength(in)) {
            /**
             * 为了支持这种写法
             * <#assign mail="chen@other.com" in=my>
             * <#assign mail="jsmith@other.com" in=you>
             * ${my.mail}, ${you.mail}
             */
            NativeObject jsObject = new NativeObject();
            jsObject.setParentScope(scriptEngine.getScope());
            try {
                jsObject.put(assignBlock.getVarName(), jsObject, scriptEngine.eval(aw.toString(), assignBlock.getLineNumber()));
            } catch (ScriptException e) {
                Object action = null;
                try {
                    action = scriptEngine.get("action");
                } catch (ScriptException ex) {
                    ex.printStackTrace();
                }
                throw new ScriptRunException(assignBlock, action + " " + assignBlock.getSource());
            }
            scriptEngine.put(in, jsObject);
        } else {
            if (AssignBlock.SCRIPT.equalsIgnoreCase(type)) {
                try {
                    scriptEngine.putVar(assignBlock.getVarName(), aw.toString());
                } catch (ScriptRunException e) {
                    Object action = null;
                    try {
                        action = scriptEngine.get("action");
                    } catch (ScriptException ex) {
                        ex.printStackTrace();
                    }
                    throw new ScriptRunException(assignBlock, action + " " + assignBlock.getSource());
                }
            } else {
                scriptEngine.put(assignBlock.getVarName(), aw.toString());
            }
        }
        list.clear();
        return 0;
    }
}