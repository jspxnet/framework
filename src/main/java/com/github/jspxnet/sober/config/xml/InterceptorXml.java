package com.github.jspxnet.sober.config.xml;

import com.github.jspxnet.sober.config.BaseXmlTagNode;

public class InterceptorXml extends BaseXmlTagNode {
    public final static  String TAG_NAME = "interceptor-ref";

    public InterceptorXml() {
        super.setTagName(TAG_NAME);
    }
}
