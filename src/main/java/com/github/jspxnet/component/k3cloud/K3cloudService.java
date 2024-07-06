package com.github.jspxnet.component.k3cloud;

import com.github.jspxnet.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/30 0:11
 * description: thermo-model
 **/
public interface K3cloudService {
    /**
     *
     * @return 得到配置的账号
     */
    KingdeeAccount getAccount();
    /**
     *
     * @param className 类名称
     * @return 得到配置的表
     */
    K3TableConf getK3TableConf(String className);
    /**
     *
     * @param cls 表类
     * @return 得到配置的表
     */
    K3TableConf getK3TableConf(Class<?> cls);
    /**
     *
     * @param cls 表类
     * @return 得到字段映射
     */
    Map<String, KingdeeField> getFieldMap(Class<?> cls);
    /**
     *
     * @param cls 表类
     * @return 得到k3查询字段列表
     */
    String getFieldKeys(Class<?> cls);
    /**
     *
     * @param cls 表类
     * @return 得到配置的映射关系
     */
    String[] getBeanFields(Class<?> cls);
    /**
     *
     * @param cls 表类
     * @return 得到字段
     */
    String createBeanFields(Class<?> cls);
    /**
     *
     * @param cls 表类
     * @return 得到key
     */
    String getKey(Class<?> cls);
    /**
     *
     * @param cls 类型对象
     * @param filter 过滤条件
     * @param limit 返回个数
     * @return 对象列表
     */
    JSONObject createQuery(Class<?> cls, String filter, int limit);
    /**
     *
     * @param cls 类型对象
     * @param filter 过滤条件
     * @param index 开始行
     * @param limit 返回个数
     * @return 对象列表
     */
    JSONObject createQuery(Class<?> cls, String filter, int index, int limit);
    /**
     *
     * @param cls 表类
     * @param filter  过滤条件
     * @param orderString  排序
     * @param index  开始条数， 每次500条
     * @param limit  每次500条
     * @return 得到请求列表
     */
    JSONObject createQuery(Class<?> cls, String filter, String orderString, int index, int limit);
    /**
     *
     * @param list k3返回的列表
     * @param cls  表类
     * @param <T> 表类
     * @return 得到实体列表
     */
    <T> List<T> copyList(List<List<Object>> list, Class<T> cls);
    /**
     *
     * @param list k3返回的列表
     * @param cls 表类
     * @param hashMd5 是否填充md5
     * @param <T> 表类
     * @return 得到实体列表
     */
    <T> List<T> copyList(List<List<Object>> list, Class<T> cls, boolean hashMd5);
}
