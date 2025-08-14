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

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.*;
import com.github.jspxnet.scriptmark.core.script.ScriptMap;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 15:18:28
 * 脚本引擎 com.github.jspxnet.scriptmark.core.ScriptMarkEngine
 */
@Slf4j
public class ScriptMarkEngine implements ScriptMark {
    final private EnvRunner runner;

    public ScriptMarkEngine(String name, Source readSource, Configurable configurable) throws IOException {
        if (configurable == null) {
            configurable = TemplateConfigurable.getInstance();
        }
        TemplateManager templateLoader = TemplateManager.getInstance();

        TemplateModel template = null;
        if (!ScriptmarkEnv.noCache.equals(name) && templateLoader.isUseCache()) {
            template = templateLoader.get(name);
            if (template != null && template.getLastModified() != readSource.getLastModified()) {
                template = null;
            }
        }

        //从新创建模板
        if (template == null) {
            template = new TemplateElement(readSource.getSource(), readSource.getLastModified(), configurable);
            if (!ScriptmarkEnv.noCache.equals(name) && templateLoader.isUseCache()) {
                templateLoader.put(name, template);
            }
        }
        runner = new EnvRunner(template);
    }

    /**
     * @param currentPath 当前路径
     */
    @Override
    public void setCurrentPath(String currentPath) {
        runner.setCurrentPath(currentPath);
    }

    /**
     * @param rootDirectory 更路径，最底层路径
     */
    @Override
    public void setRootDirectory(String rootDirectory) {
        runner.setRootDirectory(rootDirectory);
    }

    /**
     * {@code
     * if (o instanceof Map && !o.getClass().getName().equals(ScriptMap.class.getName())) {
     * scriptRunner.put(name, new ScriptMap((Map) o));
     * continue;
     * }
     * }
     *
     * @param out 输出
     * @param map 解析块
     * @throws Exception 异常
     */
    @Override
    public void process(Writer out, Map<String, Object> map) throws Exception {
        ScriptRunner scriptRunner = runner.getScriptRunner();
        try {
            //放入全局变量
            putVarMap(scriptRunner, runner.getTemplate().getConfigurable().getGlobalMap());
            if (runner.isFixUndefined())
            {
                runner.fixUndefinedMap(map);
            }
            //放入私有变量
            putVarMap(scriptRunner, map);
            //运行
            runner.run(out);
            //执行完成后不要情况模板
        } finally {
            scriptRunner.exit();
        }
    }

    /**
     * 放入变量
     * @param scriptRunner js运行环境
     * @param map 参数变量
     */
    static private void putVarMap(ScriptRunner scriptRunner, Map<String, Object> map) {
        if (map == null) {
            return;
        }
        for (String name : map.keySet()) {
            Object o = map.get(name);
            if (o instanceof HashMap && o.getClass().getName().equals(HashMap.class.getName())) {
                scriptRunner.put(name, new ScriptMap((Map) o));
                continue;
            }
            if (name.contains(StringUtil.DOT))
            {
                String[] varNames = StringUtil.split(name,StringUtil.DOT);
                if (varNames.length>1)
                {
                    JSONObject root = new JSONObject();
                    JSONObject child = new JSONObject();
                    root.put(varNames[0],child);
                    for (int i=2;i<varNames.length;i++)
                    {
                        JSONObject childA = new JSONObject();
                        childA.put(varNames[i],JSONObject.NULL);
                        child.put(varNames[i-1],childA);
                        child = childA;
                    }
                    String value = root.getJSONObject(varNames[0]).toString();
                    if (!scriptRunner.containsVar(varNames[0]))
                    {
                        try {
                            scriptRunner.putVar(varNames[0],value);
                        } catch (ScriptRunException e) {
                            log.error("putVarMap",e);
                        }
                    }
                }
            }
            scriptRunner.put(name, o);
        }


    }
}