package com.github.jspxnet.sober.util;

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.table.LockTable;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * 数据锁,数据库保存的方式
 */
public final class LockUtil {
    private LockUtil()
    {

    }

    /**
     * 锁定表
     * @param jdbcOperations jdbc操作类
     * @param obj 锁定表实体
     * @return 是否成功锁定
     */
    public static boolean lock(JdbcOperations jdbcOperations,Object obj)  {

        Class<?> cls = obj.getClass();
        TableModels tableModels  = jdbcOperations.getSoberTable(cls);
        Object id = BeanUtil.getProperty(obj,tableModels.getPrimary());
        LockTable lockTable = new LockTable();
        lockTable.setTableName(tableModels.getName());
        lockTable.setLockId(ObjectUtil.toString(id));
        if (ClassUtil.haveMethodsName(cls,"putName"))
        {
            Object putName = BeanUtil.getProperty(obj,"putName");
            lockTable.setPutName(ObjectUtil.toString(putName));
        }
        if (ClassUtil.haveMethodsName(cls,"putUid"))
        {
            Object putUid = BeanUtil.getProperty(obj,"putUid");
            lockTable.setPutUid(ObjectUtil.toLong(putUid));
        }
        if (ClassUtil.haveMethodsName(cls,"ip"))
        {
            Object ip = BeanUtil.getProperty(obj,"ip");
            lockTable.setIp(ObjectUtil.toString(ip));
        }
        if (StringUtil.isNull(lockTable.getIp()))
        {
            lockTable.setIp("127.0.0.1");
        }
        try {
            return jdbcOperations.save(lockTable)>0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param jdbcOperations jdbc操作类
     * @param obj 判断对象
     * @return 是否成功锁定
     */
    public static boolean isLock(JdbcOperations jdbcOperations,Object obj) {
        Class<?> cls = obj.getClass();
        TableModels tableModels  = jdbcOperations.getSoberTable(cls);
        Object id = BeanUtil.getProperty(obj,tableModels.getPrimary());
        return jdbcOperations.createCriteria(LockTable.class).add(Expression.eq("tableName", tableModels.getName()))
                .add(Expression.eq(tableModels.getPrimary(),id)).setProjection(Projections.rowCount()).intUniqueResult()>0;
    }

    /**
     *
     * @param jdbcOperations jdbc操作类
     * @param obj 判断对象
     * @return 解锁是否成功
     */
    public static boolean unLock(JdbcOperations jdbcOperations,Object obj) {
        Class<?> cls = obj.getClass();
        TableModels tableModels  = jdbcOperations.getSoberTable(cls);
        return jdbcOperations.delete(LockTable.class,"tableName",tableModels.getName())>=0;
    }
}
