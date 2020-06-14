/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.online.impl;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.queue.RedisStoreQueueClient;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.bundle.provider.PropertyProvider;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.LoginLog;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.util.MemberUtil;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.JWTUtil;
import com.github.jspxnet.util.LRUHashMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-8
 * Time: 17:11:07
 * <p>
 * 规则说明，登录后在将sessionId保存到 session里边，优先使用session中的变量 sessionId
 * 如果没有就检查请求中是否有参数,说明(上传的时候用到),
 * 最后检查cookie中的seesionId(要加密),满足跨域要求,从而实现单点登录
 */
@Slf4j
public class OnlineManagerImpl implements OnlineManager {

    final private static int DEFAULT_COOKIE_SECOND = 60 * 60 * 24;
    static private final EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
    //上一次清空超时时间，清空超时是全局性的，放这里比较合适
    //domain 设置你的域名不要www开始为'.',不设置将自动
    private String domain = StringUtil.empty;
    private Map<String,UserSession> onlineCache = null;
    //单点登录,一处登录，另外一处就下线
    private boolean sso = false;
    //允许哪些服务器采用sessionId直接注入登陆
    //配置格式  "(.*).gzcom.gov.cn|(.*).testaio.com"   判断成立的才允许载入
    private String allowServerName = StringUtil.ASTERISK;

    final private int UPDATE_SESSION_MINUTE = 20;
    /**
     * 设置一个通用密码，动态的，提供sms等方式，无密码登陆
     */
    private String guiPassword = RandomUtil.getRandomGUID(16);

    @Ref
    private RedisStoreQueueClient redisStoreQueueClient;

    public OnlineManagerImpl() {

    }

    @Override
    public String getGuiPassword() {
        return guiPassword;
    }

    @Param(request = false, caption = "允许的域名")
    public void setAllowServerName(String allowServerName) {
        this.allowServerName = allowServerName;
    }

    public boolean isSso() {
        return sso;
    }

    @Param(request = false, caption = "单点登录")
    public void setSso(boolean sso) {
        this.sso = sso;
    }

    /**
     * @param member 用户
     * @return 创建登录日志
     */
    static private LoginLog createLoginLog(Member member) {
        LoginLog loginLog = new LoginLog();
        loginLog.setPutUid(member.getId());
        loginLog.setPutName(member.getName());
        loginLog.setIp(member.getIp());
        return loginLog;
    }

    //验证token的安全级别,0 默认只验证签名,1:验证ip,2:验证uid
    private int verifyTokenLevel = 3;

    public void setVerifyTokenLevel(int verifyTokenLevel) {
        this.verifyTokenLevel = verifyTokenLevel;
    }


