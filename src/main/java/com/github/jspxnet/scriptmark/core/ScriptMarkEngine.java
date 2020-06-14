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

import com.github.jspxnet.scriptmark.*;
import com.github.jspxnet.scriptmark.core.script.ScriptMap;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 15:18:28
 * 脚本引擎
 */

public class ScriptMarkEngine implements ScriptMark {
    private EnvRunner runner;

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
            Map<String, Object> globalMap = runner.getTemplate().getConfigurable().getGlobalMap();
            putVarMap(scriptRunner, globalMap);
            //放入私有变量
            putVarMap(scriptRunner, map);
            runner.Runner(out);
            //执行完成后不要情况模板
        } finally {
            scriptRunner.exit();
        }
    }

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
            scriptRunner.put(name, o);
        }
    }

}