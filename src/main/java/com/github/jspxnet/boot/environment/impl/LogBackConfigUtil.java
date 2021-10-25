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
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.FileUtil;
import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-8
 * Time: 15:23:17
 * android.util.Log
 */

public class LogBackConfigUtil {
    public static void createConfig() {

        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof ch.qos.logback.classic.LoggerContext)
        {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            createLogBackConfig(lc);
        } else
        {
            createLog4jConfig();
        }
    }

    public static void createLogBackConfig(LoggerContext lc)
    {
        lc.reset();
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
            InputStream stream = LogBackConfigUtil.class.getResourceAsStream(Environment.DEFAULT_LOG_NAME);
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
                configurator.doConfigure(inputSource);
            } catch (JoranException e) {
                System.err.println("1.默认路径是否配置错误;2.检查defaultlog.xml文件是否存在");
                e.printStackTrace();
                StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
            }
        }
        lc.setPackagingDataEnabled(true);
    }


    public static void createLog4jConfig() {
        Logger log = LogManager.getRootLogger();
        log.setAdditivity(true);
        log.setLevel(Level.ALL);
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();

        //log.removeAllAppenders();
        //不想显示的日志放在这里
      /*  Set<String> loggers = new HashSet<>(Arrays.asList("org.apache", "groovyx.net.http", "org.redisson", "org.jboss", "io.netty"));
        for (String httplog : loggers) {
            Logger logger = Logger.getLogger(httplog);
            logger.setLevel(Level.DEBUG);
            logger.setAdditivity(false);
        }*/

        if (envTemplate.getBoolean(Environment.logError)) {
            RollingFileAppender errorAppender = new RollingFileAppender();
            errorAppender.setName(Environment.logError);
            errorAppender.setBufferSize(64);
            errorAppender.setAppend(true);
            errorAppender.setEncoding(envTemplate.getString(Environment.encode));
            errorAppender.setFile(envTemplate.getString(Environment.logErrorFile));
            errorAppender.setMaxFileSize("5120KB");
            errorAppender.setMaxBackupIndex(9);



            PatternLayout errorLayout = new PatternLayout();
            errorLayout.setConversionPattern("%d{yyyy-MM-dd HH:mm} %t %p %c %l - %m%n");
            errorAppender.setLayout(errorLayout);


            LevelRangeFilter errorLevel = new LevelRangeFilter();

            errorLevel.setLevelMax(Level.FATAL);
            errorLevel.setLevelMin(Level.ERROR);
            errorAppender.addFilter(errorLevel);
            errorAppender.activateOptions();
            log.addAppender(errorAppender);
        }

        //info
        if (envTemplate.getBoolean(Environment.logInfo)) {
            RollingFileAppender infoAppender = new RollingFileAppender();
            infoAppender.setName(Environment.logInfo);
            infoAppender.setBufferSize(128);
            infoAppender.setAppend(false);
            infoAppender.setEncoding(envTemplate.getString(Environment.encode));
            infoAppender.setFile(envTemplate.getString(Environment.logInfoFile));
            infoAppender.setMaxFileSize("5120KB");
            infoAppender.setMaxBackupIndex(9);

            PatternLayout infoLayout = new PatternLayout();
            infoLayout.setConversionPattern("%d{yyyy-MM-dd HH:mm} %t %p %c %l - %m%n");
            infoAppender.setLayout(infoLayout);

            LevelRangeFilter infoLevel = new LevelRangeFilter();
            infoLevel.setLevelMax(Level.INFO);
            infoLevel.setLevelMin(Level.INFO);
            infoAppender.addFilter(infoLevel);
            // infoAppender.addFilter(new StopFilter());
            infoAppender.activateOptions();
            log.addAppender(infoAppender);
        }


    }


}