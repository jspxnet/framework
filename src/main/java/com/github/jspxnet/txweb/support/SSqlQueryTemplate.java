/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.support;

import com.github.jspxnet.sober.*;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.impl.GenericDAOImpl;
import com.github.jspxnet.utils.*;
import com.github.jspxnet.txweb.annotation.TurnPage;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-6-17
 * Time: 19:07:30
 * 通用DAO页面调用查询模版
 */
@HttpMethod(caption = "通用DAO模版")
public class SSqlQueryTemplate extends ActionSupport {
    public SSqlQueryTemplate() {

    }


    private TableModels soberTable = null;

    private SoberSupport genericDAO = new GenericDAOImpl();

    private String linkPage = null;
    //查询字段
    private String queryFields = null;
    //排序字段
    private String orderFields = null;
    //排序字段
    private String groupFields = null;

    private boolean loadChild = false;

    private String term;

    private String orderBy;

    private String totalCountMethod = null;

    private boolean rollRows = false;

    private int count = 12;

    private int currentPage = 1;

    private String id = StringUtil.empty;

    public String getTurnPageParams() {
        return turnPageParams;
    }

    public void setTurnPageParams(String turnPageParams) {
        this.turnPageParams = turnPageParams;
    }

    private String turnPageParams = StringUtil.empty;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    public void setSoberFactory(SoberFactory soberFactory) {
        genericDAO.setSoberFactory(soberFactory);
    }


    public String[] getQueryFields() {
        String[] queryArray = StringUtil.split(queryFields, StringUtil.SEMICOLON);
        String[] fieldArray = soberTable.getFieldArray();
        String[] result = null;
        for (String field : fieldArray) {
            if (ArrayUtil.inArray(queryArray, field, false)) {
                result = ArrayUtil.add(result, field);
            }
        }
        return result;
    }

    public void setOrderFields(String orderFields) {
        this.orderFields = orderFields;
    }

    public void setQueryFields(String queryFields) {
        this.queryFields = queryFields;
    }

    public String[] getOrderFields() {
        String[] queryArray = StringUtil.split(queryFields, StringUtil.SEMICOLON);
        String[] fieldArrays = soberTable.getFieldArray();
        String[] result = null;
        for (String field : fieldArrays) {
            if (ArrayUtil.inArray(queryArray, field, false)) {
                result = ArrayUtil.add(result, field);
            }
        }
        return result;
    }

    public String getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(String groupFields) {
        this.groupFields = groupFields;
    }

    public void setClassName(String className) throws ClassNotFoundException {
        soberTable = genericDAO.getSoberFactory().getTableModels(ClassUtil.loadClass(className), genericDAO);
    }

    public String getLinkPage() {
        return linkPage;
    }

    public void setLinkPage(String linkPage) {
        this.linkPage = linkPage;
    }


    public TableModels getSoberTable() {
        return soberTable;
    }


    public boolean isLoadChild() {
        return loadChild;
    }

    public void setLoadChild(boolean loadChild) {
        this.loadChild = loadChild;
    }

    public String getTerm() {
        return term;
    }

    @Param(caption = "条件", max = 50)
    public void setTerm(String term) {
        this.term = term;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getTotalCountMethod() {
        return totalCountMethod;
    }

    public void setTotalCountMethod(String totalCountMethod) {
        this.totalCountMethod = totalCountMethod;
    }

    public boolean isRollRows() {
        return rollRows;
    }

    public void setRollRows(boolean rollRows) {
        this.rollRows = rollRows;
    }

    //查询返回单个对象
    public Object getEntity() throws SQLException {
        return getEntity(id);
    }

    //查询返回单个对象
    public Object getEntity(String id) throws SQLException {
        return genericDAO.get(soberTable.getEntity(), id);
    }


    public List getList() throws Exception {
        Criteria criteria = genericDAO.createCriteria(soberTable.getEntity());
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, orderBy);
        criteria = criteria.setCurrentPage(currentPage);
        criteria = criteria.setTotalCount(count);
        return criteria.list(loadChild);
    }

    public long getTotalCount() throws Exception {
        Criteria criteria = genericDAO.createCriteria(soberTable.getEntity());
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, orderFields);
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }


    private boolean pageEnable = true;

    public boolean isPageEnable() {
        return pageEnable;
    }

    public void setPageEnable(boolean pageEnable) {
        this.pageEnable = pageEnable;
    }

    @TurnPage(params = "find;boxType;sort;term")
    private String turnPage = StringUtil.empty;

    public String getTurnPage() {
        return turnPage;
    }

    public String getOption(String column, Object value) {
        if (value == null) {
            return StringUtil.empty;
        }
        if (soberTable == null) {
            return StringUtil.empty;
        }
        String values = value.toString();
        SoberColumn soberColumn = soberTable.getColumn(column);
        if (soberColumn == null) {
            return null;
        }
        String options = soberColumn.getOption();
        if (options == null || "".equals(options)) {
            return values;
        }
        String[] optionArray = StringUtil.split(options, StringUtil.SEMICOLON);
        for (String line : optionArray) {
            if (line.contains(":")) {
                if (line.equals(values)) {
                    return line;
                } else {
                    String key = StringUtil.substringBefore(line, ":");
                    if (key.equals(values)) {
                        return line.substring(key.length() + 1);
                    }
                }
            }
        }
        return values;
    }

    @Override
    public String execute()  {
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (count < 1) {
            count = 12;
        }
        return SUCCESS;
    }
}