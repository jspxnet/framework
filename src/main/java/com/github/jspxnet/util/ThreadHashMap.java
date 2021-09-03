/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-24
 * Time: 6:51:55
 * 多线程,高并发
 */
public class ThreadHashMap<K, V> extends ThreadLocal implements Map<K, V> {
    // 在调用get()方法的时候返回一个ArrayList对象
    @Override
    public Map<K, V> initialValue() {
        return new ConcurrentHashMap<K, V>(4);
    }

    //将保存在ThreadLocal中的List返回

    @Override
    public int size() {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.size();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        Map<K, V> map = (Map<K, V>) super.get();
        map.putAll(t);
    }

    @Override
    public void clear() {
        Map<K, V> map = (Map<K, V>) super.get();
        map.clear();
    }

    @Override
    public Collection<V> values() {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.entrySet();
    }

    @Override
    public Set<K> keySet() {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.keySet();
    }

    @Override
    public V remove(Object key) {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.remove(key);
    }

    @Override
    public V put(K k, V v) {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.put(k, v);
    }

    @Override
    public boolean isEmpty() {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.containsKey(key);
    }

    @Override
    public V get(Object key) {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.get(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Map<K, V> map = (Map<K, V>) super.get();
        return map.containsValue(value);
    }

}