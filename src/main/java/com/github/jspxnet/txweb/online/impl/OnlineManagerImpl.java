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
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.LoginField;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.bundle.provider.PropertyProvider;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.env.ActionEnv;
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
import lombok.Getter;
import lombok.Setter;
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
public class  OnlineManagerImpl implements OnlineManager {
    final private static int DEFAULT_COOKIE_SECOND = DateUtil.HOUR*24;
    //72小时内可以自动重新登录
    private static int defaultOnlineHour = 72;

    static private final EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
    final private static String GUI_PASSWORD_KEY = "gui:password";
    //上一次清空超时时间，清空超时是全局性的，放这里比较合适
    //domain 设置你的域名不要www开始为'.',不设置将自动
    @Setter
    private String domain = StringUtil.empty;
    private Map<String,UserSession> onlineCache = null;
    //单点登录,一处登录，另外一处就下线
    @Getter
    private boolean sso = true;
    //允许哪些服务器采用sessionId直接注入登陆
    //配置格式  "(.*).gzcom.gov.cn|(.*).testaio.com"   判断成立的才允许载入
    private String allowServerName = StringUtil.ASTERISK;

    final private int UPDATE_SESSION_MINUTE = 30;

    public OnlineManagerImpl() {


    }

    /**
     * 设置一个通用密码，动态的，提供sms等方式，无密码登陆
     * 保存在缓存中,适配分布式
     */
    @Override
    public String getGuiPassword() {
        String cacheKey = SoberUtil.getLoadKey(DefaultCache.class, GUI_PASSWORD_KEY,"",false);
        String guiPassword = (String)JSCacheManager.get(DefaultCache.class, cacheKey);
        if (StringUtil.isEmpty(guiPassword))
        {
            guiPassword = RandomUtil.getRandomGUID(8);
            JSCacheManager.put(DefaultCache.class, cacheKey,guiPassword);
        }
        return guiPassword;
    }

    @Param(request = false, caption = "允许的域名")
    public void setAllowServerName(String allowServerName) {
        this.allowServerName = allowServerName;
    }

    @Param(request = false, caption = "单点登录")
    public void setSso(boolean sso) {
        this.sso = sso;
    }

    /**
     * @param member 用户
     * @return 创建登录日志
     */
    static private LoginLog createLoginLog(HttpServletRequest request,Member member) {
        LoginLog loginLog = new LoginLog();
        loginLog.setPutUid(member.getId());
        loginLog.setPutName(member.getName());
        loginLog.setIp(member.getIp());
        loginLog.setLoginTimes(member.getLoginTimes());
        if (request!=null)
        {
            loginLog.setSessionId(SessionUtil.getSessionId(request.getSession()));
            loginLog.setUrl(request.getRequestURL().toString());
            loginLog.setSystem(RequestUtil.getSystem(request));
            loginLog.setBrowser(RequestUtil.getBrowser(request));
            loginLog.setIp(RequestUtil.getRemoteAddr(request));
        }
        return loginLog;
    }

    //验证token的安全级别,0 默认只验证签名,1:验证ip,2:验证uid
    @Setter
    private int verifyTokenLevel = 3;

    @Override
    public String getDomain() {
        return domain;
    }

    @Ref
    private MemberDAO memberDAO;

    @Init
    @Override
    public void init()  {
        //如果redis缓存被关闭开始,开启onlineCache来保存数据
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        if (!envTemplate.getBoolean(Environment.useCache))
        {
            onlineCache = new LRUHashMap<>(50);
        }
        //默认保持在线时间，单位小时
        defaultOnlineHour = envTemplate.getInt(Environment.DEFAULT_ONLINE_HOUR,72);
    }

    @Override
    public boolean isOnline(long uid) {
        return uid > 0 && memberDAO.isOnline(uid);
    }

    @Override
    public boolean isOnline(String token) {
        return token != null && memberDAO.isOnline(token);
    }


