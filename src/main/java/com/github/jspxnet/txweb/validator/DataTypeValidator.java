/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.validator;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.validator.tag.ValidElement;
import com.github.jspxnet.txweb.validator.tag.ValidatorElement;
import com.github.jspxnet.upload.MultipartRequest;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-16
 * Time: 0:01:15
 *
 */
@Slf4j
public class DataTypeValidator implements Validator {
    final private static String CACHE_KEY = "jspx:validator:%s";
    private String configFile = "validator.xml";
    //验证的action
    private Object checkObject = null;
    public static final String TAG_VALIDATOR = "validator";
    public static final String TAG_VALIDATION = "validation";

    private String encode = Environment.defaultEncode;
    private String formId = StringUtil.empty;

    @Override
    public String getEncode() {
        return encode;
    }

    @Override
    public void setEncode(String encode) {
        this.encode = encode;
    }

    @Override
    public String getId() {
        return formId;
    }


    @Override
    public void setId(String formId) {
        if (formId != null) {
            this.formId = formId.toLowerCase();
        }
    }

    @Override
    public String getConfigFile() {
        return configFile;
    }


    @Override
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public void setCheckObject(Object checkObject) {
        this.checkObject = checkObject;
    }

    private String getFileName() {
        String fileNamePath = configFile;
        if (FileUtil.isFileExist(fileNamePath)) {
            return fileNamePath;
        }
        File tempFile =  EnvFactory.getFile(configFile);
        if (tempFile!=null) {
            return tempFile.getPath() ;
        }
        URL url = ClassUtil.getResource(configFile);
        if (url == null) {
            url = ClassUtil.getResource("/" + configFile);
            if (url != null) {
                fileNamePath = url.getPath();
            } else
            {
                url = ClassUtil.getResource("/resources/" + configFile);
                if (url != null) {
                    fileNamePath = url.getPath();
                }
            }
        }
        return fileNamePath;
    }


    private String readConfigText()
    {
        String key = String.format(CACHE_KEY, configFile);
        String str = (String)JSCacheManager.get(DefaultCache.class,key);
        if (StringUtil.isNull(str))
        {
            String fileNamePath = getFileName();
            //载入配置 begin
            AutoReadTextFile readTextFile = new AutoReadTextFile();
            readTextFile.setEncode(encode);
            readTextFile.setFile(fileNamePath);
            try {
                str = readTextFile.getContent();
            } catch (IOException e) {
                str = "";
                log.error("没有读取到验证文件:{}",fileNamePath);
                e.printStackTrace();
            }
            JSCacheManager.put(DefaultCache.class,key,str);
        }
        return str;
    }

    private List<TagNode> getConfig()  {
        if (!StringUtil.hasLength(formId)) {
            return new ArrayList<>();
        }
        String xml = readConfigText();
        try {
            XmlEngine xmlEngine = new XmlEngineImpl();
            xmlEngine.putTag(TAG_VALIDATOR, ValidatorElement.class.getName());
            List<TagNode> nodes = xmlEngine.getTagNodes(xml);
            for (TagNode node : nodes) {
                if (node==null)
                {
                    continue;
                }
                if (node instanceof ValidatorElement)
                {
                    ValidatorElement validatorElement = (ValidatorElement) node;
                    String id = validatorElement.getId();
                    if (id==null)
                    {
                        id = validatorElement.getStringAttribute("formId");
                    }
                    if (id==null)
                    {
                        log.info("validator XML file 解析错误数据验证的XML文件,没有配置id={}",formId);
                        continue;
                    }
                    if (id.equalsIgnoreCase(formId)) {
                        return validatorElement.getValidElements();
                    }
                }
            }
            nodes.clear();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("validator XML file 解析错误数据验证的XML文件没有找到formId={},info:{}",formId, e.getLocalizedMessage());
        }
        return new ArrayList<>(0);
    }


    @Override
    public String getXML() throws Exception {
        StringBuilder sb = new StringBuilder("<validator id=\"" + formId + "\">\r\n");
        List<TagNode> tagNodes = getConfig();
        if (tagNodes == null) {
            throw new IOException("DataTypeValidator not find configFile XML file " + configFile + " formId:" + formId + " 数据验证的XML文件没有找到相应的配置");
        }
        for (TagNode tagNode : tagNodes) {
            ValidElement validElement = (ValidElement) tagNode;
            if (StringUtil.isNull(validElement.getUrl())) {
                sb.append("<" + TAG_VALIDATOR + " dataType=\"").append(validElement.getDataType()).append("\" field=\"").append(validElement.getField()).append("\" noteId=\"").append(validElement.getNoteId()).append("\" required=\"").append(validElement.isRequired()).append("\">\r\n");
            } else {
                sb.append("<" + TAG_VALIDATOR + " dataType=\"").append(validElement.getDataType()).append("\" url=\"").append(validElement.getUrl()).append("\" field=\"").append(validElement.getField()).append("\" noteId=\"").append(validElement.getNoteId()).append("\" required=\"").append(validElement.isRequired()).append("\">\r\n");
            }
            sb.append("     <note>").append(validElement.getNote()).append("</note>\r\n");
            sb.append("     <error>").append(validElement.getError()).append("</error>\r\n");
            sb.append("</" + TAG_VALIDATOR + ">\r\n");
        }
        sb.append("</validator>\r\n");
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        Placeholder placeholder = EnvFactory.getPlaceholder();
        return placeholder.processTemplate(envTemplate.getVariableMap(), sb.toString());
    }

