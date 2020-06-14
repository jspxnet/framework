package com.github.jspxnet.sober.config.xml;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/15 23:39
 * description: jspbox
 **/
public class SqlMapXml extends TagNode {
    public final static String TAG_NAME = "sqlmap";
    public SqlMapXml() {
        super.setTagName(TAG_NAME);
    }



    public String getNamespace()
    {
        return XMLUtil.deleteQuote(getStringAttribute("namespace"));
    }

    public String getCaption()
    {
        return XMLUtil.deleteQuote(getStringAttribute("caption"));
    }


}
