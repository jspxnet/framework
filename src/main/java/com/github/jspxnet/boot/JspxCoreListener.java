/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot;

import com.github.jspxnet.boot.conf.AppolloBootConfig;
import com.github.jspxnet.boot.conf.VcsBootConfig;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.impl.LogBackConfigUtil;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.core.JSCache;
import com.github.jspxnet.cache.store.MemoryStore;
import com.github.jspxnet.enums.BootConfigEnumType;
import com.github.jspxnet.network.rpc.env.RpcConfig;
import com.github.jspxnet.network.rpc.service.NettyRpcServiceGroup;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.factory.EntryFactory;
import com.github.jspxnet.sioc.config.ConfigureContext;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.txweb.config.DefaultConfiguration;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.evasive.EvasiveConfiguration;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-12-1
 * Time: 17:51:48
 * author chenyuan
 * jspx.env.active=dev  配置启动环境
 */
@Slf4j
public class JspxCoreListener implements ServletContextListener {

    static private int startTimes = 0;
    private String copyright = StringUtil.empty;

    public JspxCoreListener() {

    }

    public static boolean isRun = false;

    private static final long START_CURRENT_TIME_MILLIS = System.currentTimeMillis();

    public static long getStartCurrentTimeMillis() {
        return START_CURRENT_TIME_MILLIS;
    }

    static public boolean isRun() {
        return isRun;
    }

    private static String defaultPath = null;

    public void setDefaultPath(String defaultPath) {
        if (!defaultPath.endsWith("/") && !defaultPath.endsWith("\\")) {
            defaultPath = defaultPath + "/";
        }
        JspxCoreListener.defaultPath = defaultPath;
    }

