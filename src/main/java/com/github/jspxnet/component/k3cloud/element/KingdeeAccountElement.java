package com.github.jspxnet.component.k3cloud.element;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/30 0:33
 * description: thermo-model
 **/
public class KingdeeAccountElement extends TagNode {
    public final static String TAG_NAME = "kingdee";
    public KingdeeAccountElement() {

    }

    public String getAcctId() {

        return XMLUtil.getKeyValue("acctId",getSource());
    }

    public String getUserName() {
        String str = getSource();
        return XMLUtil.getKeyValue("userName",str);
    }

    public String getAppId() {
        return XMLUtil.getKeyValue("appId",getSource());
    }

    public String getAppSec() {
        return XMLUtil.getKeyValue("appSec",getSource());
    }

    public String getServerUrl() {
        return XMLUtil.getKeyValue("serverUrl",getSource());
    }

    public String getLcid() {
        return XMLUtil.getKeyValue("lcid",getSource());
    }

    public String getPwd() {
        return XMLUtil.getKeyValue("pwd",getSource());
    }

    public String getClassName() {
        return XMLUtil.deleteQuote(getStringAttribute("class"));
    }

}
