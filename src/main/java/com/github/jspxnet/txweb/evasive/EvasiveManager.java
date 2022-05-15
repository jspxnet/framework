package com.github.jspxnet.txweb.evasive;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.cache.CacheManager;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.store.MemoryStore;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.evasive.condition.*;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ChenYuan on 2017/6/15.
 * 回避管理器
 */
@Slf4j
public class EvasiveManager {

    static protected boolean debug = false;

    private final Collection<EvasiveRule> EVASIVE_RULE_LIST = new ArrayList<>();
    private final static Collection<QueryBlack> QUERY_BLACK_RULE_LIST = new ArrayList<>();

    private final static Map<String, ResultConfigBean> RESULT_CONFIG_MAP = new ConcurrentHashMap<>();
    //拦截的ip,放入的时间
    private static final Map<String, EvasiveIp> BLACK_IP_LIST = new ConcurrentHashMap<>();
    //白名单
    private static String[] whiteList = new String[0];
    //黑名单
    private static String[] blackList = new String[0];

    //密码访问目录,<密码,目录>
    private static Map<String,String> passwordFolderList = new HashMap<>();

    //黑名单后缀
    private static String[] blackSuffixList = null;

    final private static String BLACK_RESULT = "black";
    final private static String PASSWORD = "password";
    final private static String FOLDER_INDEX = "index";

    private static boolean evasiveExcludeFilter = true;
    private static String[] insecureUrlKeys = null;
    private static String[] insecureQueryStringKeys = null;

    private static final EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();


    //判断程序
    final private static Map<String, Decide> DECIDE_LIST = new HashMap<>();

    static {
        DECIDE_LIST.put("session", new SessionDecide());
        DECIDE_LIST.put("cookie", new CookieDecide());
        DECIDE_LIST.put("useragent", new UserAgentDecide());
        DECIDE_LIST.put("ip", new IpDecide());
        DECIDE_LIST.put("parameter", new ParameterDecide());
        DECIDE_LIST.put("script", new ScriptDecide());
        DECIDE_LIST.put("sql", new SqlDecide());
        DECIDE_LIST.put("referer", new RefererDecide());
    }

    final private static EvasiveManager INSTANCE = new EvasiveManager();

    public static EvasiveManager getInstance() {
        return INSTANCE;
    }

    private EvasiveManager() {
        reload();
    }

    private void reload() {

        boolean useEvasive = ENV_TEMPLATE.getBoolean(Environment.useEvasive);
        if (!useEvasive) {
            return;
        }
        EVASIVE_RULE_LIST.clear();
        QUERY_BLACK_RULE_LIST.clear();
        RESULT_CONFIG_MAP.clear();

        Configuration configuration = EvasiveConfiguration.getInstance();
        EVASIVE_RULE_LIST.addAll(configuration.getEvasiveRuleList());
        QUERY_BLACK_RULE_LIST.addAll(configuration.getQueryBlackRuleList());

        for (ResultConfigBean resultConfigBean : configuration.getResultConfigList()) {
            RESULT_CONFIG_MAP.put(resultConfigBean.getName(), resultConfigBean);
        }

        whiteList = configuration.getWhiteList();
        blackList = configuration.getBlackList();



        insecureUrlKeys = ArrayUtil.deleteRepeated(configuration.getInsecureUrlKeys(), true);
        insecureQueryStringKeys = ArrayUtil.deleteRepeated(configuration.getInsecureQueryStringKeys(), true);
        blackSuffixList = ArrayUtil.deleteRepeated(configuration.getBlackSuffixList(), true);
        passwordFolderList = configuration.getPasswordFolderList();


        log.info("white list，白名单:" + ArrayUtil.toString(whiteList, StringUtil.SEMICOLON));
        //合并字符串里边相同的内容
        log.info("black list，黑名单:" + ArrayUtil.toString(blackList, StringUtil.SEMICOLON));

        log.info("black suffix，不允许的后缀:" + ArrayUtil.toString(blackSuffixList, StringUtil.SEMICOLON));

        log.info("password folder，密码访问目录:" + ObjectUtil.toString(passwordFolderList));


        Map<String, Object> valueMap = TXWebUtil.createEnvironment();
        CacheManager cacheManager = JSCacheManager.getCacheManager();
        for (EvasiveRule evasiveRule : EVASIVE_RULE_LIST) {

            try {
                evasiveRule.setUrl(EnvFactory.getPlaceholder().processTemplate(valueMap, evasiveRule.getUrl()));
            } catch (Exception e) {
                log.error("evasive rule " + evasiveRule.getName() + " config url is error,回避过滤的规则URL部分配置错误规则名称:" + evasiveRule.getName(), e);
                e.printStackTrace();
            }

            //如果没有配置缓存，这里将创建缓存
            if (!cacheManager.containsKey(EvasiveManager.class.getName())) {
                MemoryStore store = new MemoryStore();
                try {
                    cacheManager.createCache(store, EvasiveManager.class, evasiveRule.getInterval(), evasiveRule.getCacheSize(), false, System.getProperty("user.dir"));
                } catch (Exception e) {
                    log.error("evasive create cache " + EvasiveManager.class + " fail,创建缓存失败缓存名称:" + EvasiveManager.class, e);
                    e.printStackTrace();
                }
            }
        }

        debug = ENV_TEMPLATE.getBoolean(Environment.logDebugEvasive);
        evasiveExcludeFilter = ENV_TEMPLATE.getBoolean(Environment.evasiveExcludeFilter);


    }

