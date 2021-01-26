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
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.StringUtil;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-12-10
 * Time: 下午3:27
 */
@Deprecated
@HttpMethod(caption = "在线人员")
public class OnlineView extends ActionSupport {
    public OnlineView() {

    }

    private int count = 0; //a page show row
    private int currentPage = 1; //currently page number
    private String sort = "createDate:D,replyDate:D"; //sort:use ssql transfer look com.github.jspxnet.sober.ssql.SSqlExpression
    private String term = StringUtil.empty;  //term:use ssql transfer look com.github.jspxnet.sober.ssql.SSqlExpression

    @Ref
    protected MemberDAO memberDAO;


    @Ref
    protected PermissionDAO permissionDAO;

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

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getSort() {
        return sort;
    }

    @Param(caption = "排序", max = 20)
    public void setSort(String sort) {
        this.sort = sort;
    }


    public String getTerm() {
        return term;
    }

    @Param(caption = "条件", max = 50)
    public void setTerm(String term) {
        this.term = term;
    }

    @Operate(caption = "在线列表")
    public List<UserSession> getOnlineList() {
        List<UserSession> list = memberDAO.getOnlineList(term, sort, getCurrentPage(), getCount(), true);
        for (UserSession userSession : list) {
            if (userSession.isGuest()) {
                userSession.setRole(permissionDAO.getRole(config.getString(Environment.guestRole)));
            } else {
                List<MemberRole> roles = permissionDAO.getMemberRoles(userSession.getUid(),true);
                if (!roles.isEmpty()) {
                    for (MemberRole memberRole : roles) {
                        userSession.setRole(memberRole.getRole());
                    }
                } else {
                    userSession.setRole(permissionDAO.getRole(config.getString(Environment.registerRole)));
                }
            }
        }
        return list;
    }


    @Operate(caption = "列表总数")
    public int getTotalCount()  {
        return memberDAO.getOnlineCount(term);
    }

    @TurnPage(params = "term;sort", enable = "true")
    private String turnPage = StringUtil.empty;

    public String getTurnPage() {
        return turnPage;
    }

    /**
     *
     * @param param 翻页参数
     * @return 在线人数翻页列表
     */
    @Operate(caption = "在线人数翻页列表", method = "/list/page")
    public RocResponse<List<UserSession>> getList(@Param("翻页参数") PageParam param)
    {
        IRole role = getRole();
        if (role.getUserType() < UserEnumType.MANAGER.getValue()) {
            return RocResponse.error(ErrorEnumType.POWER);
        }

        List<UserSession> list = memberDAO.getOnlineList(param.getTerm(),param.getSort(),param.getCurrentPage(),param.getCount(), true);
        for (UserSession userSession : list) {
            if (userSession.isGuest()) {
                userSession.setRole(permissionDAO.getRole(config.getString(Environment.guestRole)));
            } else {
                List<MemberRole> roles = permissionDAO.getMemberRoles(userSession.getUid(),true);
                if (!roles.isEmpty()) {
                    for (MemberRole memberRole : roles) {
                        userSession.setRole(memberRole.getRole());
                    }
                } else {
                    userSession.setRole(permissionDAO.getRole(config.getString(Environment.registerRole)));
                }
            }
        }
        RocResponse<List<UserSession>> rocResponse = RocResponse.success(list);
        return rocResponse.setData(list).setTotalCount(memberDAO.getOnlineCount(term)).setCurrentPage(param.getCurrentPage()).setCount(param.getCount());
    }

}