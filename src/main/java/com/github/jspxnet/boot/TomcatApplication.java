package com.github.jspxnet.boot;


import com.github.jspxnet.boot.conf.JarDefaultConfig;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.JspxNetListener;
import com.github.jspxnet.txweb.dispatcher.ServletDispatcher;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;
import org.apache.catalina.webresources.CachedResource;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.redisson.tomcat.JndiRedissonSessionManager;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/1 18:30
 * description: tomcat启动类
 *
 **/
@Slf4j
public class TomcatApplication {
    private final static Tomcat TOMCAT = new Tomcat();
    public static void main(String[] args) throws Exception{
        //把目录的绝对的路径获取到
        //arg[0] 运行路径

 /*       JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        if (!ArrayUtil.isEmpty(args)) {
            jspxConfiguration.setDefaultPath(args[0]);
        }
*/
       // EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
       // Properties properties = envTemplate.readDefaultProperties(jspxConfiguration.getDefaultPath() + Environment.jspx_properties_file);
        String defaultPath = "D:\\website\\webapps\\root\\WEB-INF\\classes\\";
        File configFile = new File(defaultPath,Environment.jspx_properties_file);
        Properties properties = new Properties();
        properties.load(new FileReader(configFile));

        int port = StringUtil.toInt(properties.getProperty(Environment.SERVER_PORT,"8080"));
        String webPath = properties.getProperty(Environment.SERVER_WEB_PATH,System.getProperty("user.dir"));
        String ip = properties.getProperty(Environment.SERVER_IP,"127.0.0.1");
        boolean cors = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CORS,"true"));
        int threads = StringUtil.toInt(properties.getProperty(Environment.SERVER_THREADS,"3"));

        boolean openRedis = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_SESSION_REDIS));
        String redisConfig = properties.getProperty(Environment.SERVER_REDISSON_SESSION_CONFIG);

        log.debug("tomcat web path:{}, port:{},session share:{}",webPath, port,openRedis);


        File file = new File(webPath);
        FileUtil.makeDirectory(file);


        //TOMCAT.setBaseDir(file.getParent());
        Connector connector = TOMCAT.getConnector();
        connector.setPort(port);
        connector.setURIEncoding("UTF-8");
        //设置Host

        Host host = TOMCAT.getHost();
        TOMCAT.setSilent(true);

        //我们会根据xml配置文件来
        host.setName("localhost");
        //host.setCreateDirs(true);
        //host.setAppBase(FileUtil.mendPath(file.getParent()));
        //host.setAppBase("d:/website/webapps");
        Context standardContext =  TOMCAT.addWebapp("","d:/website/webapps/root");

        //前面的那个步骤只是把Tomcat起起来了，但是没啥东西
        //要把class加载进来,把启动的工程加入进来了
        //StandardContext standardContext = new StandardContext();


        standardContext.setPath("");
        standardContext.setPrivileged(false);
        //standardContext.setWorkDir(file.getAbsolutePath());
        //standardContext.setOriginalDocBase("/"+ file.getName());
        //standardContext.setDocBase("");
        standardContext.setCrossContext(true);
        standardContext.setRequestCharacterEncoding("UTF-8");


       // standardContext.setReloadable(false);

       // standardContext.addApplicationLifecycleListener(new JspxNetListener());

        //standardContext.addLifecycleListener(new JreMemoryLeakPreventionListener());
        standardContext.addLifecycleListener(new GlobalResourcesLifecycleListener());
        standardContext.addLifecycleListener(new VersionLoggerListener());
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
/*

        File tempFile = EnvFactory.getFile("/resources/tomcat/web.xml");
        if (!FileUtil.isFileExist(tempFile))
        {
            InputStream inputStream = JarDefaultConfig.class.getResourceAsStream("/resources/tomcat/web.xml");
            if (inputStream!=null)
            {
                File tempCheckFile = new File(System.getProperty("java.io.tmpdir"),System.currentTimeMillis()+".xml");
                if (StreamUtil.copy(inputStream,new FileOutputStream(tempCheckFile),1024)&&tempCheckFile.length()>2)
                {
                    tempFile = tempCheckFile;
                }
            }
        }
        if (!FileUtil.isFileExist(tempFile))
        {
            tempFile = new File(defaultPath,"/resources/tomcat/web.xml");
        }
        if (tempFile!=null&&tempFile.isFile()&&tempFile.length()>2)
        {

            tmpContext.setDefaultContextXml(tempFile.getAbsolutePath());
        }
*/

        File webFile = new File(file,"WEB-INF/web.xml");
        if (webFile.exists()&&webFile.isFile())
        {
            tmpContext.setDefaultWebXml(webFile.getAbsolutePath());
            log.debug("default web.xml:{}",webFile.getAbsolutePath());
        }
        tmpContext.setSkipMemoryLeakChecksOnJvmShutdown(true);
        //我们要把Servlet设置进去

/*        if (cors)
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
        }*/


        //Tomcat跑起来
        //设置Tomcat的端口tomcat.setPort(9091)。两种写法都可以设置端口


        TOMCAT.start();

        //强制Tomcat server等待，避免main线程执行结束后关闭
        Server server = TOMCAT.getServer();
/*
        server.setAddress(ip);
        server.setUtilityThreads(threads);
*/
        server.await();
    }
}
