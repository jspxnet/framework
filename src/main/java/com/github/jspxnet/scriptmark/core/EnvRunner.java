/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.scriptmark.*;
import com.github.jspxnet.scriptmark.core.block.*;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;
import com.github.jspxnet.scriptmark.core.script.ScriptTypeConverter;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 15:33:04
 * js引擎封装 com.github.jspxnet.scriptmark.core.EnvRunner
 */
@Slf4j
public class EnvRunner {
    final private Map<String, Phrase> phrases;
    final private TemplateModel template;
    private ScriptRunner scriptEngine = null;
    private String variableBegin = "${";
    private String variableSafeBegin = "#{";
    private String variableEnd = "}";
    private char escapeVariable = '\\';

    private String breakBlockName = "#break";
    private String continueBlockName = "#continue";

    private String currentPath = FileUtil.mendPath(System.getProperty("user.dir")); //当前路径，f方便include 使用
    private String rootDirectory = FileUtil.mendPath(System.getProperty("user.dir")); //路径范围

    /**
     * @param template 初始
     */
    public EnvRunner(TemplateModel template) {
        this.template = template;

        /////////配置初始begin
        Configurable config = this.template.getConfigurable();
        variableBegin = config.getString(ScriptmarkEnv.VariableBegin);
        variableSafeBegin = config.getString(ScriptmarkEnv.VariableSafeBegin);
        variableEnd = config.getString(ScriptmarkEnv.VariableEnd);
        breakBlockName = config.getString(ScriptmarkEnv.BreakBlockName);
        continueBlockName = config.getString(ScriptmarkEnv.ContinueBlockName);
        escapeVariable = config.getString(ScriptmarkEnv.escapeVariable).charAt(0);
        /////////配置初始end

        phrases = config.getPhrases();
    }


    /**
     * @return 得到脚本运行环境
     */
    public ScriptRunner getScriptRunner() {
        //安卓只能使用单例的脚本引擎，否则会错误
        if (scriptEngine == null || scriptEngine.isClosed()) {
            scriptEngine = new TemplateScriptEngine();
        }
        return scriptEngine;
    }

    /**
     * @return 得到模板
     */
    public TemplateModel getTemplate() {
        return template;
    }


    /**
     * 运行输出
     *
     * @param out 输出
     * @throws ScriptRunException io错误
     */
    void run(Writer out) throws ScriptRunException {
        List<TagNode> importList = getAutoImport(template.getConfigurable());
        if (importList != null && !importList.isEmpty()) {
            Writer writer = new StringWriter();
            for (TagNode tagNode : importList) {
                runBlock(tagNode, writer);
            }
        }
        //自动包含的部分
        //out.write(autoInlucde());
        //向下执行
        List<TagNode> rootList = template.getRootTree();
        for (TagNode tagNode : rootList) {
            int b = runBlock(tagNode, out);
            if (b < 0) {
                break;
            }
        }
    }

    /**
     * @return 当前路径
     */
    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * @param currentPath 当前路径
     */
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * @return 根路径
     */
    public String getRootDirectory() {
        return rootDirectory;
    }

    /**
     * @param rootDirectory 根路径
     */
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * @param tagNode 节点
     * @param out     输出
     * @throws ScriptException 脚本错误
     * @throws IOException     读取错误
     */
    public void getInjectVariables(TagNode tagNode, Writer out) throws Exception {
        ScriptTypeConverter.getInjectVariables(scriptEngine, tagNode, out, variableBegin, variableSafeBegin,variableEnd, escapeVariable);
    }


    /**
     * 运行一个代码块,运行后的结果 保存在Writer中
     *
     * @param tagNode 块节点
     * @param out     输出
     * @return 1:break 2:continue
     * @throws ScriptRunException 脚本错误
     */
    public int runBlock(TagNode tagNode, Writer out) throws ScriptRunException {
        if (tagNode == null) {
            return 0;
        }

        if (tagNode.getTagName().equals(breakBlockName)) {
            return BreakBlock.value;
        }
        if (tagNode.getTagName().equals(continueBlockName)) {
            return ContinueBlock.VALUE;
        }

        Phrase phrase = phrases.get(tagNode.getClass().getName());
        if (phrase == null) {
            phrase = phrases.get(HtmlEngineImpl.NONE_TAG);
        }
        return phrase.getRun(this, tagNode, out);
    }

    /**
     * @param configurable 配置
     * @return 得到配置的节点 默认都是宏
     * @throws ScriptRunException 脚本错误
     */
    static private List<TagNode> getAutoImport(Configurable configurable) throws ScriptRunException {
        String[] paths = configurable.getSearchPath();
        if (paths == null) {
            return null;
        }
        TemplateLoader templateLoader = TemplateManager.getInstance();
        StringBuilder autoImportSrc = new StringBuilder();
        TemplateModel templateModel = templateLoader.get(ScriptmarkEnv.autoImportCache);
        if (templateModel!=null)
        {
            autoImportSrc.append(templateModel.getSource());
        }
        if (autoImportSrc.length() < 5) {
            for (String importFile : configurable.getAutoImports()) {
                if (StringUtil.isNull(importFile)) {
                    continue;
                }
                for (String path : paths) {
                    File f = new File(path, importFile);
                    if (FileUtil.isFileExist(f.getPath())) {
                        try {
                            autoImportSrc.append(IoUtil.autoReadText(f)).append(StringUtil.CRLF);
                        } catch (Exception e) {
                            throw new ScriptRunException(null, importFile);
                        }
                    } else
                    {
                        f = EnvFactory.getFile(importFile);
                        if (f!=null)
                        {
                            try {
                                autoImportSrc.append(IoUtil.autoReadText(f)).append(StringUtil.CRLF);
                            } catch (Exception e) {
                                throw new ScriptRunException(null, importFile);
                            }
                        }
                    }

                }
            }
        }
        TemplateElement templateEl = new TemplateElement(autoImportSrc.toString(), 0, configurable);
        return templateEl.getRootTree();
    }
}