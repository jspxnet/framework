/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.LoginField;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.ValidateCodeCache;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.view.AuthenticationView;
import com.github.jspxnet.utils.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 12-12-15
 * Time: 上午10:43
 * 远程登陆接口
 *
 */
@HttpMethod(caption = "认证登陆接口")
public class AuthenticationAction extends AuthenticationView {

    public AuthenticationAction() {

    }

    @Ref
    protected PermissionDAO permissionDAO;

    @Ref
    protected ValidateCodeCache validateCodeCache;

    //保存时间
    private int cookieDate = DateUtil.HOUR * 24;

    @Param(caption = "cookie有效时间", message = "cookie有效时间")
    public void setCookieDate(int cookieDate) {
        this.cookieDate = cookieDate;
    }

    @Operate(caption = "手机验证码登录",method = "phonelogin")
    public RocResponse<JSONObject> phoneLogin(@Param(caption = "手机号", required = true, max = 15 ,message = "错误的手机号") String mobile,
                                  @Param(caption = "验证码",max = 10) String validate)  {
        int loginTimes = validateCodeCache.getTimes(mobile);
        if (loginTimes > 10) {
            return RocResponse.error(ErrorEnumType.CONGEAL.getValue(), language.getLang(LanguageRes.validationTimesFailure));
        }

        if (!validateCodeCache.validateSms(mobile, validate)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.validationFailure));
        }
        Map<String, String> loginInfo = null;
        try {
            loginInfo = onlineManager.login(this, LoginField.Sms, mobile, onlineManager.getGuiPassword(),  cookieDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (loginInfo==null||!loginInfo.isEmpty()) {
            RocResponse<JSONObject> rocResponse = RocResponse.error(ErrorEnumType.WARN.getValue(), loginInfo);
            JSONObject json = new JSONObject();
            json.put(Environment.LOGIN_TIMES, loginTimes);
            rocResponse.setData(json);
            return rocResponse;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserSession userSession = onlineManager.getUserSession(this);
        if (userSession == null || userSession.isGuest()) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.validationFailure));
        }
        JSONObject json = new JSONObject();
        json.put(TXWeb.token, userSession.getId());
        json.put(Environment.USER_SESSION, userSession);
        json.put(Environment.message, language.getLang(LanguageRes.loginSuccess));
        return RocResponse.success(json);
    }

    @Operate(caption = "传统方式Ajax登陆")
    public RocResponse<JSONObject> login(
            @Param(caption = "用户名类型",max = 64) String field,
            @Param(caption = "用户名", required = true,max = 64 , message = "用户名必须填写") String loginId,
            @Param(caption = "密码", required = true, max = 64 , message = "密码必须填写") String password,
            @Param(caption = "验证码",max = 20) String validate) throws Exception {
        IUserSession userSession = getUserSession();
        int loginTimes = validateCodeCache.getTimes(EncryptUtil.getMd5(userSession.getId()));
        if (loginTimes > 10) {
            return RocResponse.error(ErrorEnumType.CONGEAL.getValue(), language.getLang(LanguageRes.validationTimesFailure));
        }
        if (loginTimes > 3 && !validateCodeCache.validateImg(EncryptUtil.getMd5(userSession.getId()), validate)) {
            RocResponse<JSONObject> rocResponse = RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.validationFailure));
            JSONObject json = new JSONObject();
            json.put(Environment.LOGIN_TIMES, loginTimes);
            rocResponse.setData(json);
            return rocResponse;
        }

        Map<String, String> loginInfo = onlineManager.login(this, field, loginId, password, cookieDate);
        if (!loginInfo.isEmpty()) {

            validateCodeCache.updateTimes(EncryptUtil.getMd5(userSession.getId()));
            RocResponse<JSONObject> rocResponse = RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), loginInfo);
            JSONObject json = new JSONObject();
            json.put(Environment.LOGIN_TIMES, loginTimes);
            rocResponse.setData(json);
            return rocResponse;
        }
        Thread.sleep(500);
        userSession = onlineManager.getUserSession(this);
        if (userSession == null || userSession.isGuest()) {
            return RocResponse.error(ErrorEnumType.APPLICATION.getValue(), language.getLang(LanguageRes.loginFailure));
        }

        JSONObject json = new JSONObject();
        json.put(TXWeb.token, userSession.getId());
        json.put(Environment.USER_SESSION, userSession);
        json.put(Environment.message, language.getLang(LanguageRes.loginSuccess));
        return RocResponse.success(json);
    }


    /**
     * 绑定账号登陆
     * 这个接口和上边的区别就是不会使用验证码,直接的加密验证
     * @param field 登陆命名类型
     * @param loginId 用户登陆名称
     * @param password 密码
     * @param timeMillis 当前时间搓
     * @param verify 签名验证  签名算法不包含 loginName
     * @param loginName 登陆名称
     * @param cookieSecond 有效期,单位秒
     * @return 得到登陆session
     */

    @Operate(caption = "远程登录接口",method = "remotelogin")
    public RocResponse<?> remoteLogin(
            @Param(caption = "用户名类型", required = true,max = 64, message = "用户名必须填写") String field,
            @Param(caption = "用户名", required = true,max = 64, message = "用户名必须填写") String loginId,
            @Param(caption = "密码", required = true,max = 64,  message = "密码必须填写") String password,
            @Param(caption = "时间", required = true, message = "时间必须填写") long timeMillis,
            @Param(caption = "校验码", required = true,max = 10,  message = "校验码必须填写") String verify,
            @Param(caption = "绑定登陆的用户名", max = 64,  message = "绑定登陆的用户名") String loginName,
            @Param(caption = "有效期单位秒", max = 100000,value = "0", message = "有效期单位秒") int cookieSecond

    )  {

        if (!config.getBoolean(Environment.userRemoteLogin)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.interfaceClosed));
        }
        //此验证码为参数校验

        if (StringUtil.isNull(verify)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.notInputValidate));
        }

        //公密,通过下边的公密接口得到
        String publicKey = (String)JSCacheManager.get(DefaultCache.class,TXWeb.APP_PUBLIC_KEY);
        //此验证码为参数校验

        String makeVerify = getMakeVerify(field, loginId, password, publicKey, config.getString(Environment.privateKey), timeMillis);
        //getMakeVerify 必须先设置参数
        if (!verify.equals(makeVerify)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.validationFailure));
        }

        long verifySecond = System.currentTimeMillis() - timeMillis;
        if (Math.abs(verifySecond) > DateUtil.MINUTE * 5) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.authenticationTimeOut) + "," + verifySecond);
        }

        //判断IP范围
        String ip = getRemoteAddr();
        String authIpExpression= config.get(Environment.authIpExpression);
        if (!StringUtil.isNull(authIpExpression)&& !authIpExpression.equals(StringUtil.ASTERISK)&&!IpUtil.interiorly(config.get(Environment.authIpExpression), ip)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.notAllowedIpLimits));
        }

        UserSession userSession = onlineManager.getUserSession(this);
        int loginTimes = validateCodeCache.getTimes(EncryptUtil.getMd5(userSession.getId()));
        if (loginTimes > config.getInt(Environment.maxLoginTimes)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.validationFailureLimitTimes) + loginTimes);
        }

        Map<String, String> loginInfo = null;
        try {
            loginInfo = onlineManager.login(this, field, loginId, password,  cookieSecond);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!loginInfo.isEmpty()) {
            Iterator<String> iterator = loginInfo.keySet().iterator();
            if (iterator.hasNext()) {
                return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), loginInfo.get(iterator.next()));
            }
        }

        //--------------
        boolean isLogin = loginInfo.isEmpty();
        if (isLogin) {
            //已经登陆
            userSession = onlineManager.getUserSession(this);
            Role role = permissionDAO.getComposeRole(userSession.getUid());
            if (role==null||role.getUserType()!= UserEnumType.RESET_ADMIN.getValue())
            {
                //只允许 reset_admin登陆
                return RocResponse.error(ErrorEnumType.POWER);
            }

            //切换账号和用户信息
            try {
                onlineManager.exit(userSession.getId());
                loginInfo = onlineManager.login(this,LoginField.Name, loginName, onlineManager.getGuiPassword(),  cookieSecond);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!loginInfo.isEmpty()) {
                Iterator<String> iterator = loginInfo.keySet().iterator();
                if (iterator.hasNext()) {
                    return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), loginInfo.get(iterator.next()));
                }
            }
            userSession = onlineManager.getUserSession(this);
            JSONObject json = new JSONObject();
            json.put(TXWeb.token, userSession.getId());

            json.put(Environment.USER_SESSION, userSession);
            json.put(Environment.message, language.getLang(LanguageRes.loginSuccess));
            return RocResponse.success(json);
        } else {
            validateCodeCache.updateTimes(EncryptUtil.getMd5(userSession.getId()));
        }
        return RocResponse.error(ErrorEnumType.NO_DATA.getValue(), getFieldInfo());
    }


    /**
     * 远程登陆,第三方登陆必须先得到公密
     * @return 返回公密
     */
    @Operate(caption = "公密",method = "publickey")
    public RocResponse<String> publicKey()  {
        String publicKeyHost = StringUtil.trim(config.getString(Environment.publicKeyHost));
        if (!StringUtil.isNull(publicKeyHost)&&!IpUtil.interiorly(publicKeyHost, getRemoteAddr())) {
            return RocResponse.error(ErrorEnumType.CONFIG.getValue(), language.getLang(LanguageRes.notAllowedIpLimits));
        }
        String publicKey = (String)JSCacheManager.get(DefaultCache.class,TXWeb.APP_PUBLIC_KEY);
        if (StringUtil.isNull(publicKey)) {

            publicKey = EncryptUtil.getMd5(System.currentTimeMillis() + RandomUtil.getRandomNumeric(32));
            JSCacheManager.put(DefaultCache.class,TXWeb.APP_PUBLIC_KEY,publicKey);
        }
        return RocResponse.success(publicKey);
    }

    @Operate(caption = "判断在线", post = false,method = "checksession")
    public RocResponse<Integer> checkSession()
    {
        IUserSession userSession = onlineManager.getUserSession(this);
        if (userSession != null && !userSession.isGuest()) {
            return RocResponse.success(1);
        } else {
            return RocResponse.success(0);
        }
    }

    @Operate(caption = "退出", post = false)
    public void exit() throws Exception {
        onlineManager.exit(this);
        setActionResult(SUCCESS);
    }

    @Override
    public String execute() throws Exception {
        return ROC;
    }
}