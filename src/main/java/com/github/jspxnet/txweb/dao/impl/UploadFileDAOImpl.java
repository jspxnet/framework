package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.table.CloudFileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by yuan on 2014/7/13 0013.
 * 上传DAO
 */
public class UploadFileDAOImpl<T> extends JdbcOperations implements UploadFileDAO {
    private static final Logger log = LoggerFactory.getLogger(UploadFileDAOImpl.class);

    private String organizeId = StringUtil.empty;
    private Class<T> tableClass;

    public UploadFileDAOImpl() {

    }

    @Override
    public String getOrganizeId() {
        return organizeId;
    }

    @Override
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    public String getClassName() {
        if (tableClass == null) {
            return StringUtil.empty;
        }
        return tableClass.getName();
    }

    @SuppressWarnings("all")
    @Param(request = false)
    public void setClassName(final String className) throws Exception {
        if (tableClass == null && !StringUtil.isNull(className)) {
            tableClass = (Class<T>)ClassUtil.loadClass(className);
        }
    }

    /**
     * @return 类对象
     */
    @Override
    public Class<?> getClassType() {
        return tableClass;
    }

    private String namespace = StringUtil.empty;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     *
     * @return 命名空间
     */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param id id
     * @return 载入
     */
    @Override
    public T load(Long id) {
        return super.load(tableClass, id);
    }

    /**
     *
     * @param id id
     * @return 查询
     */
    @Override
    public T get(Long id) {
        return super.get(tableClass, id);
    }

    /**
     * @param ids id
     * @return 删除
     */
    @Override
    public boolean delete(Long[] ids)  {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        try {
            for (Long id : ids) {
                Object obj = get(tableClass, id);
                if (obj != null && id.equals(BeanUtil.getProperty(obj, "organizeId"))) {
                    delete(obj);
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * @param hash hash值
     * @return 返回对象
     */
    @Override
    public T getForHash(String hash) {
        return createCriteria(tableClass).add(Expression.eq("hash", hash)).objectUniqueResult(false);
    }

    /**
     *
     * @param hash hash值
     * @return 是否存在
     */
    @Override
    public boolean haveHash(String hash) {
        return createCriteria(tableClass).add(Expression.eq("hash", hash)).setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    @Override
    public T getThumbnail(long pid) {
        return createCriteria(tableClass).add(Expression.eq("pid", pid)).addOrder(Order.desc("sortDate")).objectUniqueResult(false);
    }

    /**
     *
     * @return 得到云配配置
     */
    @Override
    public CloudFileConfig getCloudFileConfig() {
        return createCriteria(CloudFileConfig.class).add(Expression.eq("namespace", namespace)).addOrder(Order.desc("sortDate")).objectUniqueResult(false);
    }

    /**
     * 得到子图列表
     * @param pid 父id
     * @param <T> 类型
     * @return 返回子文件列表
     */
    @Override
    public <T> List<T> getChildFileList(long pid) {
        Criteria criteria = createCriteria(tableClass).add(Expression.eq("pid", pid));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.addOrder(Order.asc("sortDate")).list(false);
    }

    /**
     * @param ids      id
     * @param sortType 排序
     * @return 更新排序
     */
    @Override
    public boolean updateSortType(Long[] ids, int sortType) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        try {
            for (Long id : ids) {
                Object obj = get(tableClass, id);
                if (obj != null && id.equals(BeanUtil.getProperty(obj, "organizeId"))) {
                    BeanUtil.setSimpleProperty(obj, "sortType", sortType);
                    super.update(obj, new String[]{"sortType"});
                }
            }
        } catch (Exception e) {
            log.error("更新排序", e);
            return false;
        }
        return true;
    }

    /**
     * @param ids id
     * @return 更新排序日期
     */
    @Override
    public boolean updateSortDate(Long[] ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        try {
            for (Long id : ids) {
                Object uploadFile = get(tableClass, id);
                if (uploadFile != null) {
                    BeanUtil.setSimpleProperty(uploadFile, "sortDate", new Date());
                    super.update(uploadFile, new String[]{"sortDate"});
                }
            }
        } catch (Exception e) {
            log.error("更新排序日期", e);
            return false;
        }
        return true;
    }


    /**
     * @param field      字段
     * @param find       查询条件
     * @param term       条件
     * @param sortString 排序
     * @param uid        用户id
     * @param page       页数
     * @param count      返回数量
     * @return 返回列表
     */
    @Override
    public  <T> List<T> getList(String[] field, String[] find, String term, String sortString, long uid, long pid, int page, int count) {
        if (StringUtil.isNull(sortString)) {
            sortString = "createDate:D";
        }
        Criteria criteria = createCriteria(tableClass);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }

        if (pid > -1) {
            criteria = criteria.add(Expression.eq("pid", pid));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return SSqlExpression.getSortOrder(criteria, sortString).setCurrentPage(page).setTotalCount(count).list(false);
    }


    /**
     * @param field 字段
     * @param find  查询条件
     * @param term  条件
     * @param uid   用户id
     * @return 得到记录条数
     */
    @Override
    public int getCount(String[] field, String[] find, String term, long uid, long pid){
        Criteria criteria = createCriteria(tableClass);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }

        if (pid > -1) {
            criteria = criteria.add(Expression.eq("pid", pid));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     *
     * @param uid 用户id
     * @return 分组列表
     */
    @Override
    public  List<String> getGroups(long uid) {
        Criteria criteria = createCriteria(tableClass).addGroup("groupName").addOrder(Order.asc("groupName"));
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        List<Object> list = criteria.setCurrentPage(1).setTotalCount(getMaxRows()).groupList();
        return BeanUtil.copyFieldList(list,"groupName",true,true);
    }

    /**
     * 移动分组
     * @param groupName 原分组名称
     * @param newGroupName 新的分组名称
     * @param uid 用户id
     * @return 移动数量
     * @throws Exception 异常
     */
    @Override
    public  int moveGroup(String groupName,String newGroupName,long uid) throws Exception {
       String sql = "UPDATE " + getTableName(tableClass) + " set groupName=? WHERE groupName=? AND putUid=?";
       return super.update(sql,new Object[]{newGroupName,groupName,uid});
    }

    /**
     *
     * @param id id
     * @param newGroupName 分组名称
     * @param uid 用户id
     * @return 移动条数
     * @throws Exception 异常
     */
    @Override
    public  int moveToGroup(long id,String newGroupName,long uid) throws Exception {
        String sql = "UPDATE " + getTableName(tableClass) + " set groupName=? WHERE id=? AND putUid=?";
        return super.update(sql,new Object[]{newGroupName,id,uid});
    }

}