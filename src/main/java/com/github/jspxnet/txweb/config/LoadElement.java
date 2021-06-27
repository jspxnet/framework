package com.github.jspxnet.txweb.config;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.XMLUtil;

public class LoadElement extends TagNode {
    public final static String TAG_NAME = "load";

    public LoadElement() {

    }

    public String getFile() {
        return XMLUtil.deleteQuote(getStringAttribute("file"));
    }

    public String getEncode() {
        return XMLUtil.deleteQuote(getStringAttribute("encode"));
    }

    public String getId() {
        return XMLUtil.deleteQuote(getStringAttribute("id"));
    }

}