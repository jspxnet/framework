/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.ActionLogDAO;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-10-16
 * Time: 下午10:13
 */
@Slf4j
public class ActionLogDAOImpl extends JdbcOperations implements ActionLogDAO {


    public ActionLogDAOImpl() {

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


    private String organizeId;

    @Override
    public String getOrganizeId() {
        return organizeId;
    }

    @Override
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    /**
     * @param id id
     * @return 载入
     */
    @Override
    public Object load(String id) {
        return super.load(ActionLog.class, id);
    }

    /**
     * @param ids id 列表
     * @return 更新排序是否成功
     */
    @Override
    public boolean delete(String[] ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        try {
            for (String id : ids) {
                super.delete(ActionLog.class, id);
            }
        } catch (Exception e) {
            log.error(MessageFormatter.format("delete ActionLog {}", ArrayUtil.toString(ids, StringUtil.SEMICOLON)).getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public int clear() {
        return createCriteria(ActionLog.class).add(Expression.eq("namespace", namespace)).delete(false);
    }


    /**
     * @param field      字段
     * @param find       查询字符串 "" 自动分开查询
     * @param term       特别情况标记  0 不管   1 查询有图片的 2 查询无图片的  3 查询有下载文件的   4 查询无下载文件的  5 查询有图片的
     * @param sortString 排序字符串
     * @param uid        用户id
     * @param page       页数
     * @param count      返回数量
     * @return 返回对象列表
     */
    @Override
    public List<ActionLog> getList(String[] field, String[] find, String term, String sortString, long uid, int page, int count)  {
        if (StringUtil.isNull(sortString)) {
            sortString = "createDate:D";
        }
        Criteria criteria = createCriteria(ActionLog.class).add(Expression.eq("namespace", namespace));
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return SSqlExpression.getSortOrder(criteria, sortString).setCurrentPage(page).setTotalCount(count).list(false);
    }


    /**
     * @param field 字段
     * @param find  条件
     * @param term  条件表达式
     * @param uid   用户id
     * @return 得到记录条数
     */
    @Override
    public long getCount(String[] field, String[] find, String term, long uid) {
        Criteria criteria = createCriteria(ActionLog.class).add(Expression.eq("namespace", namespace));
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }

    @Override
    public int deleteYearBefore(int year) {
        return createCriteria(ActionLog.class).add(Expression.lt("createDate", DateUtil.addYear(-year))).delete(false);

    }

}