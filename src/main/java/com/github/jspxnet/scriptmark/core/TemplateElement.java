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

import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.HtmlEngine;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.TemplateModel;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 14:13:23
 * 保存在缓存中减少一次解析时间
 */
public class TemplateElement implements TemplateModel {
    private HtmlEngine htmlEngine = null;
    private String source;
    private Configurable config;
    private List<TagNode> nodeList;
    protected char beginTag = '<';
    protected char endTag = '>';
    private char escapeVariable = '\\';
    private long lastModified = System.currentTimeMillis(); //创建时间，判断cache 使用

    public TemplateElement(String source, long lastModified, Configurable cfg) {
        if (cfg == null) {
            cfg = TemplateConfigurable.getInstance();
        }
        this.lastModified = lastModified;
        this.config = cfg;
        String syncopate = config.getString(ScriptmarkEnv.Syncopate);
        if (syncopate == null || syncopate.length() < 1) {
            syncopate = "<>";
        }
        beginTag = syncopate.charAt(0);
        endTag = syncopate.charAt(1);

        this.source = source;
        escapeVariable = config.getString(ScriptmarkEnv.escapeVariable).charAt(0);
        this.htmlEngine = new HtmlEngineImpl(this);
        if (this.source == null) {
            this.source = StringUtil.empty;
        }
    }

    @Override
    public Configurable getConfigurable() {
        return config;
    }


    @Override
    public String getSource() {
        return source;
    }

    @Override
    public List<TagNode> getRootTree() throws ScriptRunException {
        if (nodeList == null || nodeList.isEmpty()) {
            nodeList = htmlEngine.getComputeTree();
        }
        return nodeList;
    }

    @Override
    public List<TagNode> getBlockTree(int begin, int end) throws Exception {
        return htmlEngine.getBlockTree(begin, end, config.getTagMap());
    }

    @Override
    public List<TagNode> getBlockTree(int begin, int end, Map<String, String> tagMap) throws Exception {
        return htmlEngine.getBlockTree(begin, end, tagMap);
    }

    /**
     * @param src    代码
     * @param tagMap 节点配置
     * @return 节点
     */
    @Override
    public List<TagNode> getBlockTree(String src, Map<String, String> tagMap) {
        return htmlEngine.getBlockTree(src, tagMap);
    }

    /**
     * @param begin 开始
     * @param end   结束
     * @return 得到代码substring部分
     */
    @Override
    public String getSource(int begin, int end) {
        return source.substring(begin, end);
    }

    /**
     * 得到范围标签的内容
     *
     * @param begin 开始位置
     * @param end   结尾位置
     * @return 正文内容
     */
    @Override
    public String getBody(int begin, int end) {
        int bStart = begin;
        int bend = end;
        char old = ' ';
        for (int i = begin; i < end; i++) {
            char c = source.charAt(i);
            if (i > begin) {
                if (old != escapeVariable && c == endTag) {
                    bStart = i + 1;
                    break;
                }
                old = c;
            }
        }


        //<![CDATA[${jdbcUrl}]]>
/*        boolean h = false;
        int iTemp = source.indexOf("<![CDATA[", bStart);
        if (iTemp != -1) {
            if ("".equals(source.substring(bStart, iTemp).trim())) {
                bStart = iTemp;
                h = true;
            }
        }*/
        for (int i = bStart; i < end; i++) {
            char c = source.charAt(i);
            if (i > begin) {
                if (old != escapeVariable && c == beginTag) {
                    bend = i;
                }
                old = c;
            }
        }
/*        if (h) {
            iTemp = source.indexOf("]]>", bStart);
            while (iTemp != -1 && iTemp < bend) {
                iTemp = source.indexOf("]]>", bStart);
                bend = iTemp;
            }
            if (iTemp != -1 && iTemp < bend) {
                bend = iTemp;
            }
        }*/
        return source.substring(bStart, bend);
    }

    /**
     * @return 缓存时间
     */
    @Override
    public long getLastModified() {
        return lastModified;
    }

    /**
     * 清除缓存 ,在缓存释放的时候才调用，普通调用的时候不使用
     */
    @Override
    public void clear() {
        if (nodeList != null) {
            ////////断开引用关系
            for (TagNode tagNode : nodeList) {
                tagNode.setEndLength(0);
                tagNode.setTagName(StringUtil.NULL);
            }
            nodeList.clear();
        }
        source = null;
    }

}