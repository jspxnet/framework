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

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.annotation.Describe;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.config.ActionConfigBean;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.vo.OperateVo;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.utils.StringUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-8
 * Time: 16:26:27
 * 权限查看
 */
@HttpMethod(caption = "权限")
public class PermissionView extends ActionSupport {
    private final WebConfigManager WEB_CONFIG_MANAGER = TXWebConfigManager.getInstance();

    public PermissionView() {

    }

    @Ref
    protected PermissionDAO permissionDAO;

    private String find = StringUtil.empty;

    @Param(caption = "查询数据",max = 20)
    public void setFind(String find) {
        this.find = find;
    }

    public String getFind() {
        return find;
    }

    @Operate(caption = "命名空间列表")
    public List<String> getNamespaceList()  {
        return WEB_CONFIG_MANAGER.getNamespaceList();
    }

    @Operate(caption = "继承关系表")
    public Map<String, String> getExtendList() {
        return WEB_CONFIG_MANAGER.getExtendList();
    }

    @Operate(caption = "动作映射表")
    public Map<String, ActionConfigBean> getActionMap()  {
        return WEB_CONFIG_MANAGER.getActionMap(permissionDAO.getNamespace());
    }

    @Param(caption = "放入机构ID",request = false)
    public void setOrganizeId(String organizeId)
    {
        permissionDAO.setOrganizeId(organizeId);
    }

    private String id;

    public String getId() {
        return id;
    }

    @Describe("兼容老版本,不建议是用")
    @Param(caption = "id")
    public void setId(String id) {
        this.id = id;
    }


    @Operate(caption = "兼容老版本")
    public Role getMemberRole() {
        return permissionDAO.getRole(id);
    }


    @Operate(caption = "全局id得到角色")
    public Role getMemberRole(@Param(caption = "角色id",max = 64) String id) {
        return permissionDAO.getRole(id);
    }

    /**
     * @param id 用户ID
     * @return 得到用户角色
     * @throws SQLException 异常
     */
    @Operate(caption = "得到角色")
    public Role getMemberRole(@Param(caption = "角色id") long id) throws SQLException {
        MemberRole memberRole = permissionDAO.getMemberRole(id);
        if (memberRole != null) {
            return memberRole.getRole();
        }
        return new Role();
    }


    @Operate(caption = "操作列表")
    public List<OperateVo> getOperateList() throws Exception {
        return WEB_CONFIG_MANAGER.getOperateList(permissionDAO.getNamespace());
    }

    @Describe("兼容老版本")
    @Operate(caption = "角色列表")
    public List<Role> getList() throws SQLException {
        return permissionDAO.getRoleList(find);
    }

    @Operate(caption = "角色列表")
    public List<Role> getList(@Param(caption = "查询数据",max = 20)String find) throws SQLException {
        return permissionDAO.getRoleList(find);
    }

    @Operate(caption = "列表")
    public int getTotalCount() throws Exception {
        return permissionDAO.getRoleCount(find);
    }

    @Operate(caption = "分页列表", method = "list/page" )
    public RocResponse<List<Role>> getListPage(@Param(caption = "查询数据",max = 20)String find,
                                               @Param(caption = "行数",min = 1,max = 5000,value = "12") Integer count,
                                               @Param(caption = "当前页数",max = 5000,value = "1") Integer currentPage
                                               ) throws SQLException {
        int totalCount = permissionDAO.getRoleCount(find);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(), language.getLang(LanguageRes.notDataFind));
        }
        List<Role> list =  permissionDAO.getRoleList(find,count,currentPage);
        return RocResponse.success(list).setTotalCount(count).setCurrentPage(currentPage);
    }


    @Operate(caption = "得到选项")
    public String getOptionList() throws SQLException {
        StringBuilder sb = new StringBuilder();
        List<Role> roles = getList();
        sb.append(0).append(":").append("无;");
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            sb.append(role.getId()).append(":").append(role.getName());
            if (i < roles.size() - 1) {
                sb.append(StringUtil.SEMICOLON);
            }
        }
        return sb.toString();
    }



}