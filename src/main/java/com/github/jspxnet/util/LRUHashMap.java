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



public class LRUHashMap<K, V> extends org.apache.commons.collections4.map.LRUMap<K, V> {

    public LRUHashMap(int maxSize) {
        super(maxSize,  maxSize/2, 0.75F, true);
    }

    public LRUHashMap(int maxSize, int initialSize, float loadFactor, boolean scanUntilRemovable) {
        super(maxSize,initialSize, loadFactor,scanUntilRemovable);
    }
}