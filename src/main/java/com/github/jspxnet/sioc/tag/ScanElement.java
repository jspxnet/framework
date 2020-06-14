package com.github.jspxnet.sioc.tag;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.XMLUtil;

/**
 * 自动扫描包中是否包含Bean配置
 */
public class ScanElement extends TagNode {
    public final static String TAG_NAME = "scan";

    public ScanElement() {

    }

    public String getPackage() {
        return XMLUtil.deleteQuote(getStringAttribute("package"));
    }


}