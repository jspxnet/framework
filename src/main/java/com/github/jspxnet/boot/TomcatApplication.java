package com.github.jspxnet.boot;

import com.github.jspxnet.boot.annotation.JspxNetBootApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.JspxNetFilter;
import com.github.jspxnet.txweb.dispatcher.JspxNetListener;
import com.github.jspxnet.txweb.dispatcher.ServletDispatcher;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.thetransactioncompany.cors.CORSConfiguration;
import com.thetransactioncompany.cors.CORSFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.redisson.tomcat.JndiRedissonSessionManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2021/3/1 18:30
 * description: tomcat启动类
 * com.github.jspxnet.boot.TomcatApplication
 **/

@Slf4j
public class TomcatApplication {

    //@Parameter(names = "-port", description = "tomcat server port")
    private static int port;
    //@Parameter(names = "-webPath", description = "web path")
    private static String webPath;
    //@Parameter(names = "-ip", description = "default 127.0.0.1")
    private static String ip;
    //@Parameter(names = "-cors", description = "boolean is website cors")
    private static boolean cors;

    // @Parameter(names = "-threads", description = "threads tomcat start threads")
    private static int threads;

    //@Parameter(names = "-config", description = "application config path")
    private static int config;

    private static int maxPostSize;


    private static JspxNetBootApplication jspxNetBootApplication;

    public static void setJspxNetBootApplication(JspxNetBootApplication jspxNetBootApplication) {
        TomcatApplication.jspxNetBootApplication = jspxNetBootApplication;
    }

    public static void main(String[] args) throws Exception {

        //把目录的绝对的路径获取到
        //arg[0] 运行路径
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        if (!ArrayUtil.isEmpty(args)) {
            log.debug("tomcat param:{}", args[0]);
            jspxConfiguration.setDefaultPath(args[0]);
        }
        System.setProperty("log4j.ignoreTCL", "true");
        String defaultPath = jspxConfiguration.getDefaultPath();
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
<<<<<<< HEAD
        Properties properties = environmentTemplate.readDefaultProperties(FileUtil.mendFile((defaultPath == null ? "" : defaultPath) + "/" + Environment.jspx_properties_file));

        if (properties.size() > 3 && properties.containsKey(Environment.SERVER_PORT) && properties.containsKey(Environment.SERVER_WEB_PATH)) {
            port = StringUtil.toInt(properties.getProperty(Environment.SERVER_PORT, "8080"));
            webPath = properties.getProperty(Environment.SERVER_WEB_PATH, System.getProperty("user.dir"));
            ip = properties.getProperty(Environment.SERVER_IP, "127.0.0.1");
            cors = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CORS, "true"));
            threads = StringUtil.toInt(properties.getProperty(Environment.SERVER_THREADS, "3"));
            maxPostSize = StringUtil.toInt(properties.getProperty(Environment.SERVER_MAX_POST_SIZE, "10000000"));
=======
        Map<String,String>  properties = environmentTemplate.readDefaultProperties(FileUtil.mendFile((defaultPath == null ? "" : defaultPath) + "/" + Environment.jspx_properties_file));

        if (properties.size() > 3 && properties.containsKey(Environment.SERVER_PORT) && properties.containsKey(Environment.SERVER_WEB_PATH)) {
            port = StringUtil.toInt(properties.getOrDefault(Environment.SERVER_PORT, "8080"));
            webPath = properties.getOrDefault(Environment.SERVER_WEB_PATH, System.getProperty("user.dir"));
            ip = properties.getOrDefault(Environment.SERVER_IP, "127.0.0.1");
            cors = StringUtil.toBoolean(properties.getOrDefault(Environment.SERVER_CORS, "true"));
            threads = StringUtil.toInt(properties.getOrDefault(Environment.SERVER_THREADS, "3"));
            maxPostSize = StringUtil.toInt(properties.getOrDefault(Environment.SERVER_MAX_POST_SIZE, "10000000"));
>>>>>>> dev


        } else if (TomcatApplication.jspxNetBootApplication != null && properties.size() < 2) {
            port = TomcatApplication.jspxNetBootApplication.port();
            webPath = TomcatApplication.jspxNetBootApplication.webPath();
            ip = TomcatApplication.jspxNetBootApplication.ip();
            cors = TomcatApplication.jspxNetBootApplication.cors();
            threads = TomcatApplication.jspxNetBootApplication.threads();
            maxPostSize = TomcatApplication.jspxNetBootApplication.maxPostSize();

        } else {
<<<<<<< HEAD
            port = StringUtil.toInt(properties.getProperty(Environment.SERVER_PORT, "8080"));
            webPath = properties.getProperty(Environment.SERVER_WEB_PATH, System.getProperty("user.dir"));
            ip = properties.getProperty(Environment.SERVER_IP, "127.0.0.1");
            cors = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CORS, "true"));
            threads = StringUtil.toInt(properties.getProperty(Environment.SERVER_THREADS, "3"));
            maxPostSize = StringUtil.toInt(properties.getProperty(Environment.SERVER_MAX_POST_SIZE, "10000000"));
