/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.util.StringMap;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 12-12-15
 * Time: 下午2:04
 * 认证挂接接口
 */
public abstract class AuthenticationView extends ActionSupport {
    public static final String KEY_loginField = "field";
    public static final String KEY_loginId = "loginId";
    public static final String KEY_timeMillis = "timeMillis";
    public static final String KEY_password = "password";
    public static final String KEY_publicKey = "publicKey";
    public static final String KEY_privateKey = "privateKey";


    /**
     * 创建校验码
     *
     * @param field      登陆字段类型
     * @param loginId    用户id
     * @param password   密码
     * @param publicKey  公密
     * @param privateKey 私密
     * @param timeMillis 时间
     * @return 创建校验码
     */
    public static String getMakeVerify(String field, String loginId, String password, String publicKey, String privateKey, long timeMillis) {
        StringMap<String, Object> postParameter = new StringMap<>();
        postParameter.put(KEY_loginField, field);
        postParameter.put(KEY_loginId, loginId);
        postParameter.put(KEY_timeMillis, Long.toString(timeMillis));
        postParameter.put(KEY_password, password);
        postParameter.put(KEY_publicKey, publicKey);
        postParameter.put(KEY_privateKey, privateKey);
        postParameter.sortByKey(true);
        return EncryptUtil.getMd5(postParameter.toString());
    }


    /**
     * @param field      字段名称
     * @param loginId    登陆识别id
     * @param password   密码
     * @param timeMillis 记忆有效期
     * @param verify     验证码
     * @return 创建参数json
     */
    public static JSONArray createPostParameter(String field, String loginId, String password, long timeMillis, String verify) {
        JSONArray param = new JSONArray();
        param.add(0, field);
        param.add(1, loginId);
        param.add(2, password);
        param.add(3, timeMillis);
        param.add(4, verify);
        return param;
    }

/*    @Operate(caption = "得到自己登陆后的信息")
    public MemberVo getMember() {
        UserSession userSession = onlineManager.getUserSession(this);
        if (userSession == null || userSession.isGuest()) {
            return new MemberVo();
        }
        return BeanUtil.copy(userSession, MemberVo.class);
    }*/

}