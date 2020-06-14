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

import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;

import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-2-4
 * Time: 下午10:10
 */
@HttpMethod(caption = "默认浏览")
public class GenericView extends ActionSupport {
    private String namespace = StringUtil.empty;
    private String className = StringUtil.empty;
    private String turnPage = StringUtil.empty;
    private int count = 0;
    private long id = 0;
    private long uid = 0;
    private int currentPage = 0;
    private String term = StringUtil.empty;
    private String sort = "sortType:D;fileType:A";
    private String field = StringUtil.empty;
    private String find = StringUtil.empty;
    private String turnPageFile = "turnpage.ftl";

    @Ref
    protected GenericDAO genericDAO;

    public void setGenericDAO(GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    public String getNamespace() {
        return namespace;
    }

    @Param(caption = "命名空间", max = 50)
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getClassName() {
        return className;
    }

    @Param(caption = "类名", max = 100)
    public void setClassName(String className) {
        this.className = className;
    }

    public String getTurnPage() {
        return turnPage;
    }

    @Param(request = false)
    public void setTurnPage(String turnPage) {
        this.turnPage = turnPage;
    }

    public int getCount() {
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
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

    public String getField() {
        return field;
    }

    @Param(caption = "查询字段", max = 20)
    public void setField(String field) {
        this.field = field;
    }

    public String getFind() {
        return find;
    }

    @Param(caption = "查询数据", max = 20)
    public void setFind(String find) {
        this.find = find;
    }

    public String getTurnPageFile() {
        return turnPageFile;
    }

    @Param(request = false)
    public void setTurnPageFile(String turnPageFile) {
        this.turnPageFile = turnPageFile;
    }

    public List getList() throws Exception {
        return genericDAO.getList(ClassUtil.loadClass(className), field, find, getTerm(), uid, sort, getCurrentPage(), getCount(), false);
    }

    public long getTotalCount() throws Exception {
        return genericDAO.getCount(ClassUtil.loadClass(className), field, find, getTerm(), uid);
    }
}