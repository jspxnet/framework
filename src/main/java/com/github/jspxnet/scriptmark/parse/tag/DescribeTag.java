package com.github.jspxnet.scriptmark.parse.tag;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;

public class DescribeTag extends TagNode {
    public DescribeTag() {

    }

    public String getId() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("id"));
    }

    public String getFlag() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("flag"));
    }


}