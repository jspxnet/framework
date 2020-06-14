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


import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.scriptmark.*;
import com.github.jspxnet.scriptmark.core.block.CallBlock;
import com.github.jspxnet.scriptmark.core.block.CommentBlock;
import com.github.jspxnet.scriptmark.core.block.NoneBlock;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-15
 * Time: 8:16:17
 * 模板解析器,能够兼容HTML,需要解析的标签通过 TemplateConfigurable 配置来完成
 * 解析中 宏调用使用@开头,注释使用 ！--# 格式，这两个标签比较特殊
 * 模板执行后，如果采用 <!--# #-->  格式的注释，系统会自动清空,如果采用 <!-- --> 格式将会保留在页面中
 */
@Slf4j
public class HtmlEngineImpl implements HtmlEngine {
    final static public String NoneTag = "none";
    private TemplateModel templateElement;
    private String macroCallTag = "@";
    private char beginTag = '<';
    private char fenTag = '/';
    private char endTag = '>';

    //为了兼容HTML 采用注释格式为 <!--# 注释内容 #-->
    final static private String noteTagBegin = CommentBlock.noteTagBegin;
    final static private String noteTagEnd = CommentBlock.noteTagEnd;

    protected char escapeVariable = '\\';
    private boolean htmlExtType = false;

    public HtmlEngineImpl(TemplateModel templateElement) {
        this.templateElement = templateElement;
        Configurable config = this.templateElement.getConfigurable();
        macroCallTag = config.getString(ScriptmarkEnv.MacroCallTag);
        String syncopate = config.getString(ScriptmarkEnv.Syncopate);
        if (syncopate == null || syncopate.length() < 1) {
            syncopate = "<>";
        }
        beginTag = syncopate.charAt(0);
        endTag = syncopate.charAt(1);
        String fen = config.getString(ScriptmarkEnv.SyncopateFenTag);
        if (StringUtil.isNull(fen)) {
            fenTag = '/';
        } else {
            fenTag = fen.charAt(0);
        }
        escapeVariable = config.getString(ScriptmarkEnv.escapeVariable).charAt(0);
        htmlExtType = config.getBoolean(ScriptmarkEnv.htmlExtType);
    }

    /**
     * 得到全部树
     *
     * @return 返回代码树
     */
    @Override
    public List<TagNode> getComputeTree() {
        return getBlockTree(templateElement.getSource(), templateElement.getConfigurable().getTagMap());
    }

    /**
     * 得到部分代码树
     *
     * @param begin  开始位置
     * @param end    结束位置
     * @param tagMap 要取的标签
     * @return 节点列表
     */
    @Override
    public List<TagNode> getBlockTree(int begin, int end, Map<String, String> tagMap) {
        if (tagMap == null || tagMap.isEmpty()) {
            tagMap = templateElement.getConfigurable().getTagMap();
        }
        if (end < 0) {
            end = templateElement.getSource().length();
        }
        return getBlockTree(templateElement.getSource(begin, end), tagMap);
    }