    private boolean conditionDecides(List<Condition> conditions, String logic, HttpServletRequest request, HttpServletResponse response) {
        if (conditions.isEmpty()) {
            return true;
        }
        //logicArray 保存的是每个逻辑关系的计算结果
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put("request", request);
        envParams.put("response", response);
        envParams.put("ip", RequestUtil.getRemoteAddr(request));
        envParams.put("remoteHost", request.getRemoteHost());
        envParams.put("method", request.getMethod());

        int[] logicArray = ArrayUtil.getInitedIntArray(conditions.size(), 0);
        for (int i = 0; i < logicArray.length; i++) {
            Condition condition = conditions.get(i);
            Decide decide = DECIDE_LIST.get(condition.getRuleType().toLowerCase());
            //如果找不到默认就是0
            if (decide == null) {
                continue;
            }

            decide.setRequest(request);
            decide.setResponse(response);
            String runScript = null;
            try {
                runScript = EnvFactory.getPlaceholder().processTemplate(envParams, condition.getScript());
            } catch (Exception e) {
                log.error("condition decides script content has error " + condition.getScript());
                e.printStackTrace();
            }
            decide.setContent(runScript);
            logicArray[i] = decide.execute() ? 1 : 0;
            //判断and 有一个不成立,就不判断其他的了
            if ("and".equalsIgnoreCase(logic) && logicArray[i] == 0) {
                return false;
            }
        }
        return "or".equalsIgnoreCase(logic) && ArrayUtil.contains(logicArray, 1);
    }


