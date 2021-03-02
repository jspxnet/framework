package com.github.jspxnet.boot;


import com.github.jspxnet.boot.conf.JarDefaultConfig;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.txweb.dispatcher.JspxNetListener;
import com.github.jspxnet.txweb.dispatcher.ServletDispatcher;
import com.github.jspxnet.utils.*;
import com.thetransactioncompany.cors.CORSConfiguration;
import com.thetransactioncompany.cors.CORSFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    static private final Tomcat TOMCAT = new Tomcat();
    public static void main(String[] args) throws Exception{
        //把目录的绝对的路径获取到
        //arg[0] 运行路径
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        if (!ArrayUtil.isEmpty(args)) {
            jspxConfiguration.setDefaultPath(args[0]);
        }

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        Properties properties = envTemplate.readDefaultProperties(jspxConfiguration.getDefaultPath() + Environment.jspx_properties_file);

        int port = StringUtil.toInt(properties.getProperty(Environment.SERVER_PORT,"8080"));
        String path = properties.getProperty(Environment.SERVER_WEB_PATH,System.getProperty("user.dir"));
        String ip = properties.getProperty(Environment.SERVER_IP,"127.0.0.1");
        boolean cors = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CORS,"true"));
        int threads = StringUtil.toInt(properties.getProperty(Environment.SERVER_THREADS,"3"));

        boolean openRedis = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_SESSION_REDIS));
        String redisConfig = properties.getProperty(Environment.SERVER_REDISSON_SESSION_CONFIG);

        log.debug("tomcat path:{}, port:{},session share:{}",path, port,openRedis);

        //设置Tomcat的端口tomcat.setPort(9091)。两种写法都可以设置端口
        Connector connector = TOMCAT.getConnector();
        connector.setPort(port);
        //设置Host
        Host host = TOMCAT.getHost();
        host.setAutoDeploy(false);


        File file = new File(path);
        FileUtil.makeDirectory(file);

        //我们会根据xml配置文件来
        host.setName("localhost");
        host.setCreateDirs(true);
        host.setAutoDeploy(false);

        host.setAppBase(file.getParent());
        //前面的那个步骤只是把Tomcat起起来了，但是没啥东西
        //要把class加载进来,把启动的工程加入进来了
        Connector conn = TOMCAT.getConnector();
        conn.setURIEncoding(properties.getProperty(Environment.encode,Environment.defaultEncode));

        Context context =TOMCAT.addContext(host, "","/"+ file.getName());
        context.setReloadable(true);


        if (openRedis&&!StringUtil.isNull(redisConfig))
        {
            Manager manager = context.getManager();
            TomcatRedissonSessionManager redissonSessionManager = new TomcatRedissonSessionManager();
            redissonSessionManager.setBroadcastSessionEvents(true);
            redissonSessionManager.setReadMode("REDIS");
            redissonSessionManager.setUpdateMode("DEFAULT");
            redissonSessionManager.setConfigPath(redisConfig);
            context.setManager(redissonSessionManager);
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

/*        Wrapper wrapper =  TOMCAT.addServlet("", "jspxServlet", new ServletDispatcher());
        wrapper.addMapping("/");
        wrapper.setEnabled(true);*/
        if(context instanceof StandardContext){
            StandardContext standardContext = (StandardContext) context;

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
                tempFile = new File(jspxConfiguration.getDefaultPath(),"/resources/tomcat/web.xml");

            }
            if (tempFile!=null&&tempFile.isFile()&&tempFile.length()>2)
            {
                standardContext.setDefaultContextXml(tempFile.getAbsolutePath());
            }

            File webFile = new File(file,"WEB-INF/web.xml");
            if (webFile.exists()&&webFile.isFile())
            {
                standardContext.setDefaultWebXml(webFile.getAbsolutePath());
                log.debug("default web.xml:{}",webFile.getAbsolutePath());
            }
            standardContext.setAddWebinfClassesResources(true);
            standardContext.setSkipMemoryLeakChecksOnJvmShutdown(true);
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
            standardContext.addApplicationEventListener(new JspxNetListener());
        }

        //Tomcat跑起来
        TOMCAT.start();

        //强制Tomcat server等待，避免main线程执行结束后关闭
        Server server = TOMCAT.getServer();
        server.setAddress(ip);
        server.setUtilityThreads(threads);
        server .await();
    }
}