    /**
     * 只判断自己的开始和结束
     *
     * @param code   结束长度
     * @param tagMap 要的得到的标签,主要方便 else 和 case
     * @return 返回代码树
     */
    @Override
    public List<TagNode> getBlockTree(final String code, Map<String, String> tagMap) {
        if (tagMap == null) {
            log.error("need tag define parse,没有定义要解析的标签");
            return new ArrayList<>();
        }
        List<TagNode> nodeTree = new LinkedList<>();
        if (code == null) {
            return nodeTree;
        }
        int times = 0;
        int i = 0;
        boolean in = false;
        int end = code.length();
        String tagName = StringUtil.empty;
        TagNode tagNode = null;


        while (i < end) {
            char str = code.charAt(i);
            /////自己开始 begin
            if (str == beginTag && code.charAt(i + 1) != fenTag) {
                //处理注释begin
                if (!in && code.startsWith(noteTagBegin, i + 1)) {
                    int pos = code.indexOf(noteTagEnd + endTag, i + noteTagBegin.length());
                    if (pos == -1) {
                        i = i + noteTagBegin.length();
                        log.error(noteTagBegin + " not end,注释标签没有结尾，" + code);
                        break;
                    }
                    TagNode commentTagNode = new CommentBlock();
                    commentTagNode.setStarLength(i);
                    commentTagNode.setEndLength(pos + (noteTagEnd + endTag).length());
                    commentTagNode.setTemplate(templateElement);
                    i = commentTagNode.getEndLength();
                    nodeTree.add(commentTagNode);
                    tagNode = null;
                    continue;
                }
                //处理注释end

                String stag = code.substring(i + 1);
                int mi = stag.indexOf(' ');
                if (mi == -1) {
                    mi = stag.indexOf(fenTag);
                }
                if (mi != -1) {
                    stag = stag.substring(0, mi);
                    mi = stag.indexOf(endTag);
                    if (mi != -1) {
                        stag = stag.substring(0, mi);
                    }
                    if (tagMap.containsKey(stag.toLowerCase()) || stag.startsWith(macroCallTag)) {
                        //////////得到标签名称
                        tagName = stag;
                    }
                }

                if (i + tagName.length() + 1 < end) {
                    String temp = code.substring(i + 1, i + tagName.length() + 1);
                    char k = code.charAt(i + tagName.length() + 1);
                    if (StringUtil.hasLength(tagName) && tagName.equalsIgnoreCase(temp) && (k == ' ' || k == fenTag || k == endTag))         //<#if  or <#if/>
                    {
                        ////////////翻转修复 begin
                        if (in && tagNode.getTagName().equalsIgnoreCase(tagName) && tagNode.isRepair()) {
                            int ri = code.indexOf(String.valueOf(beginTag) + fenTag + endTag, i);
                            if (ri == -1) {
                                ri = code.indexOf(endTag, tagNode.getStarLength());
                                if (ri != -1) {
                                    tagNode.setEndLength(ri + 1);
                                    i = i - (i - ri) + 1;
                                    in = false;
                                    continue;
                                }
                            }
                        }
                        ////////////翻转修复 end
                        if (in && tagNode.getTagName().equalsIgnoreCase(tagName) && !NoneTag.equals(tagNode.getTagName())) {
                            times++;

                        } else if (!in) {
                            /////////////////无标签结束
                            if (tagNode != null && tagNode.getEndLength() <= 0) {
                                tagNode.setEndLength(i);
                            }
                            in = true;
                            times = 1;
                            if (tagName.toLowerCase().startsWith(macroCallTag.toLowerCase())) {
                                tagNode = new CallBlock();
                            } else {
                                String className = tagMap.get(tagName.toLowerCase());
                                try {
                                    tagNode = (TagNode) ClassUtil.newInstance(className);
                                } catch (Exception e) {
                                    log.error("反射载入 " + className + "失败,newInstance load class error:" + className);
                                    e.printStackTrace();
                                }
                            }
                            tagNode.setTagName(tagName);
                            tagNode.setStarLength(i);
                            tagNode.setEndLength(i);
                            tagNode.setTemplate(templateElement);
                            nodeTree.add(tagNode);
                        }
                    }
                }
            }
            /////自己开始 end

            ///////////////文本begin
            if (!in && htmlExtType) {
                if (tagNode != null && NoneTag.equals(tagNode.getTagName())) {
                    tagNode.setEndLength(i + 1);
                } else {
                    tagNode = new NoneBlock();
                    tagNode.setTagName(NoneTag);
                    tagNode.setStarLength(i);
                    tagNode.setEndLength(i + 1);
                    tagNode.setTemplate(templateElement);
                    nodeTree.add(tagNode);

                }
            }

            if (in && str == endTag)//&& (i > 0 && code.charAt(i - 1) != escapeVariable))
            {
                //自己结束
                if (i > 0 && code.charAt(i - 1) == fenTag && code.charAt(i - 1) != escapeVariable) // <#if xxx /> <#if/>
                {
                    //到起查找开始的标记名称 有escapeVariable 跳开
                    String sTemp = code.substring(0, i);
                    int ib;
                    while ((ib = sTemp.lastIndexOf(beginTag)) != -1) {
                        if (ib > 1 && sTemp.charAt(ib - 1) == escapeVariable) {
                            sTemp = sTemp.substring(0, ib);
                            continue;
                        }
                        sTemp = sTemp.substring(ib);
                        break;
                    }
                    int mi = sTemp.indexOf(' ');
                    if (mi == -1) {
                        mi = sTemp.indexOf(fenTag);
                    }
                    if (mi != -1) {
                        sTemp = sTemp.substring(1, mi);
                        if (sTemp.equalsIgnoreCase(tagNode.getTagName())) {
                            times--;
                        }
                    }
                } else if (i > tagName.length()) {
                    String temp = code.substring(i - 1 - tagNode.getTagName().length(), i + 1);
                    if (temp.equalsIgnoreCase(fenTag + tagNode.getTagName() + endTag))         //</#if>   </#if>
                    {
                        times--;
                    }
                }
                if (times < 1) {
                    in = false;
                }
                tagNode.setEndLength(i + 1);
            }
            i++;
            //最后修复
            ////////////翻转修复 begin
            if (i == end && in && !NoneTag.equals(tagNode.getTagName()) && tagNode.isRepair()) {
                int ri = code.indexOf(endTag, tagNode.getStarLength());
                if (ri != -1) {
                    tagNode.setEndLength(ri + 1);
                    i = i - (i - ri) + 1;
                    in = false;
                }
            }
            ////////////翻转修复 end
        }
        return nodeTree;
    }

}