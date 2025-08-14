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

import com.github.jspxnet.boot.annotation.JspxNetBootApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.store.MemoryStore;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.config.ConfigureContext;
import com.github.jspxnet.sioc.factory.EntryFactory;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-5-25
 * Time: 16:31:31
 */
public final class JspxNetApplication {
    private JspxNetApplication()
    {

    }

    final private static JspxCoreListener JSPX_CORE_LISTENER = new JspxCoreListener();

    public static boolean checkRun() {
        return com.github.jspxnet.boot.JspxCoreListener.isRun();
    }

    public static void autoRun() {
        autoRun(null);
    }

    public static void autoRun(String defaultPath) {
        if (JspxCoreListener.isRun()) {
            return;
        }
        if (!StringUtil.isNull(defaultPath)) {
            JSPX_CORE_LISTENER.setDefaultPath(defaultPath);
        }
        JSPX_CORE_LISTENER.contextInitialized(null);

    }

    public static void run(Class<?> cls,String[] args) {
        try {
            JspxNetBootApplication jspxNetBootApplication = cls.getAnnotation(JspxNetBootApplication.class);
            if (jspxNetBootApplication!=null)
            {
                TomcatApplication.setJspxNetBootApplication(jspxNetBootApplication);
            }
            TomcatApplication.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 最小嵌入方式，实用二开环境
     * @param fileName 配置文件
     */
    public static void runInEmbed(String fileName)
    {
        runInEmbed(null,fileName,null);
    }


    /**
     * 最小嵌入方式，实用二开环境,嵌入致远OA类似系统
     * @param path 默认配置路径
     * @param fileName 默认配置文件
     * @param context spring import org.springframework.context.ApplicationContext上下文
     */
    public static void runInEmbed(String path,String fileName,Object context)
    {
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        jspxConfiguration.setDefaultConfigFile(fileName);
        if (!StringUtil.isEmpty(path))
        {
            jspxConfiguration.setDefaultPath(path);
        }

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        envTemplate.createPathEnv(jspxConfiguration.getDefaultPath());
        if (context!=null)
        {
            Object springEnvironment = BeanUtil.getProperty(context,"getEnvironment");
            Object debug = BeanUtil.getProperty(springEnvironment,"getProperty",new Object[]{Environment.DEBUG},false);
            Object useCache = BeanUtil.getProperty(springEnvironment,"getProperty",new Object[]{Environment.useCache},false);

            envTemplate.put(Environment.DEBUG,debug);
            envTemplate.put("jspxDebug",debug);
            envTemplate.put("useCache",useCache);
            envTemplate.put(Environment.useTxWeb,false);
        } else
        {
            envTemplate.put(Environment.DEBUG,false);
            envTemplate.put("jspxDebug",false);
            envTemplate.put("useCache",false);
            envTemplate.put(Environment.useTxWeb,false);
        }
        envTemplate.createSystemEnv();


        //////////////////////初始化脚本语言环境 begin
        Configurable templateConfigurable = TemplateConfigurable.getInstance();
        String defaultPath = jspxConfiguration.getDefaultPath();
        if (defaultPath.contains(".jar!"))
        {
            templateConfigurable.setSearchPath(null);
        } else
        {
            templateConfigurable.setSearchPath(new String[]{defaultPath});
        }

        envTemplate.createJspxEnv(jspxConfiguration.getConfigFilePath());

        templateConfigurable.setAutoIncludes(StringUtil.split(envTemplate.getString(Environment.autoIncludes), StringUtil.SEMICOLON));
        templateConfigurable.setAutoImports(StringUtil.split(envTemplate.getString(Environment.autoImports), StringUtil.SEMICOLON));
        templateConfigurable.setGlobalMap(envTemplate.getVariableMap());
        //////////////////////初始化脚本语言环境 end

        ////////////导入Ioc配置 begin
        IocContext iocContext = ConfigureContext.getInstance();
        iocContext.setConfigFile(jspxConfiguration.getIocConfigFile());
        EntryFactory beanFactory = (EntryFactory) com.github.jspxnet.boot.EnvFactory.getBeanFactory();
        beanFactory.setIocContext(iocContext);

        if (context!=null)
        {
            SpringBeanContext.setApplicationContext(context);
        }
        //载入定时任务
        beanFactory.initScheduler();
    }

    /**
     * 嵌入spring方式运行
     * @param fileName 默认配置文件
     * @param context spring org.springframework.context.ApplicationContext 上下文
     */
    public static void runInSpring(String fileName, Object context)
    {
        runInSpring( fileName,  context,null);
    }

    /**
     *
     * 嵌入spring方式运行
     * @param fileName 默认配置文件
     * @param context spring org.springframework.context.ApplicationContext 上下文
     * @param cacheList 超小应用直接配置缓存
     */
    public static void runInSpring(String fileName, Object context,Class<?>[] cacheList)
    {
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        jspxConfiguration.setDefaultConfigFile(fileName);
        String defaultPath = jspxConfiguration.getDefaultPath();
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        envTemplate.createPathEnv(defaultPath);

        Object springEnvironment = BeanUtil.getProperty(context,"getEnvironment");
        Object debug = BeanUtil.getProperty(springEnvironment,"getProperty",new Object[]{Environment.DEBUG},false);
        Object useCache = BeanUtil.getProperty(springEnvironment,"getProperty",new Object[]{Environment.useCache},false);


        envTemplate.put(Environment.DEBUG,debug);
        envTemplate.put("jspxDebug",debug);
        envTemplate.put("useCache",useCache);
        envTemplate.put(Environment.useTxWeb,false);
        envTemplate.createSystemEnv();

        //////////////////////初始化脚本语言环境 begin
        Configurable templateConfigurable = TemplateConfigurable.getInstance();
        if (defaultPath.contains(".jar!"))
        {
            templateConfigurable.setSearchPath(null);
        } else
        {
            templateConfigurable.setSearchPath(new String[]{defaultPath});
        }
        envTemplate.createJspxEnv(jspxConfiguration.getConfigFilePath());

        templateConfigurable.setAutoIncludes(StringUtil.split(envTemplate.getString(Environment.autoIncludes), StringUtil.SEMICOLON));
        templateConfigurable.setAutoImports(StringUtil.split(envTemplate.getString(Environment.autoImports), StringUtil.SEMICOLON));
        templateConfigurable.setGlobalMap(envTemplate.getVariableMap());
        //////////////////////初始化脚本语言环境 end

        ////////////导入Ioc配置 begin
        IocContext iocContext = ConfigureContext.getInstance();
        iocContext.setConfigFile(jspxConfiguration.getIocConfigFile());

        EntryFactory beanFactory = (EntryFactory) com.github.jspxnet.boot.EnvFactory.getBeanFactory();
        beanFactory.setIocContext(iocContext);

        //载入定时任务
        beanFactory.initScheduler();

        //系统默认超时时间begin
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");
        //系统默认超时时间end

        SpringBeanContext.setApplicationContext(context);

        //jdk java.sdk.security 文件中添加配置        sdk.security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
        SystemUtil.encode = envTemplate.getString(Environment.systemEncode, SystemUtil.OS == SystemUtil.WINDOWS ? "GBK" : StandardCharsets.UTF_8.name());

        if (cacheList!=null)
        {
            for (Class<?> cls:cacheList)
            {
                try {
                    JSCacheManager.getCacheManager().createCache(new MemoryStore(), cls,100,100,false,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 卸载服务
     */
    public static void destroy() {
        JSPX_CORE_LISTENER.contextDestroyed(null);
    }

    /**
     * 重启服务
     */
    public static void restart() {
        JSPX_CORE_LISTENER.contextDestroyed(null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSPX_CORE_LISTENER.contextInitialized(null);
    }

    /**
     *
     * @return 得到服务器运行天数
     */
    public static long runDay() {
        return DateUtil.compareDay(new Date(JspxCoreListener.getStartCurrentTimeMillis()));
    }

    /**
     *
     * @return 得到服务器运行天数
     */
    public static Date getRunDate() {
        return new Date(JspxCoreListener.getStartCurrentTimeMillis());
    }
}