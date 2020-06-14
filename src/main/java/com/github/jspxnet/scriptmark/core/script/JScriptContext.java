/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.script;

import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.scriptmark.util.ScriptConverter;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.SingletonContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-29
 * Time: 14:14:54
 * 脚本全局环境,本环境实用jdk7内置了
 */
@Slf4j
public class JScriptContext implements SingletonContext {
    final static private transient SingletonContext instance = new JScriptContext();
    private ScriptableObject globalScope = null;
    private static boolean useDynamicScope = true;

    final private static class WebScriptWrapFactory extends ContextFactory {
        @Override
        protected boolean hasFeature(Context context, int featureIndex) {
            context.setWrapFactory(ScriptWrapFactory.getInstance());
            //这里不能用下边这句,多线程会载入错误
            //context.setApplicationClassLoader(Thread.currentThread().getContextClassLoader());
            if (featureIndex == Context.FEATURE_DYNAMIC_SCOPE) {
                return useDynamicScope;
            }
            return super.hasFeature(context, featureIndex);
        }
    }

    public static SingletonContext getInstance() {
        return instance;
    }

    private JScriptContext() {
        if (!ContextFactory.hasExplicitGlobal()) {
            ContextFactory.initGlobal(new WebScriptWrapFactory());
        }
        try {
            Context context = JScriptContextEnter.getContext();
            context.setLanguageVersion(Context.VERSION_ES6);
            context.setOptimizationLevel(-1);
            context.setWrapFactory(ScriptWrapFactory.getInstance());
            globalScope = context.initStandardObjects();
            globalScope.put(Catalina.VAR_CATALINA, globalScope, new Catalina());
            globalScope.put(ScriptConverter.var_converter, globalScope, ScriptConverter.getInstance());
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.ArrayIterator.class);
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.CollectionIterator.class);
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.MapIterator.class);
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.NativeArrayIterator.class);
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.NullIterator.class);
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.RangeIterator.class);
            ScriptableObject.defineClass(globalScope, com.github.jspxnet.scriptmark.core.iterator.StringIterator.class);

            useDynamicScope = false;
            //路径是正确的，但中安卓下 执行异常
            InputStream inputStream = ScriptmarkEnv.class.getResourceAsStream(ScriptmarkEnv.default_jslib);
            if (inputStream == null) {
                inputStream = ClassUtil.getResourceAsStream(ScriptmarkEnv.default_jslib);
            }

            if (inputStream == null) {
                URL url = ClassUtil.getResource(ScriptmarkEnv.default_jslib);
                if (url != null) {
                    inputStream = url.openStream();
                }
            }
            if (inputStream != null) {
                InputStreamReader libReader = new InputStreamReader(inputStream, Environment.defaultEncode);
                context.evaluateReader(globalScope, libReader, "sharedScript", 1, null);
                libReader.close();
            } else {
                log.error(ScriptmarkEnv.default_jslib + " not found rhino, 模板库文件没有载入,rhino依赖包是否载入");
            }
        } catch (Exception e) {
            log.error(ScriptmarkEnv.default_jslib + " not found rhino, 模板库文件没有载入,rhino依赖包是否载入", e);
        } finally {
            useDynamicScope = true;
            JScriptContextEnter.exit();
        }
    }

    @Override
    public ScriptableObject getGlobalScope() {
        return globalScope;
    }
}