/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-4-25
 * Time: 下午5:09
 * 将页面执行后的结果保存为文件，文件名使用 变量 saveFile 保存中环境中
 * 文件保存将会直接覆盖，所以是比较危险的，有重要文件最好先备份
 */
public class FileSaveResult extends ResultSupport {
    private final static Logger log = LoggerFactory.getLogger(FileSaveResult.class);
    private final static TemplateConfigurable CONFIGURABLE = new TemplateConfigurable();

    private static String TEMPLATE_PATH = envTemplate.getString(Environment.templatePath);
    public static final String SAVE_FILE = "saveFile";

    static {
        CONFIGURABLE.addAutoIncludes(envTemplate.getString(Environment.autoIncludes));
    }

    public FileSaveResult() {

    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();
        HttpServletRequest request = action.getRequest();
        String defaultEncode = envTemplate.getString(Environment.encode, Environment.defaultEncode);
        File f = new File(action.getTemplatePath(), action.getTemplateFile());
        FileSource fileSource = new FileSource(f, action.getTemplateFile(), defaultEncode);
        //如果使用cache 就使用uri

        CONFIGURABLE.setSearchPath(new String[]{action.getTemplatePath(), Dispatcher.getRealPath(), TEMPLATE_PATH});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(ScriptmarkEnv.noCache, fileSource, CONFIGURABLE);
        } catch (Exception e) {
            if (debug) {
                log.info("TemplateResult file not found:" + f.getAbsolutePath(), e);
                TXWebUtil.errorPrint("TemplateResult file not found:" + f.getAbsolutePath() + "\r\n" + e.getMessage(),null, response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("file not found,不存在的文件",null, response, HttpStatusType.HTTP_status_404);
            }
            return;
        }
        scriptMark.setRootDirectory(Dispatcher.getRealPath());
        scriptMark.setCurrentPath(action.getTemplatePath());
        if (StringUtil.isNull(action.getEnv(SAVE_FILE))) {
            TXWebUtil.errorPrint(SAVE_FILE + ",保存文件路径没有定义", null,response, HttpStatusType.HTTP_status_404);
            return;
        }

        File saveFileName = new File(action.getEnv(SAVE_FILE));
        //输出模板数据
        FileUtil.makeDirectory(saveFileName.getParent());
        Writer out = new OutputStreamWriter(new FileOutputStream(saveFileName), defaultEncode);
        Map<String, Object> valueMap = action.getEnv();
        initPageEnvironment(action, valueMap);
        try {
            scriptMark.process(out, valueMap);
        } finally {
            valueMap.clear();
        }
        out.flush();
        out.close();

    }
}