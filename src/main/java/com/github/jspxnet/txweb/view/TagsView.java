/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;

import com.github.jspxnet.txweb.dao.TagsDAO;
import com.github.jspxnet.txweb.env.TXWebIoc;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.table.TagWord;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-12
 * Time: 16:32:07
 */
@HttpMethod(caption = "关键字")
public class TagsView extends ActionSupport {
    private int currentPage = 1;
    private int count = 12;
    private String word = StringUtil.empty;

    public TagsView() {

    }

    private TagsDAO tagsDAO;

    @Ref(name = TXWebIoc.tagsDAO)
    public void setTagsDAO(TagsDAO tagsDAO) {
        this.tagsDAO = tagsDAO;
    }

    public String getWord() {
        return word;
    }

    @Param(caption = "关键字")
    public void setWord(String word) {
        this.word = word;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        if (count <= 0) {
            count = config.getInt(Environment.rowCount, 18);
        }
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    @Operate(caption = "列表", method = "list")
    public List<TagWord> getList() throws Exception {

        return tagsDAO.getList(currentPage, count);
    }

    @Operate(caption = "列表,内部参数方式", method = "many")
    public List<TagWord> getMany(@Param(caption = "页数", required = true)int currentPage,@Param(caption = "行数", required = true)int count) throws Exception {
        return tagsDAO.getList(currentPage, count);
    }

    @Operate(caption = "子单纯", method = "tagchild")
    public List<TagWord> getTagChild(@Param(caption = "单词", required = true)String word,@Param(caption = "页数", required = true)int currentPage,@Param(caption = "行数", required = true)int count)  {
        return tagsDAO.getTagWordChild(word, currentPage, count);
    }

    @Deprecated
    public List<TagWord> getTagWordChild()  {
        return tagsDAO.getTagWordChild(word, currentPage, count);
    }



}