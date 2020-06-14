/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block.template;

import com.github.jspxnet.scriptmark.core.block.BaseSwitchBlock;
import com.github.jspxnet.scriptmark.core.block.CaseBlock;
import com.github.jspxnet.scriptmark.core.block.DefaultBlock;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.utils.StringUtil;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 17:30:39
 * <pre>{@code
 * <#switch var=ifthree>
 * 3333333333
 * <#case where="aa">
 * 444444444444
 * </#case>
 * <#case where="bbb">
 * 444444444444
 * </#case>
 * <#default>
 * 55555555555555
 * </#default>
 * </#if>
 * }</pre>
 */
public class SwitchBlock extends TagNode implements BaseSwitchBlock {


    @Override
    public String getVarName() {
        String s = getStringAttribute("var");
        if (StringUtil.hasLength(s)) {
            return s;
        }
        s = getAttributes();
        return ScriptMarkUtil.deleteQuote(s);
    }

    /**
     * @return 得到的是节点case
     */
    @Override
    public List<TagNode> getCaseAndDefaultBlock() {
        TemplateElement templateEl = new TemplateElement(getBody(), getTemplate().getLastModified(), getTemplate().getConfigurable());
        Map<String, String> tremTagMap = new HashMap<>();
        tremTagMap.put("#case", CaseBlock.class.getName());
        tremTagMap.put("#default", DefaultBlock.class.getName());
        return templateEl.getBlockTree(getBody(), tremTagMap);
    }
}