=======
            port = StringUtil.toInt(properties.getOrDefault(Environment.SERVER_PORT, "8080"));
            webPath = properties.getOrDefault(Environment.SERVER_WEB_PATH, System.getProperty("user.dir"));
            ip = properties.getOrDefault(Environment.SERVER_IP, "127.0.0.1");
            cors = StringUtil.toBoolean(properties.getOrDefault(Environment.SERVER_CORS, "true"));
            threads = StringUtil.toInt(properties.getOrDefault(Environment.SERVER_THREADS, "3"));
            maxPostSize = StringUtil.toInt(properties.getOrDefault(Environment.SERVER_MAX_POST_SIZE, "10000000"));
>>>>>>> dev
        }


        if (webPath != null && webPath.contains("${") && webPath.contains("}")) {
            webPath = EnvFactory.getPlaceholder().processTemplate(new HashMap<String, Object>((Map) System.getProperties()), webPath);
        }

        if (StringUtil.isNull(webPath)) {
            webPath = System.getProperty("user.dir");
        }

        boolean cachingAllowed = true;
        if (properties.containsKey(Environment.SERVER_CACHING_ALLOWED)) {
<<<<<<< HEAD
            cachingAllowed = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CACHING_ALLOWED));
        }

        boolean openRedis = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_SESSION_REDIS));
        String redisConfig = properties.getProperty(Environment.SERVER_REDISSON_SESSION_CONFIG);
        if (!StringUtil.isEmpty(webPath)) {
            FileUtil.makeDirectory(webPath);
        }
        boolean filterMode = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_FILTER_MODE));
=======
            cachingAllowed = StringUtil.toBoolean(properties.get(Environment.SERVER_CACHING_ALLOWED));
        }

        boolean openRedis = StringUtil.toBoolean(properties.get(Environment.SERVER_SESSION_REDIS));
        String redisConfig = properties.get(Environment.SERVER_REDISSON_SESSION_CONFIG);
        if (!StringUtil.isEmpty(webPath)) {
            FileUtil.makeDirectory(webPath);
        }
        boolean filterMode = StringUtil.toBoolean(properties.get(Environment.SERVER_FILTER_MODE));
>>>>>>> dev
        System.setProperty("catalina.home", webPath);
        System.setProperty("catalina.base", webPath);
        System.setProperty("user.dir", new File(webPath, "WEB-INF").getPath());


<<<<<<< HEAD
        String encode = properties.getProperty(Environment.encode, Environment.defaultEncode);
=======
        String encode = properties.getOrDefault(Environment.encode, Environment.defaultEncode);
>>>>>>> dev

        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        connector.setURIEncoding(encode);
        //让 URI 和 body 编码一致。(针对POST请求)
        connector.setUseBodyEncodingForURI(true);
        connector.setMaxPostSize(maxPostSize); //默认100M 上传大小限制
        connector.setMaxSavePostSize(-1);
        connector.setEnableLookups(false);
        connector.setMaxCookieCount(threads * 100000);
        connector.setUseBodyEncodingForURI(true);

        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        //设置最大连接数
        protocol.setMaxConnections(threads * 1000);
        //设置最大线程数
        protocol.setMaxThreads(threads * 1000);
        protocol.setConnectionTimeout(80000);

        //设置Host
        tomcat.setConnector(connector);

        tomcat.setSilent(true);
        tomcat.setAddDefaultWebXmlToWebapp(true);
        tomcat.setHostname("localhost");
        tomcat.setBaseDir(webPath);

        //我们会根据xml配置文件来
        Host host = tomcat.getHost();
        host.setName("localhost");
        host.setCreateDirs(false);
        //host.setAppBase(FileUtil.mendPath(file.getParent()));
        host.setAppBase(webPath);
        Context standardContext = tomcat.addWebapp("", webPath);
        //100000
        //92160

        StandardRoot standardRoot = new StandardRoot();
        standardRoot.setCacheMaxSize((threads * 20) * 1024); //最大100M
        standardRoot.setCacheObjectMaxSize(1024);
        standardRoot.setCachingAllowed(cachingAllowed);
        standardContext.setResources(standardRoot);

        //前面的那个步骤只是把Tomcat起起来了，但是没啥东西
        //要把class加载进来,把启动的工程加入进来了
        //StandardContext standardContext = new StandardContext();
        standardContext.setPath("");
        standardContext.setPrivileged(true);
        standardContext.setAddWebinfClassesResources(false);


        standardContext.addLifecycleListener(new Tomcat.FixContextListener());

        if (filterMode) {
            FilterDef jspxFilterDef = new FilterDef();
            jspxFilterDef.setFilterName("JspxNetFilter");
            jspxFilterDef.setDisplayName("jspx.net 默认过滤器");
            jspxFilterDef.setFilter(new JspxNetFilter());
            standardContext.addFilterDef(jspxFilterDef);

            FilterMap jspxFilterMap = new FilterMap();
            jspxFilterMap.setFilterName(jspxFilterDef.getFilterName());
            jspxFilterMap.addURLPattern("/*");
            standardContext.addFilterMap(jspxFilterMap);
        } else {
            //servlet方式
            tomcat.addServlet("", "jspxServlet", new ServletDispatcher());
            standardContext.addServletMappingDecoded("/*", "jspxServlet");
        }
