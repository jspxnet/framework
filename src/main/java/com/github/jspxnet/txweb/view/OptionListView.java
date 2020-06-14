/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.OptionDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 *
 * author: yuan
 * date: 14-2-16
 * 所有数据都放在这个表里边 字典表
 */
@HttpMethod(caption = "字典表")
public class OptionListView extends ActionSupport {

    @Ref
    protected OptionDAO optionDAO;

    private int count = 0;
    private int currentPage = 0;
    private String term = StringUtil.empty;
    private String sort = "sortType:A";
    private String[] field = ArrayUtil.emptyString;
    private String[] find = ArrayUtil.emptyString;
    private long id;

    public OptionListView() {

    }

    public String getNamespace() {
        return optionDAO.getNamespace();
    }

    @Param(caption = "命名空间")
    public void setNamespace(String namespace) {
        optionDAO.setNamespace(namespace);
    }

    public String getCaption() {
        return optionDAO.getCaption();
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

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        if (count <= 0) {
            count = 18;
        }
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }

    @Param(caption = "ID")
    public void setId(long id) {
        this.id = id;
    }


    @Operate(caption = "列表")
    public List<OptionBundle> getList() {
        return optionDAO.getList(field, find, getTerm(), sort, currentPage, getCount());
    }

    @Operate(caption = "列表总数")
    public int getTotalCount()  {
        return optionDAO.getCount(field, find, term);
    }

    @Operate(caption = "标题")
    public String getCaption(String key) {
        return optionDAO.getSpaceMap().get(key);
    }

    @Operate(caption = "翻页列表",method = "list/page")
    public RocResponse<List<OptionBundle>> getListPage(@Param(caption = "查询关键字",required = true) PageParam pageParam) {
        optionDAO.setNamespace(pageParam.getNamespace());
        RocResponse<List<OptionBundle>> response = RocResponse.success(optionDAO.getList(pageParam.getField(),pageParam.getFind(),
                null,pageParam.getSort(),pageParam.getCurrentPage(),pageParam.getCount()));
        response.setTotalCount(optionDAO.getCount(pageParam.getField(),pageParam.getFind(),null));
        return response.setCount(count).setCurrentPage(currentPage);
    }

    @Operate(caption = "列表",method = "list")
    public RocResponse<List<OptionBundle>> getLis(@Param(caption = "查询关键字",required = true) PageParam pageParam) {
        optionDAO.setNamespace(pageParam.getNamespace());
        return RocResponse.success(optionDAO.getList(pageParam.getField(),pageParam.getFind(),null,pageParam.getSort(),1,50000));
    }

    @Override
    public String execute() {
        put("option", optionDAO.load(OptionBundle.class,id));
        return getActionResult();
    }
}