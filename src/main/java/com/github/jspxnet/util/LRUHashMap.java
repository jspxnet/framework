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


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {

    private  int maxCapacity;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Lock lock = new ReentrantLock();

    public LRUHashMap(int maxCapacity) {
        super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity)
    {
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }

    @Override
    public V get(Object key) {
        lock.lock();
        try {
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }
    //可以根据实际情况，考虑对不同的操作加锁
    @Override
    public V put(K key, V value) {
        lock.lock();
        try {
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) throws Exception {
        Map<Integer, Integer> map = new LRUHashMap<>(4);
        for (int i = 1; i <= 10; i++) {
            map.put(i, i);
        }
        //用for-each语句，遍历此时内存中的页面并输出
        for (java.util.Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println(entry.getValue());
        }
    }
}