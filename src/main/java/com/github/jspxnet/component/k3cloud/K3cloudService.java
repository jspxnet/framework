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
    KingdeeAccount getAccount();

    K3TableConf getK3TableConf(String className);

    K3TableConf getK3TableConf(Class<?> cls);

    Map<String, KingdeeField> getFieldMap(Class<?> cls);

    String getFieldKeys(Class<?> cls);

    String[] getBeanFields(Class<?> cls);

    String createBeanFields(Class<?> cls);

    String getKey(Class<?> cls);

    JSONObject createQuery(Class<?> cls, String filter, int index);

    JSONObject createQuery(Class<?> cls, String filter, int index, int limit);

    <T> List<T> copyList(List<List<Object>> list, Class<T> cls);

    <T> List<T> copyList(List<List<Object>> list, Class<T> cls, boolean hashMd5);
}
