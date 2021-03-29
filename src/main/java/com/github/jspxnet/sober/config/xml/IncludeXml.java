package com.github.jspxnet.sober.config.xml;

import com.github.jspxnet.sober.config.BaseXmlTagNode;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/4 21:20
 * description: 包含功能
 **/
public class IncludeXml extends BaseXmlTagNode {
    public final static  String TAG_NAME = "include";

    public IncludeXml() {
        super.setTagName(TAG_NAME);
    }
}
