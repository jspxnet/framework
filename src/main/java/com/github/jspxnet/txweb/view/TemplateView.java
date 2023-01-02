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

import com.github.jspxnet.txweb.annotation.TurnPage;
import com.github.jspxnet.txweb.dao.TemplateDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-30
 * Time: 下午4:18
 */
@HttpMethod(caption = "默认页面浏览")
public class TemplateView extends ActionSupport {
    protected TemplateDAO templateDAO;

    public void setTemplateDAO(TemplateDAO templateDAO) {
        this.templateDAO = templateDAO;
    }


    private int count = 0;
    private long uid = 0;
    private int currentPage = 0;
    private String term = StringUtil.empty;
    private String sort = "sortType:D;sortDate:D";
    private String[] field = ArrayUtil.EMPTY_STRING_ARRAY;
    private String[] find = ArrayUtil.EMPTY_STRING_ARRAY;
    private Serializable id;

    public TemplateView() {

    }

    @TurnPage(params = "find;field;sort;uid")
    private String turnPage = StringUtil.empty;

    public String getTurnPage() {
        return turnPage;
    }

    public String[] getField() {
        return field;
    }

    @Param(caption = "查询字段", max = 20)
    public void setField(String[] field) {
        this.field = field;
    }

    public String[] getFind() {
        return find;
    }

    @Param(caption = "查询数据", max = 20)
    public void setFind(String[] find) {
        this.find = find;
    }

    public String getTerm() {
        return term;
    }

    @Param(caption = "条件", max = 50)
    public void setTerm(String term) {
        this.term = term;
    }

    public String getSort() {
        return sort;
    }

    @Param(caption = "排序", max = 20)
    public void setSort(String sort) {
        this.sort = sort;
    }

    public long getUid() {
        return uid;
    }

    @Param(caption = "用户ID")
    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Operate(caption = "行数")
    public int getCount() {
        if (count <= 0) {
            count = 12;
        }
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    @Operate(caption = "id")
    public Serializable getId() {
        return id;
    }

    @Param(caption = "ID")
    public void setId(Serializable id) {
        this.id = id;
    }

    @Operate(caption = "列表")
    public List<?> getList() throws Exception {
        return templateDAO.getList(field, find, getTerm(), getUid(), sort, getCurrentPage(), getCount());
    }

    @Operate(caption = "列表总数")
    public int getTotalCount() throws Exception {
        return templateDAO.getCount(field, find, term, getUid());
    }

    @Operate(caption = "命名空间")
    public String getNamespace() {
        return templateDAO.getNamespace();
    }


    @Override
    public String execute() throws Exception {
        String className = StringUtil.uncapitalize(StringUtil.substringAfterLast(templateDAO.getClassType().getName(), StringUtil.DOT));
        put(className, templateDAO.load(id));
        return getActionResult();
    }

}