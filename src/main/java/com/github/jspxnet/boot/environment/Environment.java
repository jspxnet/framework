/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.environment;


import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-1
 * Time: 16:45:27
 * 陈原
 */
public abstract class Environment {
    private Environment() {

    }


    final public static String appBeanId = "appBeanId";

    final public static String ConfigFile = "configFile";

    final public static String Global = "global";

    final public static String namespace = "namespace";

    final public static String DEV_CENTER = "devcenter";

    final public static String DYN_OUT = "dynout";

    final public static String none = "none";

    public static final String defaultValue = "default";

    final public static String TXWebConfig = "txweb_config";

    final public static String evasive_config = "evasive_config";

    public static String config_file = "jspx.net.xml";

    final public static String templateSuffix = "templateSuffix";

    //markdown 文件后缀
    final public static String markdownSuffix = "markdownSuffix";

    final public static String templatePath = "templatePath";

    //错误信息提示有样式模版
    final public static String errorInfoPageTemplate = "errorInfoPageTemplate";
    final public static String markdownTemplate = "markdownTemplate";

    //final static public String templateType = "templateType";

    //Tomcat得到参数是否修复编码
    final public static String httpServerName = "httpServerName";

    final public static String repairEncode = "repairEncode";

    final public static String auto = "auto";

    final public static String repairRequestMethod = "repairRequestMethod";


    //final public static String Date_format = "date_format";

    //final public static String Time_format = "time_format";

    //final public static String Number_format = "number_format";

    final public static String autoImports = "autoImports";

    final public static String autoIncludes = "autoIncludes";

    final public static String dateTimeFormat = "dateTimeFormat";

    final public static String defaultPath = "defaultPath";

    final public static String sqlXmlPath = "sqlXmlPath";

    final public static String lucenePath = "lucenePath";

    final public static String databasePath = "databasePath";

    final public static String libPath = "libPath";


    final public static String DEFAULT_LOAD_LOG_NAME = "logback.xml";

    final public static String LOG4J_CONFIG_NAME = "log4j2.xml";


    //开启日志保存到数据库
    final public static String DB_LOG = "dbLog";

    final public static String DEFAULT_LOG_NAME = "defaultlog.xml";

    final public static String DB_LOG_NAME = "dblog.xml";


    final public static String webInfPath = "webInfPath";

    final public static String tempPath = "tempPath";

    final public static String fontsPath = "fontsPath";

    final public static String resPath = "resPath";

    final public static String realPath = "realPath";

    final public static String log4jPath = "log4jPath";

    final public static String logPath = "logPath";

    final public static String startRunDate = "startRunDate";

    final public static String upgradeXMLFile = "upgrade.cfg.xml";

    final public static String versionId = "version";

    final public static String filterSuffix = "suffix";

    final public static String ApiFilterSuffix = "apiSuffix";

    final public static String domainName = "domainName";

    final public static String remote = "remote";

    final public static String frameworkName = "jspx cloud framework";

    final public static String useEvasive = "useEvasive";

    final public static String evasiveExcludeFilter = "evasiveExcludeFilter";

    final static public String locale = "locale";

    final static public String localeIP = "127.0.0.1";

    final static public String myDomainName = "<a href=\"http://www.jspx.net\">www.jspx.net</a>";

    //版本号
    final static public String VERSION = "6.71";

    //发布许可方式
    final static public String licenses = "AGPLv3";

    final static public String author = "陈原";

    final static public String mail = "39793751@qq.com";

    final static public String startDevelopDeDate = "2004-4-1";

    final static public String notFindLink = "notFindLink";

    //jspx.net chenYuan 13511993665
    final static public String JSPX_NET_MD5 = "79fada3a1762f29f80997d23b3304947";

    final public static String logInfoFile = "logInfoFile";

    final public static String logDebugFile = "logDebugFile";

    final public static String logErrorFile = "logErrorFile";

    final public static String logFatalFile = "logFatalFile";

    final public static String log_info_file = "sys_info.log";

    final public static String log_error_file = "sys_error.log";

    final public static String jspxProperties = "jspxProperties";

    final public static String jspx_properties_file = "jspx.properties";

    final public static String jspx_properties_template = "jspx-${active}.properties";

    final public static String logInfo = "info";

    final public static String logError = "error";

    final public static String DEBUG = "debug";

    final public static String LOGIN_TIMES = "loginTimes";

    final static public String maxLoginTimes = "maxLoginTimes";

