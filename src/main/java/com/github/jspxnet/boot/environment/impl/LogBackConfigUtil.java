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

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        File file = new File(envTemplate.getString(Environment.defaultPath),Environment.DEFAULT_LOAD_LOG_NAME);
        String defaultConfigTxt = null;
        if (FileUtil.isFileExist(file))
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
            byte[] bytes = FileUtil.getBytesFromInputStream(stream);
            if (bytes==null)
            {
                bytes = new byte[0];
            }
            try {
                defaultConfigTxt = new String(bytes,Environment.defaultEncode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (StringUtil.isNull(defaultConfigTxt))
        {
            System.err.println("LogBack defaultConfigTxt:" + defaultConfigTxt);
        }
        Map<String, Object> valueMap = envTemplate.getVariableMap();
        if (!valueMap.containsKey("logMaxHistory"))
        {
            valueMap.put("logMaxHistory",60);
        }
        String confTxt = EnvFactory.getPlaceholder().processTemplate(valueMap,defaultConfigTxt);
        org.xml.sax.InputSource inputSource = new InputSource(new StringReader(confTxt));
        try {
            configurator.doConfigure(inputSource);
        } catch (JoranException e) {
            System.err.println("默认路径是否配置错误");
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }


}