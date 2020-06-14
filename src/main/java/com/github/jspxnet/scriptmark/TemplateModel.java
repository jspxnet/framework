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

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-19
 * Time: 14:13:12
 */
public interface TemplateModel extends Serializable {
    /**
     *
     * @return 得到配置
     */
    Configurable getConfigurable();

    /**
     *
     * @return 得到代码
     */
    String getSource();

    /**
     *
     * @return 树结构方式得到xml节点
     * @throws ScriptRunException 异常
     */
    List<TagNode> getRootTree() throws ScriptRunException;

    /**
     *
     * @param begin 开始位置
     * @param end 结束位置
     * @return 树结构方式得到xml节点
     * @throws Exception 异常
     */
    List<TagNode> getBlockTree(int begin, int end) throws Exception;

    /**
     *
     * @param begin 开始位置
     * @param end 结束位置
     * @param tagMap 需要转换的标签列表
     * @return 块
     * @throws Exception 异常
     */
    List<TagNode> getBlockTree(int begin, int end, Map<String, String> tagMap) throws Exception;

    /**
     *
     * @param src 代码
     * @param tagMap 需要转换的标签列表
     * @return 块
     * @throws Exception 异常
     */
    List<TagNode> getBlockTree(String src, Map<String, String> tagMap) throws Exception;

    /**
     *
     * @param begin 开始位置
     * @param end 结束位置
     * @return 异常
     */
    String getSource(int begin, int end);

    /**
     * 得到范围标签的内容
     *
     * @param begin 开始位置
     * @param end   结尾位置
     * @return 正文内容
     */
    String getBody(int begin, int end);

    /**
     *
     * @return 缓存时间
     */
    long getLastModified();

    /**
     * 清除缓存 ,在缓存释放的时候才调用，普通调用的时候不使用
     */
    void clear();
}