    //防刷拦截器
    final public static String logDebugEvasive = "debugEvasive";

    /////////////////////////常用参数
    //static public final String uploadFileDAO = "uploadFileDAO";

    static public final String UserBundleDAO = "userBundleDAO";

    //文件上传对象Ueditor里边使用
    //static public final String uploadFileAction = "uploadFileAction";
    //验证
    static public final String validate = "validate";


    //最高金额
    static public final String MAX_AMOUNT = "maxAmount";

    static public final String MAX_POINTS = "maxPoints";

    static public final String POINTS_NAME = "pointsName";

    static public final String AMOUNT_NAME = "AmountName";

    //修复路径,上传的文件显示修复
    static public final String postFixPath = "postFixPath";

    //翻页风格
    final static public String pageStyle = "pageStyle";

    //组织
    final static public String organise = "organise";

    //字符编码
    final static public String encode = "encode";

    final static public String xmlFormatClass = "xmlFormatClass";


    final static public String systemEncode = "systemEncode";

    final static public String defaultLanguage = "zh";

    final static public String defaultEncode = "UTF-8";

    final static public String timezone = "user.timezone";

    //主服务器URL
    final static public String remoteHostUrl = "remoteHostUrl";

    final static public String scriptPath = "scriptPath";

    final static public String sitePath = "sitePath";

    //拦截器里边使用
    final static public String interceptorRole = "interceptorRole";

    //FTP权限
    final static public String permission = "permission";

    //日期格式
    final static public String dateFormat = "dateFormat";

    //安装路径
    final static public String setupPath = "setupPath";
    //上传路径

    //上传是否覆盖
    final static public String uploadCovering = "uploadCovering";

    //上传提起txt保存
    final static public String useUploadConverterTxt = "useUploadConverterTxt";

    final static public String maxImageWidth = "maxImageWidth";

    final static public String maxImageHeight = "maxImageHeight";
    //上传路径
    final static public String uploadPath = "uploadPath";

    //备份路径
    final static public String BackupPath = "backupPath";

    //允许上传的文件类型
    final static public String allowedTypes = "allowedTypes";

    //允许跨站ajax调用
    final static public String ACCESS_ALLOW_ORIGIN = "accessAllowOrigin";

    //最大上传大小
    final static public String uploadMaxSize = "uploadMaxSize";

    //上传类型
    final static public String uploadPathType = "uploadPathType";

    //用户上传次数
    static public final String userUploadTimes = "userUploadTimes";

    //最大下载数
    final static public String maxDownloader = "maxDownloader";

    //搜索的路径
    static public final String searchPaths = "searchPaths";

    //积分计算公式
    final static public String expressions = "expressions";

    //删除备份路径
    static public final String backFolder = "backFolder";

    //下载类型，单线程,多线程
    final static public String downloadType = "downloadType";

    //是否开启远程登陆
    final static public String userRemoteLogin = "userRemoteLogin";

    //远程登陆验证配置begin---------------------------------------------------------------------------------------------
    final static public String publicKeyHost = "publicKeyHost";


    final static public String authIpExpression = "authIpExpression";

    //远程登陆验证配置end-----------------------------------------------------------------------------------------------

    //是否开启串口 短信猫
    final static public String useSerial = "useSerial";

    final static public String useHistory = "useHistory";

    final static public String guestId = "guestId";

    final static public String guestName = "guestName";

    final static public String script = "script";

    //应用的安装URL路径
    static public final String rootUrl = "rootUrl";

    static public final String LOGO = "logo";

    static public final String openSite = "openSite";

    //游客是否允许访问
    static public final String useGuestVisit = "useGuestVisit";

    //时段访问限制
    static public final String accessForbiddenRange = "accessForbiddenRange";


    static public final String accessForbiddenTip = "accessForbiddenTip";


    static public final String closeInfo = "closeInfo";

    static public final String closeGuestVisitInfo = "closeGuestVisitInfo";

    static public final String useMail = "useMail";

    static public final String useSms = "useSms";


    static public final String mailSmtp = "mailSmtp";

    static public final String mailPop = "mailPop";

    static public final String mailUser = "mailUser";

    static public final String mailPassword = "mailPassword";

    static public final String manageMail = "manageMail";

    static public final String languageDAO = "languageDAO";

    static public final String rowCount = "rowCount";


    static public final String mobileRowCount = "mobileRowCount";


    static public final String notRefurbish = "notRefurbish";

    static public final String registerRole = "registerRole";

