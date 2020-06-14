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
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.block.MacroBlock;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-3
 * Time: 11:32:48
 * 宏调用
 */
public class MacroPhrase implements Phrase {
    public MacroPhrase() {

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
    public int getRun(EnvRunner env, TagNode tagNode, Writer out)  throws ScriptRunException {
        MacroBlock macroBlock = (MacroBlock) tagNode;
        TemplateConfigurable.regMacro(macroBlock.getMacroName(), macroBlock);
        return 0;
    }
}