    static public void main(String[] arge) throws Exception {
        JspxNetApplication.autoRun();
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        String strM = encrypt.getEncode("f26c29d9b52364237351f5cb72e3c3f8f26c29d9b52364237351f5cb72e3c3f8");
        String sign = encrypt.sign(strM,EnvFactory.getHashAlgorithmKey());
        boolean verify = encrypt.verify(strM,EnvFactory.getHashAlgorithmKey(),sign);
        String str = encrypt.getDecode(strM);
        System.out.println(encrypt.getClass().getName()+"-------------sign="+ sign);
        System.out.println("-------------verify="+ verify);
        System.out.println("-------------strM="+ strM);
        System.out.println("-------------str="+ str);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain domain 设置你的域名不要www开始为'.',不设置将自动
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Ref
    private MemberDAO memberDAO;

    @Init
    @Override
    public void init()  {
        //如果redis缓存被关闭开始,开启onlineCache来保存数据
        if (!memberDAO.getSoberFactory().isUseCache())
        {
            onlineCache = new LRUHashMap<>(50);
        }
    }

    @Override
    public boolean isOnline(long uid) {
        return uid > 0 && memberDAO.isOnline(uid);
    }

    @Override
    public boolean isOnline(String sessionId) {
        return sessionId != null && memberDAO.isOnline(sessionId);
    }

    /**
     * 创建游客用户session
     * @return  游客用户session
     */
    @Override
    public UserSession createGuestUserSession() {
        ///////////////////判断是否有游客帐号，没有就创建一个
        UserSession userSession = new UserSession();
        userSession.setUid(ENV_TEMPLATE.getLong(Environment.guestId, 0));
        userSession.setName(ENV_TEMPLATE.getString(Environment.guestName, "游客"));
        userSession.setIp("127.0.0.1");
        userSession.setLastRequestTime(System.currentTimeMillis());
        userSession.setCreateDate(new Date());
        ///////////////////
        return userSession;
    }

    /**
     * @param uid 用户ID
     * @return 如果上次登录，和本次IP不同就提示
     */
    @Override
    public Map<String, String> getSafePrompt(long uid) {
        Map<String, String> safeInfo = new HashMap<>();
        if (uid <= 0) {
            return safeInfo;
        }
        if (memberDAO.getIpPrompt(uid)) {
            safeInfo.put(Environment.promptInfo, "上次登录的IP不一致,ip location check  login safe");
        }
        return safeInfo;
    }


    public static String getLoginType(String loginId) {
        String isId = ValidUtil.isMail(loginId) ? "mail" : null;
        if (StringUtil.isNull(isId)) {
            isId = ValidUtil.isMobile(loginId) ? "phone" : "";
        }
        if (StringUtil.isNull(isId)) {
            isId = "name";
        } else if (ValidUtil.isNumber(isId)) {
            isId = "uid";
        }
        return isId;
    }

    /**
     * 应用快捷登录
     *
     * @param loginId         登录ID
     * @param password        密码
     * @param client          客户端类型
     * @return 提供给应用程序方式登录
     * @throws Exception 异常
     */
    @Override
    public JSONObject login(HttpSession session, String loginId, String password,  String client, String ip) throws Exception {
        JSONObject resultInfo = new JSONObject();
        resultInfo.put(Environment.SUCCESS, YesNoEnumType.NO.getValue());
        if ((StringUtil.isNull(loginId) || StringUtil.getLength(loginId) < 4)) {
            resultInfo.put(Environment.message, "非法的用户名长度,error login name length");
            return resultInfo;
        }

        if (StringUtil.isNull(password) || password.length() < 6) {
            resultInfo.put(Environment.message, "密码不能少于6个字符,error password");
            return resultInfo;
        }

        if (StringUtil.isNull(client)) {
            resultInfo.put(Environment.message, "必须说明客户端类型");
            return resultInfo;
        }

        Member member = memberDAO.getMember(getLoginType(loginId), loginId);
        if (member == null) {
            resultInfo.put(Environment.message, "不存在的用户，not found user");
            memberDAO.evict(Member.class);
            return resultInfo;
        }


        if (!MemberUtil.verifyPassword(password,member.getPassword())) {
            resultInfo.put(Environment.message, "错误的登录ID或密码");
            return resultInfo;
        }

        //判断冻结 begin
        if (member.getCongealType() == CongealEnumType.YES_CONGEAL.getValue()) {
            resultInfo.put(Environment.message, "账号目前已经被冻结, user is congeal don't login");
            //邮件激活
            if (StringUtil.toBoolean(ENV_TEMPLATE.getString(Environment.mailActive))) {
                resultInfo.put(Environment.message, "还没有使用邮件激活, need mail transfer active");
            }
            return resultInfo;
        }

        if (resultInfo.has(Environment.message)) {
            return resultInfo;
        }
        //信息检查完成


        String token = null;
        if (session != null) {
            token =  JWTUtil.createToken(ip,member.getId()+"",session.getId());
        } else {
            token =  JWTUtil.createToken(ip,member.getId()+"",EncryptUtil.getHashEncode(member.getId() + member.getName() + client + EnvFactory.getHashAlgorithmKey(), EnvFactory.getHashAlgorithm()));
        }

        //更新用户信息 begin
        member.setLoginTimes(member.getLoginTimes() + 1);
        member.setLoginDate(new Date());
        member.setIp(ip);
        //更新用户信息 end

        //创建session信息更新 begin
        UserSession userSession = BeanUtil.copy(member,UserSession.class);
        userSession.setId(token);
        userSession.setUid(member.getId());
        userSession.setInvisible(0);
        //创建session信息更新 end

        if (!StringUtil.isNull(token)) {
            //SSO  单点登录
            if (sso) {
                memberDAO.deleteSession(userSession.getId(), userSession.getUid());
            }
            memberDAO.save(userSession);
        }
        memberDAO.update(member, new String[]{"loginTimes", "loginDate", "ip"});


        //保存登录日志 begin
        LoginLog loginLog = createLoginLog(member);
        if (client.contains(StringUtil.COLON)) {
            loginLog.setAppId(StringUtil.toLong(StringUtil.substringAfter(client, StringUtil.COLON)));
        }
        loginLog.setClient(client);
        loginLog.setUrl(Environment.unknown);
        if (client.contains(StringUtil.COLON)) {
            loginLog.setSystem(StringUtil.substringBefore(client, StringUtil.COLON));
        } else {
            loginLog.setSystem(client);
        }
        if (session!=null)
        {
            loginLog.setSessionId(session.getId());
        }
        loginLog.setToken(token);
        loginLog.setLoginTimes(member.getLoginTimes());
        loginLog.setIp(ip);

        memberDAO.save(loginLog);
        //保存登录日志 end

        if (session != null) {
            session.setMaxInactiveInterval(DateUtil.HOUR * 24);
        }
        resultInfo.put(Environment.SUCCESS, YesNoEnumType.YES.getValue());
        resultInfo.put(TXWeb.token, token);

        //在线信息保存到缓存中
        updateUserSessionCache(userSession);
        return resultInfo;
    }

    /**
     * 判断是否可以登录
     *
     * @param loginId         登录id
     * @param password        密码
     * @return 判断是否可以登录
     */
    @Override
    public Map<String, String> login(ActionSupport action, String isId, String loginId, String password, int cookieSecond) throws Exception {
        String lan = RequestUtil.getLanguage(action.getRequest());
        Bundle language = action.getLanguage();
        if (language==null)
        {
            PropertyProvider lang = new PropertyProvider();
            lang.setNamespace(Environment.language);
            lang.setDataType(lan);
            lang.loadMap();
            language = lang;
        }

        Map<String, String> errorInfo = new HashMap<String, String>();
        if (StringUtil.getLength(loginId) < 3) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorLoginName));
            return errorInfo;
        }
        //tomcat6 bug 修复, 这里如果是中文，很可能接收到的为 编码后的字符串，需要判断解码
        if (loginId.length() > 5 && loginId.startsWith("%") && StringUtil.countMatches(loginId, "%") > 6) {
            loginId = URLUtil.getURLDecoder(loginId, Environment.defaultEncode);
        }
        if (StringUtil.isNull(password) || password.length() < 4) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorPassword));
            return errorInfo;
        }

        final HttpSession session = action.getSession();
        Member member = null;
        //短信方式登录
        if (com.github.jspxnet.boot.sign.LoginField.Sms.equalsIgnoreCase(isId)) {
            member = memberDAO.getMember(com.github.jspxnet.boot.sign.LoginField.Phone, loginId);
            if (member == null) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.noFoundUser));
                memberDAO.evict(Member.class);
                return errorInfo;
            }
            if (!password.equalsIgnoreCase(guiPassword)) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorSmsValid));
                return errorInfo;
            }
        } else {
            if (StringUtil.isNull(isId)) {
                isId = getLoginType(loginId);
            }
            member = memberDAO.getMember(isId, loginId);
            if (member == null) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.noFoundUser));
                memberDAO.evict(Member.class);
                return errorInfo;
            }

            if (!MemberUtil.verifyPassword(password,member.getPassword())) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorNameOrPassword));
                return errorInfo;
            }
        }

        //判断冻结 begin
        if (member.getCongealType() == CongealEnumType.YES_CONGEAL.getValue()) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.alreadyCongealType));
            //邮件激活
            if (StringUtil.toBoolean(ENV_TEMPLATE.getString(Environment.mailActive))) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.needMailActive));
            }
            return errorInfo;
        }

        //信息检查完成
        HttpServletRequest request = action.getRequest();
        String token = JWTUtil.createToken(action.getRemoteAddr(),member.getId()+"", session.getId());
        //创建session信息更新 begin
        UserSession userSession = BeanUtil.copy(member,UserSession.class);
        userSession.setId(token);
        userSession.setUid(member.getId());
        userSession.setInvisible(0);
        //创建session信息更新 end

        if (cookieSecond <= 0) {
            cookieSecond = DEFAULT_COOKIE_SECOND;
        }
        session.setMaxInactiveInterval(cookieSecond * DateUtil.SECOND);

        //单点登录，就删除其他的登录信息
        if (sso) {
            memberDAO.saveOrUpdate(userSession);
        } else {
            memberDAO.update(userSession);
        }
        //如果有必要，这里加入同步 end

        //用户信息更新 begin
        member.setLoginTimes(member.getLoginTimes() + 1);
        member.setLoginDate(new Date());
        member.setIp(RequestUtil.getRemoteAddr(request));
        memberDAO.update(member, new String[]{"loginTimes", "loginDate", "ip"});
        //更新用户信息 end
        memberDAO.evictLoad(UserSession.class,"id",userSession.getId());

        //保存登录日志 begin
        LoginLog loginLog = createLoginLog(member);
        loginLog.setToken(token);
        loginLog.setClient("web");
        loginLog.setLoginTimes(member.getLoginTimes());
        loginLog.setSessionId(session.getId());
        loginLog.setUrl(request.getRequestURL().toString());
        loginLog.setSystem(RequestUtil.getSystem(request));
        loginLog.setBrowser(RequestUtil.getBrowser(request));
        loginLog.setIp(RequestUtil.getRemoteAddr(request));
        //用户session信息更新 end
        if (!StringUtil.isNull(token))
        {
            memberDAO.save(loginLog);
        }
        //保存登录日志 end

        //在线信息保存到缓存中
        updateUserSessionCache(userSession);
        //登录的时候作为验证

        session.setAttribute(TXWeb.token,userSession.getId());
        setCookieTicket(request, action.getResponse(), userSession.getId(), cookieSecond);
        return errorInfo;
    }

    @Override
    public void exit(ActionSupport action) {
        HttpServletResponse response = action.getResponse();
        if (response != null) {
            CookieUtil.cookieClear(action.getRequest(), response);
        }
        SessionUtil.cleanAll(action.getRequest());
        UserSession userSession = action.getUserSession();
        if (userSession!=null)
        {
            exit(userSession.getId());
        }
    }

    @Override
    public void exit(String sessionId) {
        if (memberDAO.deleteSession(sessionId, -1))
        {
            JSCacheManager.remove(UserSession.class,SoberUtil.getLoadKey(UserSession.class, "id", sessionId, false));
            memberDAO.deleteOvertimeSession(DateUtil.MINUTE * UPDATE_SESSION_MINUTE);
        }
    }

    /**
     *
     * @param token 认证Token
     * @param ip ip
     * @return  得到用户session
     */
    @Override
    public UserSession getUserSession(String token, String ip)
    {
        return getUserSession( token,ip,0);
    }
    /**
     * @param token 认证Token
     * @return 得到用户session
     */
    @Override
    public UserSession getUserSession(String token,String ip,long uid)
    {
        //验证token
        if (uid>0&&!JWTUtil.tokenVerify(token,verifyTokenLevel>0?ip:null,verifyTokenLevel>1?uid:0))
        {
            UserSession userSession = createGuestUserSession();
            userSession.setId(token);
            userSession.setName("非法进入者");
            return userSession;
        }

        UserSession userSession = null;
        if (onlineCache==null)
        {
            userSession = memberDAO.load(UserSession.class,token,false);
        }
        else
        {
            userSession = onlineCache.get(token);
            if (userSession==null)
            {
                userSession = memberDAO.get(UserSession.class,token,false);
            }
        }

        if (userSession==null||StringUtil.isEmpty(userSession.getId()))
        {
            userSession = memberDAO.getUserSession(token);
            if (StringUtil.isNull(userSession.getId()))
            {
                userSession = createGuestUserSession();
                userSession.setId(token);
                updateUserSessionCache(userSession);
                return userSession;
            }

            //验证token
            if (verifyTokenLevel>1&&!JWTUtil.tokenVerify(token,null,userSession.getUid()))
            {
                userSession = createGuestUserSession();
                userSession.setName("非法进入者");
                return userSession;
            }

            //更新缓存
            if (System.currentTimeMillis() - userSession.getLastRequestTime() > UPDATE_SESSION_MINUTE * DateUtil.MINUTE) {
                userSession.setLastRequestTime(System.currentTimeMillis());
                updateUserSessionCache(userSession);
                redisStoreQueueClient.update(userSession);
            }
        }

        return userSession;
    }

    /**
     * 得到在线用户信息,并且可以自动登录
     *
     * @param action 页面对象
     * @return UserSession 用户session
     * <p>
     * 规则说明，登录后在将sessionId保存到 session里边，优先使用session中的变量 sessionId
     * 如果没有就检查请求中是否有参数,说明(上传的时候用到),
     * 最后检查cookie中的seesionId(要加密),满足跨域要求,从而实现单点登录
     */
    @Override
    public UserSession getUserSession(Action action) {
        //有可能多个拦截器，之前已经读取过一次了
        //DDOS攻击检查 begin
        //DDOS 攻击 sessionId 不同

        HttpServletRequest request = action.getRequest();
        //外挂接收登陆信息有一定风险性
        if (request != null && allowServerName != null && !StringUtil.ASTERISK.equals(allowServerName)) {
            if (!request.getServerName().matches(allowServerName)) {
                UserSession userSession = createGuestUserSession();
                userSession.setIp(RequestUtil.getRemoteAddr(request));
                userSession.setId(request.getSession().getId());
                userSession.setName("非法用户");
                return userSession;
            }
        }


        //才有头部传输sesionId的方式,这种方式比较标准一点
        //头部个数 Authorization: Bearer {seesionId}
        String token = null;
        if (request != null) {
            token = RequestUtil.getToken(request);
            if (StringUtil.isEmpty(token))
            {
                token = CookieUtil.getCookieString(request, TXWeb.COOKIE_TICKET, null);
            }
            if (token!=null&&!token.contains(StringUtil.DOT))
            {
                token = null;
            }
        }

        HttpSession session = action.getSession();
        if (session != null & StringUtil.isNull(token) ) {
            token = (String) session.getAttribute(TXWeb.token);
        }

        //如果是二级域名共享登陆
        if (session != null && StringUtil.isNull(token)) {
            UserSession userSession = createGuestUserSession();
            userSession.setId(JWTUtil.createToken(action.getRemoteAddr(),"0", session.getId()));
            updateUserSessionCache(userSession);
            return userSession;
        }
        return getUserSession(token,action.getRemoteAddr());
    }

     /**
     * 删除是否成功
     *
     * @param token 用户sessionID
     * @param uid       用户ID
     */
    @Override
    public void deleteUserSession(String token, long uid) {
        memberDAO.deleteSession(token, uid);
    }

    /**
     * @param request      请求
     * @param response     应答
     * @param sid          sessionID
     * @param cookieSecond 保存时间
     */
    @Override
    public void setCookieTicket(HttpServletRequest request, HttpServletResponse response, String sid, int cookieSecond) {
        //保存 Cookie begin
        if (StringUtil.isNull(sid)) {
            return;
        }
        Cookie sessionCookie = new Cookie(TXWeb.COOKIE_TICKET, sid);
        sessionCookie.setMaxAge(cookieSecond * DateUtil.SECOND);
        sessionCookie.setPath("/");
        sessionCookie.setSecure(true);
        sessionCookie.setSecure(true);
        if (!StringUtil.isNull(domain)) {
            sessionCookie.setDomain(domain);
        } else if (request != null) {
            sessionCookie.setDomain(URLUtil.getTopDomain(request.getServerName()));
        }
        response.addCookie(sessionCookie);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void updateUserSessionCache(UserSession userSession) {
        if (userSession == null) {
            return;
        }
        if (onlineCache!=null)
        {
            onlineCache.put(userSession.getId(),userSession);
        } else
        {
            JSCacheManager.put(UserSession.class, SoberUtil.getLoadKey(UserSession.class, "id", userSession.getId(), false), userSession);
        }
    }
}