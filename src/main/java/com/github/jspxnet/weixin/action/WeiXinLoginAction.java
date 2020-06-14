package com.github.jspxnet.weixin.action;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.weixin.WeiXinManager;
import com.github.jspxnet.weixin.env.WeiXinEnv;
import org.weixin4j.Weixin;
import org.weixin4j.component.SnsComponent;

/**
 * 不会联动系统登陆信息，主要是构造微信的登陆连接请求
 * 请求后得到code
 */
@HttpMethod(caption = "微信验证请求")
public class WeiXinLoginAction extends ActionSupport {

    private final static String authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
    //这个地址为  WeiXinOAuth2Action 的路径
    //这个地址发送给微信,微信将数据传输到这个地址上
    private String redirectUrl = StringUtil.empty;

    @Param(request = false)
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    // snsapi_base 只能获取用户openid,snsapi_userinfo 可以得到用户信息
    private String scope = "snsapi_userinfo"; //snsapi_userinfo

    @Param(request = false)
    public void setScope(String scope) {
        this.scope = scope;
    }

    //如果用户已经登陆到，就直接跳转到这里地址
    private String loginUrl = StringUtil.empty;

    @Param(request = false)
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    private boolean loginJump = false;

    @Param(request = false)
    public void setLoginJump(boolean loginJump) {
        this.loginJump = loginJump;
    }


    @Ref(namespace = Sioc.global)
    protected OnlineManager onlineManager;

    @Override
    public String execute() throws Exception {
        UserSession userSession = onlineManager.getUserSession(this);
        if (loginJump && userSession != null && !userSession.isGuest()) {
            if (StringUtil.isNull(userSession.getPhone())) {
                return WeiXinOAuth2Action.PAGE_PHONE_VERIFY;
            } else {
                response.sendRedirect(loginUrl);
            }
        } else {
            Weixin weinxin = WeiXinManager.getWeinXin();

            SnsComponent snsComponent = weinxin.sns(authorize_url);
            String state = RandomUtil.getRandomAlphanumeric(5);
            //为了防止攻击 state 为随机数，接受回来验证

            session.setAttribute(WeiXinEnv.weiXinState, state);
            session.setAttribute(WeiXinEnv.weiXinScope, scope);
            //以后优化用,现在先不动
            //WeiXinUtil.putCacheWeiXinState(userSession.getId(),state,scope);

            String url = snsComponent.getOAuth2CodeUrl(redirectUrl, scope, state);
            response.sendRedirect(url);
        }
        return NONE;
    }

}
