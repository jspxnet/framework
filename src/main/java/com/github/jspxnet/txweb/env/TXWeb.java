/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.env;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-26
 * Time: 15:05:29
 */
public abstract class TXWeb {
    private TXWeb() {

    }

    final public static String global = "global";

    final public static String jspxSmsValidate = "jspx_sms_validate";
    final public static String jspxSmsValidateTime = "jspx_sms_validate_time";

    //final public static String jspxValidate = "jspx_validate";
    //final public static String jspxValidateTime = "jspx_validate_Time";

    //-------------------------------------
    final public static String GET = "GET";
    final public static String PUT = "PUT";
    final public static String DELETE = "DELETE";
    final public static String POST = "POST";
    //------------------------------------
    //应用部分
    final public static String CONFIG_INCLUDE = "include";
    final public static String CONFIG_PACKAGE = "package";
    final public static String CONFIG_FILE = "file";
    final public static String CONFIG_NAMESPACE = "namespace";
    final public static String CONFIG_EXTENDS = "extends";
    final public static String CONFIG_DEFAULT = "default";
    final public static String CONFIG_NAME = "name";
    final public static String CONFIG_STATUS = "status";

    final public static String CONFIG_CAPTION = "caption";
    final public static String CONFIG_CLASS = "class";
    final public static String CONFIG_METHOD = "method";
    final public static String CONFIG_PARAM = "param";

    final public static String CONFIG_TEMPLATE = "template";
    final public static String CONFIG_RESULT = "result";
    final public static String CONFIG_TYPE = "type";
    final public static String CONFIG_ACTION = "action";
    final public static String CONFIG_SCAN = "scan";
    final public static String CONFIG_SECRET = "secret";
    final public static String CONFIG_MOBILE = "mobile";


    final public static String CONFIG_INTERCEPTOR_REF = "interceptor-ref";
    final public static String CONFIG_INTERCEPT_URL = "intercept-url";
    //------------------------------------


    final static public String CONFIG_EVASIVE = "evasive";
    final static public String EVASIVE_INTERVAL = "interval";
    final static public String EVASIVE_MAX_TIMES = "maxTimes";
    final static public String EVASIVE_URL = "url";
    final static public String EVASIVE_CACHE_NAME = "cacheName";
    final static public String EVASIVE_IMPRISON_SECOND = "imprisonSecond";
    final public static String EVASIVE_CONDITION = "condition";
    final public static String EVASIVE_WHITELIST = "whiteList";
    final public static String EVASIVE_BlackList = "blackList";

    final public static String EVASIVE_blackSize = "blackSize";

    final public static String EVASIVE_ipField = "ipField";
    final public static String EVASIVE_timesField = "timesField";
    final public static String EVASIVE_minTimes = "minTimes";


    final public static String none = "none";

    final public static String httpPOST = "POST";

    final public static String httpGET = "GET";
    //积分计算公式

    //全局保存一个登录时候的sessionID,这样与他服务器登录只需要这个ID就标识已经登录
    //OOS 方式 注意大小写
    //final static public String sessionId = "sessionId";

    final static public String token = "token";
    //机构ID
    final static public String organizeId = "organizeId";

    final static public String ticketName = "ticketName";

    //浏览器默认会保存一个sessionID
    final static public String CookieSessionId = "JSESSIONID";

    static public final String COOKIE_TICKET = "jspxCookieTicket"; //加密

    //全局高速唯一变量  放在config中,因为有缓存,会不稳定 begin
    static public String publicKey = StringUtil.empty;
    static public long publicKeyCreateTimeMillis = 0;
    //全局高速唯一变量  end
    //-----------------------------



}