    @Override
    public String getJson() throws Exception {
        List<TagNode> tagNodes = getConfig();
        if (tagNodes == null || tagNodes.isEmpty()) {
            log.debug("DataTypeValidator not find configFile XML file " + configFile + " formId:" + formId + " 数据验证的XML文件没有找到相应的配置");
            return StringUtil.empty;
        }
        JSONArray jsonArray = new JSONArray();
        for (TagNode tagNode : tagNodes) {
            ValidElement validElement = (ValidElement) tagNode;
            JSONObject ja = new JSONObject();
            ja.put("field", validElement.getField());
            ja.put("noteId", validElement.getNoteId());
            ja.put("dataType", XMLUtil.escapeDecrypt(validElement.getDataType()));
            ja.put("url", validElement.getUrl());
            ja.put("required", validElement.isRequired());
            ja.put("note", XMLUtil.escapeDecrypt(validElement.getNote()));
            ja.put("error", validElement.getError());
            jsonArray.add(ja);
        }
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        Placeholder placeholder = EnvFactory.getPlaceholder();
        return placeholder.processTemplate(envTemplate.getVariableMap(), jsonArray.toString());
    }

    /**
     * 在这里验证
     * @return 保持在map中返回
     */
    @Override
    public Map<String, String> getInformation()
    {
        Map<String, String> result = new HashMap<>();
        if (checkObject==null)
        {
            result.put(Environment.warningInfo,"null object,空数据");
            return result;
        }
        Placeholder placeholder = EnvFactory.getPlaceholder();
        ScriptRunner scriptRunner = new TemplateScriptEngine();

        List<TagNode> tagNodes = getConfig();
        if (tagNodes == null) {
            throw new NullPointerException("Not Find Validator file:" + configFile + "  formId:" + formId);
        }

        Map<String, Object> valueMap = getValueMap();
        for (String name:valueMap.keySet())
        {
            scriptRunner.put(name,valueMap.get(name));
        }
        try {
            for (TagNode tagNode : tagNodes) {
                ValidElement validElement = (ValidElement) tagNode;
                Object ov = scriptRunner.get(validElement.getField());
                boolean isNeed = validElement.isRequired();
                if (!isNeed && (ov instanceof String) && !StringUtil.isNull((String) ov)) {
                    isNeed = true;
                }
                if (!isNeed) {
                    continue;
                }
                String expression = placeholder.processTemplate(valueMap, validElement.getDataType());
                if (expression.startsWith(" ")) {
                    expression = expression.trim();
                }
                //checkbox 判断
                String field = validElement.getField();
                if (field.contains(StringUtil.SEMICOLON)) {
                    field = StringUtil.quote(field, false) + ".toArray(';')";
                }
                expression = field + "." + expression;
                if (expression.contains("$(")) {
                    //web界面的验证，跳过
                    continue;
                }
                if (!expression.trim().endsWith(")") && !StringUtil.indexOfArray(expression, new String[]{">", "<", "=", "+", "-", StringUtil.ASTERISK, "/"})) {
                    expression = expression.trim() + "()";
                }
                boolean b = ObjectUtil.toBoolean(scriptRunner.eval(expression, 0));
                if (!b) {
                    result.put(validElement.getField(), validElement.getError());
                }
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        } finally {
            scriptRunner.exit();
        }
        return result;
    }

    /**
     *
     * @return 得到请求的所有参数,平铺化为map
     */
    private Map<String,Object> getValueMap()
    {
        if (checkObject instanceof ActionSupport)
        {
            ActionSupport actionSupport = (ActionSupport) checkObject;
            if (RequestUtil.isRocRequest(actionSupport.getRequest()))
            {
                JSONObject json = (JSONObject)actionSupport.getEnv().get(ActionEnv.Key_CallRocJsonData);
                if (json!=null)
                {
                    //内部方法优先
                    JSONObject paramJson = null;
                    JSONObject methodJson = json.getJSONObject(Environment.rocMethod);
                    JSONObject goldParams = json.getJSONObject(Environment.rocParams);
                    if (methodJson!=null&&methodJson.containsKey(Environment.rocParams))
                    {
                        paramJson = methodJson.getJSONObject(Environment.rocParams);
                    }
                    Map<String,Object> result = new HashMap<>(0);
                    if (paramJson!=null)
                    {
                        result = paramJson.toMap();
                    }
                    if (goldParams!=null)
                    {
                        for (String key:goldParams.keys())
                        {
                            if (!result.containsKey(key))
                            {
                                result.put(key,goldParams.get(key));
                            }
                        }
                    }
                    return result;
                }
            }
            return RequestUtil.getRequestMap(actionSupport.getRequest());
        } else if (checkObject instanceof MultipartRequest || checkObject instanceof HttpServletRequest) {
            return RequestUtil.getRequestMap(((HttpServletRequest) checkObject));
        } else if (checkObject instanceof Map) {
            return (Map) checkObject;
        } else {
            return ObjectUtil.getMap(checkObject);
        }
    }


}