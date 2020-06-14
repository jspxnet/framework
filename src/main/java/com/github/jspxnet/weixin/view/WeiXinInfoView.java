package com.github.jspxnet.weixin.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.weixin.env.WeiXinEnv;
import com.github.jspxnet.weixin.table.WeiXinUser;

/**
 * 根据授权方式，得到的数据是不同的
 */
@HttpMethod(caption = "得到用户信息")
public class WeiXinInfoView extends ActionSupport {

    public String getOpenId() {
        return (String) session.getAttribute(WeiXinEnv.weiXinOpenId);
    }


    public WeiXinUser getWeiXinUser() {
        return (WeiXinUser) session.getAttribute(WeiXinEnv.weiXinSnsUser);
    }
}
