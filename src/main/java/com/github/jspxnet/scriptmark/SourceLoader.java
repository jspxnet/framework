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

import com.github.jspxnet.scriptmark.load.Source;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:48:57
 */
public interface SourceLoader extends Serializable {

    /**
     * 通过模板名获取相应模板源
     *
     * @param name 模板名
     * @return 模板源，(注：后验条件不返回null并且资源存在，不存在时抛异常)
     * @throws java.io.IOException 当模板不存在时抛出
     */
    Source getSource(String name) throws IOException;

    /**
     * 通过模板名获取相应模板源, 并指定加载编码方式
     *
     * @param name     模板名
     * @param encoding 加载编码方式
     * @return 模板源，(注：后验条件不返回null并且资源存在，不存在时抛异常)
     * @throws IOException 当模板不存在时抛出
     */
    Source getSource(String name, String encoding) throws IOException;

    /**
     * 通过模板名获取相应模板源
     *
     * @param name   模板名
     * @param locale 国际化区域信息
     * @return 模板源，(注：后验条件不返回null并且资源存在，不存在时抛异常)
     * @throws IOException 当模板不存在时抛出
     */
    Source getSource(String name, Locale locale) throws IOException;

    /**
     * 通过模板名获取相应模板源, 并指定加载编码方式
     *
     * @param name     模板名
     * @param locale   国际化区域信息
     * @param encoding 加载编码方式
     * @return 模板源，(注：后验条件不返回null并且资源存在，不存在时抛异常)
     * @throws IOException 当模板不存在时抛出
     */
    Source getSource(String name, Locale locale, String encoding) throws IOException;

}