    static public final String guestRole = "guestRole";

    static public final String SUCCESS = "success";

    public static final String FAIL = "fail";

    static public final String ERROR = "error";

    static public final String mailActive = "mailActive";

    static public final String CACHE = "cache";

    static public final String DEFAULT_STORE = "defaultStore";



    static public final String useCache = "useCache";
    static public final String useTxWeb = "useTxWeb";

    static public final String onlinePrivilege = "onlinePrivilege";

    final static public String turnPageFile = "turnpage.ftl";

    //文档导出等地方统一使用的分割线
    static public final String splitLine = "#__________split_line___________#";

    static public final String license = "license";

    // 0,没有注册,1：专业版:2:企业版
    static public final String versionType = "versionType";

    //许可绑定方式
    static public final String versionBind = "versionBind";


    //图片显示方式,download 下载代理方式，或者直接 使用图片后缀
    static public final String photoModel = "photoModel";

    static public final int enterprise = 1;


    static public final String mac = "mac";

    static public final String singleLogin = "singleLogin";

    static public final String webServerTomcat = "tomcat";

    static public final String webServerResin = "resin";

    //常量begin
    //调试角色
    public static final String DEBUG_ROLE_ID = "10000";
    public static final String DEBUG_ROLE_NAME = "调试角色";

    public static final String SYSTEM_ORGANIZE_ID = "10000";
    public static final int SYSTEM_ID = 10000;
    public static final String SYSTEM_NAME = "system";

    public static final int GUEST_ID = 0;
    public static final String GUEST_NAME = "guest";
    //常量end

    //系统常用接口
    //语言
    static public final String language = "language";
    //配置
    static public final String config = "config";
    //字典库
    static public final String option = "option";


    static public final String versionFree = "Free";

    static public final String versionProfessional = "Professional";

    static public final String versionEnterprise = "Enterprise";

    //系统默认密钥
    static public final String secretKey = "secretKey";

    //密码算法模式
    static public final String cipherAlgorithm = "cipherAlgorithm";

    //加密算法偏移量
    static public final String cipherIv = "cipherIv";

    //系统对称加密算法
    static public final String symmetryAlgorithm = "symmetryAlgorithm";


    static public final String asymmetricAlgorithm = "asymmetricAlgorithm";

    //系统默认验证算法
    static public final String hashAlgorithm = "hashAlgorithm";

    //系统默认验证算法秘钥
    static public final String hashAlgorithmKey = "hashAlgorithmKey";


    //RSA验证算法
    static public final String signAlgorithm = "signAlgorithm";

    //在没有配置的情况下的密钥
    static public final String defaultDrug = StringUtil.cut(EncryptUtil.getMd5("sda90879385oi43jds3425"), 16, StringUtil.empty);


    //变动的，2个地方在用，远程登陆，和非对称加密
    final static public String publicKey = "publicKey";

    //私秘Key，2个地方在用，远程登陆，和非对称加密
    final static public String privateKey = "privateKey";


    //支付标识，是用户自己定义的标识
    final static public String paySign = "paySign";

    //加密签名
    final static public String sign = "sign";

    //加密签名的加密类型
    final static public String signType = "signType";

    //-----------------------------------------------------------------------------------------------------------------

    final public static String MESSAGE = "message";

    //remote begin
    final public static String jspxNetRoc = "jspx.net-roc";
    final public static String jspxNetRocVersion = "3.0";
    final public static String Protocol = "protocol";
    final public static String rocFormat = "format";
    //final public static String rocMethodCall = "methodCall";

    //方法调用方式的 actinName
    final public static String rocId = "id";
    final public static String rocMethod = "method";
    final public static String rocName = "name";
    final public static String rocVersion = "version";
    final public static String rocParams = "params";
    final public static String rocResult = "result";
    final public static String rocSecret = "secret-roc";
    //json 解析是否包括子对象数据

    final public static String THREAD_SAFE_MODE = "threadSafe";


    final public static String id_errorCode = "code";

    final public static String MESSAGES = "messages";
    final public static String id_info_type = "infoType";
    //final public static String return_id = "returnId"; //数据保存后返回的ID
    //remote end

    //信息类型begin
    final public static String infoType = "infoType";
    final public static String errorInfo = "error";
    final public static String warningInfo = "warning";
    final public static String promptInfo = "prompt";
    final public static String message = "message";
    final public static String FieldInfoList = "FieldInfoList";
    final public static String chain = "chain";
    static public final String unknown = "unknown";
    //信息类型end