    @Override
    public void contextInitialized(javax.servlet.ServletContextEvent servletContextEvent) {
        boolean isAndroid = SystemUtil.isAndroid();
        //开始不能调用时间,调用了时间设置不了时区
        copyright = Environment.frameworkName + " " + Environment.VERSION + " " + Environment.licenses + " Powered By chenYuan ";
        log.info("-" + copyright + " start-" + startTimes++);
        log.debug("提示:启动参数中加入jspx.env.active=dev,配置文件指向到jspx-dev.properties,这样可以缺环配置到dev模式");

        //////初始化环境变量 begin
        try {
            //log.debug("开始载入org.bouncycastle.jce.provider.BouncyCastleProvider,它是加密解密的重要库");
            Security.addProvider(new BouncyCastleProvider());
            ClassLoader loader = BouncyCastleProvider.class.getClassLoader();
            URL url = loader.getResource("org/bouncycastle/jce/provider/BouncyCastleProvider.class");
            log.debug("BouncyCastle加载路径：{}", url);
            Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            log.debug("完成载入org.bouncycastle.jce.provider.BouncyCastleProvider");
        } catch (Exception e) {
            log.error("1. 加密类bcprov-jdk18on-xxx.jar 拷贝到 %JAVA_HOME%\\jre\\lib\\ext 目录，一般jdk安装后有两个位置\r\n"
                    + "2. 在 jdk安装目录下（%JAVA_HOME%\\jre\\lib\\security）修改 java.security 文件修改为 security.provider.7=org.bouncycastle.jce.provider.BouncyCastleProvider\r\n" +
                      "3.上边两步如果不能启动，添加java -Djava.ext.dirs=%JAVA_HOME%\\jre\\lib\\ext 指定到jar所在目录",e);
        }
        String jspx_properties_file = Environment.jspx_properties_file;
        String active_config = System.getenv(Environment.JSPX_ENV_ACTIVE);
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        if (!StringUtil.isNull(active_config))
        {
            log.info("当前启动配置文件指向到jspx-{}.properties",active_config);
            Map<String,Object> valueMap = new HashMap<>();
            valueMap.put("active",active_config);
            jspx_properties_file = EnvFactory.getPlaceholder().processTemplate(valueMap,Environment.jspx_properties_template);
            jspxConfiguration.setDefaultConfigFile(jspx_properties_file);
            jspxConfiguration.loadPath();
        }

        if (!StringUtil.isNull(defaultPath)) {
            jspxConfiguration.setDefaultPath(defaultPath);
        }

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        Map<String,String> properties = envTemplate.readDefaultProperties(jspxConfiguration.getDefaultPath() + jspx_properties_file);
        String bootConfMode = properties.getOrDefault(Environment.BOOT_CONF_MODE,Environment.defaultValue);
        if (BootConfigEnumType.VCS.getName().equalsIgnoreCase(bootConfMode))
        {
            synchronized (this) {
                VcsBootConfig vcsBootConfig = new VcsBootConfig();
                vcsBootConfig.bind(properties);
            }
        }
        if (BootConfigEnumType.APPOLLO.getName().equalsIgnoreCase(bootConfMode))
        {
            //appollo 启动配置
            AppolloBootConfig appolloBootConfig = new AppolloBootConfig();
            appolloBootConfig.bind(properties);
        }
        else
        {
           envTemplate.createJspxEnv(StringUtil.isNull(jspxConfiguration.getDefaultPath())?"":jspxConfiguration.getDefaultPath() + jspx_properties_file);
        }

        //默认方式
        envTemplate.createPathEnv(jspxConfiguration.getDefaultPath());
        //jdbc配置
        File confFile = new File(jspxConfiguration.getDefaultPath(),jspx_properties_file);
        if (!FileUtil.isFileExist(confFile.getPath())) {
            String info = "not found " + confFile + ",不能找到基本的配置文件" + jspx_properties_file + ",构架将不能正常工作";
            log.error(info);
            return;
        }

        log.info("create jspx.net system Environment config file:{}",jspx_properties_file);
        //环境配置
        envTemplate.createSystemEnv();

        //修复占位符号
        log.info("create placeholder Environment");
        try {
            envTemplate.restorePlaceholder();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogBackConfigUtil.createConfig();
        //////初始化环境变量 end

        log.info("default Path=" + envTemplate.getString(Environment.defaultPath));
        log.info("template Path=" + envTemplate.getString(Environment.templatePath));

        //////////////////////初始化脚本语言环境 begin
        Configurable templateConfigurable = TemplateConfigurable.getInstance();

        String defaultPath = envTemplate.getString(Environment.defaultPath);
        if (defaultPath.contains(".jar!"))
        {
            templateConfigurable.setSearchPath(null);
        } else
        {
            templateConfigurable.setSearchPath(new String[]{defaultPath, envTemplate.getString(Environment.templatePath)});
        }

        templateConfigurable.setAutoIncludes(StringUtil.split(envTemplate.getString(Environment.autoIncludes), StringUtil.SEMICOLON));
        templateConfigurable.setAutoImports(StringUtil.split(envTemplate.getString(Environment.autoImports), StringUtil.SEMICOLON));
        templateConfigurable.setGlobalMap(envTemplate.getVariableMap());
        //////////////////////初始化脚本语言环境 end

        ////////////导入Ioc配置 begin
        IocContext iocContext = ConfigureContext.getInstance();
        iocContext.setConfigFile(jspxConfiguration.getIocConfigFile());
        EntryFactory beanFactory = (EntryFactory) com.github.jspxnet.boot.EnvFactory.getBeanFactory();
        beanFactory.setIocContext(iocContext);

        //系统默认超时时间begin
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");
        //系统默认超时时间end

        //jdk java.sdk.security 文件中添加配置        sdk.security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
        SystemUtil.encode = envTemplate.getString(Environment.systemEncode, SystemUtil.OS == SystemUtil.WINDOWS ? "GBK" : StandardCharsets.UTF_8.name());
        ////////////////////////AOP begin
        if (!isAndroid) {
            log.info("repairEncode=" + envTemplate.getString(Environment.repairEncode));
            log.info("httpServer=" + envTemplate.getString(Environment.httpServerName));
            log.info("user.timezone=" + System.getProperty("user.timezone"));
        }
        ////////////////////////AOP end

        if (!envTemplate.getBoolean(Environment.useCache))
        {
            JSCache cache = new JSCache();
            cache.setName(DefaultCache.class.getName());
            cache.setStore(new MemoryStore());
            JSCacheManager.getCacheManager().registeredCache(cache);
        } else {
            SoberUtil.clearSystemCache();
        }
        //启动的时候清空缓存

        com.github.jspxnet.txweb.config.Configuration configuration = DefaultConfiguration.getInstance();
        try {
            configuration.loadConfigMap();
        } catch (Exception e) {
            log.error("载入Web配置错误", e);
        }


        TxWebConfigManager.getInstance().checkLoad();
        //rpc服务器,提供外部rpctcp调用 begin
        if (RpcConfig.getInstance().isUseNettyRpc()) {
            log.info("启动RPC服务器");
            NettyRpcServiceGroup.getInstance().start();
        }
        //rpc服务器,提供外部rpctcp调用 end

        //载入定时任务
        beanFactory.initScheduler();

        boolean dbLog = envTemplate.getBoolean(Environment.DB_LOG);
        if (dbLog)
        {
            //切换到
            log.info("日志切换到数据库保存");
            LogBackConfigUtil.changeDbLogBackConfig();
        }
        log.info("-" + copyright + " start completed " + (isAndroid ? "for Android" : " J2SDK"));
        isRun = true;
    }

    @Override
    public void contextDestroyed(javax.servlet.ServletContextEvent servletContextEvent) {
        log.info(Environment.frameworkName + " " + copyright + " shutdown start");
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        boolean forceExit = envTemplate.getBoolean(Environment.forceExit);

        //Evasive配置卸载begin
        EvasiveConfiguration.getInstance().shutdown();
        log.info("Evasive config clean");
        //Evasive配置卸载begin

        if (RpcConfig.getInstance().isUseNettyRpc()) {
            log.info("关闭RPC服务器");
            NettyRpcServiceGroup.getInstance().stop();
        }

        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        beanFactory.getIocContext().shutdown();
        beanFactory.shutdown();
        log.info("bean factory shutdown");

        //关闭缓存和线程begin
        JSCacheManager.shutdown();
        log.info("JSCache shutdown");
        //关闭缓存和线程end

        Dispatcher.shutdown();

        //卸载jdbc驱动begin
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver d = null;
        while (drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement();
                DriverManager.deregisterDriver(d);
            } catch (SQLException ex) {
                log.error(String.format("Error deregistering driver %s", d));
            }
        }
        try {
          ClassUtil.invokeStaticMethod("com.mysql.jdbc.AbandonedConnectionCleanupThread","uncheckedShutdown",null);
        } catch (Exception e)
        {
            try {
                ClassUtil.invokeStaticMethod("com.mysql.jdbc.AbandonedConnectionCleanupThread","shutdown",null);
            } catch (Exception exception) {
                //..
            }
            //...
        }

        //定时任务
        SchedulerManager schedulerManager = SchedulerTaskManager.getInstance();
        schedulerManager.shutdown();
        log.info("scheduler shutdown");
        //关闭定时器和其他线程end
        try {
            DaemonThreadFactory.shutdown();
            log.info("Thread shutdown");
        } catch (Exception exception) {
            //...exception.printStackTrace();
            if (forceExit)
            {
                System.exit(0);
            }
        }
        if (forceExit)
        {
            System.exit(0);
        }
        isRun = false;
        log.info(Environment.frameworkName + " " + copyright + " dispatcher shutdown completed ");
     }
}