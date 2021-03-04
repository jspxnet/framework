package com.github.jspxnet.sober.config.xml;

import com.github.jspxnet.sober.config.BaseXmlTagNode;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/3/5 0:33
 * @description: jspbox
 **/
public class SqlXml extends BaseXmlTagNode {
    public final static  String TAG_NAME = "sql";
    public SqlXml() {
        super.setTagName(TAG_NAME);
    }
}

