package com.github.jspxnet.component.k3cloud.element;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/30 0:20
 * description: thermo-model
 **/
public class TableElement extends TagNode {
    public final static String TAG_NAME = "table";

    public TableElement() {

    }

    public String getId() {
        return XMLUtil.deleteQuote(getStringAttribute("id"));
    }

    public String getCaption() {
        return XMLUtil.deleteQuote(getStringAttribute("caption"));
    }

    public String getClassName() {
        return XMLUtil.deleteQuote(getStringAttribute("class"));
    }

    public String getKey() {
        return XMLUtil.deleteQuote(getStringAttribute("key"));
    }

}
