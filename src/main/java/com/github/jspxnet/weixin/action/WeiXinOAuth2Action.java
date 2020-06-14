package com.github.jspxnet.weixin.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.boot.sign.OAuthOpenIdType;
import com.github.jspxnet.enums.SexEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.txweb.table.OAuthOpenId;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.util.MemberUtil;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.weixin.WeiXinManager;
import com.github.jspxnet.weixin.env.WeiXinEnv;
import com.github.jspxnet.weixin.table.WeiXinUser;
import com.github.jspxnet.weixin.util.WeiXinUtil;
import lombok.extern.slf4j.Slf4j;
import org.weixin4j.Weixin;
import org.weixin4j.model.sns.SnsUser;


/**
 * Created by ChenYuan on 2017/8/8.
 * 联动到系统登陆信息
 */
@Slf4j
@HttpMethod(caption = "微信授权接口")
public class WeiXinOAuth2Action extends ActionSupport {

    final static private String KEY_CODE = "code";
    final static private String KEY_STATE = "state";
    final static public String PAGE_PHONE_VERIFY = "phoneVerify";

    final static private String KEY_SMS_PHOBNE = "smsPhobne";

    @Ref
    private OnlineManager onlineManager;

    @Ref
    private MemberDAO memberDAO;

    private static OAuthOpenId createAuthOpenId(WeiXinUser weiXinUser) {
        OAuthOpenId newAuthOpenId = new OAuthOpenId();
        newAuthOpenId.setName(weiXinUser.getNickname());
        newAuthOpenId.setSex(SexEnumType.find(weiXinUser.getSex()).getName());
        newAuthOpenId.setOpenId(weiXinUser.getOpenid());
        newAuthOpenId.setUnionid(weiXinUser.getUnionid());
        newAuthOpenId.setFaceImage(weiXinUser.getHeadimgurl());
        newAuthOpenId.setNamespace(OAuthOpenIdType.weiXin);
        newAuthOpenId.setAccessToken(weiXinUser.getAccessToken());
        return newAuthOpenId;

    }

    @Operate(caption = "账号绑定微信")
    public void bind() throws Exception {
        UserSession userSession = onlineManager.getUserSession(this);
        if (userSession == null || userSession.isGuest()) {
            addFieldInfo(Environment.warningInfo, "登录后才能绑定");
            return;
        }

        WeiXinUser weiXinUser = (WeiXinUser) session.getAttribute(WeiXinEnv.weiXinSnsUser);
        if (weiXinUser != null && !StringUtil.isNull(weiXinUser.getOpenid())) {
            OAuthOpenId newAuthOpenId = createAuthOpenId(weiXinUser);
            if (memberDAO.save(newAuthOpenId) >= 0) {
                //微信账号绑定本地账号成功
                setActionResult(SUCCESS);
            } else {
                setActionResult(ERROR);
            }
        }
    }


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
            Member member = openIdTable.createMember();
            if (StringUtil.isNull(member.getName())) {
                resultInfo.put(Environment.message, "微信登陆方式不支持创建用户,请先创建用户后在绑定微信");
                return resultInfo;
            }
            member.setPassword(MemberUtil.createPasswordSaveFormat(RandomUtil.getRandomAlphanumeric(8),RandomUtil.getRandomGUID(8)));
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

        //验证是否有手机号
        if (StringUtil.isNull(member.getPhone())) {
            resultInfo.put(KEY_SMS_PHOBNE, PAGE_PHONE_VERIFY);
            return resultInfo;
        }

