/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.TurnPage;
import com.github.jspxnet.txweb.dao.ActionLogDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-10-16
 * Time: 下午10:11
 */

public class ActionLogView extends ActionSupport {

    public ActionLogView() {

    }

    @Ref
    protected ActionLogDAO actionLogDAO;

    @Param(request = false)
    public void setOrganizeId(String organizeId) {
        actionLogDAO.setOrganizeId(organizeId);
    }


    private int count = 0;
    private long uid = 0;
    private int currentPage = 0;
    private String term = StringUtil.empty;
    private String sort = "createDate:D";
    private String[] field = ArrayUtil.emptyString;
    private String[] find = ArrayUtil.emptyString;
    private String id = StringUtil.empty;

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

    public String getId() {
        return id;
    }

    @Param(caption = "ID")
    public void setId(String id) {
        this.id = id;
    }

    @Operate(caption = "列表")
    public List<ActionLog> getList()  {
        return actionLogDAO.getList(field, find, getTerm(), sort, getUid(), getCurrentPage(), getCount());
    }

    @Operate(caption = "列表总数")
    public long getTotalCount()  {
        return actionLogDAO.getCount(field, find, term, getUid());
    }

    /**
     * @param param 翻页参数
     * @return 日志列表
     */
    @Operate(caption = "日志翻页列表", method = "list/page")
    public RocResponse<List<ActionLog>> getList(@Param("翻页参数") PageParam param) {
        IRole role = getRole();
        if (role.getUserType() < UserEnumType.MANAGER.getValue()) {
            return RocResponse.error(ErrorEnumType.POWER);
        }
        RocResponse<List<ActionLog>> rocResponse = RocResponse.success(actionLogDAO.getList(param.getField(),
                param.getFind(), param.getTerm(), param.getSort(), param.getUid(), param.getCurrentPage(), param.getCount()));
        rocResponse.setTotalCount(actionLogDAO.getCount(param.getField(), param.getFind(), param.getTerm(), param.getUid()));
        return rocResponse.setCurrentPage(param.getCurrentPage()).setCount(param.getCount());
    }

    @Override
    public String execute() throws Exception {
        put("actionLog", actionLogDAO.load(id));
        return getActionResult();
    }
}