/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.impl;

import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.SoberEnv;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.LogicalExpression;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.criteria.projection.Projection;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 10:28:37
 * hibernate 的 Criteria 方式扩展
 */
@Slf4j
public class CriteriaImpl<T> implements Criteria, Serializable {

    //当前处理类
    final private Class<T> criteriaClass;
    //数据源工厂
    final private SoberFactory soberFactory;
    //表达式列表
    final private List<CriterionEntry> criterionEntries = new ArrayList<>();
    //排序列表
    final private List<OrderEntry> orderEntries = new ArrayList<>();
    //分组列表
    final private List<String> groupList = new ArrayList<>();
    //基本的JDBC操作类
    final private JdbcOperations jdbcOperations;
    //页数
    private Integer currentPage = 1;
    //行数
    private int totalCount;

    private Projection projection = null;

    public CriteriaImpl(Class<T> criteriaClass, JdbcOperations jdbcOperations) {
        this.criteriaClass = criteriaClass;
        this.jdbcOperations = jdbcOperations;
        this.soberFactory = jdbcOperations.getSoberFactory();
        this.totalCount = jdbcOperations.getMaxRows();

    }

    @Override
    public Class<T> getCriteriaClass() {
        return criteriaClass;
    }

    @Override
    public Criteria setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public Criteria setTotalCount(Integer totalCount) {
        if (totalCount > jdbcOperations.getMaxRows()) {
            totalCount = jdbcOperations.getMaxRows();
        }
        this.totalCount = totalCount;
        return this;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    private Criteria add(Criteria criteriaInst, Criterion expression) {
        criterionEntries.add(new CriterionEntry(expression, criteriaInst));
        return this;
    }

    @Override
    public Criteria add(Criterion criterion) {
        return add(this, criterion);
    }

    @Override
    public Criteria addOrder(Order ordering) {
        orderEntries.add(new OrderEntry(ordering, this));
        return this;
    }

    @Override
    public Criteria addGroup(String group) {
        groupList.add(group);
        return this;
    }

    /**
     * @param loadChild 是否载入映射
     * @return 载入单个对象
     */
    @Override
    public T objectUniqueResult(boolean loadChild) {
        setCurrentPage(1);
        setTotalCount(1);
        List<T> list = list(loadChild);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public boolean booleanUniqueResult() {
        return ObjectUtil.toBoolean(uniqueResult());
    }

    @Override
    public int intUniqueResult() {
        return ObjectUtil.toInt(uniqueResult());
    }

    @Override
    public long longUniqueResult() {
        return ObjectUtil.toLong(uniqueResult());
    }

    @Override
    public float floatUniqueResult() {
        return ObjectUtil.toFloat(uniqueResult());
    }

    @Override
    public double doubleUniqueResult() {
        return ObjectUtil.toDouble(uniqueResult());
    }

    /**
     * 返回单个对象
     * 是要统计的对象,sql 中包含 sum avg count 这些的才使用
     *
     * @return 返回单个对象
     */
    @Override
    public Object uniqueResult() {
        if (projection == null) {
            return null;
        }
        TableModels soberTable = soberFactory.getTableModels(criteriaClass, jdbcOperations);
        String databaseName = soberFactory.getDatabaseType();
        if (soberTable == null) {
            return null;
        }

        String errorInfo = StringUtil.empty;
        StringBuilder termText = new StringBuilder();
        Object[] objectArray = null;
        for (int i = 0; i < criterionEntries.size(); i++) {
            CriterionEntry criterionEntry = criterionEntries.get(i);
            if (criterionEntry.getCriterion().getFields()!=null&&!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                errorInfo = ObjectUtil.toString(criterionEntry.getCriterion().getFields());
                continue;
            }
            String term = criterionEntry.getCriterion().toSqlString(soberTable, databaseName);
            termText.append(term);
            if (i != (criterionEntries.size() - 1) && !StringUtil.isNull(StringUtil.trim(term))) {
                termText.append(" AND ");
            }
            if (criterionEntry.getCriterion().getParameter(soberTable) != null) {
                objectArray = JdbcUtil.appendArray(objectArray, criterionEntry.getCriterion().getParameter(soberTable));
            }
        }
        if (StringUtil.trim(termText.toString()).endsWith(" AND"))
        {
            log.error("SQL 存在错误,检查字段名称是否匹配:{}",errorInfo);
            return null;
        }

        StringBuilder groupText = new StringBuilder();
        for (int i = 0; i < groupList.size(); i++) {
            groupText.append(groupList.get(i));
            if (i != (groupList.size() - 1)) {
                groupText.append(",");
            }
        }

        StringBuilder orderText = new StringBuilder();
        for (int i = 0; i < orderEntries.size(); i++) {
            OrderEntry orderEntry = orderEntries.get(i);
            if (!SoberUtil.containsField(soberTable, orderEntry.getOrder().getFields())) {
                continue;
            }

            orderText.append(orderEntry.getOrder().toSqlString(databaseName));
            if (i != (orderEntries.size() - 1)) {
                orderText.append(",");
            }
        }

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_PROJECTION, projection.toSqlString(databaseName));
        valueMap.put(Dialect.KEY_TERM, termText.toString());
        valueMap.put(Dialect.KEY_FIELD_GROUPBY, groupText.toString());
        valueMap.put(Dialect.KEY_FIELD_ORDERBY, orderText.toString());

        Object result = null;

        //取出cache  begin
        String cacheKey = null;
       if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            StringBuilder termKey = new StringBuilder();
            termKey.append("_").append(termText.toString()).append("_p_").append(projection == null ? "" : projection.toSqlString(soberFactory.getDatabaseType())).append("_g_").append(groupText.toString()).append("_o_").append(orderText.toString());
            if (objectArray != null) {
                for (Object po : objectArray) {
                    termKey.append(ObjectUtil.toString(po));
                }
            }
            cacheKey = SoberUtil.getListKey(criteriaClass, StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"),orderText.toString(),1,1, false);
            result = JSCacheManager.get(criteriaClass, cacheKey);
            if (result != null) {
                return result;
            }
        }
        //取出cache  end

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection conn = null;

        Dialect dialect = soberFactory.getDialect();
        String sqlText = StringUtil.empty;
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            sqlText = dialect.processTemplate(Dialect.SQL_CRITERIA_UNIQUERESULT, valueMap);
            jdbcOperations.debugPrint(sqlText);
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }

            statement.setMaxRows(1);
            if (objectArray != null) {
                for (int i = 0; i < objectArray.length; i++) {
                    jdbcOperations.debugPrint("SetPrepared[" + (i + 1) + "]=" + objectArray[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, objectArray[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (resultSet.getMetaData().getColumnCount() <= 1) {
                if (resultSet.next()) {
                    result = dialect.getResultSetValue(resultSet, 1);
                }
            } else if (resultSet.next()) {
                Map<String, Object> resultMap = new HashMap<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    resultMap.put(resultSet.getMetaData().getColumnLabel(i), dialect.getResultSetValue(resultSet, i));
                }
                result = resultMap;
            }
        } catch (Exception e) {
            log.error("table:" + soberTable + " sql:" + sqlText, e);
            e.printStackTrace();
            throw new IllegalArgumentException("table:" + soberTable + " sql:" + sqlText);
        } finally {
            valueMap.clear();
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
        }

        //放入cache
        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            JSCacheManager.put(criteriaClass, cacheKey, result);
        }
        return result;
    }

    /**
     * 快速删除,不删除映射对象
     *
     * @return boolean 是否成功
     */
    private int delete() {
        TableModels soberTable = soberFactory.getTableModels(criteriaClass, jdbcOperations);
        String databaseType = soberFactory.getDatabaseType();
        Dialect dialect = soberFactory.getDialect();
        StringBuilder termText = new StringBuilder();
        Object[] objectArray = null;
        for (int i = 0; i < criterionEntries.size(); i++) {
            CriterionEntry criterionEntry = criterionEntries.get(i);
            if (!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                continue;
            }
            String term = criterionEntry.getCriterion().toSqlString(soberTable, databaseType);
            termText.append(term);
            if (i != (criterionEntries.size() - 1) && !StringUtil.isNull(StringUtil.trim(term))) {
                termText.append(" AND ");
            }
            if (criterionEntry.getCriterion().getParameter(soberTable) != null) {
                objectArray = JdbcUtil.appendArray(objectArray, criterionEntry.getCriterion().getParameter(soberTable));
            }
        }

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_NAME, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_PRIMARY_KEY, soberTable.getPrimary());
        valueMap.put(Dialect.KEY_TERM, termText.toString());
        int result = 0;
        String sqlText = null;
        try {
            sqlText = dialect.processTemplate(Dialect.SQL_CRITERIA_DELETE, valueMap);
            result = jdbcOperations.update(sqlText, objectArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("查询异常SQL:" + sqlText);
        }
        if (soberFactory.isUseCache()) {
            //同时更新缓存
            JSCacheManager.remove(criteriaClass, getDeleteListCacheKey());
        }
        return result;
    }


    /**
     * 快速删除,不删除映射对象
     *
     * @return boolean 是否成功
     */
    @Override
    public int update(Map<String, Object> updateMap) {
        TableModels soberTable = soberFactory.getTableModels(criteriaClass, jdbcOperations);
        if (soberTable == null) {
            log.error("no fond sober Config :" + criteriaClass.getName());
            return -1;
        }
        Dialect dialect = soberFactory.getDialect();
        String databaseType = soberFactory.getDatabaseType();

        Object[] objectArray = null;
        String[] fieldList = null;
        for (String key : updateMap.keySet()) {
            if (!soberTable.containsField(key)) {
                continue;
            }
            fieldList = ArrayUtil.add(fieldList, key);
            objectArray = JdbcUtil.appendArray(objectArray, updateMap.get(key));
        }

        String errorInfo = StringUtil.empty;
        StringBuilder termText = new StringBuilder();
        for (int i = 0; i < criterionEntries.size(); i++) {
            CriterionEntry criterionEntry = criterionEntries.get(i);
            if (!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                errorInfo = ObjectUtil.toString(criterionEntry.getCriterion().getFields());
                continue;
            }
            String term = criterionEntry.getCriterion().toSqlString(soberTable, databaseType);
            termText.append(term);
            if (i != (criterionEntries.size() - 1) && !StringUtil.isNull(StringUtil.trim(term))) {
                termText.append(" AND ");
            }
            if (criterionEntry.getCriterion().getParameter(soberTable) != null) {
                objectArray = JdbcUtil.appendArray(objectArray, criterionEntry.getCriterion().getParameter(soberTable));
            }
        }
        if (StringUtil.trim(termText.toString()).endsWith(" AND"))
        {
            log.error("SQL 存在错误,检查字段名称是否匹配:{}",errorInfo);
            return -2;
        }
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_FIELD_LIST, fieldList);
        valueMap.put(Dialect.KEY_TERM, termText.toString());
        int result = 0;
        try {
            result = jdbcOperations.update(dialect.processTemplate(Dialect.SQL_CRITERIA_UPDATE, valueMap), objectArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
        jdbcOperations.evict(criteriaClass);
        return result;
    }


    /**
     * 删除对象
     *
     * @param delChild 是否删除映射对象
     * @return boolean 是否成功
     */
    @Override
    public int delete(boolean delChild) {
        int result = 0;
        if (delChild) {
            List<T> list = list(false);
            for (T o : list) {
                if (o == null) {
                    continue;
                }
                result = result + jdbcOperations.deleteNexus(o);
                //删除 缓存
                if (soberFactory.isUseCache()) {
                    //同时更新缓存
                    TableModels soberTable = soberFactory.getTableModels(o.getClass(), jdbcOperations);
                    String cacheKey = SoberUtil.getLoadKey(o.getClass(), soberTable.getPrimary(), BeanUtil.getProperty(o, soberTable.getPrimary()), true);
                    JSCacheManager.remove(o.getClass(), cacheKey);
                }
            }
        }
        if (delete() < 0) {
            return -1;
        }
        return result + 1;
    }


    /**
     * 查询返回对象列表
     * 当多线程查询的时候很可能后边后边的cache还没放入，前边的查询已经取了执行了，这样cache在高并发下并不是理想的一次查询，
     * 而是同一个查询有可能会执行多次，到下一次检查到cache中有数据。所以如果是在多线程中使用加入 synchronized 来同步，效果更好
     *
     * @param loadChild 是否载入子对象
     * @return List 返回列表
     */
    @Override
    public List<T> list(boolean loadChild)
    {
        TableModels soberTable = soberFactory.getTableModels(criteriaClass, jdbcOperations);
        if (soberTable == null) {
            log.error("no fond sober Config :" + criteriaClass.getName());
            return new ArrayList<T>(0);
        }

        Dialect dialect = soberFactory.getDialect();
        String databaseType = soberFactory.getDatabaseType();
        String errorInfo = StringUtil.empty;
        StringBuilder termText = new StringBuilder();
        Object[] objectArray = null;
        for (int i = 0; i < criterionEntries.size(); i++) {
            CriterionEntry criterionEntry = criterionEntries.get(i);
            if (!(criterionEntry.getCriterion() instanceof LogicalExpression)&& !SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                errorInfo = ObjectUtil.toString(criterionEntry.getCriterion().getFields());
                continue;
            }
            String term = criterionEntry.getCriterion().toSqlString(soberTable, databaseType);
            termText.append(term);
            if (i != (criterionEntries.size() - 1) && !StringUtil.isNull(StringUtil.trim(term))) {
                termText.append(" AND ");
            }
            if (criterionEntry.getCriterion().getParameter(soberTable) != null) {
                objectArray = JdbcUtil.appendArray(objectArray, criterionEntry.getCriterion().getParameter(soberTable));
            }
        }
        if (StringUtil.trim(termText.toString()).endsWith(" AND"))
        {
            log.error("SQL存在错误,检查字段名称是否匹配:{}",errorInfo);
            return new ArrayList<T>(0);
        }
        StringBuilder groupText = new StringBuilder();
        for (int i = 0; i < groupList.size(); i++) {
            groupText.append(groupList.get(i));
            if (i != (groupList.size() - 1)) {
                groupText.append(",");
            }
        }
        StringBuilder orderText = new StringBuilder();
        for (int i = 0; i < orderEntries.size(); i++) {
            OrderEntry orderEntry = orderEntries.get(i);
            if (!SoberUtil.containsField(soberTable, orderEntry.getOrder().getFields())) {
                continue;
            }
            orderText.append(orderEntry.getOrder().toSqlString(databaseType));
            if (i != (orderEntries.size() - 1)) {
                orderText.append(",");
            }
        }
        if (orderText.toString().endsWith(",")) {
            orderText.setLength(orderText.length() - 1);
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int iEnd = currentPage * totalCount;
        if (iEnd < 0) {
            iEnd = 1;
        }
        int iBegin = iEnd - totalCount;
        if (iBegin < 0) {
            iBegin = 1;
        }

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_TERM, termText.toString());
        valueMap.put(Dialect.KEY_FIELD_GROUPBY, groupText.toString());
        valueMap.put(Dialect.KEY_FIELD_ORDERBY, orderText.toString());
        valueMap.put(Dialect.SQL_RESULT_BEGIN_ROW, iBegin);
        valueMap.put(Dialect.SQL_RESULT_END_ROW, iEnd);

        List<T> resultList = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection conn = null;
        String sqlText = StringUtil.empty;
        //取出cache  begin
        String cacheKey = null;
        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            StringBuilder termKey = new StringBuilder();
            for (int i = 0; i < criterionEntries.size(); i++) {
                CriterionEntry criterionEntry = criterionEntries.get(i);
                if (!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                    continue;
                }
                termKey.append(criterionEntry.getCriterion().termString());
                if (i != criterionEntries.size() - 1) {
                    termKey.append("_");
                }
            }
            if (termKey.toString().endsWith("_")) {
                termKey.setLength(termKey.length() - 1);
            }
            if (StringUtil.hasLength(groupText.toString())) {
                termKey.append("_g_").append(groupText.toString());
            }
            if (termKey.toString().endsWith("_")) {
                termKey.setLength(termKey.length() - 1);
            }
            cacheKey = SoberUtil.getListKey(criteriaClass, StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"),orderText.toString(),iBegin,iEnd, loadChild);
            resultList = (List<T>) JSCacheManager.get(criteriaClass, cacheKey);
            if (resultList!=null) {
                return resultList;
            }
        }
        //取出cache  end
        resultList = new ArrayList<>();
        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            sqlText = dialect.processTemplate(Dialect.SQL_CRITERIA_QUERY, valueMap);
            sqlText = dialect.getLimitString(sqlText, iBegin, iEnd, soberTable);

            jdbcOperations.debugPrint(sqlText);
            //结果集的游标可以上下移动，当数据库变化时，当前结果集不变
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
            if (!dialect.supportsLimit()) {
                if (totalCount<24)
                {
                    statement.setFetchSize(Math.max(totalCount, 24));
                } else
                {
                    statement.setFetchSize(50);
                }
            }
            statement.setMaxRows(iEnd);
            if (objectArray != null) {
                for (int i = 0; i < objectArray.length; i++) {
                    jdbcOperations.debugPrint("setPrepared[" + (i + 1) + "]=" + objectArray[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, objectArray[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (resultSet == null) {
                return resultList;
            }
            if (!dialect.supportsLimit()) {
                resultSet.absolute(iBegin);
            }

            while (resultSet.next()) {
                T tempObj = jdbcOperations.loadColumnsValue(criteriaClass, resultSet);
                jdbcOperations.calcUnique(soberTable, tempObj);
                resultList.add(tempObj);
                if (resultList.size() >= totalCount) {
                    break;
                }
            }
            if (loadChild) {
                jdbcOperations.loadNexusList(criteriaClass, resultList);
            }
        } catch (Exception e) {
            log.info(sqlText, e);
            e.printStackTrace();
            throw new IllegalArgumentException("查询异常SQL:" + sqlText);
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
        }
        //放入cache
        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            if (!JSCacheManager.put(criteriaClass, cacheKey, resultList)) {
                log.error(criteriaClass + " put cache key " + cacheKey);
            }
        }
        return resultList;
    }

    @Override
    public List<Object> groupList() {
        TableModels soberTable = soberFactory.getTableModels(criteriaClass, jdbcOperations);
        if (soberTable == null) {
            log.error("no fond sober Config :" + criteriaClass.getName());
        }
        Dialect dialect = soberFactory.getDialect();
        String databaseType = soberFactory.getDatabaseType();
        String errorInfo = StringUtil.empty;
        StringBuilder termText = new StringBuilder();
        Object[] objectArray = null;
        for (int i = 0; i < criterionEntries.size(); i++) {
            CriterionEntry criterionEntry = criterionEntries.get(i);
            if (!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                errorInfo = ObjectUtil.toString(criterionEntry.getCriterion().getFields());
                continue;
            }
            String term = criterionEntry.getCriterion().toSqlString(soberTable, databaseType);
            termText.append(term);
            if (i != (criterionEntries.size() - 1) && !StringUtil.isNull(StringUtil.trim(term))) {
                termText.append(" AND ");
            }
            if (criterionEntry.getCriterion().getParameter(soberTable) != null) {
                objectArray = JdbcUtil.appendArray(objectArray, criterionEntry.getCriterion().getParameter(soberTable));
            }
        }
        if (StringUtil.trim(termText.toString()).endsWith(" AND"))
        {
            log.error("SQL存在错误,检查字段名称是否匹配:{}",errorInfo);
            return new ArrayList<>(0);
        }
        StringBuilder groupText = new StringBuilder();
        for (int i = 0; i < groupList.size(); i++) {
            groupText.append(groupList.get(i));
            if (i != (groupList.size() - 1)) {
                groupText.append(",");
            }
        }
        StringBuilder orderText = new StringBuilder();
        for (int i = 0; i < orderEntries.size(); i++) {
            OrderEntry orderEntry = orderEntries.get(i);
            if (!SoberUtil.containsField(soberTable, orderEntry.getOrder().getFields())) {
                continue;
            }
            orderText.append(orderEntry.getOrder().toSqlString(databaseType));
            if (i != (orderEntries.size() - 1)) {
                orderText.append(",");
            }
        }
        if (orderText.toString().endsWith(",")) {
            orderText.setLength(orderText.length() - 1);
        }
        if (currentPage <= 0) {
            currentPage = 1;
        }
        int iEnd = currentPage * totalCount;
        if (iEnd < 0) {
            iEnd = 1;
        }
        int iBegin = iEnd - totalCount;
        if (iBegin < 0) {
            iBegin = 1;
        }

        Map<String, Object> valueMap = new HashMap<>();
        assert soberTable != null;
        valueMap.put(Dialect.KEY_TABLE_NAME, soberTable.getName());
        valueMap.put(Dialect.KEY_TERM, termText.toString());
        valueMap.put(Dialect.KEY_FIELD_GROUPBY, groupText.toString());
        valueMap.put(Dialect.KEY_FIELD_ORDERBY, orderText.toString());
        valueMap.put(Dialect.SQL_RESULT_BEGIN_ROW, iBegin);
        valueMap.put(Dialect.SQL_RESULT_END_ROW, iEnd);

        List<Object> resultList = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection conn = null;
        String sqlText = StringUtil.empty;
        //取出cache  begin
        String cacheKey = null;

        if (soberFactory.isUseCache() && soberTable.isUseCache()) {
            StringBuilder termKey = new StringBuilder();

            for (int i = 0; i < criterionEntries.size(); i++) {
                CriterionEntry criterionEntry = criterionEntries.get(i);
                if (!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                    continue;
                }
                termKey.append(criterionEntry.getCriterion().termString());
                if (i != criterionEntries.size() - 1) {
                    termKey.append("_");
                }
            }
            if (termKey.toString().endsWith("_")) {
                termKey.setLength(termKey.length() - 1);
            }
            if (StringUtil.hasLength(groupText.toString())) {
                termKey.append("_g_").append(groupText.toString());
            }
            if (termKey.toString().endsWith("_")) {
                termKey.setLength(termKey.length() - 1);
            }
            cacheKey = SoberUtil.getListKey(soberTable.getEntity(), StringUtil.replace(termText.toString(), StringUtil.EQUAL, "_"),orderText.toString(),iBegin,iEnd,false);
            resultList = (List) JSCacheManager.get(criteriaClass, cacheKey);
            if (!ObjectUtil.isEmpty(resultList)) {
                return resultList;
            }
        }
        //取出cache  end

        try {
            conn = jdbcOperations.getConnection(SoberEnv.READ_ONLY);
            sqlText = dialect.processTemplate(Dialect.SQL_CRITERIA_GROUP_QUERY, valueMap);
            sqlText = dialect.getLimitString(sqlText, iBegin, iEnd, soberTable);

            jdbcOperations.debugPrint(sqlText);
            //结果集的游标可以上下移动，当数据库变化时，当前结果集不变
            if (!dialect.supportsConcurReadOnly()) {
                statement = conn.prepareStatement(sqlText);
            } else {
                statement = conn.prepareStatement(sqlText, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
            if (!dialect.supportsLimit()) {
                if (totalCount<24)
                {
                    statement.setFetchSize(Math.max(totalCount, 24));
                } else
                {
                    statement.setFetchSize(50);
                }
            }
            statement.setMaxRows(iEnd);
            if (objectArray != null) {
                for (int i = 0; i < objectArray.length; i++) {
                    jdbcOperations.debugPrint("SetPrepared[" + (i + 1) + "]=" + objectArray[i]);
                    dialect.setPreparedStatementValue(statement, i + 1, objectArray[i]);
                }
            }
            resultSet = statement.executeQuery();
            if (!dialect.supportsLimit()) {
                resultSet.absolute(iBegin);
            }
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            resultList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String,Object> beanMap = SoberUtil.getHashMap(resultSetMetaData, dialect, resultSet);
                resultList.add(ReflectUtil.createDynamicBean(beanMap));
                if (resultList.size() >= totalCount) {
                    break;
                }
            }
        } catch (Exception e) {
            log.info(sqlText, e);
            e.printStackTrace();
            throw new IllegalArgumentException("查询异常:" + sqlText);
        } finally {
            JdbcUtil.closeResultSet(resultSet);
            JdbcUtil.closeStatement(statement);
            JdbcUtil.closeConnection(conn);
            valueMap.clear();
        }

        //放入cache
        if (soberFactory.isUseCache() && soberTable.isUseCache() && resultList != null && !resultList.isEmpty()) {
            if (!JSCacheManager.put(criteriaClass, cacheKey, resultList)) {
                log.error(criteriaClass + " put cache key " + cacheKey);
            }
        }
        return resultList;
    }


    @Override
    public Criteria setProjection(Projection projection) {
        this.projection = projection;
        return this;
    }

    @Override
    public String  getDeleteListCacheKey()
    {
        TableModels soberTable = soberFactory.getTableModels(criteriaClass, jdbcOperations);
        if (soberTable == null) {
            log.error("no fond sober Config :" + criteriaClass.getName());
        }

        StringBuilder groupText = new StringBuilder();
        for (int i = 0; i < groupList.size(); i++) {
            groupText.append(groupList.get(i));
            if (i != (groupList.size() - 1)) {
                groupText.append(",");
            }
        }

        StringBuilder termKey = new StringBuilder();
        for (int i = 0; i < criterionEntries.size(); i++) {
            CriterionEntry criterionEntry = criterionEntries.get(i);
            if (!SoberUtil.containsField(soberTable, criterionEntry.getCriterion().getFields())) {
                continue;
            }
            termKey.append(criterionEntry.getCriterion().termString());
            if (i != criterionEntries.size() - 1) {
                termKey.append("_");
            }
        }
        if (termKey.toString().endsWith("_")) {
            termKey.setLength(termKey.length() - 1);
        }
        if (StringUtil.hasLength(groupText.toString())) {
            termKey.append("_g_").append(groupText.toString());
        }
         if (termKey.toString().endsWith("_")) {
            termKey.setLength(termKey.length() - 1);
        }
        return StringUtil.substringBefore(SoberUtil.getListKey(soberTable.getEntity(), StringUtil.replace(termKey.toString(), StringUtil.EQUAL, "_"),StringUtil.empty,1,1,false),"_T_")+StringUtil.ASTERISK;
    }

}