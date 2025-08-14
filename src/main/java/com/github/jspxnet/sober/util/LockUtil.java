package com.github.jspxnet.sober.util;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.LockCache;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * 数据锁,数据库保存的方式
 */
public final class LockUtil {
    private LockUtil() {

    }

    /**
     * 锁定表
     *
     * @param soberSupport jdbc操作类
     * @param obj          锁定表实体
     * @return 是否成功锁定
     */
    public static boolean lock(SoberSupport soberSupport, Object obj) {

        Class<?> cls = null;
        if (obj instanceof Class) {
            cls = (Class<?>) obj;
        } else {
            cls = obj.getClass();
        }

        //会循环调用
        String id = StringUtil.empty;
        String tableName = StringUtil.empty;
        if (obj instanceof Class) {
            tableName = AnnotationUtil.getTableName(cls);
            if (StringUtil.isNullOrWhiteSpace(tableName)) {
                tableName = cls.getName();
            }
            id = Environment.Global;
        } else {
            TableModels tableModels = soberSupport.getSoberTable(cls);
            id = ObjectUtil.toString(BeanUtil.getProperty(obj, tableModels.getPrimary()));
        }

        String key = tableName + "_" + id;
        //保存到缓存，如果保存到数据库，自动建库的时候有可能死循环 begin
        return JSCacheManager.put(LockCache.class, key, 1);
        //保存到缓存，如果保存到数据库，自动建库的时候有可能死循环 end
    }

    /**
     * @param soberSupport jdbc操作类
     * @param obj          判断对象
     * @return 是否成功锁定
     */
    public static boolean isLock(SoberSupport soberSupport, Object obj) {
        Class<?> cls = null;
        if (obj instanceof Class) {
            cls = (Class<?>) obj;
        } else {
            cls = obj.getClass();
        }

        String id = StringUtil.empty;
        String tableName = StringUtil.empty;
        if (obj instanceof Class) {
            tableName = AnnotationUtil.getTableName(cls);
            if (StringUtil.isNullOrWhiteSpace(tableName)) {
                tableName = Environment.defaultValue;
            }
            id = Environment.Global;
        } else {
            TableModels tableModels = soberSupport.getSoberTable(cls);
            id = ObjectUtil.toString(BeanUtil.getProperty(obj, tableModels.getPrimary()));
        }
        String key = tableName + "_" + id;
        //保存到缓存，如果保存到数据库，自动建库的时候有可能死循环 begin
        return JSCacheManager.get(LockCache.class, key) != null;
    }

    /**
     * @param soberSupport jdbc操作类
     * @param obj          判断对象
     * @return 解锁是否成功
     */
    public static boolean unLock(SoberSupport soberSupport, Object obj) {
        Class<?> cls = null;
        if (obj instanceof Class) {
            cls = (Class<?>) obj;
        } else {
            cls = obj.getClass();
        }

        String id = StringUtil.empty;
        String tableName = StringUtil.empty;
        if (obj instanceof Class) {
            tableName = AnnotationUtil.getTableName(cls);
            if (StringUtil.isNullOrWhiteSpace(tableName)) {
                tableName = Environment.defaultValue;
            }
            id = Environment.Global;
        } else {
            TableModels tableModels = soberSupport.getSoberTable(cls);
            id = ObjectUtil.toString(BeanUtil.getProperty(obj, tableModels.getPrimary()));
        }

        String key = tableName + "_" + id;
        //保存到缓存，如果保存到数据库，自动建库的时候有可能死循环 begin
        return JSCacheManager.remove(LockCache.class, key);
    }
}
