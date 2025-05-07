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

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.scriptmark.load.AbstractSource;
import com.github.jspxnet.scriptmark.load.InputStreamSource;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.handle.ActionHandle;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.util.MimeTypesUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.EnvFactory;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 21:58:28
 * com.github.jspxnet.txweb.result.TemplateResult
 * 最基本的返回输出方式，将模板文件执行后输出
 */
@Slf4j
public class TemplateResult extends ResultSupport {

    private final static String DEFAULT_ENCODE = ENV_TEMPLATE.getString(Environment.encode, Environment.defaultEncode);
    private final static String TEMPLATE_PATH = ENV_TEMPLATE.getString(Environment.templatePath);
    private final static TemplateConfigurable CONFIGURABLE = new TemplateConfigurable();

    static {
        CONFIGURABLE.addAutoIncludes(ENV_TEMPLATE.getString(Environment.autoIncludes));
        CONFIGURABLE.put(ScriptmarkEnv.FixUndefined,ENV_TEMPLATE.getBoolean(Environment.templateFixUndefined));
    }

    public TemplateResult() {

    }


    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {

        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        HttpServletResponse response = actionContext.getResponse();


        //浏览器缓存控制begin
        checkCache(actionContext);
        //浏览器缓存控制end

        Action action = actionInvocation.getActionProxy().getAction();

        //兼容Roc返回提示方式begin
        Object result = actionContext.getResult();
        if (result!=null&&!actionContext.hasFieldInfo()&&!actionContext.hasActionMessage() && result instanceof RocResponse)
        {
            RocResponse<?> rocResponse = (RocResponse<?>)result;
            if (YesNoEnumType.YES.getValue()==rocResponse.getSuccess())
            {
                if (StringUtil.isEmpty(rocResponse.getMessage()))
                {
                    rocResponse.setMessage(action.getLanguage().getString(LanguageRes.success));
                }
                actionContext.addActionMessage(rocResponse.getMessage());
            } else
            {
                actionContext.addFieldInfo(Environment.warningInfo,rocResponse.getMessage());
            }
        }
        //兼容Roc返回提示方式end

        String contentType = actionContext.getString(ActionEnv.CONTENT_TYPE);
        if (!StringUtil.isNull(contentType)) {
            response.setContentType(contentType);
            String tempEncode = StringUtil.substringAfterLast(StringUtil.replace(contentType, " ", ""), "charset=");
            if (!StringUtil.isNull(tempEncode)) {
                response.setCharacterEncoding(tempEncode);
            }
        } else {
            String actionName = actionContext.getActionName();
            String fileType = StringUtil.substringAfterLast(actionName, StringUtil.DOT);
            if (StringUtil.hasLength(fileType)) {
                response.setContentType( MimeTypesUtil.getContentType(fileType,Dispatcher.getEncode()));
            } else {
                response.setContentType("text/html; charset=" + Dispatcher.getEncode());
            }
            response.setCharacterEncoding(Dispatcher.getEncode());
        }

        //请求编码end

        //处理下载情况 begin
        String disposition = actionContext.getString(ActionEnv.CONTENT_DISPOSITION);
        if (!StringUtil.isNull(disposition)) {
            response.setHeader(ActionEnv.CONTENT_DISPOSITION, disposition);
        }
        //处理下载情况 end
        //如果 resultConfig 里边有明确的返回，先使用明确的返回
        File f;
        if (getResultConfig() == null) {
            f = new File(action.getTemplatePath(), action.getTemplateFile());
        } else {
            //这里有个路径问题 /的就直接从根目录开始,有个目录拼接的过程
            String confFile = EnvFactory.getPlaceholder().processTemplate(action.getEnv(), StringUtil.trim(getResultConfig().getValue()));
            if (confFile.startsWith("/")) {
                f = new File(Dispatcher.getRealPath(), confFile);
            } else {
                f = new File(action.getTemplatePath(), confFile);
            }
        }

        AbstractSource fileSource = new FileSource(f, action.getTemplateFile(), DEFAULT_ENCODE);
        if (!fileSource.isFile())
        {
            InputStream inputStream = TXWebUtil.class.getResourceAsStream("/resources/template/"+action.getTemplateFile());
            if (inputStream==null)
            {
                inputStream = TXWebUtil.class.getResourceAsStream("/template/"+action.getTemplateFile());
            }
            if (inputStream!=null)
            {
                fileSource = new InputStreamSource(inputStream,action.getTemplateFile(), DEFAULT_ENCODE);
            }
        }
        //如果使用cache 就使用uri

        String cacheKey = ScriptmarkEnv.noCache;
        if (!DEBUG) {
            cacheKey = EncryptUtil.getMd5(f.getAbsolutePath()); //为了防止特殊符号错误，转换为md5 格式
        }
        CONFIGURABLE.setSearchPath(new String[]{action.getTemplatePath(), Dispatcher.getRealPath(), TEMPLATE_PATH});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(cacheKey, fileSource, CONFIGURABLE);
        } catch (Exception e) {
            log.debug("template file not found:" + f.getAbsolutePath(), e);
            if (DEBUG) {
                TXWebUtil.errorPrint("template file not found:" + action.getTemplateFile() + "\r\n" + e.getLocalizedMessage() + "\r\n提示:ROC API调用请使用ROC协议",
                        null,response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("file not found,不存在的文件", null,response, HttpStatusType.HTTP_status_404);
            }
            return;
        }
        scriptMark.setRootDirectory(Dispatcher.getRealPath());
        scriptMark.setCurrentPath(action.getTemplatePath());
        //输出模板数据
        Map<String, Object> valueMap = action.getEnv();
        initPageEnvironment(action, valueMap);

        StringWriter out = new StringWriter();
        scriptMark.process(out, valueMap);
        //页面缓存支持begin
        ActionConfig actionConfig = actionInvocation.getActionConfig();
        if (actionConfig!=null&&actionConfig.isCache())
        {

            String key = actionConfig.getCacheName() + ActionHandle.PAGE_KEY + EncryptUtil.getMd5(request.getRequestURL().toString()+ "?"+request.getQueryString() + ObjectUtil.toString(RequestUtil.getSortMap(request)));
            log.debug("put page cache url:{}",request.getRequestURL().toString()+ "?"+request.getQueryString() );
            if (!StringUtil.isEmpty(out.toString()))
            {
                JSCacheManager.put(actionConfig.getCacheName(),key,out.toString());
            }
        }
        //页面缓存支持end
        try (PrintWriter writer = response.getWriter()){
            writer.print(out);
            writer.flush();
        } finally {
            out.close();
        }

    }
}