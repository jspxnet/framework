package com.github.jspxnet.sioc.tag;

import com.github.jspxnet.utils.XMLUtil;


public class LoadElement extends com.github.jspxnet.scriptmark.core.TagNode {
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