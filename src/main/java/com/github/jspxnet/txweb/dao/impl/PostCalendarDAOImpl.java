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

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.txweb.table.PostCalendar;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.txweb.dao.PostCalendarDAO;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-10-6
 * Time: 15:08:15
 * 记录软件发布数量,详细到天(几号),updatePostCalendar() 将更新，每次加1
 */
public class PostCalendarDAOImpl extends JdbcOperations implements PostCalendarDAO {
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 得到两个日期间的数据列表
     */
    @Override
    public List<PostCalendar> getBetweenList(Date beginDate, Date endDate) {
        return createCriteria(PostCalendar.class).add(Expression.between("postDate", beginDate, endDate))
                .add(Expression.eq("namespace", namespace)).add(Expression.eq("organizeId", organizeId)).addOrder(Order.asc("postDate")).list(false);

    }

    /**
     * @param date 日期
     * @return 得到某一天的提交数据
     */
    @Override
    public PostCalendar getPostCalendar(Date date) {
        return  createCriteria(PostCalendar.class).add(Expression.eq("shortDate", DateUtil.toString(date, DateUtil.DAY_FORMAT))).add(Expression.eq("namespace", namespace)).add(Expression.eq("organizeId", organizeId)).objectUniqueResult(false);
    }

    /*
     *
     * @param year 年
     * @return 返回一年的所有月份提交数据
     */
    @Override
    public Map<String, Integer> getMonthListCountMap(int year) {
        TableModels soberTable = getSoberTable(PostCalendar.class);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dateMonth,sum(postCount) as NUM FROM ").append(soberTable.getName());
        sql.append(" WHERE dateYear>=").append(year).append(" AND namespace=").append(StringUtil.quote(StringUtil.toScript(namespace),false));
        if (!StringUtil.isEmpty(organizeId)) {
            sql.append(" AND organizeId=").append(StringUtil.quote(organizeId,false));
        }
        sql.append(" GROUP BY dateMonth ORDER BY dateMonth DESC");
        List<Object> list = query(sql.toString(), null, 1, 5000);
        Map<String, Integer> result = new HashMap<>();
        for (Object bean : list) {
            if (bean == null) {
                continue;
            }
            result.put(year + "-" + BeanUtil.getProperty(bean, "dateMonth"), ObjectUtil.toInt(BeanUtil.getProperty(bean, "NUM")));
        }
        return result;
    }

    @Override
    public boolean updatePostCalendar() throws Exception {
        return updatePostCalendar(new Date(), 1);
    }

    /**
     * @param date  日期
     * @param count 数量
     * @return 更新日历统计数据
     */
    @Override
    public boolean updatePostCalendar(Date date, long count) throws Exception {
        PostCalendar postCalendar = getPostCalendar(date);
        if (postCalendar == null) {
            postCalendar = new PostCalendar();
            postCalendar.setPostCount(count);
            postCalendar.setPostDate(date);
            postCalendar.setNamespace(namespace);
            postCalendar.setOrganizeId(organizeId);
            save(postCalendar);
        } else if (postCalendar.getPostCount() != count) {
            postCalendar.setPostCount(postCalendar.getPostCount() + count);
            postCalendar.setPostDate(date);
            postCalendar.setNamespace(namespace);
            postCalendar.setOrganizeId(organizeId);
            update(postCalendar);
        }
        return true;
    }

}