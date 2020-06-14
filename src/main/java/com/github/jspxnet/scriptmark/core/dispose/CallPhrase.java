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
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.scriptmark.core.block.CallBlock;
import com.github.jspxnet.scriptmark.core.block.MacroBlock;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.XMLUtil;
import org.mozilla.javascript.Scriptable;

import java.io.Writer;
import java.io.StringWriter;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:22:54
 * 宏调用
 */
public class CallPhrase implements Phrase {
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
        CallBlock callBlock = (CallBlock) tagNode;
        MacroBlock macroBlock = TemplateConfigurable.getMacro(callBlock.getCallName());
        if (macroBlock == null) {
            throw new ScriptRunException(tagNode, tagNode.getTagName());
        }
        List<TagNode> macList = macroBlock.getChildList();
        Scriptable oldScope = scriptEngine.getScope();
        Scriptable scope = scriptEngine.copyScope();
        scriptEngine.setScope(scope);
        String[] attributeNames = callBlock.getAttributeName();

        final String varBegin = env.getTemplate().getConfigurable().getString(ScriptmarkEnv.VariableBegin);
        final String varEnd = env.getTemplate().getConfigurable().getString(ScriptmarkEnv.VariableEnd);
        for (String attribute : attributeNames) {
            String str = callBlock.getExpressionAttribute(attribute);

            if (str != null && str.contains(varBegin) && str.contains(varEnd)) {
                str = XMLUtil.deleteQuote(str);
                StringWriter sw = new StringWriter();
                TemplateElement templateElement = new TemplateElement(str, 0, env.getTemplate().getConfigurable());
                List<TagNode> list = templateElement.getRootTree();
                for (TagNode node : list) {
                    try {
                        env.getInjectVariables(node, sw);
                    } catch (Exception e) {
                        throw new ScriptRunException(node, node.getSource());
                    }
                }
                scriptEngine.put(attribute, sw.toString());
                list.clear();
            } else {
                scriptEngine.putVar(attribute, str);
            }
        }
        int bc;
        for (TagNode node : macList) {
            bc = env.runBlock(node, out);
            if (bc != 0) {
                return bc;
            }
        }
        scriptEngine.setScope(oldScope);
        //清空变量
        for (Object k : scope.getIds()) {
            scope.delete(k.toString());
        }
        macList.clear();
        return 0;
    }
}