    //用户标识begin
    static public final String marker_user_startTag = "[";

    static public final String marker_user_endTag = "]";

    static public final String marker_user_centerTag = ":";

    static public final String marker_group_startTag = "{";

    static public final String marker_group_endTag = "}";

    static public final String marker_group_centerTag = ":";

    static public final String marker_contacts_startTag = "<";

    static public final String marker_contacts_endTag = ">";

    static public final String marker_contacts_centerTag = ":";

    static public final String marker_follow_startTag = "「";

    static public final String marker_follow_endTag = "」";

    static public final String marker_follow_centerTag = ":";

    static public final String marker_split = StringUtil.SEMICOLON;
    //用户标识end

    //分页标签
    static public final String turnPageMark = "_ueditor_page_break_tag_";

    static public final String kindTurnPageMarkSplit = "<hr style=\"page-break-after:always;\" class=\"ke-pagebreak\" />";

    //定时任务全局开关
    static public final String USE_SCHEDULE = "useSchedule";

    //分片上传零时目录
    static public final String NAME_TYPE_CHUNK = "chunk";


    static public final String EXIF_SATE = "exifSate";

    static public final String userLoginUrl = "userLoginUrl";

    static public final String untitledUrl = "untitledUrl";


    //启动配置模式
    static public final String BOOT_CONF_MODE = "bootConfMode";

    //VCS启动配置
    static public final String VCS_URL = "vcs_url";
    static public final String VCS_LOCAL_PATH = "vcs_localPath";
    static public final String VCS_USER_NAME = "vcs_name";
    static public final String VCS_USER_PASSWORD = "vcs_password";

    //appollo启动配置
    static public final String APOLLO_ENV = "env";
    static public final String APOLLO_APP_ID = "app.id";
    static public final String APOLLO_BOOTSTRAP_ENABLED = "apollo.bootstrap.enabled";
    static public final String APOLLO_BOOTSTRAP_NAMESPACES = "apollo.bootstrap.namespaces";
    static public final String APOLLO_BOOTSTRAP_EAGERLOAD_ENABLED = "apollo.bootstrap.eagerLoad.enabled";
    static public final String APOLLO_META = "apollo.meta";
    //static public final String APOLLO_IP = "apollo.ip";


    static public final String HTTP_RPC_DOMAIN = "http.rpc.domain";
    static public final String HTTP_RPC_ROUTES = "http.rpc.routes.";

    //网络RPC调用服务发现模式
    static public final String SERVICE_DISCOVER_MODE = "serviceDiscoverMode";
    static public final String consul = "consul";

    //是否为嵌入tomcat方式运行
    static public final String SERVER_EMBED = "server.embed";
    static public final String SERVER_PORT = "server.port";

    static public final String SERVER_WEB_PATH = "server.web.path";

    static public final String SERVER_IP = "server.ip";

    static public final String SERVER_CORS = "server.cors";

    static public final String SERVER_THREADS = "server.threads";

    static public final String SERVER_CACHING_ALLOWED = "server.CachingAllowed";

    static public final String SERVER_MAX_POST_SIZE = "server.maxPostSize";

    static public final String SERVER_SESSION_REDIS = "server.session.redis";

    static public final String SERVER_REDISSON_SESSION_CONFIG = "server.redissonSessionConfig";

    static public final String SERVER_FILTER_MODE = "server.filter.mode";

    public final static String SPRING_PATH_SIGN = ".jar!/BOOT-INF/classes!";


    final public static String forceExit = "forceExit";

    final public static String USER_SESSION = "userSession";

    //开发这中心用户
    public final static String KEY_DCV_ENTER_USER_LIST = "dev_center_user_list";

    //表结构模型缓存
    public static final String KEY_SOBER_TABLE_CACHE = "sober_table_cache";


    //配置文件方式的启动环境
    public static final String JSPX_ENV_ACTIVE = "jspx.env.active";

    //任务注册中心begin
    public static final String USE_SCHEDULER_REGISTER = "useSchedulerRegister";
    public static final String SCHEDULER_REGISTER_URL = "schedulerRegisterUrl";
    public static final String SCHEDULER_REGISTER_API = "schedulerRegisterApi";
    public static final String SCHEDULER_REGISTER_NAME = "schedulerRegisterName";
    public static final String SCHEDULER_REGISTER_TOKEN = "schedulerRegisterToken";
    //任务注册中心end

}