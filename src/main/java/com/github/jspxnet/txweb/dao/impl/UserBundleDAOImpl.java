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
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.txweb.dao.UserBundleDAO;
import com.github.jspxnet.txweb.table.MemberBundle;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-28
 * Time: 下午4:41
 * 用户数据配置绑定
 */
public class UserBundleDAOImpl extends JdbcOperations implements UserBundleDAO {

    protected String namespace; //命名空间

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public MemberBundle getUserBundle(String key, long uid)  {
        if (uid < 0) {
            return null;
        }
        List<MemberBundle> memberBundles = getList(uid);
        for (MemberBundle memberBundle : memberBundles) {
            if (memberBundle.getIdx().equals(key)) {
                return memberBundle;
            }
        }
        return null;
    }


    @Override
    public List<MemberBundle> getList(long uid)  {
        Criteria criteria = createCriteria(MemberBundle.class).add(Expression.eq("namespace", namespace));
        if (uid != -1) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        return criteria.addOrder(Order.asc("createDate")).setCurrentPage(1).setTotalCount(1000).list(true);
    }


    /**
     * 保存
     *
     * @param bundleTable 绑定对象
     * @return boolean  是否成功
     */
    @Override
    public boolean save(MemberBundle bundleTable) throws Exception {
        if (bundleTable == null) {
            return false;
        }
        Criteria criteria = createCriteria(MemberBundle.class).add(Expression.eq("namespace", namespace)).add(Expression.eq("putUid", bundleTable.getPutUid()));
        if (criteria.add(Expression.eq("idx", bundleTable.getIdx())).delete(false) >= 0) {
            bundleTable.setNamespace(namespace);
            return super.save(bundleTable) > 0;
        }
        return false;
    }

}