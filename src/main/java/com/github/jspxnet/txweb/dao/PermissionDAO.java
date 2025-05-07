/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.config.ActionConfigBean;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.table.Role;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-8
 * Time: 10:47:26
 */
public interface PermissionDAO extends SoberSupport {

    /**
     * 该账号是否是创建人
     *
     * @param oid  oid
     * @param uid 用户id
     * @return true = 创建人
     */
    boolean existsCreate(String oid, long uid);

    MemberRole getMemberRole(long uid) throws SQLException;

    Role getRole(String roleId);

    List<MemberRole> getMemberRoles(long uid, boolean load);


    Role getComposeRole(long uid, String organizeId);

    List<Role> getRoleList();

    List<Role> getRoleList(String find) throws SQLException;

    int getRoleCount(String find) throws SQLException;

    boolean updateSortDate(String[] ids);

    boolean updateCongealType(String[] ids, int congealType);

    List<Role> getRoleList(String find, int count, int page);

    List<Role> getRoleList(String namespace, String organizeId, String find, int page, int count);

    Map<String, ActionConfigBean> getActionList() throws Exception;

    boolean deleteRoles(String[] ids);

    String getNamespace();

    void setNamespace(String namespace);

    void setOrganizeId(String organizeId);

    String getOrganizeId();

}