    /**
     * @param ip ip
     * @return 判断是否在白名单
     */
    private boolean isInWhiteList(String ip) {
        if (ArrayUtil.isEmpty(whiteList)) {
            return false;
        }
        for (String ipEx : whiteList) {
            if (IpUtil.interiorly(ipEx, ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param ip IP
     * @return 判断是否在黑名单
     */
    private static boolean isInBlackList(String ip) {
        if (ArrayUtil.isEmpty(blackList)) {
            return false;
        }
        for (String ipEx : blackList) {
            if (IpUtil.interiorly(ipEx, ip)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInBlackSuffix(String suffix) {
        if (ArrayUtil.isEmpty(blackSuffixList)) {
            return false;
        }
        return ArrayUtil.inArray(blackSuffixList,suffix,true);
    }

    /**
     *
     * @param url url路径
     * @return  返回密码
     */
    private static String isInPasswordFolder(String url) {
        for (String folder:passwordFolderList.keySet())
        {
            if (FileUtil.isPatternEquals(url,folder))
            {
                return passwordFolderList.get(folder);
            }
        }
        return null;
    }

    /**
     * 查询方式判断
     * ip 查询黑名单,根据条件查询数据库，ip访问记录表，来分析是否有拦截
     */
    private static void runQueryBlack() {
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        for (QueryBlack queryBlack : QUERY_BLACK_RULE_LIST) {
            //执行周期为5分钟执行一次
            if (System.currentTimeMillis() - queryBlack.getLastQueryTimeMillis() < DateUtil.MINUTE * 2) {
                continue;
            }
            queryBlack.setLastQueryTimeMillis(System.currentTimeMillis());
            GenericDAO genericDAO = EnvFactory.getBeanFactory().getBean(GenericDAO.class);
            if (genericDAO == null) {
                log.error("not find genericDAO,没有找到 genericDAO配置");
                return;
            }
            String sql = queryBlack.getSql();
            if (StringUtil.isNull(sql)) {
                log.error(queryBlack.getName() + "not find sql,没有查询sql");
                return;
            }

            try {
                sql = EnvFactory.getPlaceholder().processTemplate(envParams, sql);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(queryBlack.getName() + " sql " + sql, e);
            }
            if (debug) {
                log.debug(sql);
            }
            List<?> dataMapList = genericDAO.prepareQuery(sql, null);
            if (dataMapList == null || dataMapList.isEmpty()) {
                continue;
            }
            List<String> saveList = new ArrayList<>();
            for (Object bean : dataMapList) {
                String qIp = (String) BeanUtil.getProperty(bean, queryBlack.getIpField());
                int qTimes = ObjectUtil.toInt(BeanUtil.getProperty(bean, queryBlack.getTimesField()));
                //满足次数,并且不在黑名单
                if (qTimes > queryBlack.getMinTimes() && !BLACK_IP_LIST.containsKey(qIp)) {
                    saveList.add(qIp);
                }
                if (saveList.size() > queryBlack.getBlackSize()) {
                    break;
                }
            }
            //保持到黑名单
            if (!saveList.isEmpty()) {
                for (String sIp : saveList) {
                    if (queryBlack.getImprisonSecond() <= 0) {
                        if (!ArrayUtil.contains(blackList, sIp)) {
                            blackList = ArrayUtil.add(blackList, sIp);
                        }
                    } else {
                        EvasiveIp evasiveIp = new EvasiveIp();
                        evasiveIp.setId(IpUtil.toLong(sIp));
                        evasiveIp.setIp(sIp);
                        evasiveIp.setTimes(1);
                        evasiveIp.setImprisonSecond(queryBlack.getImprisonSecond());
                        evasiveIp.setResult(queryBlack.getResult());
                        BLACK_IP_LIST.put(sIp, evasiveIp);
                    }
                }
            }
        }
    }

    /**
     * 判断规则
     */
    private void runEvasiveRule(HttpServletRequest request, HttpServletResponse response) {
        String localUrl = request.getRequestURI(); //只是文件部分,没有参数
        if (debug) {
            log.debug("evasive check url:{}" ,localUrl);
        }
        if (localUrl != null) {
            localUrl = localUrl.toLowerCase();
        } else {
            localUrl = "";
        }
        String ip = RequestUtil.getRemoteAddr(request);
        for (EvasiveRule evasiveRule : EVASIVE_RULE_LIST) {
            if (!StringUtil.ASTERISK.equals(evasiveRule.getMethod()) && !request.getMethod().equalsIgnoreCase(evasiveRule.getMethod())) {
                continue;
            }
            if (!StringUtil.ASTERISK.equals(evasiveRule.getUrl()) && !localUrl.matches(evasiveRule.getUrl())) {
                continue;
            }
            EvasiveIp evasiveIp = (EvasiveIp) JSCacheManager.get(EvasiveRule.class, ip);
            if (evasiveIp == null) {
                evasiveIp = new EvasiveIp();
                evasiveIp.setId(IpUtil.toLong(ip));
                evasiveIp.setIp(ip);
                evasiveIp.setTimes(1);
                evasiveIp.setImprisonSecond(evasiveRule.getImprisonSecond());
                evasiveIp.setResult(evasiveRule.getResult());
                JSCacheManager.put(EvasiveManager.class, ip, evasiveIp);
            }
            //判断条件满足到才能加


            //判断是否满足脚本条件
            if (conditionDecides(evasiveRule.getConditions(), evasiveRule.getLogic(), request, response)) {
                int times = evasiveIp.updateTimes();
                if (debug) {
                    log.debug(ip + ":" + times);
                }
            }
            //判断是否满足脚本条件



            //如果创建的时间大于一个周期时间, 访问次数大于最大允许次数的数据，清空，重置,从黑名单里边放出来
            if (evasiveIp.getTimes() > evasiveRule.getMaxTimes()) {
                if (System.currentTimeMillis() - evasiveIp.getCreateTimeMillis() >= evasiveRule.getInterval() * DateUtil.SECOND) {
                    evasiveIp.setCreateTimeMillis(System.currentTimeMillis());
                    evasiveIp.setTimes(0);
                } else {
                    if (evasiveIp.getImprisonSecond() <= 0) {
                        if (!ArrayUtil.contains(blackList, ip)) {
                            blackList = ArrayUtil.add(blackList, ip);
                        }
                    } else {
                        evasiveIp = new EvasiveIp();
                        evasiveIp.setId(IpUtil.toLong(ip));
                        evasiveIp.setIp(ip);
                        evasiveIp.setTimes(1);
                        evasiveIp.setImprisonSecond(evasiveIp.getImprisonSecond());
                        evasiveIp.setResult(evasiveRule.getResult());
                        if (!BLACK_IP_LIST.containsKey(ip)) {
                            BLACK_IP_LIST.put(ip, evasiveIp);
                        }
                    }
                    //删除计数器,只是为了减少内存占用
                    JSCacheManager.remove(EvasiveManager.class, ip);
                }
                return;
            }
        } //for (EvasiveRule evasiveRule:evasiveRuleList)
    }

    /**
     * 转发器
     *
     * @param request  请求
     * @param response 应答
     * @return boolean   返回true:表示已经拦截,false 表示不拦截
     */
    public boolean execute(HttpServletRequest request, HttpServletResponse response) {
        String localUrl = request.getRequestURI(); //只是文件部分,没有参数
        if (debug) {
            log.debug("evasive check url:{}",localUrl);
        }
        if (localUrl != null) {
            localUrl = localUrl.toLowerCase();
        } else {
            localUrl = "";
        }

        ///////////////这里是网站的安全隔离 begin
        //过滤URL非法字符串
        if (!ArrayUtil.isEmpty(insecureUrlKeys)) {
            for (String key : insecureUrlKeys) {
                if (localUrl.contains(key)) {
                    TXWebUtil.errorPrint("禁止使用不安全的URL文件名访问",null, response, HttpStatusType.HTTP_status_500);
                    return true;
                }
            }
        }
        //过滤参数中非法字符串
        if (!ArrayUtil.isEmpty(insecureQueryStringKeys)) {
            String queryString = request.getQueryString(); //只是参数部分
            if (queryString != null) {
                queryString = URLUtil.getUrlDecoder(queryString, Dispatcher.getEncode());
                queryString = queryString.toLowerCase();
                for (String key : insecureQueryStringKeys) {
                    if (queryString.contains(key)) {
                        TXWebUtil.errorPrint("禁止使用不安全的参数访问",null, response, HttpStatusType.HTTP_status_500);
                        return true;
                    }
                }
            }
        }
        ///////////////这里是网站的安全隔离 end

        //过滤不允许方式的后缀begin
        if (isInBlackSuffix(FileUtil.getTypePart(localUrl)))
        {
            TXWebUtil.errorPrint("禁止访问的文件后缀",null, response, HttpStatusType.HTTP_status_405);
            return true;
        }
        //过滤不允许方式的后缀end


        //过滤不允许方式的后缀begin
        if (isInBlackSuffix(FileUtil.getTypePart(localUrl)))
        {
            TXWebUtil.errorPrint("禁止访问的文件后缀",null, response, HttpStatusType.HTTP_status_405);
            return true;
        }
        //过滤不允许方式的后缀end

        String password = isInPasswordFolder(localUrl);
        if (!StringUtil.isNull(password) && !password.equalsIgnoreCase(request.getParameter(PASSWORD))) {
            //无密码
            TXWebUtil.errorPrint("安全目录,需要密码访问",null, response, HttpStatusType.HTTP_status_405);
            return true;
        } else
        if (!StringUtil.isNull(password) && password.equalsIgnoreCase(request.getParameter(PASSWORD)) && FOLDER_INDEX.equalsIgnoreCase(URLUtil.getFileNamePart(localUrl)))
        {
            //列表目录
            File path;
            if (ENV_TEMPLATE.getBoolean(Environment.SERVER_EMBED))
            {
                //嵌入模式
                path = new File(ENV_TEMPLATE.getString(Environment.SERVER_WEB_PATH,Dispatcher.getRealPath()),URLUtil.getNamespace(localUrl));
            } else
            {
                path = new File(Dispatcher.getRealPath(),URLUtil.getNamespace(localUrl));
            }
            if (path.exists())
            {
                List<File> fileList = FileUtil.getLatestFileList(path,20);
                StringBuilder sb = new StringBuilder();
                sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" +
                        "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">" +
                        "<style>#name{ padding-left: 10px;color: #00695C;} #length{padding-left: 10px;color: blue;}</style>" +
                        "</head><body>");
                for (File file: fileList)
                {
                    sb.append("<br><span id=\"name\">").append(file.getName()).append("</span><span id=\"length\">[").append(file.length()).append("]</span>").append("</br>");
                }
                sb.append("</body>").append("</html>");
                TXWebUtil.print(sb.toString(),WebOutEnumType.HTML.getValue(), response);
                fileList.clear();
                return true;
            }
            TXWebUtil.errorPrint("非法路径",null, response, HttpStatusType.HTTP_status_405);
            return true;
        } else
        if (!StringUtil.isNull(password) && password.equalsIgnoreCase(request.getParameter(PASSWORD)))
        {
            //有密码能访问
            return false;
        }


        //优化排除不需要判断的URL,如果tomcat单独作为web服务器的时候很有用
        if (evasiveExcludeFilter && localUrl.matches("/\\S+\\.\\S+." + Dispatcher.getFilterSuffix())) {
            return false;
        }

        //白名单检测begin
        String ip = RequestUtil.getRemoteAddr(request);
        if (isInWhiteList(ip)) {
            return false;
        }
        //白名单检测end

        //黑名单判断
        if (isInBlackList(ip)) {
            printBlackError(request, response);
            return true;
        }


        EvasiveIp blackEvasiveIp = BLACK_IP_LIST.get(ip);
        if (blackEvasiveIp != null) {
            if (System.currentTimeMillis() - blackEvasiveIp.getCreateTimeMillis() > blackEvasiveIp.getImprisonSecond() * DateUtil.SECOND) {
                //到期的黑名单人员放出来
                BLACK_IP_LIST.remove(ip);
                return false;
            }
            //这里是要拦截的数据begin
            printError(request, response, blackEvasiveIp);
            return true;
            //这里是要拦截的数据end
        }

        //计算查询表方式拦截 begin
        runQueryBlack();
        //计算查询表方式拦截 begin

        //规则判断 begin
        runEvasiveRule(request, response);
        //规则判断 end
        return false;
    }

    private static void printBlackError(HttpServletRequest request, HttpServletResponse response) {
        String ip = RequestUtil.getRemoteAddr(request);
        //为了支持脚本和更多功能
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put("request", request);
        envParams.put("response", response);
        envParams.put("ip", ip);
        envParams.put("waitSecond", Long.MAX_VALUE);
        envParams.put("waitTime", "forever,永远");
        ResultConfigBean resultConfig = RESULT_CONFIG_MAP.get(BLACK_RESULT);
        doResult(request, response, envParams, resultConfig);
    }

    private static void printError(HttpServletRequest request, HttpServletResponse response, EvasiveIp blackEvasiveIp) {

        String ip = RequestUtil.getRemoteAddr(request);
        //为了支持脚本和更多功能
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put("request", request);
        envParams.put("response", response);
        envParams.put("ip", ip);
        long currentTimeMillis = System.currentTimeMillis();
        int second = 30;
        if (blackEvasiveIp!=null)
        {
            second = blackEvasiveIp.getImprisonSecond();
        }
        long waitTime = second * DateUtil.SECOND - (currentTimeMillis - blackEvasiveIp.getCreateTimeMillis());
        if (waitTime < 0) {
            waitTime = 0;
        }
        envParams.put("waitSecond", (waitTime / DateUtil.SECOND));
        envParams.put("waitTime", DateUtil.getTimeMillisFormat(waitTime, "zh"));
        ResultConfigBean resultConfig = RESULT_CONFIG_MAP.get(blackEvasiveIp.getResult());
        doResult(request, response, envParams, resultConfig);
    }


    static private void doResult(HttpServletRequest request, HttpServletResponse response, Map<String, Object> envParams, ResultConfigBean resultConfig) {
        if (resultConfig == null) {
            TXWebUtil.print("evasive result not find config<br>配置有错误,稍后后再试<br>", WebOutEnumType.HTML.getValue(), response);
            return;
        }

        String resultSrc = StringUtil.empty;
        try {
            resultSrc = EnvFactory.getPlaceholder().processTemplate(envParams, resultConfig.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            if (debug) {
                log.error("getPlaceholder processTemplate {}",resultConfig.getValue());
                TXWebUtil.errorPrint("evasive安全配置有错误:" + StringUtil.toBrLine(e.getMessage()),null, response, resultConfig.getStatus());
            }
        }
        if (ActionEnv.REDIRECT_TYPE.equalsIgnoreCase(resultConfig.getType())) {
            try {
                if (!StringUtil.isNull(resultSrc)) {
                    response.sendRedirect(resultSrc);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (debug) {
                    log.error("sendRedirect {}" ,resultConfig.getValue());
                    TXWebUtil.errorPrint("sendRedirect " + resultConfig.getValue() + " " + StringUtil.toBrLine(e.getMessage()), null,response, resultConfig.getStatus());
                } else {
                    TXWebUtil.errorPrint("转发地址错误:" + resultSrc,null, response, resultConfig.getStatus());
                }
            }
        } else if (ActionEnv.CHAIN_TYPE.equalsIgnoreCase(resultConfig.getType())) {
            try {
                if (!StringUtil.isNull(resultSrc)) {
                    request.getRequestDispatcher(resultSrc).forward(request, response);
                }
            } catch (Exception e) {

                if (debug) {
                    TXWebUtil.errorPrint("chain forward " + resultConfig.getValue() + " fail", null,response, resultConfig.getStatus());
                    log.error("chain forward" + resultConfig.getValue());
                } else {
                    TXWebUtil.errorPrint("chain forward fail", null,response, resultConfig.getStatus());
                }
            }
        } else if (WebOutEnumType.HTML.getValue()==WebOutEnumType.find(resultConfig.getType()).getValue()) {
            //输出HTML代码
            try {
                String str = EnvFactory.getPlaceholder().processTemplate(envParams, resultSrc);
                TXWebUtil.print(str, WebOutEnumType.HTML.getValue(), response, resultConfig.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("evasive config error :{}",resultConfig.getName());
            }
        } else if (WebOutEnumType.XML.getValue()==WebOutEnumType.find(resultConfig.getType()).getValue()) {
            try {
                String str = EnvFactory.getPlaceholder().processTemplate(envParams, resultSrc);
                TXWebUtil.print(str, WebOutEnumType.XML.getValue(), response, resultConfig.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("evasive config error :{}",resultConfig.getName());
            }
        } else if (WebOutEnumType.JSON.getValue()==WebOutEnumType.find(resultConfig.getType()).getValue())
        {
            try {
                String str = EnvFactory.getPlaceholder().processTemplate(envParams, resultSrc);
                TXWebUtil.print(str, WebOutEnumType.JSON.getValue(), response, resultConfig.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("evasive config error :{}",resultConfig.getName());
            }

        } else if (WebOutEnumType.JAVASCRIPT.getValue()==WebOutEnumType.find(resultConfig.getType()).getValue() || "script".equalsIgnoreCase(resultConfig.getType())) {
            try {
                String str = EnvFactory.getPlaceholder().processTemplate(envParams, resultSrc);
                response.setStatus(resultConfig.getStatus());
                TXWebUtil.print(str, WebOutEnumType.JAVASCRIPT.getValue(), response);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("evasive config error : {}", resultConfig.getName());
            }
        } else {
            //消息类型
            TXWebUtil.errorPrint(resultConfig.getValue(), null,response, resultConfig.getStatus());
        }
    }

    public String[] getWhiteList() {
        return whiteList;
    }


    public String[] getBlackList() {
        return blackList;
    }

    public Collection<EvasiveIp> getBlackIpList() {
        return BLACK_IP_LIST.values();
    }

    public Collection<EvasiveRule> getEvasiveRuleList() {

        return EVASIVE_RULE_LIST;
    }

    public Collection<QueryBlack> getQueryBlackRuleList() {
        return QUERY_BLACK_RULE_LIST;
    }

    public Collection<ResultConfigBean> getResultConfigList() {
        return RESULT_CONFIG_MAP.values();
    }


    public void shutdown() {
        EVASIVE_RULE_LIST.clear();
        BLACK_IP_LIST.clear();
    }
}