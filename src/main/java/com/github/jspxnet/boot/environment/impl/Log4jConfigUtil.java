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


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.FileUtil;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.varia.StringMatchFilter;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-8
 * Time: 15:23:17
 * android.util.Log
 */

public class Log4jConfigUtil  {


    public static void createConfig() {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        FileUtil.makeDirectory(new File(FileUtil.getPathPart(envTemplate.getString(Environment.logErrorFile))));
        String log4jPath = envTemplate.getString(Environment.log4jPath);
        if (!StringUtil.isNull(log4jPath) && FileUtil.isFileExist(log4jPath)) {
            if (log4jPath.endsWith("xml")) {
                DOMConfigurator.configure(log4jPath);
            } else if (log4jPath.endsWith("properties")) {
                PropertyConfigurator.configure(log4jPath);
            } else {
                createDefaultConfig(Logger.getRootLogger());
            }
        } else {
            createDefaultConfig(Logger.getRootLogger());

        }
    }

    public static void createDefaultConfig(Logger log) {
        //Logger log = LogManager.getRootLogger();
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


            SystemLogFilter errorLevel = new SystemLogFilter();

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

            SystemLogFilter infoLevel = new SystemLogFilter();
            infoLevel.setLevelMax(Level.INFO);
            infoLevel.setLevelMin(Level.INFO);
            infoAppender.addFilter(infoLevel);
           // infoAppender.addFilter(new StopFilter());
            infoAppender.activateOptions();
            log.addAppender(infoAppender);
        }


        //------------------------------jspx begin
        if (envTemplate.getBoolean(Environment.logJspxError)) {
            RollingFileAppender errorAppender = new RollingFileAppender();
            errorAppender.setName(Environment.logJspxError);
            errorAppender.setBufferSize(64);
            errorAppender.setAppend(false);
            errorAppender.setEncoding(envTemplate.getString(Environment.encode));
            errorAppender.setFile(envTemplate.getString(Environment.logJspxErrorFile));
            errorAppender.setMaxFileSize("5120KB");
            errorAppender.setMaxBackupIndex(9);

            PatternLayout errorLayout = new PatternLayout();
            errorLayout.setConversionPattern("%d{yyyy-MM-dd HH:mm} %t %p %c %l - %m%n");
            errorAppender.setLayout(errorLayout);

            JspLogFilter errorLogFilter = new JspLogFilter();
            errorLogFilter.setLevelMax(Level.FATAL);
            errorLogFilter.setLevelMin(Level.ERROR);

            errorAppender.addFilter(errorLogFilter);
            //errorAppender.addFilter(new StopFilter());
            errorAppender.activateOptions();
            log.addAppender(errorAppender);

        }

        //info
        if (envTemplate.getBoolean(Environment.logJspxInfo)) {
            RollingFileAppender infoAppender = new RollingFileAppender();
            //RollingFileAppender infoAppender = new RollingFileAppender();
            infoAppender.setName(Environment.logJspxInfo);
            infoAppender.setBufferSize(128);
            infoAppender.setAppend(false);
            infoAppender.setEncoding(envTemplate.getString(Environment.encode));
            infoAppender.setFile(envTemplate.getString(Environment.logJspxInfoFile));
            infoAppender.setMaxFileSize("5120KB");
            infoAppender.setMaxBackupIndex(9);

            PatternLayout infoLayout = new PatternLayout();
            infoLayout.setConversionPattern("%d{yyyy-MM-dd HH:mm} %t %p %c %l - %m%n");
            infoAppender.setLayout(infoLayout);

            JspLogFilter classLogFilter = new JspLogFilter();
            classLogFilter.setLevelMax(Level.INFO);
            classLogFilter.setLevelMin(Level.INFO);

            infoAppender.addFilter(classLogFilter);
           // infoAppender.addFilter(new StopFilter());
            infoAppender.activateOptions();
            log.addAppender(infoAppender);
        }

        //debug
        if (envTemplate.getBoolean(Environment.logJspxDebug)) {
            WriterAppender debugAppender = new ConsoleAppender();
            debugAppender.setName(Environment.logJspxDebug);
            PatternLayout debugLayout = new PatternLayout();
            debugLayout.setConversionPattern("%-d{yyyy-MM-dd HH:mm} %c %l\r\n%m%n");
            debugAppender.setLayout(debugLayout);
            //debugAppender.addFilter(new StopFilter());
            debugAppender.activateOptions();
            log.addAppender(debugAppender);
        }

        if (log.getAllAppenders()==null||!log.getAllAppenders().hasMoreElements())
        {
            BasicConfigurator.configure();
        }
    }

    static class JspLogFilter extends StringMatchFilter {
        final static private String stringToMatch = "jspx";
        private Level levelMin = Level.INFO;
        private Level levelMax = Level.FATAL;

        @Override
        public int decide(LoggingEvent event) {
            if (event.getLoggerName().contains(stringToMatch) && (levelMin.toInt() <= event.getLevel().toInt() && event.getLevel().toInt() <= levelMax.toInt())) {
                return Filter.ACCEPT;
            } else {
                return Filter.DENY;
            }

        }

        @Override
        public boolean getAcceptOnMatch() {
            return true;
        }

        void setLevelMin(Level levelMin) {
            this.levelMin = levelMin;
        }

        void setLevelMax(Level levelMax) {
            this.levelMax = levelMax;
        }
    }

    static class SystemLogFilter extends StringMatchFilter {
        final static private String stringToMatch = "jspx";

        private Level levelMin = Level.INFO;
        private Level levelMax = Level.FATAL;


        @Override
        public int decide(LoggingEvent event) {
            if (!event.getLoggerName().contains(stringToMatch) && (levelMin.toInt() <= event.getLevel().toInt() && event.getLevel().toInt() <= levelMax.toInt())) {
                return Filter.ACCEPT;
            } else {
                return Filter.DENY;
            }
        }

        @Override
        public boolean getAcceptOnMatch() {
            return true;
        }

        void setLevelMin(Level levelMin) {
            this.levelMin = levelMin;
        }

        void setLevelMax(Level levelMax) {
            this.levelMax = levelMax;
        }
    }

    static class StopFilter extends StringMatchFilter {
        private final String[] stopNames = {"org.apache.http", "groovyx.net.http", "org.redisson", "org.jboss", "io.netty"};

        private boolean isInFilter(String name) {
            for (String stopName : stopNames) {
                if (name.contains(stopName)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int decide(LoggingEvent event) {
            if (isInFilter(event.getLoggerName())) {
                return Filter.DENY;
            } else {
                return Filter.ACCEPT;
            }
        }

        @Override
        public boolean getAcceptOnMatch() {
            return true;
        }

    }


}