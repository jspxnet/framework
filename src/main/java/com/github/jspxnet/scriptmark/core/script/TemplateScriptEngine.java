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


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.SingletonContext;
import java.io.*;
import java.util.Collection;
import java.util.Map;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;
import com.github.jspxnet.utils.XMLUtil;
import org.mozilla.javascript.*;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-17
 * Time: 15:09:29
 */


public class TemplateScriptEngine implements ScriptRunner {

    final private Context context;
    private Scriptable scope;

    /**
     * 创建脚本方式
     */
    public TemplateScriptEngine() {
        SingletonContext singletonContext = JScriptContext.getInstance();
        ScriptableObject sharedScope = singletonContext.getGlobalScope();
        context = JScriptContextEnter.getContext();
        scope = context.newObject(sharedScope);
        scope.setPrototype(sharedScope);
        scope.setParentScope(null);
        if (EnvFactory.getEnvironmentTemplate().getBoolean(Environment.DEBUG)) {
            scope.put("console", scope, java.lang.System.out);
        }
    }

    /**
     * @return Scriptable 得到当前的变量环境
     */
    @Override
    public Scriptable getScope() {
        return scope;
    }

    /**
     * @param scope 设置变量空间
     */
    @Override
    public void setScope(Scriptable scope) {
        this.scope = scope;
    }

    /**
     * 拷贝共享空间
     *
     * @return Scriptable
     */
    @Override
    public Scriptable copyScope() {
        Scriptable scriptable = context.newObject(scope);
        scriptable.setPrototype(scope);
        scriptable.setParentScope(null);
        return scriptable;
    }

    /**
     * @param s 代码
     * @return 根据代码返回
     */
    @Override
    public Object eval(String s, int lineNo) throws ScriptException {
        //如果存在#() 异常的时候运行 这里边的内容
        boolean canDo = false;
        String catchRun = null;
        int i = s.indexOf("#(");
        if (i != -1) {
            String temp = s;
            s = s.substring(0, i);
            catchRun = StringUtil.trim(temp.substring(i + 1));
            if (catchRun.startsWith("(") && catchRun.endsWith(")")) {
                catchRun = catchRun.substring(1, catchRun.length() - 1);
            }
            canDo = true;
        }
        Object o = null;
        try {
            o = context.evaluateString(scope, s, "js-cache-code", lineNo, null);
        } catch (Exception e) {
            if (canDo && !StringUtil.isNull(catchRun)) {
                o = context.evaluateString(scope, catchRun, "js-eval-catchRun", lineNo, null);
                if (o instanceof NativeJavaObject) {
                    NativeJavaObject nativeJavaObject = (NativeJavaObject) o;
                    o = nativeJavaObject.unwrap();
                }
            } else {
                throw new ScriptException(s + " " + e.getMessage());
            }
        }
        if (o instanceof UniqueTag || o instanceof Undefined) {
            o = StringUtil.empty;
        }
        if (o instanceof NativeJavaObject) {
            o = ((NativeJavaObject) o).unwrap();
        }
        if (!StringUtil.isNull(catchRun) && (ObjectUtil.isEmpty(o) || StringUtil.isNull(o+"") || o instanceof Undefined)) {
            o = context.evaluateString(scope, catchRun, "js-eval-check-null", 3, null);
            if (o instanceof NativeJavaObject) {
                NativeJavaObject nativeJavaObject = (NativeJavaObject) o;
                o = nativeJavaObject.unwrap();
            }
        }
        return o;
    }

    /**
     * @param reader 代码
     * @return 根据代码返回
     */
    @Override
    public Object eval(java.io.Reader reader) throws IOException {
        return context.evaluateReader(scope, reader, "js-eval-reader", 1, null);
    }

    /**
     * 放入本地环境变量  ScriptContext.ENGINE_SCOPE中
     *
     * @param name 变量名称
     * @param o    对象
     */
    @Override
    public void put(String name, Object o) {
        scope.put(name,scope, o);
    }

    /**
     * 放入变量
     *
     * @param name 变量名称
     * @param o    变量
     */
    @Override
    public void putVar(String name, Object o) throws ScriptRunException {
        if (name == null || "".equals(name)) {
            return;
        }
        String value = o.toString();
        if (ScriptTypeConverter.isArray(value) && value.indexOf("..") > 0) {
            try {
                eval("var " + name + StringUtil.EQUAL + ScriptTypeConverter.toString(ScriptTypeConverter.toArray(value)) + StringUtil.SEMICOLON, 0);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ScriptRunException(null, value);
            }
        }
        else if (o instanceof Collection || o instanceof Map) {
            put(name, o);
        } else
        {
            String noQuoteStr = XMLUtil.deleteQuote(value);
            if (StringUtil.isJsonArray(noQuoteStr)||StringUtil.isJsonObject(noQuoteStr))
            {
                try {
                    eval("var " + name + StringUtil.EQUAL + noQuoteStr + StringUtil.SEMICOLON, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ScriptRunException(null, value);
                }
            }
            else
            {
                try {
                    eval("var " + name + StringUtil.EQUAL + value + StringUtil.SEMICOLON, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ScriptRunException(null, value);
                }
            }
        }
    }

    /**
     * @param s 变量名称
     * @return 判断是否存在变量
     */
    @Override
    public boolean containsVar(String s) {
        return scope.has(s, scope);
    }

    /**
     * 得到 js 中的变量值
     *
     * @param s 变量名称
     * @return value
     */
    @Override
    public Object get(String s) {
        Object o = scope.get(s, scope);
        if (o instanceof NativeJavaObject) {
            NativeJavaObject nativeJavaObject = (NativeJavaObject) o;
            return nativeJavaObject.unwrap();
        }
        if (o == null || o instanceof Undefined) {
            //return StringUtil.empty;
            return null;
        }
        return o;
    }

    @Override
    public boolean isClosed() {
        return scope == null;
    }

    @Override
    public void exit() {
        //清空变量
        if (scope != null) {
            for (Object k : scope.getIds()) {
                scope.delete((String) k);
            }
            scope = null;
        }

        if (!SystemUtil.isAndroid())
        {
            JScriptContextEnter.exit();
        }
    }

}