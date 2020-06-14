package com.github.jspxnet.txweb.helper.tag;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by chenyuan on 15-3-30.
 */
public class HelpElement extends TagNode {
    public HelpElement() {

    }

    public String getId() {
        return XMLUtil.deleteQuote(getStringAttribute("id"));
    }

    public String getType() {
        return XMLUtil.deleteQuote(getStringAttribute("type"));
    }

}
