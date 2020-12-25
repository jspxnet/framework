/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.sioc.annotation.Ref;
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
 * author: yuan
 * date: 14-2-16
 * 所有数据都放在这个表里边 字典表
 */

public class OptionView extends ActionSupport {
    private static final String ALL_NAMESPACE = "all";
    @Ref
    protected OptionDAO optionDAO;

    public OptionView() {

    }
    private int count = 0;
    private int currentPage = 0;
    private String term = StringUtil.empty;
    private String sort = null;
    private String namespace;
    private String[] field = ArrayUtil.emptyString;
    private String[] find = ArrayUtil.emptyString;
    private Long id;

    public String getNamespace() {
        return namespace;
    }
    @Param(caption = "命名空间", max = 20)
    public void setNamespace(String namespace) {
        this.namespace = namespace;
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

    public Long getId() {
        return id;
    }

    @Param(caption = "ID")
    public void setId(Long id) {
        this.id = id;
    }


    @Operate(caption = "列表")
    public List<OptionBundle> getList() {
        return optionDAO.getList(field, find, getTerm(), namespace,sort, currentPage, getCount());
    }

    @Operate(caption = "列表总数")
    public int getTotalCount()  {
        return optionDAO.getCount(field, find, term,namespace);
    }
    //------上边为老接口-------------下边为新接口

    @Operate(caption = "命名空间名称列表", method = "all/list/page")
    public RocResponse<List<OptionBundle>> getNamespaceList(@Param(caption = "查询关键字", required = true) PageParam params) {
        RocResponse<List<OptionBundle>> response = RocResponse.success(optionDAO.getList(params.getField(), params.getFind(),
                params.getTerm(), ALL_NAMESPACE, params.getSort(), params.getCurrentPage(), params.getCount()));
        response.setTotalCount(optionDAO.getCount(params.getField(), params.getFind(), params.getTerm(), ALL_NAMESPACE));
        return response.setCount(params.getCount()).setCurrentPage(params.getCurrentPage());
    }


    @Operate(caption = "翻页列表", method = "list/page")
    public RocResponse<List<OptionBundle>> getListPage(@Param(caption = "查询关键字", required = true) PageParam params) {
        RocResponse<List<OptionBundle>> response = RocResponse.success(optionDAO.getList(params.getField(), params.getFind(),
                params.getTerm(), params.getNamespace(), params.getSort(), params.getCurrentPage(), params.getCount()));
        response.setTotalCount(optionDAO.getCount(params.getField(), params.getFind(), params.getTerm(), params.getNamespace()));
        return response.setCount(params.getCount()).setCurrentPage(params.getCurrentPage());
    }

    @Operate(caption = "列表", method = "list")
    public RocResponse<List<OptionBundle>> getList(@Param(caption = "查询关键字", required = true) PageParam params) {
        return RocResponse.success(optionDAO.getList(params.getField(), params.getFind(), params.getTerm(), params.getNamespace(), params.getSort(), 1, 50000));
    }

    @Operate(caption = "详细", method = "detail")
    public RocResponse<OptionBundle> detail(@Param(caption = "id", required = true, message = "id不允许为空") long id) {
        return RocResponse.success(optionDAO.load(OptionBundle.class, id));
    }

    @Override
    public String execute() throws Exception {
        if (id!=null&&id>0)
        {
            put("option",optionDAO.load(OptionBundle.class, id));
        }
        return super.execute();
    }
}