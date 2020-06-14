/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark;

import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import org.mozilla.javascript.Scriptable;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-9
 * Time: 16:20:58
 */
public interface ScriptRunner extends Serializable {


    /**
     * @param s      脚本
     * @param lineno 行号
     * @return 运行一段脚本, 并返回 对象
     * @throws ScriptException 异常
     */
    Object eval(String s, int lineno) throws ScriptException;

    /**
     * 放入环境数据
     *
     * @param name 变量名称
     * @param o    值
     */
    void put(String name, java.lang.Object o);

    /**
     * @param name 变量名称
     * @return 判断变量是否存在
     */
    boolean containsVar(String name);

    /**
     * @param name 变量名称
     * @return 取出环境变量
     * @throws ScriptException 异常
     */
    Object get(String name) throws ScriptException;

    /**
     * @param reader 执行代码
     * @return 原接口
     * @throws Exception 异常
     */
    java.lang.Object eval(java.io.Reader reader) throws Exception;

    /**
     * 设置环境变量
     *
     * @param name 名称
     * @param o    变量
     * @throws ScriptRunException 异常
     */
    void putVar(java.lang.String name, Object o) throws ScriptRunException;

    /**
     * @param scope 设置环境变量
     */
    void setScope(Scriptable scope);

    /**
     * @return 得到环境变量
     */
    Scriptable getScope();

    /**
     * @return 拷贝环境变量
     */
    Scriptable copyScope();

    /**
     * 清空内存，释放变量空间
     */
    void exit();

    /**
     * @return 判断是否已经释放
     */
    boolean isClosed();

}