    /**
     * 创建游客用户session
     * @return  游客用户session
     */
    @Override
    public UserSession createGuestUserSession() {
        ///////////////////判断是否有游客帐号，没有就创建一个
        UserSession userSession = new UserSession();
        userSession.setId(createGuestToken());
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
        if (ValidUtil.isNumber(isId)) {
            isId = "uid";
        }
        else
        {
            isId = "name";
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
    public JSONObject login(HttpServletRequest request, String loginId, String password, String client, String ip) throws Exception {
        JSONObject resultInfo = new JSONObject();
        if (StringUtil.isNull(client)) {
            resultInfo.put(Environment.message, "必须说明客户端类型");
            return resultInfo;
        }
        Member member = memberDAO.getMember(LoginField.ID, loginId);
        if (member == null) {
            resultInfo.put(Environment.message, "不存在的用户，not found user");
            memberDAO.evict(Member.class);
            return resultInfo;
        }
        if (!password.equalsIgnoreCase(getGuiPassword())&&!MemberUtil.verifyPassword(password,member.getPassword()))
        {
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

        HttpSession session = request.getSession();
        SessionUtil.cleanAll(request);
        //清空上一次登陆信息begin
        if (session!=null)
        {
            String oldToken = (String)session.getAttribute(ActionEnv.KEY_TOKEN);
            memberDAO.deleteSession(oldToken, ObjectUtil.toLong(loginId));
            session.setAttribute(ActionEnv.KEY_TOKEN,StringUtil.empty);
        }
        //清空上一次登陆信息end


        //更新用户信息 begin
        member.setLoginTimes(member.getLoginTimes() + 1);
        member.setLoginDate(new Date());
        member.setIp(ip);
        //更新用户信息 end

        //创建session信息更新 begin
        final String token =  JWTUtil.createToken(ip,NumberUtil.toString(member.getId()));
        UserSession userSession = BeanUtil.copy(member,UserSession.class);
        userSession.setId(token);
        userSession.setUid(member.getId());
        userSession.setInvisible(0);
        //创建session信息更新 end
        if (session!=null)
        {
            session.setAttribute(ActionEnv.KEY_TOKEN,token);
        }
        //SSO  单点登录
        if (sso) {
            memberDAO.deleteSession(userSession.getId(), userSession.getUid());
        }
        if (memberDAO.save(userSession)>=0)
        {
            memberDAO.update(member, new String[]{"loginTimes", "loginDate", "ip"});
        }

        //保存登录日志 begin
        LoginLog loginLog = createLoginLog(null,member);
        if (client.contains(StringUtil.COLON)) {
            loginLog.setAppId(StringUtil.toLong(StringUtil.substringAfter(client, StringUtil.COLON)));
        }
        loginLog.setClient("local");
        loginLog.setClient(client);
        loginLog.setUrl(Environment.unknown);
        if (client.contains(StringUtil.COLON)) {
            loginLog.setSystem(StringUtil.substringBefore(client, StringUtil.COLON));
        } else {
            loginLog.setSystem(client);
        }
        if (session!=null)
        {
            loginLog.setSessionId(SessionUtil.getSessionId(session));
            session.setMaxInactiveInterval(DateUtil.HOUR * 24);
            session.setAttribute(ActionEnv.KEY_TOKEN,token);
        }
        loginLog.setToken(token);
        if (!StringUtil.isNull(token))
        {
            memberDAO.save(loginLog);
        }
        //保存登录日志 end
        resultInfo.put(Environment.SUCCESS, YesNoEnumType.YES.getValue());
        resultInfo.put(ActionEnv.KEY_TOKEN, token);

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
    public Map<String, String> login(ActionSupport action, String isId, String loginId, String password, int cookieSecond) throws Exception
    {
        HttpServletRequest request = action.getRequest();
        String lan = RequestUtil.getLanguage(request);
        Bundle language = action.getLanguage();
        if (language==null)
        {
            PropertyProvider lang = new PropertyProvider();
            lang.setNamespace(Environment.language);
            lang.setDataType(lan);
            lang.loadMap();
            language = lang;
        }

        HttpServletResponse response = action.getResponse();
        CookieUtil.cookieClear(request,response);

        Map<String, String> errorInfo = new HashMap<>();
        if (StringUtil.getLength(loginId) < 1) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorLoginName));
            return errorInfo;
        }
        //tomcat6 bug 修复, 这里如果是中文，很可能接收到的为 编码后的字符串，需要判断解码
        if (loginId!=null&&loginId.length() > 5 && loginId.startsWith("%") && StringUtil.countMatches(loginId, "%") > 6) {
            loginId = URLUtil.getUrlDecoder(loginId, Environment.defaultEncode);
        }
        if (StringUtil.isNull(password) || password.length() < 4) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorPassword));
            return errorInfo;
        }

        Member member = null;
        //短信方式登录
        if (LoginField.SMS.equalsIgnoreCase(isId)) {
            member = memberDAO.getMemberV2(LoginField.PHONE, loginId);
            if (member == null) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.noFoundUser));
                memberDAO.evict(Member.class);
                return errorInfo;
            }
            if (!password.equalsIgnoreCase(getGuiPassword())) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorSmsValid));
                return errorInfo;
            }
        } else {
            if (StringUtil.isNull(isId)) {
                isId = getLoginType(loginId);
            }
            member = memberDAO.getMemberV2(isId, loginId);
            if (member == null) {
                errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.noFoundUser));
                memberDAO.evict(Member.class);
                return errorInfo;
            }

            if (!password.equalsIgnoreCase(getGuiPassword())&&!MemberUtil.verifyPassword(password,member.getPassword()))
            {
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
        //判断冻结 begin

        HttpSession session = request.getSession();
        final String token = JWTUtil.createToken(action.getRemoteAddr(),NumberUtil.toString(member.getId()));
        //创建session信息更新 begin
        UserSession userSession = BeanUtil.copy(member,UserSession.class);
        userSession.setId(token);
        userSession.setUid(member.getId());
        userSession.setInvisible(0);
        if (cookieSecond <= 0) {
            cookieSecond = DEFAULT_COOKIE_SECOND;
        }
        session.setMaxInactiveInterval(cookieSecond * DateUtil.SECOND);
        //创建session信息更新 end
        //信息检查完成
        //登录的时候作为验证
        session.setAttribute(ActionEnv.KEY_TOKEN,token);
        action.put(ActionEnv.KEY_TOKEN,token);

        setCookieToken(request, action.getResponse(), userSession.getId(), cookieSecond* DateUtil.SECOND);
        updateUserSessionCache(userSession);

        if (sso) {
            //单点登录，就删除其他的登录信息
            memberDAO.saveOrUpdate(userSession);
        } else {
            memberDAO.update(userSession);
        }
        //如果有必要，这里加入同步 end

        //用户信息更新 begin
        member.setLoginTimes(member.getLoginTimes() + 1);
        member.setLoginDate(new Date());
        member.setIp(action.getRemoteAddr());
        memberDAO.update(member, new String[]{"loginTimes", "loginDate", "ip"});
        //更新用户信息 end
        memberDAO.evictLoad(UserSession.class,"id",userSession.getId());

        //在线信息保存到缓存中

        //保存登录日志 begin
        LoginLog loginLog = createLoginLog(request,member);
        loginLog.setToken(token);
        loginLog.setClient("web");

        //用户session信息更新 end
        if (!StringUtil.isNull(token))
        {
            memberDAO.save(loginLog);
        }
        //保存登录日志 end
        return errorInfo;
    }


    /**
     * 判断是否可以登录
     *
     * @param loginName         登录用户名
     * @param password        密码
     * @return 判断是否可以登录
     */
    @Override
    public Map<String, String> apiLogin(ActionSupport action, String loginName, String password, String userName) throws Exception
    {
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

        final HttpSession session = action.getSession();
        CookieUtil.cookieClear(action.getRequest(),action.getResponse());

        Map<String, String> errorInfo = new HashMap<>();
        if (StringUtil.getLength(loginName) < 3) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorLoginName));
            return errorInfo;
        }
        //tomcat6 bug 修复, 这里如果是中文，很可能接收到的为 编码后的字符串，需要判断解码
        if (loginName!=null&&loginName.length() > 5 && loginName.startsWith("%") && StringUtil.countMatches(loginName, "%") > 6) {
            loginName = URLUtil.getUrlDecoder(loginName, Environment.defaultEncode);
        }
        if (StringUtil.isNull(password) || password.length() < 4) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorPassword));
            return errorInfo;
        }

        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        String userNameConf = environmentTemplate.getString(Environment.API_LOGIN_NAME);
        String userNamePass = environmentTemplate.getString(Environment.API_LOGIN_PASSWORD);

        if (loginName==null||!loginName.equals(userNameConf)||!password.equals(userNamePass))
        {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.errorLoginName));
            return errorInfo;
        }


        Member member =  memberDAO.getMember(LoginField.NAME, userName);
        if (member == null) {
            errorInfo.put(Environment.warningInfo, language.getLang(LanguageRes.noFoundUser));
            memberDAO.evict(Member.class);
            return errorInfo;
        }

        //信息检查完成
        HttpServletRequest request = action.getRequest();
        final String token = JWTUtil.createToken(action.getRemoteAddr(),NumberUtil.toString(member.getId()));
        //创建session信息更新 begin
        UserSession userSession = BeanUtil.copy(member,UserSession.class);
        userSession.setId(token);
        userSession.setUid(member.getId());
        userSession.setInvisible(0);
        //创建session信息更新 end

        session.setMaxInactiveInterval(DEFAULT_COOKIE_SECOND);

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
        LoginLog loginLog = createLoginLog(request,member);
        loginLog.setToken(token);
        loginLog.setClient("API");
        //用户session信息更新 end
        if (!StringUtil.isNull(token))
        {
            memberDAO.save(loginLog);
        }
        //保存登录日志 end

        //在线信息保存到缓存中
        updateUserSessionCache(userSession);
        //登录的时候作为验证

        session.setAttribute(ActionEnv.KEY_TOKEN,token);
        action.put(ActionEnv.KEY_TOKEN,token);
        setCookieToken(request, action.getResponse(), userSession.getId(), DEFAULT_COOKIE_SECOND);
        return errorInfo;
    }


    /**
     * @param token 认证Token
     * @return 得到用户session
     */
    @Override
    public UserSession getUserSession(String token,String ip)
    {
        //验证token
        if (!JWTUtil.tokenVerify(token))
        {
            //只是验证规则
            UserSession userSession = createGuestUserSession();
            userSession.setName("非法进入者");
            return userSession;
        }

        UserSession userSession = null;
        if (onlineCache!=null)
        {
            userSession = onlineCache.get(token);
            if (userSession==null)
            {
                userSession = memberDAO.load(UserSession.class,token,false);
            }
        }
        else
        {
            userSession = memberDAO.getUserSession(token);
        }

        //长时间没有登陆的，这里重新创建加载用户信息begin
        if (!StringUtil.isNullOrWhiteSpace(token) && userSession.getUid()==0 && JWTUtil.tokenVerify(token,ip,defaultOnlineHour))
        {
            JSONObject tokenJson = JWTUtil.getTokenJson(token);
            if (tokenJson!=null)
            {
                String uid = tokenJson.getString(JWTUtil.JWT_UID);
                if (!StringUtil.isNullOrWhiteSpace(uid) && !"0".equals(uid))
                {
                    ActionContext actionContext = ThreadContextHolder.getContext();
                    HttpServletRequest request = actionContext.getRequest();
                    if (request!=null)
                    {
                        HttpSession session = request.getSession();
                        if (session!=null)
                        {
                            try {
                               JSONObject result = login(request,uid,getGuiPassword(),"web",RequestUtil.getRemoteAddr(request));
                               if (result.getBoolean(Environment.SUCCESS))
                               {
                                   //登陆成功
                                   token = result.getString(ActionEnv.KEY_TOKEN);
                                   session.setAttribute(ActionEnv.KEY_TOKEN,token);
                               } else {
                                   //自动登陆失败，重置到游客
                                   session.setAttribute(ActionEnv.KEY_TOKEN,token);
                                   userSession = createGuestUserSession();
                                   userSession.setName("非法进入者");
                                   return userSession;
                               }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
        //长时间没有登陆的，这里重新创建加载用户信息end

        //更新缓存
        if (System.currentTimeMillis() - userSession.getLastRequestTime() > (UPDATE_SESSION_MINUTE/2) * DateUtil.MINUTE) {
            userSession.setLastRequestTime(System.currentTimeMillis());
            updateUserSessionCache(userSession);
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
    @Deprecated
    @Override
    public UserSession getUserSession(Action action) {
        return getUserSession();
    }


    @Override
    public UserSession getUserSession()
    {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return getUserSession(actionContext);
    }

    @Override
    public UserSession getUserSession(ActionContext actionContext)
    {
        if (actionContext==null)
        {
            UserSession userSession = createGuestUserSession();
            userSession.setName("非法用户");
            return userSession;
        }
        //先通过token 直接得到
        HttpServletRequest request = actionContext.getRequest();

        //有可能多个拦截器，之前已经读取过一次了
        //DDOS攻击检查 begin
        //DDOS 攻击 sessionId 不同

        //HttpServletRequest request = action.getRequest();
        //外挂接收登陆信息有一定风险性
        if (!ObjectUtil.isEmpty(request) && allowServerName != null && !StringUtil.ASTERISK.equals(allowServerName)) {
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
        String token =  getToken(request);
        HttpSession session = request.getSession();
        //如果是二级域名共享登陆
        if (session != null && StringUtil.isNull(token)) {
            UserSession userSession = createGuestUserSession();
            userSession.setId(createGuestToken());
            session.setAttribute(ActionEnv.KEY_TOKEN,token);
            updateUserSessionCache(userSession);
            return userSession;
        }
        return getUserSession(token,actionContext.getRemoteAddr());
    }



    @Override
    public String getToken(HttpServletRequest request)
    {
        if (request==null)
        {
            return null;
        }
        String token = RequestUtil.getToken(request);
        HttpSession session = request.getSession();
        if (session != null) {
            return  (String) session.getAttribute(ActionEnv.KEY_TOKEN);
        }
        if (StringUtil.isEmpty(token))
        {
            token = CookieUtil.getCookieString(request, ActionEnv.KEY_TOKEN, null);
        }
        if (!StringUtil.isEmpty(token))
        {
            return token;
        }

        return null;
    }

    /**
     * @param request      请求
     * @param response     应答
     * @param sid          sessionID
     * @param cookieSecond 保存时间
     */
    @Override
    public void setCookieToken(HttpServletRequest request, HttpServletResponse response, String sid, int cookieSecond) {
        //保存 Cookie begin
        if (StringUtil.isNull(sid)) {
            return;
        }
        Cookie sessionCookie = new Cookie(ActionEnv.KEY_TOKEN, sid);
        sessionCookie.setMaxAge(cookieSecond * DateUtil.SECOND);
        sessionCookie.setPath("/");
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
    /**
     * @return 创建游客token
     */
    private String createGuestToken()
    {
        ActionContext actionContext = ThreadContextHolder.getContext();
        String ip = StringUtil.empty;
        if (actionContext!=null)
        {
            ip = actionContext.getRemoteAddr();
        }
        return JWTUtil.createToken(ip,"0");
    }

    /**
     * @param userSession session
     * @return 更新缓存
     */
    @Override
    public boolean updateUserSessionCache(UserSession userSession) {
        if (userSession == null) {
            return false;
        }
        if (onlineCache!=null)
        {
            return onlineCache.put(userSession.getId(),userSession)!=null;
        } else
        {
           return JSCacheManager.put(UserSession.class, SoberUtil.getLoadKey(UserSession.class, "id", userSession.getId(), false), userSession);
        }
    }

    @Override
    public UserSession getUserSession(String token) {
        return memberDAO.getUserSession(token);
    }


    @Override
    public void exit(ActionSupport action) {
        HttpServletResponse response = action.getResponse();
        if (response != null) {
            CookieUtil.cookieClear(action.getRequest(), response);
        }
        HttpSession session = action.getSession();
        if (session != null) {
            session.removeAttribute(ActionEnv.KEY_TOKEN);
        }
        UserSession userSession = action.getUserSession();
        if (userSession!=null)
        {
            if (memberDAO.deleteSession(userSession.getId(),userSession.getUid()))
            {
                JSCacheManager.remove(UserSession.class,SoberUtil.getLoadKey(UserSession.class, "id", userSession.getId(), false));
                memberDAO.deleteOvertimeSession(UPDATE_SESSION_MINUTE);
            }
        }
    }

    @Override
    public void exit(String sessionId) {
        if (memberDAO.deleteSession(sessionId, -1))
        {
            JSCacheManager.remove(UserSession.class,SoberUtil.getLoadKey(UserSession.class, "id", sessionId, false));
            memberDAO.deleteOvertimeSession(UPDATE_SESSION_MINUTE);
        }
    }

    @Override
    public void exit(long uid) {
        try {
            UserSession userSession = memberDAO.getUserSession(uid);
            if (userSession!=null)
            {
                exit(userSession.getId());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}