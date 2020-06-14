package com.github.jspxnet.component.qq;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.OAuthOpenIdType;
import com.github.jspxnet.enums.SexEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.txweb.table.OAuthOpenId;
import com.github.jspxnet.txweb.util.MemberUtil;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@HttpMethod(caption = "QQ登陆接口")
public class QqAfterLoginRedirectAction extends ActionSupport {


    @Ref
    protected OnlineManager onlineManager;

    @Ref
    private MemberDAO memberDAO;


    /**
     * 微信，QQ等第三方平台验证登陆
     *
     * @param openIdTable openid表
     * @param ip          ip地址
     * @return openId方式登录
     * @throws Exception 异常
     */
    public JSONObject openIdLogin(OAuthOpenId openIdTable, String ip) throws Exception {
        JSONObject resultInfo = new JSONObject();
        resultInfo.put(Environment.SUCCESS, 0);
        if (StringUtil.isNull(openIdTable.getNamespace())) {
            resultInfo.put(Environment.message, "命名空间不能为空");
            return resultInfo;
        }

        long uid;
        OAuthOpenId oAuthOpenId = memberDAO.getOAuthOpenId(openIdTable.getNamespace(), openIdTable.getOpenId());
        if (oAuthOpenId == null) {
            //第一次登陆进来,自动创建一个用户
            if (isGuest()) {
                Member member = openIdTable.createMember();
                if (StringUtil.isNull(member.getName())) {
                    resultInfo.put(Environment.message, "微信登陆方式不支持创建用户,请先创建用户后在绑定微信");
                    return resultInfo;
                }
                member.setPassword(MemberUtil.createPasswordSaveFormat(RandomUtil.getRandomAlphanumeric(8),RandomUtil.getRandomNumberGUID(8)));
                member.setPutName(openIdTable.getNamespace()); //通过这里识别来源平台

                member.setPutUid(Environment.SYSTEM_ID);

                //判断用户是否重名
                Member checkMember = memberDAO.getForName(member.getName());
                if (checkMember != null) {
                    member.setName(memberDAO.createName(member.getName()));

                }
                if (memberDAO.save(member) < 0) {
                    resultInfo.put(Environment.message, "第三方登陆创建用户信息失败");
                    return resultInfo;
                }

                openIdTable.setUid(member.getId());
                openIdTable.setPutName(OAuthOpenIdType.weiXin);
                openIdTable.setPutUid(Environment.SYSTEM_ID);
            } else {
                IUserSession userSession = getUserSession();

                if (StringUtil.isNull(userSession.getName())) {
                    resultInfo.put(Environment.message, "微信登陆方式不支持创建用户,请先创建用户后在绑定微信");
                    return resultInfo;
                }
                openIdTable.setUid(userSession.getUid());
                openIdTable.setPutName(OAuthOpenIdType.weiXin);
                openIdTable.setPutUid(Environment.SYSTEM_ID);
            }

            if (memberDAO.save(openIdTable) < 0) {
                resultInfo.put(Environment.message, "第三方登陆数据保存失败");
                return resultInfo;
            }
            uid = openIdTable.getUid();
        } else {
            //已经存在的用户
            if (!oAuthOpenId.getAccessToken().equalsIgnoreCase(openIdTable.getAccessToken())) {
                oAuthOpenId.setAccessToken(openIdTable.getAccessToken());
                memberDAO.update(oAuthOpenId, new String[]{"accessToken"});
            }
            uid = oAuthOpenId.getUid();
        }

        if (uid <= 0) {
            resultInfo.put(Environment.message, "保存登陆信息失败");
            return resultInfo;
        }

        Member member = memberDAO.getForId(uid);
        if (member == null) {
            resultInfo.put(Environment.message, "用户信息不匹配");
            return resultInfo;
        }
        session.setAttribute("uid", member.getId());


        return onlineManager.login(session, member.getName(), MemberUtil.getPasswordHash(member.getPassword()),  openIdTable.getNamespace(), ip);
    }


    @Override
    public String execute() throws Exception {

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken, openID;
            long tokenExpireIn = 0L;


            if ("".equals(accessTokenObj.getAccessToken())) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                printError("没有获取到响应参数", 403);
                return NONE;
            } else {

                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

                session.setAttribute("qq_tokenExpireIn", String.valueOf(tokenExpireIn));
                //  request.getSession().setAttribute("demo_access_token", accessToken);
                //  request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj = new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                // 利用获取到的accessToken 去获取当前用户的openid --------- end
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                if (userInfoBean.getRet() == 0) {
                    OAuthOpenId newOAuthOpenId = new OAuthOpenId();
                    newOAuthOpenId.setName(userInfoBean.getNickname());
                    newOAuthOpenId.setSex(SexEnumType.valueOf(userInfoBean.getGender()).getName());
                    newOAuthOpenId.setOpenId(openID);
                    newOAuthOpenId.setUnionid("");
                    newOAuthOpenId.setFaceImage(userInfoBean.getAvatar().getAvatarURL100());
                    newOAuthOpenId.setNamespace(OAuthOpenIdType.qq);
                    newOAuthOpenId.setAccessToken(accessToken);
                    JSONObject jsonobject = openIdLogin(newOAuthOpenId, RequestUtil.getRemoteAddr(request));
                    if (jsonobject.getBoolean(Environment.SUCCESS)) {
                        return SUCCESS;
                    } else {
                        printError(jsonobject.getString(Environment.message), 403);
                    }
                } else {
                    printError("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg(), 403);
                }
            }
        } catch (QQConnectException e) {
            log.error("QQ登陆验证失败", e);
        }
        return NONE;
    }
}
