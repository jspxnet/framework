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

import java.util.Map;
import java.io.*;

import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.scriptmark.load.StringSource;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-27
 * Time: 16:22:16
 */
@Slf4j
public class PlaceholderImpl implements Placeholder {
    private String currentPath = FileUtil.mendPath(System.getProperty("user.dir")); //当前路径，f方便include 使用
    private String rootDirectory = FileUtil.mendPath(System.getProperty("user.dir")); //路径范围
    //private Configurable configuration = new TemplateConfigurable();
/*

    static {
        configuration.put(ScriptmarkEnv.NumberFormat, "###0.######");
        configuration.put(ScriptmarkEnv.DateFormat, DateUtil.DAY_FORMAT);
        configuration.put(ScriptmarkEnv.DateTimeFormat, "yyyy-MM-dd HH:mm");
        configuration.put(ScriptmarkEnv.TimeFormat, "HH:mm");
        configuration.put(ScriptmarkEnv.Template_update_delay, 3600);
        configuration.put(ScriptmarkEnv.MacroCallTag, "@");
        configuration.put(ScriptmarkEnv.Language, "JavaScript");
        configuration.put(ScriptmarkEnv.Syncopate, "<>");
        configuration.put(ScriptmarkEnv.VariableBegin, "${");
        configuration.put(ScriptmarkEnv.VariableEnd, "}");
        configuration.put(ScriptmarkEnv.VariableSafeBegin, "${");
        configuration.put(ScriptmarkEnv.escapeVariable, "\\");
    }
*/

    public PlaceholderImpl() {

    }

    /**
     * @param valueMap       变量map
     * @param templateString 字符方式
     * @return 模版转换后的字符串
     */
    @Override
    public String processTemplate(Map<String, Object> valueMap, String templateString) {
        if (templateString == null || valueMap==null) {
            return StringUtil.empty;
        }
        try (Writer writer = new StringWriter()) {
            ScriptMark scriptMark = new ScriptMarkEngine(ScriptmarkEnv.noCache, new StringSource(templateString), TemplateConfigurable.getInstance());
            scriptMark.process(writer, valueMap);
            return writer.toString();
        } catch (Exception e) {
            log.error(templateString, e);
        }
        return StringUtil.empty;
    }

    /**
     * @param valueMap 变量map
     * @param file     文件方式
     * @param encode   编码
     * @return 解析后字符串
     */
    @Override
    public String processTemplate(Map<String, Object> valueMap, File file, String encode) {
        Source fs = null;
        if (file.isFile())
        {
            fs = new FileSource(file, file.getName(), encode);
        } else
        {
            try {
                fs =  new StringSource(IoUtil.autoReadText(file.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Writer writer = new StringWriter()) {
            ScriptMark scriptMark = new ScriptMarkEngine(EncryptUtil.getMd5(file.getAbsolutePath()), fs, TemplateConfigurable.getInstance());
            scriptMark.process(writer, valueMap);
            writer.close();
            return writer.toString();
        } catch (Exception e) {
            log.error(String.format("processTemplate file %s", file.getPath()), e);
            return StringUtil.empty;
        }
    }

    @Override
    public String getCurrentPath() {
        return currentPath;
    }

    @Override
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    @Override
    public String getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}