        return onlineManager.login(session, member.getName(), MemberUtil.getPasswordHash(member.getPassword()),  openIdTable.getNamespace(), ip);
    }


    @Override
    public String execute() throws Exception {
        String code = getString(KEY_CODE);
        if (StringUtil.isNull(code)) {
            return ERROR;
        }

        String state = getString(KEY_STATE);

       // WeiXinUtil.getCacheWeiXinScope(state);

        String weiXinState = (String) session.getAttribute(WeiXinEnv.weiXinState);
        if (weiXinState == null || !weiXinState.startsWith(state)) {
            //这里是微信提供的一种验证，防止跨域跳转攻击
            printError("state 验证失败", HttpStatusType.HTTP_status_500);
            log.info("session state:" + weiXinState + ",request state:" + state);
            return NONE;
        }
        WeiXinUser weiXinUser = (WeiXinUser) session.getAttribute(WeiXinEnv.weiXinSnsUser);
        if (weiXinUser != null && !StringUtil.isNull(weiXinUser.getOpenid())) {
            OAuthOpenId newAuthOpenId = createAuthOpenId(weiXinUser);
            JSONObject jsonobject = openIdLogin(newAuthOpenId, RequestUtil.getRemoteAddr(request));
            if (jsonobject.getString(KEY_SMS_PHOBNE).equals(PAGE_PHONE_VERIFY)) {
                return PAGE_PHONE_VERIFY;
            }

            if (jsonobject.getBoolean(Environment.SUCCESS)) {
                //todo check
                //IUserSession userSession = onlineManager.getUserSession(this);//有值说明登录成功
                //session.setAttribute(ActionSupport.Key_UserSession, userSession);
                //微信账号绑定本地账号成功
                return SUCCESS;
            } else {
                printError(jsonobject.getString(Environment.message), 403);
                return NONE;
            }
        }

        Weixin weiXin = WeiXinManager.getWeinXin();
        String accessToken = weiXin.getToken().getAccess_token();
        if (WeiXinEnv.snsapi_userinfo.equalsIgnoreCase((String) session.getAttribute(WeiXinEnv.weiXinScope))) {
            //这里能得到用户的 信息
            SnsUser snsUser = weiXin.sns().getSnsUserByCode(code);
            if (snsUser != null) {
                weiXinUser = WeiXinUtil.toWeiXinUser(snsUser);

                session.setAttribute(WeiXinEnv.weiXinSnsUser, weiXinUser);
                session.setAttribute(WeiXinEnv.weiXinOpenId, weiXinUser.getOpenid());
                //-------------------------------------
                //如果用户不是游客，就绑定当前已经登陆的用户信息
                //绑定登陆微信

                OAuthOpenId newAuthOpenId = createAuthOpenId(weiXinUser);
                JSONObject jsonobject = openIdLogin(newAuthOpenId, RequestUtil.getRemoteAddr(request)); //true
                if (jsonobject.getString(KEY_SMS_PHOBNE).equals(PAGE_PHONE_VERIFY)) {
                    //("跳转到手机验证界面-------------------------------");
                    return PAGE_PHONE_VERIFY;
                }

                if (jsonobject.getBoolean(Environment.SUCCESS)) {
                    //微信账号绑定本地账号成功
                    return SUCCESS;
                } else {
                    printError(jsonobject.getString(Environment.message), 403);
                    return NONE;
                }
            }
        } else {
            //这里只能得到openid不能够得到用户信息
            String openId = weiXin.sns().getOpenId(code);
            session.setAttribute(WeiXinEnv.weiXinOpenId, openId);
            //绑定登陆微信
            OAuthOpenId newAuthOpenId = new OAuthOpenId();
            newAuthOpenId.setOpenId(openId);
            newAuthOpenId.setNamespace(OAuthOpenIdType.weiXin);
            newAuthOpenId.setAccessToken(accessToken);
            JSONObject jsonobject = openIdLogin(newAuthOpenId, RequestUtil.getRemoteAddr(request)); //false

            if (jsonobject.getString(KEY_SMS_PHOBNE).equals(PAGE_PHONE_VERIFY)) {
                //跳转到手机验证界面
                return PAGE_PHONE_VERIFY;
            }
            if (jsonobject.getBoolean(Environment.SUCCESS)) {
                return SUCCESS;
            } else {
                printError(jsonobject.getString(Environment.message), 403);
                return NONE;
            }
        }

        //跳转到界面
        return SUCCESS;
    }
}
