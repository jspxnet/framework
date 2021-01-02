package com.github.jspxnet.sober.config.xml;

import com.github.jspxnet.sober.config.BaseXmlTagNode;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/29 16:31
 * description: 数据库初始化
 **/
public class TableXml extends BaseXmlTagNode  {
    public final static String TAG_NAME = "table";

    public TableXml() {
        super.setTagName(TAG_NAME);
    }
}
