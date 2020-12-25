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

    //-------------------

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

}