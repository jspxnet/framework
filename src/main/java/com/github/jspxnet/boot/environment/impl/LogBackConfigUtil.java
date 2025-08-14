/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.environment.impl;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.dblog.JspxDBAppender;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-8
 * Time: 15:23:17
 * android.util.Log
 */

public class LogBackConfigUtil {
    static boolean init = false;
    public static void createConfig() {
        if (init)
        {
            return;
        }
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof ch.qos.logback.classic.LoggerContext)
        {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            //spring 环境，让给spring配置
            if (lc.getObject("org.springframework.boot.logging.LoggingSystem")==null)
            {
                createLogBackConfig(lc);
            }
        }
        else
        {
            createLog4jConfig();
        }
        init = true;
    }

    public static void createLogBackConfig(LoggerContext lc)
    {

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        boolean isDefaultConfig = false;
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        File file = new File(envTemplate.getString(Environment.defaultPath),Environment.DEFAULT_LOAD_LOG_NAME);
        String defaultConfigTxt = null;
        if (file.isFile())
        {
            try {
                defaultConfigTxt = IoUtil.autoReadText(file.getPath(),Environment.defaultEncode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtil.isEmpty(defaultConfigTxt))
        {

            InputStream  stream = LogBackConfigUtil.class.getResourceAsStream(Environment.DEFAULT_LOG_NAME);
            if (stream!=null)
            {
                byte[] bytes = FileUtil.getBytesFromInputStream(stream);
                if (bytes==null)
                {
                    bytes = new byte[0];
                }
                try {
                    defaultConfigTxt = new String(bytes,Environment.defaultEncode);
                    isDefaultConfig = true;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if (StringUtil.isNull(defaultConfigTxt))
        {
            System.err.println("LogBack defaultConfig:" + defaultConfigTxt);
        }
        Map<String, Object> valueMap = envTemplate.getVariableMap();
        if (!valueMap.containsKey("logMaxHistory"))
        {
            valueMap.put("logMaxHistory",60);
        }
        String confTxt = isDefaultConfig?EnvFactory.getPlaceholder().processTemplate(valueMap,defaultConfigTxt):defaultConfigTxt;
        if (!StringUtil.isEmpty(confTxt))
        {
            org.xml.sax.InputSource inputSource = new InputSource(new StringReader(confTxt));
            try {
                lc.reset();
                configurator.doConfigure(inputSource);
            } catch (JoranException e) {
                System.err.println("1.默认路径是否配置错误;2.检查defaultlog.xml文件是否存在");
                StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
            }
        }
        lc.setPackagingDataEnabled(true);
    }

    public static void changeDbLogBackConfig()
    {
        Object logContext = LoggerFactory.getILoggerFactory();
        if (logContext==null || logContext.getClass().getName().contains("Log4jLoggerFactory"))
        {
            System.out.println("日志配置错误,不能切换到数据库");
            return;
        }

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        boolean isDefaultConfig = false;
        String defaultConfigTxt = null;
        InputStream  stream = LogBackConfigUtil.class.getResourceAsStream(Environment.DB_LOG_NAME);
        if (stream!=null)
        {
            byte[] bytes = FileUtil.getBytesFromInputStream(stream);
            if (bytes==null)
            {
                bytes = new byte[0];
            }
            try {
                defaultConfigTxt = new String(bytes,Environment.defaultEncode);
                isDefaultConfig = true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        if (StringUtil.isNull(defaultConfigTxt))
        {
            System.err.println("LogBack defaultConfig:" + defaultConfigTxt);
        }
        Map<String, Object> valueMap = envTemplate.getVariableMap();
        if (!valueMap.containsKey("logMaxHistory"))
        {
            valueMap.put("logMaxHistory",60);
        }

        String confTxt = isDefaultConfig?EnvFactory.getPlaceholder().processTemplate(valueMap,defaultConfigTxt):defaultConfigTxt;
        if (!StringUtil.isEmpty(confTxt))
        {
            org.xml.sax.InputSource inputSource = new InputSource(new StringReader(confTxt));
            try {
                configurator.doConfigure(inputSource);
            } catch (JoranException e) {
                System.err.println("1.默认路径是否配置错误;2.检查defaultlog.xml文件是否存在");
                e.printStackTrace();

            }
        }

        JspxDBAppender<ILoggingEvent> appender = new JspxDBAppender<>();
        appender.setContext(loggerContext);
        appender.start();
        appender.setName("DATABASE");
        loggerContext.setPackagingDataEnabled(true);

    }



    public static void createLog4jConfig() {

        boolean isDefaultConfig = false;
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        File file = new File(envTemplate.getString(Environment.defaultPath),Environment.LOG4J_CONFIG_NAME);
        String defaultConfigTxt = null;
        if (file.isFile())
        {
            try {
                defaultConfigTxt = IoUtil.autoReadText(file.getPath(),Environment.defaultEncode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtil.isEmpty(defaultConfigTxt))
        {
            InputStream stream = LogBackConfigUtil.class.getResourceAsStream(Environment.LOG4J_CONFIG_NAME);
            if (stream!=null)
            {
                byte[] bytes = FileUtil.getBytesFromInputStream(stream);
                if (bytes==null)
                {
                    bytes = new byte[0];
                }
                try {
                    defaultConfigTxt = new String(bytes,Environment.defaultEncode);
                    isDefaultConfig = true;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        Map<String, Object> valueMap = envTemplate.getVariableMap();
        String confTxt = isDefaultConfig?EnvFactory.getPlaceholder().processTemplate(valueMap,defaultConfigTxt):defaultConfigTxt;
        if (!StringUtil.isEmpty(confTxt))
        {
            // 创建XML配置解析工厂
            ConfigurationFactory configFactory = XmlConfigurationFactory.getInstance();
            // 设置配置工厂为XML方式
            ConfigurationFactory.setConfigurationFactory(configFactory);
            // 转换配置为输入流

            // 转换配置流为配置源
            ConfigurationSource configurationSource = null;
            try {
                configurationSource = new ConfigurationSource(new ByteArrayInputStream(confTxt.getBytes()));
                // 获取日志环境
                org.apache.logging.log4j.spi.LoggerContext ctx =  LogManager.getContext(false);
                // 生成新的配置
                Configuration configuration = configFactory.getConfiguration((org.apache.logging.log4j.core.LoggerContext)ctx, configurationSource);
                // 使用新配置重新配置环境
                ((org.apache.logging.log4j.core.LoggerContext) ctx).reconfigure(configuration);
                // 忽略其它资源释放代码
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}