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

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 11:12:16
 */
public class ScriptmarkEnv {
    private ScriptmarkEnv() {

    }

    //模板更新时间
    public static final String Template_update_delay = "template_update_delay";
    //模板更新时间
    public static final String Template_cache_size = "template_cache_size";
    //日期格式
    public static final String DateTimeFormat = "dateTimeFormat";
    //日期格式
    public static final String DateFormat = "dateFormat";
    //时间格式
    public static final String TimeFormat = "timeFormat";
    //数字格式
    public static final String NumberFormat = "numberFormat";

    //数字格式
    public static final String Language = "language";

    //代码标识 默认<>
    public static final String Syncopate = "syncopate";

    public static final String SyncopateFenTag = "syncopateFen";

    //目录读取的根目录
    public static final String BasePath = "basePath";

    //////变量开始
    public static final String VariableBegin = "variableBegin";

    ///带引号变量开始
    public static final String VariableSafeBegin = "variableSafeBegin";

    //////变量结束
    public static final String VariableEnd = "variableEnd";
    //////变量转义字符
    public static final String escapeVariable = "escapeVariable";
    //////debugNull
    public static final String debugNull = "debugNull";
    ///////noCache
    public static final String noCache = "none";

    public static final String autoImportCache = "autoImportCache";

    //返回和跳出循环特殊处理,方便配置
    public static final String BreakBlockName = "breakBlockName";
    public static final String ContinueBlockName = "continueBlockName";
    public static final String CompressBlockName = "compressBlockName";

    //////扩展方式，为模板解析方式
    public static final String htmlExtType = "htmlExtType";

    //是否清除xml转义
    public static final String xmlEscapeClean = "xmlEscapeClean";

    //////////////////////标识
    public static final String MacroCallTag = "macroCallTag";

    public static final String DefaultStartTag = "defaultStartTag";

    public static final String DefaultEndTag = "defaultEndTag";

    public static final String MacroStartTag = "macroStartTag";

    public static final String MacroEndTag = "macroEndTag";

    public static final String default_jslib = "js/jslib.js";

}