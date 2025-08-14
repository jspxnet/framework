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
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.scriptmark.TemplateLoader;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.TemplateModel;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.cache.TemplateLifecycle;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-17
 * Time: 16:11:47
 * 模版容器，包括缓存及解析
 */
@Setter
public class TemplateManager implements TemplateLoader {

    static private final TemplateManager INSTANCE = new TemplateManager();
    private boolean useCache = false;
    private static TemplateLoader templateLoader;

    static public TemplateManager getInstance() {
        return INSTANCE;
    }

    private TemplateManager() {
        ///////得到配置 begin
        Configurable configurable = TemplateConfigurable.getInstance();
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        int second = environmentTemplate.getInt(ScriptmarkEnv.Template_update_delay, configurable.getInt(ScriptmarkEnv.Template_update_delay));
        int size = environmentTemplate.getInt(ScriptmarkEnv.Template_cache_size, configurable.getInt(ScriptmarkEnv.Template_cache_size));
        useCache = !environmentTemplate.getBoolean(Environment.DEBUG);
        //如果为调试模式，将自动关闭缓存
        if (size < 5) {
            size = 30;
        }
        if (second < 3) {
            second = 5;
        }
        ///////得到配置 end

        //大于2秒表示使用cahce否则就不使用cache了
        if (templateLoader == null) {
            templateLoader = new TemplateLifecycle(second, size);
        }
    }

    @Override
    public boolean isUseCache() {
        return useCache;
    }

    @Override
    public void clear() {
        if (useCache) {
            templateLoader.clear();
        }
    }

    @Override
    public TemplateModel get(String name) {
        return templateLoader.get(name);
    }

    /**
     * 不使用缓存就释放了
     *
     * @param name            template name
     * @param lifecycleObject temlate element
     */
    @Override
    public void put(String name, TemplateModel lifecycleObject) {
        if (useCache) {
            try {
                templateLoader.put(name, lifecycleObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (lifecycleObject != null) {
            lifecycleObject.clear();
        }
    }

}