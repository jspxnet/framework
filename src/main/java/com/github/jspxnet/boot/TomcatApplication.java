package com.github.jspxnet.boot;


import com.beust.jcommander.Parameter;
import com.github.jspxnet.boot.annotation.JspxNetBootApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.ServletDispatcher;
import com.github.jspxnet.utils.*;
import com.thetransactioncompany.cors.CORSConfiguration;
import com.thetransactioncompany.cors.CORSFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.redisson.tomcat.JndiRedissonSessionManager;
import java.io.File;
import java.util.Properties;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/1 18:30
 * description: tomcat启动类
 * com.github.jspxnet.boot.TomcatApplication
 **/
@Slf4j
public class TomcatApplication {
    private final static Tomcat TOMCAT = new Tomcat();
    @Parameter(names = "-port", description = "tomcat server port")
    private static int port;
    @Parameter(names = "-webPath", description = "web path")
    private static String webPath;
    @Parameter(names = "-ip", description = "default 127.0.0.1")
    private static String ip;
    @Parameter(names = "-cors", description = "boolean is website cors")
    private static boolean cors;

    @Parameter(names = "-threads", description = "threads tomcat start threads")
    private static int threads;

    @Parameter(names = "-config", description = "application config path")
    private static int config;


    private static JspxNetBootApplication jspxNetBootApplication;
    public static void setJspxNetBootApplication(JspxNetBootApplication jspxNetBootApplication) {
        TomcatApplication.jspxNetBootApplication = jspxNetBootApplication;
    }