<<<<<<< HEAD

        //放入环境变量begin
        for (Object key : properties.keySet()) {
            String keyName = (String) key;
            if (!environmentTemplate.containsName(keyName)) {
                environmentTemplate.put(keyName, properties.getProperty(keyName));
            }
        }
        environmentTemplate.put(Environment.SERVER_PORT, port);
        environmentTemplate.put(Environment.SERVER_WEB_PATH, webPath);
        environmentTemplate.put(Environment.SERVER_IP, ip);
        environmentTemplate.put(Environment.SERVER_CORS, cors);
        environmentTemplate.put(Environment.SERVER_THREADS, threads);
        environmentTemplate.put(Environment.SERVER_MAX_POST_SIZE, maxPostSize);
        environmentTemplate.put(Environment.SERVER_FILTER_MODE, filterMode);
        environmentTemplate.put(Environment.SERVER_SESSION_REDIS, openRedis);
        environmentTemplate.put(Environment.SERVER_EMBED, true);
=======

        //放入环境变量begin
        for (Object key : properties.keySet()) {
            String keyName = (String) key;
            if (!environmentTemplate.containsName(keyName)) {
                environmentTemplate.put(keyName, properties.get(keyName));
            }
        }
        environmentTemplate.put(Environment.SERVER_PORT, port);
        environmentTemplate.put(Environment.SERVER_WEB_PATH, webPath);
        environmentTemplate.put(Environment.SERVER_IP, ip);
        environmentTemplate.put(Environment.SERVER_CORS, cors);
        environmentTemplate.put(Environment.SERVER_THREADS, threads);
        environmentTemplate.put(Environment.SERVER_MAX_POST_SIZE, maxPostSize);
        environmentTemplate.put(Environment.SERVER_FILTER_MODE, filterMode);
        environmentTemplate.put(Environment.SERVER_SESSION_REDIS, openRedis);
        environmentTemplate.put(Environment.SERVER_EMBED, true);

