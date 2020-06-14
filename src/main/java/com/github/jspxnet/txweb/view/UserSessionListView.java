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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.TurnPage;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-30
 * Time: 17:49:10
 * com.github.jspxnet.txweb.view.UserSessionListView
 */
@HttpMethod(caption = "在线用户")
public class UserSessionListView extends ActionSupport {
    public UserSessionListView() {

    }

    private String find = StringUtil.empty;
    private String term = StringUtil.empty;
    private String sort = StringUtil.empty;
    private int count = 0;
    private int currentPage = 1;
    private boolean load = false;

    @Ref
    private MemberDAO memberDAO;

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        if (count <= 0) {
            count = config.getInt(Environment.rowCount, 18);
        }
        return count;
    }

    public boolean isLoad() {
        return load;
    }

    @Param(caption = "载入关系")
    public void setLoad(boolean load) {
        this.load = load;
    }

    @TurnPage(params = "find;sort;term")
    private String turnPage = StringUtil.empty;

    public String getTurnPage() {
        return turnPage;
    }


    public String getFind() {
        return find;
    }

    @Param(caption = "查询数据")
    public void setFind(String find) {
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

    public List<UserSession> getList()  {
        return memberDAO.getOnlineList(term, sort, getCurrentPage(), getCount(), load);
    }

    public long getTotalCount()  {
        return memberDAO.getOnlineCount(term);
    }


    @Operate(caption = "在线人数翻页列表", method = "/list/page")
    public RocResponse<List<UserSession>> getList(@Param("翻页参数") PageParam param)
    {
        IRole role = getRole();
        if (role.getUserType() < UserEnumType.MANAGER.getValue()) {
            return RocResponse.error(ErrorEnumType.POWER);
        }
        RocResponse<List<UserSession>> rocResponse = BeanUtil.copy(param,RocResponse.class);
        return rocResponse.setData( memberDAO.getOnlineList(param.getTerm(),param.getSort(),param.getCurrentPage(),param.getCount(), load)).setTotalCount(memberDAO.getOnlineCount(param.getTerm()));
    }

}