    public static void main(String[] args) throws Exception{
        //把目录的绝对的路径获取到
        //arg[0] 运行路径

        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        if (!ArrayUtil.isEmpty(args)) {
            log.debug("tomcat param:{}",args[0]);
            jspxConfiguration.setDefaultPath(args[0]);
        }
        String defaultPath = jspxConfiguration.getDefaultPath();
        Properties properties = EnvFactory.getEnvironmentTemplate().readDefaultProperties(FileUtil.mendFile((defaultPath==null?"":defaultPath) + "/" + Environment.jspx_properties_file));

        log.debug("defaultPath:{}",defaultPath);

        if (TomcatApplication.jspxNetBootApplication!=null)
        {
            port = TomcatApplication.jspxNetBootApplication.port();
            webPath = TomcatApplication.jspxNetBootApplication.webPath();
            ip = TomcatApplication.jspxNetBootApplication.ip();
            cors = TomcatApplication.jspxNetBootApplication.cors();
            threads = TomcatApplication.jspxNetBootApplication.threads();
        } else {
            port = StringUtil.toInt(properties.getProperty(Environment.SERVER_PORT,"8080"));
            webPath = properties.getProperty(Environment.SERVER_WEB_PATH,System.getProperty("user.dir"));
            ip = properties.getProperty(Environment.SERVER_IP,"127.0.0.1");
            cors = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CORS,"true"));
            threads = StringUtil.toInt(properties.getProperty(Environment.SERVER_THREADS,"3"));
        }

        boolean openRedis = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_SESSION_REDIS));
        String redisConfig = properties.getProperty(Environment.SERVER_REDISSON_SESSION_CONFIG);
        log.debug("tomcat web path:{}, port:{},session share:{}",webPath, port,openRedis);

        File file = new File(webPath);
        FileUtil.makeDirectory(file);

        Connector connector = TOMCAT.getConnector();
        connector.setPort(port);
        connector.setURIEncoding(Environment.defaultEncode);
        //让 URI 和 body 编码一致。(针对POST请求)
        connector.setUseBodyEncodingForURI(true);
        connector.setMaxPostSize(-1);
        connector.setMaxSavePostSize(-1);
        connector.setEnableLookups(true);
        //设置Host


        TOMCAT.setSilent(true);
        TOMCAT.setAddDefaultWebXmlToWebapp(true);
        TOMCAT.setHostname("localhost");
        TOMCAT.setBaseDir(webPath);

        //我们会根据xml配置文件来
        Host host = TOMCAT.getHost();
        host.setName("localhost");
        host.setCreateDirs(false);
        //host.setAppBase(FileUtil.mendPath(file.getParent()));
        //host.setAppBase("d:/website/webapps");
        Context standardContext = TOMCAT.addWebapp("",webPath);

        //前面的那个步骤只是把Tomcat起起来了，但是没啥东西
        //要把class加载进来,把启动的工程加入进来了
        //StandardContext standardContext = new StandardContext();

        standardContext.setPath("");
        standardContext.setPrivileged(false);
        standardContext.setAddWebinfClassesResources(true);





        //standardContext.addLifecycleListener(new JreMemoryLeakPreventionListener());
        //standardContext.addLifecycleListener(new GlobalResourcesLifecycleListener());
        standardContext.addLifecycleListener(new Tomcat.FixContextListener());
       // host.addChild(standardContext);

        TOMCAT.addServlet("", "jspxServlet", new ServletDispatcher());
        standardContext.addServletMappingDecoded("*.jhtml", "jspxServlet");
        standardContext.addServletMappingDecoded("*.jwc", "jspxServlet");
        standardContext.addServletMappingDecoded("*.md", "jspxServlet");


        Dispatcher.setRealPath(webPath);

        TOMCAT.addServlet("", "jsp", new JspServlet());
        standardContext.addServletMappingDecoded("*.jsp", "jsp");

        JarScanner scanner = new StandardJarScanner();
        StandardJarScanFilter scanFilter = new StandardJarScanFilter();
        scanFilter.setDefaultPluggabilityScan(false);
        scanFilter.setDefaultTldScan(false);
        scanner.setJarScanFilter(scanFilter);
        standardContext.setJarScanner(scanner);
        standardContext.setAddWebinfClassesResources(true);

        if (openRedis&&!StringUtil.isNull(redisConfig))
        {
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
            redisResource.setProperty("factory",TomcatJndiRedissonFactory.class.getName());
            TOMCAT.getServer().getGlobalNamingResources().addResource(redisResource);
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

        StandardContext tmpContext = (StandardContext)standardContext;


        tmpContext.setWorkDir(webPath);
        File webFile = new File(file,"WEB-INF/web.xml");
        if (webFile.exists()&&webFile.isFile())
        {
            tmpContext.setDefaultWebXml(webFile.getPath());
            log.debug("default web.xml:{}",webFile.getPath());
        }

        //我们要把Servlet设置进去

       if (cors)
        {
            FilterDef filterDef = new FilterDef();
            CORSFilter corsFilter = new CORSFilter();

            Properties props = new Properties();
            props.setProperty("cors.allowOrigin","*");
            props.setProperty("cors.supportedMethods","GET, POST, HEAD, PUT, DELETE");
            props.setProperty("cors.supportedHeaders","Accept, Origin, X-Requested-With,Authorization,Content-Type, Last-Modified");
            props.setProperty("cors.exposedHeaders","Set-Cookie");
            props.setProperty("cors.supportsCredentials","Set-Cookie");
            props.setProperty("cors.supportsCredentials","true");

            CORSConfiguration corsConfiguration = new CORSConfiguration(props);
            corsFilter.setConfiguration(corsConfiguration);
            filterDef.setFilter(corsFilter);
            filterDef.setFilterName("corsFilter");
            filterDef.setDisplayName("跨域");

            standardContext.addFilterDef(filterDef);
        }


        //Tomcat跑起来
        //设置Tomcat的端口tomcat.setPort(9091)。两种写法都可以设置端口
        //tmpContext.addApplicationLifecycleListener(new JspxNetListener());
        tmpContext.setValidateClientProvidedNewSessionId(true);
        tmpContext.setSessionTimeout(60);
        tmpContext.setSessionCookiePathUsesTrailingSlash(false);
        tmpContext.setOverride(true);
        tmpContext.setDistributable(true);
        tmpContext.setSessionCookiePath("/");
        tmpContext.setSkipMemoryLeakChecksOnJvmShutdown(true);
        tmpContext.setReplaceWelcomeFiles(true);

        standardContext.setAddWebinfClassesResources(true);
        standardContext.setCrossContext(true);
        standardContext.setUseHttpOnly(true);
        standardContext.setCookies(true);
        standardContext.setSessionCookiePathUsesTrailingSlash(true);
        standardContext.setResponseCharacterEncoding(Environment.defaultEncode);
        standardContext.setRequestCharacterEncoding(Environment.defaultEncode);
        standardContext.setReloadable(true);

        /*
        　　defaultSessionTimeOut="3600" isWARExpanded="true"
　　isWARValidated="false" isInvokerEnabled="true"
　　isWorkDirPersistent="false

         */
        TOMCAT.start();

        //强制Tomcat server等待，避免main线程执行结束后关闭
        Server server = TOMCAT.getServer();
        server.setAddress(ip);
        server.setUtilityThreads(threads);
        server.await();
    }
}
