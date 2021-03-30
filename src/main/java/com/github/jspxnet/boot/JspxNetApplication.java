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
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-5-25
 * Time: 16:31:31
 */

@Slf4j
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
     * 嵌入spring方式运行
     * @param fileName 默认配置文件
     * @param context spring 上下文
     */
    public static void runInSpring(String fileName, ApplicationContext context)
    {
        runInSpring( fileName,  context,null);
    }

    /**
     *
     * 嵌入spring方式运行
     * @param fileName 默认配置文件
     * @param context spring 上下文
     * @param cacheList 超小应用直接配置缓存
     */
    public static void runInSpring(String fileName, ApplicationContext context,Class<?>[] cacheList)
    {
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        jspxConfiguration.setDefaultConfigFile(fileName);
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        envTemplate.createPathEnv(jspxConfiguration.getDefaultPath());
        envTemplate.createSystemEnv();
        envTemplate.put(Environment.useTxWeb,false);

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

        //jdk java.sdk.security 文件中添加配置        sdk.security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
        SystemUtil.encode = envTemplate.getString(Environment.systemEncode, SystemUtil.OS == SystemUtil.WINDOWS ? "GBK" : "UTF-8");

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




    public static void destroy() {
        JSPX_CORE_LISTENER.contextDestroyed(null);
    }

    public static void restart() {
        JSPX_CORE_LISTENER.contextDestroyed(null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSPX_CORE_LISTENER.contextInitialized(null);
    }

    //得到服务器运行天数

    public static long runDay() {
        return DateUtil.compareDay(new Date(JspxCoreListener.getStartCurrentTimeMillis()));
    }

    //得到服务器运行天数

    public static Date getRunDate() {
        return new Date(JspxCoreListener.getStartCurrentTimeMillis());
    }
}