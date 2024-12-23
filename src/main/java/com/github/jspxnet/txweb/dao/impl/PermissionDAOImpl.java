/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao.impl;


import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfigBean;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.util.RoleUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-8
 * Time: 10:47:08
 * 权限管理,负责role roleOperate 的关系
 * com.github.jspxnet.txweb.dao.impl.PermissionDAOImpl
 */
@Slf4j
@Bean
public class PermissionDAOImpl extends JdbcOperations implements PermissionDAO {
    private String namespace = StringUtil.empty; //软件名称
    private String organizeId = StringUtil.empty;

    final static private String ADMIN_ORGANIZE_ID = "10000";
    public PermissionDAOImpl() {

    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getOrganizeId() {
        return organizeId;
    }

    @Override
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    /**
     * @param roleId 得到角色
     * @return 返回角色
     */
    @Override
    public Role getRole(String roleId) {
        return load(Role.class, roleId);
    }

    /**
     * 角色合并
     * @param uid 用户id
     * @return 权限最搞的角色
     */
    @Override
    public MemberRole getMemberRole(long uid) {
        Criteria criteria = createCriteria(MemberRole.class).add(Expression.eq("namespace", namespace)).add(Expression.eq("uid", uid));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID)));
        }
        else
        {
            criteria = criteria.add(Expression.isNull("organizeId"));
        }
        List<MemberRole> memberRoles = criteria.list(true);
        //多个返回权值最大的一个
        if (ObjectUtil.isEmpty(memberRoles))
        {
            return null;
        }
        if (memberRoles.size()==1)
        {
            return memberRoles.get(0);
        }
        MemberRole memberRole =  memberRoles.get(0);
        for (MemberRole mr:memberRoles)
        {
            if (mr==null)
            {
                continue;
            }
            if (CongealEnumType.NO_CONGEAL.getValue()!= mr.getRole().getCongealType())
            {
                continue;
            }
            if (mr.getRole().getUserType()>memberRole.getRole().getUserType())
            {
                memberRole = mr;
            }
        }

        if (memberRole.getRole()==null  || memberRole.getRole().getCongealType()!= CongealEnumType.NO_CONGEAL.getValue())
        {
            return null;
        }
        return memberRole;
    }

    /**
     *
     * @param uid 用户ID
     * @return 得到用户所有的角色
     */
    public List<MemberRole> getMemberRoles(long uid)
    {
        return getMemberRoles(uid,true);
    }

    /**
     *
     * @param uid 用户ID
     * @param load 缓存中得到
     * @return  得到用户所有的角色
     */
    @Override
    public List<MemberRole> getMemberRoles(long uid,boolean load) {
        Criteria criteria = createCriteria(MemberRole.class).add(Expression.eq("namespace", namespace)).add(Expression.eq("uid", uid));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID)) );
        }
        else
        {
            criteria = criteria.add(Expression.isNull("organizeId"));
        }
        return criteria.list(load);
    }

    /**
     * 根据配置生成一个组合角色, 权限合并,权值起高
     * @param uid 用户ID
     * @return 角色
     */
    @Override
    public Role getComposeRole(long uid,String organizeId)
    {
        Criteria criteria = createCriteria(MemberRole.class).add(Expression.eq("namespace", namespace)).add(Expression.eq("uid", uid));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID)) );
        }
        else
        {
            criteria = criteria.add(Expression.isNull("organizeId"));
        }
        List<MemberRole> memberRoleList = criteria.list(true);
        if (ObjectUtil.isEmpty(memberRoleList))
        {
            return null;
        }
        List<Role> roles = new ArrayList<>();
        for (MemberRole memberRole:memberRoleList)
        {
            Role role = memberRole.getRole();
            if (role==null || role.getCongealType()!= CongealEnumType.NO_CONGEAL.getValue())
            {
                continue;
            }
            if (StringUtil.isEmpty(role.getOrganizeId()))
            {
                role.setOrganizeId(organizeId);
            }
            roles.add(role);
        }
        return RoleUtil.mergeRole(roles,organizeId);
    }

    @Override
    public List<Role> getRoleList() {
        return getRoleList(null);
    }

    /**
     * 得到本软件的角色列表
     *
     * @return 返回角色列表
     */
    @Override
    public List<Role> getRoleList(String find) {
        Criteria criteria = createCriteria(Role.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like("name", "%" + find + "%"));
        }
        if (!StringUtil.isNull(organizeId)) {
            criteria = criteria.add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID)) );
        }
        return criteria.addOrder(Order.desc("sortDate")).addOrder(Order.desc("userType")).list(false);
    }

    @Override
    public int getRoleCount(String find) {
        Criteria criteria = createCriteria(Role.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like("name", "%" + find + "%"));
        }
        if (!StringUtil.isNull(organizeId)) {
            criteria = criteria.add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID)) );
        }  else
        {
            criteria = criteria.add(Expression.isNull("organizeId"));
        }
        return criteria.intUniqueResult();
    }

    /**
     * 得到本软件的角色列表
     *
     * @return 返回角色列表
     */
    @Override
    public List<Role> getRoleList(String find,int count,int page) {
        Criteria criteria = createCriteria(Role.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like("name", "%" + find + "%"));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID)) );
        }  else
        {
            criteria = criteria.add(Expression.isNull("organizeId"));
        }
        return criteria.addOrder(Order.desc("sortDate")).addOrder(Order.desc("userType")).setTotalCount(count).setCurrentPage(page)
                .list(false);
    }


    /**
     * 得到本软件的所有动作事件
     *
     * @return 得到动作列表
     */
    @Override
    public Map<String, ActionConfigBean> getActionList() {
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        final Map<String, ActionConfigBean> actionConfigMap = new HashMap<String, ActionConfigBean>();
        final Map<String, String> map = webConfigManager.getExtendList();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value.equalsIgnoreCase(namespace) || key.equalsIgnoreCase(namespace)) {
                actionConfigMap.putAll(webConfigManager.getActionMap(namespace));
            }
        }
        return actionConfigMap;
    }

    @Override
    public boolean deleteRoles(String[] ids) {
        //删除 MemberRole 的映射关系后在删除角色


        if (!StringUtil.isEmpty(organizeId)) {
            List<MemberRole> memberRoles = createCriteria(MemberRole.class).add(Expression.eq("namespace", namespace)).add(Expression.in("roleId", ids)).add(Expression.or(Expression.eq("organizeId", organizeId),Expression.eq("organizeId", ADMIN_ORGANIZE_ID))).list(false);
            List<String> roleIds = BeanUtil.copyFieldList(memberRoles,"roleId");
            if (!ObjectUtil.isEmpty(roleIds))
            {
                createCriteria(Role.class).add(Expression.eq("namespace", namespace)).add(Expression.in("id", roleIds)).delete(false);
                delete(memberRoles);
            }
        } else {
            createCriteria(MemberRole.class).add(Expression.eq("namespace", namespace)).add(Expression.in("roleId", ids)).delete(false);
            createCriteria(Role.class).add(Expression.eq("namespace", namespace)).add(Expression.in("id", ids)).delete(false);
        }
        return true;
    }

    /**
     * 排序时间
     *
     * @param ids id array
     * @return boolean  是否成功
     */
    @Override
    public boolean updateSortDate(String[] ids) {
        if (null == ids) {
            return true;
        }
        try {
            for (String mId : ids) {
                if (StringUtil.isEmpty(mId)) {
                    continue;
                }
                Role role = get(Role.class, mId, false);
                if (role != null) {
                    role.setSortDate(new Date());
                    update(role, new String[]{"sortDate"});
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateCongealType(String[] ids, int congealType) {
        if (null == ids) {
            return true;
        }
        try {
            for (String mId : ids) {
                if (StringUtil.isEmpty(mId)) {
                    continue;
                }
                Role role = get(Role.class, mId, false);
                if (role != null) {
                    role.setCongealType(congealType);
                    update(role, new String[]{"congealType"});
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }


}