>>>>>>> dev
        //放入环境变量end


        //放入默认的JSP
        tomcat.addServlet("", "jsp", new JspServlet());
        standardContext.addServletMappingDecoded("*.jsp", "jsp");

        StandardJarScanFilter scanFilter = new StandardJarScanFilter();
        scanFilter.setDefaultPluggabilityScan(false);
        scanFilter.setDefaultTldScan(true);
        scanFilter.setTldSkip("*.jar");

        //scanner.setJarScanFilter(scanFilter);
        JarScanner scanner = new StandardJarScanner();
        scanner.setJarScanFilter(scanFilter);
        standardContext.setJarScanner(scanner);

        if (openRedis && !StringUtil.isNull(redisConfig)) {
            TomcatRedissonSessionManager redissonSessionManager = new TomcatRedissonSessionManager();
            redissonSessionManager.setBroadcastSessionEvents(true);
            redissonSessionManager.setReadMode("REDIS");
            redissonSessionManager.setUpdateMode("DEFAULT");
            redissonSessionManager.setConfigPath(redisConfig);
            standardContext.setManager(redissonSessionManager);

            ContextResourceLink contextResourceLink = new ContextResourceLink();
            contextResourceLink.setName("bean/redisson");
            contextResourceLink.setGlobal("bean/redisson");
            contextResourceLink.setType("org.redisson.api.RedissonClient");
            standardContext.getNamingResources().addResourceLink(contextResourceLink);

            JndiRedissonSessionManager jndiRedissonSessionManager = new JndiRedissonSessionManager();
            jndiRedissonSessionManager.setReadMode("REDIS");
            jndiRedissonSessionManager.setJndiName("bean/redisson");
            standardContext.setManager(redissonSessionManager);

            //------------------------------

            ContextResource redisResource = new ContextResource();
            redisResource.setName("bean/redisson");
            redisResource.setAuth("Container");
            redisResource.setProperty("factory", TomcatJndiRedissonFactory.class.getName());
            tomcat.getServer().getGlobalNamingResources().addResource(redisResource);
        }

        /*
        <Manager className="org.redisson.tomcat.RedissonSessionManager"
          configPath="c:/jwebs/tomcat/conf/redisson.conf" readMode="REDIS" updateMode="DEFAULT"/>

	<ResourceLink name="bean/redisson" global="bean/redisson"
		  type="org.redisson.api.RedissonClient" />

    <Manager className="org.redisson.tomcat.JndiRedissonSessionManager"
         readMode="REDIS"
         jndiName="bean/redisson" />
         */

        StandardContext tmpContext = (StandardContext) standardContext;
        if (!StringUtil.isEmpty(webPath)) {
            tmpContext.setWorkDir(webPath);
            File webFile = new File(webPath, "WEB-INF/web.xml");
            if (webFile.exists() && webFile.isFile()) {
                tmpContext.setDefaultWebXml(webFile.getPath());
                log.debug("default web.xml:{}", webFile.getPath());
            } else {
                webFile = new File(webPath, "web.xml");
                if (webFile.exists()&&webFile.isFile())
                {
                    tmpContext.setDefaultWebXml(webFile.getPath());
                    log.debug("default web.xml:{}", webFile.getPath());
                }
            }
        }

        //我们要把Servlet设置进去
        if (cors) {
            FilterDef corsFilterDef = new FilterDef();
            CORSFilter corsFilter = new CORSFilter();

            Properties props = new Properties();
            props.setProperty("cors.allowOrigin", StringUtil.ASTERISK);
            props.setProperty("cors.supportedMethods", "GET, POST, HEAD, PUT, DELETE");
            props.setProperty("cors.supportedHeaders", "Accept, Origin, X-Requested-With,Authorization,Content-Type, Last-Modified");
            props.setProperty("cors.exposedHeaders", "Set-Cookie");
            props.setProperty("cors.supportsCredentials", "Set-Cookie");
            props.setProperty("cors.supportsCredentials", "true");

            CORSConfiguration corsConfiguration = new CORSConfiguration(props);
            corsFilter.setConfiguration(corsConfiguration);
            corsFilterDef.setFilter(corsFilter);
            corsFilterDef.setFilterClass(CORSFilter.class.getName());
            corsFilterDef.setFilterName("corsFilter");
            corsFilterDef.setDisplayName("跨域");
            standardContext.addFilterDef(corsFilterDef);

            FilterMap corsFilterMap = new FilterMap();
            corsFilterMap.setFilterName(corsFilterDef.getFilterName());
            corsFilterMap.addURLPattern("/*");

            standardContext.addFilterMap(corsFilterMap);

        }

        //Tomcat跑起来
        //设置Tomcat的端口tomcat.setPort(9091)。两种写法都可以设置端口
        tmpContext.addApplicationLifecycleListener(new JspxNetListener());
        tmpContext.setValidateClientProvidedNewSessionId(true);
        tmpContext.setSessionTimeout(60);
        tmpContext.setSessionCookiePathUsesTrailingSlash(false);
        tmpContext.setOverride(false);
        tmpContext.setDistributable(true);
        tmpContext.setSessionCookiePath("/");
        tmpContext.setSkipMemoryLeakChecksOnJvmShutdown(true);
        tmpContext.setReplaceWelcomeFiles(true);

        standardContext.setAddWebinfClassesResources(true);
        standardContext.setCrossContext(true);
        standardContext.setUseHttpOnly(true);
        standardContext.setCookies(true);
        standardContext.setSessionCookiePathUsesTrailingSlash(true);
        standardContext.setResponseCharacterEncoding(encode);
        standardContext.setRequestCharacterEncoding(encode);
        standardContext.setReloadable(true);

        if (standardContext.getLoader() == null) {
            WebappLoader webappLoader = new WebappLoader();
            webappLoader.setDelegate(true);
            webappLoader.setLoaderClass(ParallelWebappClassLoader.class.getName());
            webappLoader.setLoaderInstance(new ParallelWebappClassLoader());

            standardContext.setLoader(webappLoader);
        }


        //强制Tomcat server等待，避免main线程执行结束后关闭
        Server server = tomcat.getServer();
        ClassLoader classLoader = server.getParentClassLoader();
        if (classLoader != null) {
            classLoader.setDefaultAssertionStatus(true);
        } else {
            ParallelWebappClassLoader webappLoader = new ParallelWebappClassLoader();
            webappLoader.setDelegate(true);
            server.setParentClassLoader(webappLoader);
        }
        server.setAddress(ip);
        server.setUtilityThreads(threads);
        Dispatcher.setRealPath(webPath);

        System.out.println("defaultPath=" + defaultPath);
<<<<<<< HEAD
=======
        System.out.println("webPath=" + webPath);
>>>>>>> dev
        System.out.println("config server=" + ip + ":" + port);
        tomcat.start();

        server.await();
    }
}