package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/12/4 12:54
 * description: thermo-model
 **/
public interface SyncIndexDAO extends SoberSupport {
    /**
     * 清理数据
     * @param cls 类对象
     * @param <T> 类型
     */
    <T> void clear(Class<T> cls);


    /**
     * 保存同步的索引
     * @param cls 类
     * @param keyField 索引字段
     * @param <T> 类型
     */
    <T> void deleteNoKeyData(Class<T> cls, String keyField);
}
