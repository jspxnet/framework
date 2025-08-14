package com.github.jspxnet.boot.environment.impl;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.config.SqlMapTemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.scriptmark.load.StringSource;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2020/10/14 0:20
 * description: jspbox
 **/
@Slf4j
public class SqlMapPlaceholderImpl implements Placeholder {
    final static protected EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
    final static protected Configurable configurable = SqlMapTemplateConfigurable.getInstance();
    private String currentPath = FileUtil.mendPath(System.getProperty("user.dir")); //当前路径，f方便include 使用
    private String rootDirectory = FileUtil.mendPath(System.getProperty("user.dir")); //路径范围


    public SqlMapPlaceholderImpl() {

    }

    /**
     * @param valueMap       变量map
     * @param templateString 字符方式
     * @return 模版转换后的字符串
     */
    @Override
    public String processTemplate(Map<String, Object> valueMap, String templateString) {
        if (templateString == null) {
            return StringUtil.empty;
        }
        configurable.put(ScriptmarkEnv.FixUndefined,ENV_TEMPLATE.getBoolean(Environment.templateFixUndefined));
        try (Writer writer = new StringWriter()) {
            ScriptMark scriptMark = new ScriptMarkEngine(ScriptmarkEnv.noCache, new StringSource(templateString), configurable);
            scriptMark.process(writer, valueMap);
            return writer.toString();
        } catch (Exception e) {
            log.error(templateString, e);
        }
        return StringUtil.empty;
    }

    /**
     * 和上边的区别是是否报出异常
     * @param valueMap 变量map
     * @param templateString 字符方式
     * @return 模版转换后的字符串
     * @throws Exception 异常
     */
    @Override
    public String processTemplateException(Map<String, Object> valueMap, String templateString) throws Exception {
        if (templateString == null) {
            return StringUtil.empty;
        }
        configurable.put(ScriptmarkEnv.FixUndefined,ENV_TEMPLATE.getBoolean(Environment.templateFixUndefined));
        try (Writer writer = new StringWriter()) {
            ScriptMark scriptMark = new ScriptMarkEngine(ScriptmarkEnv.noCache, new StringSource(templateString),configurable);
            scriptMark.process(writer, valueMap);
            return writer.toString();
        }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try (Writer writer = new StringWriter()) {
            ScriptMark scriptMark = new ScriptMarkEngine(EncryptUtil.getMd5(file.getAbsolutePath()), fs, SqlMapTemplateConfigurable.getInstance());
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