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

import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.txweb.table.MemberTree;
import com.github.jspxnet.txweb.dao.MemberTreeDAO;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-4-13
 * Time: 10:46:40
 */
public class MemberTreeDAOImpl extends JdbcOperations implements MemberTreeDAO {
    private List<MemberTree> cache = null;

    public MemberTreeDAOImpl() {

    }

    private String namespace = StringUtil.empty;

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    private String organizeId = StringUtil.empty;

    @Override
    public String getOrganizeId() {
        return organizeId;
    }

    @Override
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    /**
     * @param uid 用户名称
     * @return 得到用户树
     */
    @Override
    public List<MemberTree> getMemberTree(long uid) {
        if (cache == null || cache.isEmpty()) {
            Criteria criteria = createCriteria(MemberTree.class).add(Expression.eq("namespace", namespace));
            if (!StringUtil.isEmpty(organizeId)) {
                criteria = criteria.add(Expression.eq("organizeId", organizeId));
            }
            cache = criteria.list(false);
        }
        List<MemberTree> result = new ArrayList<MemberTree>();
        for (MemberTree memberTree : cache) {
            if (memberTree.getUid() == uid) {
                result.add(memberTree);
            }
        }
        return result;

    }

    /**
     * @param uid 用户名称
     * @return 得到用户树map
     */
    @Override
    public Map<String, MemberTree> getMemberTreeMap(long uid) {
        Map<String, MemberTree> map = new LinkedHashMap<String, MemberTree>();
        List<MemberTree> list = getMemberTree(uid);
        for (Object o : list) {
            MemberTree manTree = (MemberTree) o;
            map.put(manTree.getNodeId(), manTree);
        }
        return map;
    }

    /**
     * @param uid 用户id
     * @return 返回用户树, 格式为 jcms1;jcms2;jcms3
     */
    @Override
    public String getMemberTreeSplitString(long uid) {
        List<MemberTree> list = getMemberTree(uid);
        if (list == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (MemberTree memberTree : list) {
            sb.append(memberTree.getNodeId()).append(StringUtil.SEMICOLON);
        }
        return sb.toString();
    }

    /**
     * @param uid 用户id
     * @return 用户树
     */
    @Override
    public String[] getMemberTreeArray(long uid) {
        String[] result = new String[0];
        List<MemberTree> list = getMemberTree(uid);
        if (list == null) {
            return new String[0];
        }
        for (MemberTree memberTree : list) {
            result = ArrayUtil.add(result, memberTree.getNodeId());
        }
        return result;
    }

    /**
     * @return boolean
     */
    @Override
    public boolean deleteAll() {
        Criteria criteria = createCriteria(MemberTree.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.delete(false) > 0;
    }



    /**
     *
     * @param uid 用户id
     * @param treeId 树id
     * @return 是否删除
     */
    @Override
    public boolean deleteForUid(long uid,String treeId)
    {
        Criteria criteria = createCriteria(MemberTree.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("uid", uid));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        if (!StringUtil.isEmpty(treeId)) {
            criteria = criteria.add(Expression.eq("treeId", treeId));
        }
        return criteria.delete(false) > 0;
    }

    /**
     * @param treeItemId 用户id
     * @return boolean   删除不是本数的节点
     */
    @Override
    public boolean fixTreeItem(String[] treeItemId) {
        Criteria criteria = createCriteria(MemberTree.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }

        criteria = criteria.add(Expression.notIn("nodeId", treeItemId));
        return criteria.delete(false) > 0;
    }

    /**
     * 刷新
     */
    @Override
    public void flush() {
        if (cache != null) {
            cache.clear();
        }
    }

}