/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.scriptmark.load.StringSource;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.util.TXWebUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.utils.StringUtil;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-6-2
 * Time: 下午5:27
 * 将Markdown格式转换为html显示出来
 * 载入代码着色,和转换
 */
@Slf4j
public class MarkdownResult extends ResultSupport {
    private static Source fileSource;
    public MarkdownResult() {

    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        Action action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();

        //浏览器缓存控制begin
        checkCache(action, response);
        //浏览器缓存控制end

        //请求编码begin
        String contentType = action.getEnv(ActionEnv.ContentType);
        if (!StringUtil.isNull(contentType)) {
            response.setContentType(contentType);
            String tempEncode = StringUtil.substringAfterLast(StringUtil.replace(contentType, " ", ""), "charset=");
            if (!StringUtil.isNull(tempEncode)) {
                response.setCharacterEncoding(tempEncode);
            }
        } else {
            response.setContentType("text/html; charset=" + Dispatcher.getEncode());
            response.setCharacterEncoding(Dispatcher.getEncode());
        }
        //请求编码end

        //处理下载情况 begin
        String disposition = action.getEnv(ActionEnv.Content_Disposition);
        if (!StringUtil.isNull(disposition)) {
            response.setHeader(ActionEnv.Content_Disposition, disposition);
        }
        //处理下载情况 end


        TemplateConfigurable configurable = new TemplateConfigurable();
        String markdownTemplate = ENV_TEMPLATE.getString(Environment.markdownTemplate);
        String templatePath = ENV_TEMPLATE.getString(Environment.templatePath);
        configurable.addAutoIncludes(ENV_TEMPLATE.getString(Environment.autoIncludes));

        //找到模版文件
        if (fileSource==null)
        {
            File f = new File(action.getTemplatePath(), markdownTemplate);
            if (!f.exists()) {
                f = new File(templatePath, markdownTemplate);
            }
            if (f==null||!f.isFile()) {
                f = EnvFactory.getFile(markdownTemplate);
            }
            fileSource = new StringSource(IoUtil.autoReadText(f.getPath(), Dispatcher.getEncode()));
        }

        //如果使用cache 就使用uri

        configurable.setSearchPath(new String[]{action.getTemplatePath(), Dispatcher.getRealPath(), templatePath});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(ScriptmarkEnv.noCache, fileSource, configurable);
        } catch (Exception e) {
            if (DEBUG) {
                log.info("TemplateResult file not found, markdown 模版文件没有找到", e);
                TXWebUtil.errorPrint("TemplateResult file not found,markdown 模版文件没有找到," + e.getMessage(), null,response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("file not found,不存在的文件",null, response, HttpStatusType.HTTP_status_404);
            }
            return;
        }
        scriptMark.setRootDirectory(Dispatcher.getRealPath());
        scriptMark.setCurrentPath(action.getTemplatePath());

        //载入md文件begin
        action.put(Environment.templateSuffix, Dispatcher.getMarkdownSuffix());
        action.put(Environment.scriptPath,ENV_TEMPLATE.getString(Environment.scriptPath));
        File mdFile = new File(action.getTemplatePath(), action.getTemplateFile());
        FileSource mdFileSource = new FileSource(mdFile, action.getTemplateFile(), Dispatcher.getEncode());
        //载入md文件end

        //输出模板数据
        PrintWriter out = response.getWriter();
        Map<String, Object> valueMap = action.getEnv();
        initPageEnvironment(action, valueMap);
        valueMap.put("title", action.getEnv(ActionEnv.Key_ActionName));
        valueMap.put("content", ScriptMarkUtil.getMarkdownHtml(mdFileSource.getSource()));
        try {
            scriptMark.process(out, valueMap);
        } catch (Exception e) {
            if (DEBUG) {
                log.info("TemplateResult file not found:" + mdFile.getPath(), e);
                TXWebUtil.errorPrint("TemplateResult file not found:" + mdFile.getPath() + "\r\n" + StringUtil.toBrLine(e.getMessage()), null,response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("file not found,不存在的文件", null,response, HttpStatusType.HTTP_status_404);
                return;
            }
        }
        out.flush();
        out.close();
    }
}