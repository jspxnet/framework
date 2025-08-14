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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.SecurityReadFile;
import com.github.jspxnet.utils.*;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-6-18
 * Time: 13:58:30
 * 内存映射表
 * 保存格式
 * [:-br]
 */

public class StringMap<K, V> extends LinkedHashMap<K, V> implements Serializable {
    final private String[] keySplits = new String[]{StringUtil.COMMAS, StringUtil.EQUAL, StringUtil.COLON};
    final private String[] lineSplits = new String[]{StringUtil.SEMICOLON, StringUtil.CRLF};

    @Getter
    private String keySplit = StringUtil.COLON;
    @Getter
    private String lineSplit = StringUtil.CRLF;
    @Setter
    @Getter
    private String encode = Environment.defaultEncode;
    @Setter
    @Getter
    private boolean filter = false;
    @Setter
    @Getter
    private boolean split = false;
    @Setter
    @Getter
    private boolean security = false;

    public void setKeySplit(String keySplit) {
        this.keySplit = keySplit;
        split = true;
    }

    public void setLineSplit(String lineSplit) {
        this.lineSplit = lineSplit;
        split = true;
    }

    public StringMap() {
        super();
    }

    public StringMap(String text) {
        super();
        setString(text);
    }

    public void setMap(Map<K, V> map) {
        putAll(map);
    }

    public void loadFile(String fileName) throws Exception {
        loadFile(fileName, null);
    }

    public void loadFile(String fileName, String encode) throws Exception {
        this.fileName = fileName;
        this.encode = encode;
        clear();
        com.github.jspxnet.io.AbstractRead ar = null;
        if (security) {
            ar = new SecurityReadFile();
        } else {
            ar = new com.github.jspxnet.io.AutoReadTextFile();
        }
        ar.setFile(fileName);
        if (!StringUtil.isNull(this.encode)) {
            ar.setEncode(this.encode);
        }
        setString(ar.getContent());
    }

    public String getString(String key, String defValue) {
        V o = super.get(key);
        if (!ObjectUtil.isEmpty(o)) {
            return (String) o;
        }
        return defValue;
    }

    public String getString(String key) {
        return getString(key, StringUtil.empty);
    }

    public boolean getBoolean(String key) {
        return ObjectUtil.toBoolean(super.get(key));
    }

    public int getInt(String key) {
        return ObjectUtil.toInt(super.get(key));
    }

    public long getLong(String key) {
        return ObjectUtil.toInt(super.get(key));
    }

    public int getInt(String key, int def) {
        if (!super.containsKey(key)) {
            return def;
        }
        return ObjectUtil.toInt(super.get(key));
    }

    public Object removeFirst() {
        Object obj = null;
        Iterator<K> it = keySet().iterator();
        if (it.hasNext()) {
            K v = it.next();
            obj = super.get(v);
            it.remove();
        }
        return obj;

    }

    @Override
    public V remove(Object key) {
        V obj = null;
        Iterator<K> it = keySet().iterator();
        while (it.hasNext()) {
            K k = it.next();
            if (k.equals(key)) {
                super.get(k);
                it.remove();
            }
        }
        return obj;
    }

    public Object removeLast() {
        K lastKey = null;
        for (K k : keySet()) {
            lastKey = k;
        }
        return remove(lastKey);
    }


    /**
     * LRU 方式删除，会保留最新的 size 个
     *
     * @param size 保留数
     */
    public void removeLRU(int size) {
        Iterator<K> it = keySet().iterator();
        while (it.hasNext() && super.size() > size) {
            it.next();
            it.remove();
        }
    }

    @Override
    public V put(K key, V value) {
        remove(key);
        return super.put(key, value);
    }


    @Getter
    private String fileName;

    public boolean save() {
        return save(fileName, encode);
    }

    public boolean save(String fileName) {
        return save(fileName, encode);
    }

    public boolean save(String fileName, String encode) {
        com.github.jspxnet.io.AbstractWrite aw = null;
        if (security) {
            aw = new com.github.jspxnet.io.SecurityWriteFile();
        } else {
            aw = new com.github.jspxnet.io.WriteFile();
        }
        aw.setFile(fileName);
        if (!StringUtil.isNull(encode)) {
            aw.setEncode(encode);
        }
        return aw.setContent(toString(), false);
    }

    public void setString(String text) {
        clear();
        if (text == null) {
            return;
        }
        if (!split) {
            int[] chkArray = ArrayUtil.getInitedIntArray(keySplits.length, 0);
            for (int i = 0; i < keySplits.length; i++) {
                chkArray[i] = StringUtil.countMatches(text, keySplits[i]);
            }
            int maxIndex = ArrayUtil.maxIndex(chkArray);
            keySplit = keySplits[maxIndex];

            int[] chkLineArray = ArrayUtil.getInitedIntArray(lineSplits.length, 0);
            for (int i = 0; i < lineSplits.length; i++) {
                chkLineArray[i] = StringUtil.countMatches(text, lineSplits[i]);
            }
            int maxLineIndex = ArrayUtil.maxIndex(chkLineArray);
            lineSplit = lineSplits[maxLineIndex];
        }


        String[] textArray = StringUtil.split(text, lineSplit);
        for (String str : textArray) {
            if (str.startsWith("#")) {
                continue;
            }
            if (!filter && !str.contains(keySplit)) {
                put((K) str, (V) str);
            } else if (filter && !str.contains(keySplit)) {
                //..
            } else {
                String keys = StringUtil.trim(StringUtil.substringBefore(str, keySplit));
                if (keys.contains("/~")) {
                    StringUtil.replace(keys, "/~", keySplit);
                }
                String value = StringUtil.trim(StringUtil.substringAfter(str, keySplit));
                if (StringUtil.hasLength(keys)) {
                    put((K) keys, (V) value);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (K key : keySet()) {
            if (key != null) {
                sb.append(StringUtil.replace((String) key, keySplit, "/~")).append(keySplit).append(ObjectUtil.toString(get(key)));
                sb.append(lineSplit);
            }
        }
        return sb.toString();
    }

    public Map<K, V> getValueMap() {
        Map<K, V> vMap = new HashMap<>();
        for (K key : super.keySet()) {
            vMap.put(key, super.get(key));
        }
        return vMap;
    }

    public List<K> getSortByKey() {
        List keyList = new ArrayList();
        keyList.addAll(super.keySet());
        Collections.sort(keyList);
        return keyList;
    }


    public void sortByKey(boolean delNull) {
        Map<K, V> vMap = new LinkedHashMap<>();
        List<K> keyList = getSortByKey();
        for (K key : keyList) {
            if (key == null || StringUtil.empty.equals(key)) {
                continue;
            }
            Object obj = super.get(key);
            if (delNull && obj == null) {
                continue;
            }
            if (obj == null) {
                vMap.put(key, null);
            } else {
                if (ClassUtil.isNumberType(obj.getClass())) {
                    vMap.put(key, (V) ObjectUtil.toString(obj));
                } else {
                    vMap.put(key, (V) obj.toString());
                }
            }
        }
        super.clear();
        super.putAll(vMap);
    }


}