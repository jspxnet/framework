/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.util.StringMap;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-7-19
 * Time: 21:48:44
 */
public class MapUtil {
    private MapUtil() {

    }

    public static boolean isNotEmpty(Map map) {
        return !(map == null || map.isEmpty());
    }

    /**
     * 哈西表中对象用字符串输出
     * @param map map对象
     * @return 字符串
     */
    public static String toString(Map map) {
        if (map == null || map.isEmpty()) {
            return StringUtil.empty;
        }
        if (map instanceof JSONObject||map instanceof JSONArray) {
            return map.toString();
        }
        return new JSONObject(map).toString();
    }

    /**
     * @param map map对象
     * @param keySplit 值描述分割 符号
     * @param lineSplit  行分割符号
     * @return 字符串
     */
    public static String toString(Map map,String keySplit,String lineSplit) {
        StringMap stringMap = new StringMap();
        stringMap.setLineSplit(lineSplit);
        stringMap.setKeySplit(keySplit);
        for (Object key:map.keySet())
        {
            Object value = map.get(key);
            stringMap.put(ObjectUtil.toString(key),ObjectUtil.toString(value));
        }
        return stringMap.toString();
    }

    /**
     * @param map map
     * @return 转换为
     */
    public static String toQueryString(Map map) {
        if (map == null || map.isEmpty()) {
            return StringUtil.empty;
        }
        StringBuilder result = new StringBuilder();
        for (Object key : map.keySet()) {
            String value = (String) map.get(key);
            result.append(key).append("=").append(URLUtil.getUrlEncoder(value, Environment.defaultEncode)).append("&");
        }
        if (result.toString().endsWith("&")) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }


    public static <String extends Comparable, V> Map sortByKey(Map<String, V> map) {
        List<Map.Entry<String, V>> list = new LinkedList<Map.Entry<String, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, V>>() {
            @Override
            public int compare(Map.Entry<String, V> o1, Map.Entry<String, V> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        Map<String, V> result = new LinkedHashMap();
        for (Map.Entry<?, V> entry : list) {
            result.put((String) entry.getKey(), entry.getValue());
        }
        return result;
    }


    /**
     * @param map map
     * @param <K> 泛型k
     * @param <V> 泛型v
     * @return 使用 Map按key进行排序
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String phpKSort(Map<String, Object> map) {

        String sb = "";
        String[] key = new String[map.size()];
        int index = 0;
        for (String k : map.keySet()) {
            key[index] = k;
            index++;
        }
        Arrays.sort(key);
        for (String s : key) {
            sb += s + "=" + map.get(s) + "&";
        }
        sb = sb.substring(0, sb.length() - 1);
        // 将得到的字符串进行处理得到目标格式的字符串
        try {
            sb = URLEncoder.encode(sb, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }// 使用常见的UTF-8编码
        return sb.replace("%3D", "=").replace("%26", "&");
    }

    /**
     * 创建签名字符串
     *
     * @param params 参数map
     * @return 创建签名字符串
     */
    public static String createLinkStringWithKeyAndValue(Map<String, ?> params) {
        List<String> keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (String s : keys) {
            if (!"sign".equals(s) && !"signature".equals(s) && !"userInfo".equals(s) && !"ext".equals(s) && !"payPassword".equals(s)) {

                if (!ObjectUtil.isEmpty(params.get(s))) {
                    String value = params.get(s).toString();
                    prestr = prestr + s + "=" + value + "&";
                }
            }
        }
        if ("&".equals(prestr.substring(prestr.length() - 1))) {
            prestr = prestr.substring(0, prestr.length() - 1);
        }
        return prestr;
    }

    public static String createLinkStringWithKeyAndValueWithoutSignType(Map<String, Object> params) {
        List<String> keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        String prestr = "";

        for(int i = 0; i < keys.size(); ++i) {
            if (!"sign".equals(keys.get(i)) && !"signature".equals(keys.get(i)) && !"sign_type".equals(keys.get(i))) {
                String key = keys.get(i);
                if (!ObjectUtil.isEmpty(params.get(key))) {
                    String value = params.get(key).toString();
                    if (i == keys.size() - 1) {
                        prestr = prestr + key + "=" + value;
                    } else {
                        prestr = prestr + key + "=" + value + "&";
                    }
                }
            }
        }

        if (prestr.endsWith("&")) {
            prestr = prestr.substring(0, prestr.length() - 1);
        }

        return prestr;
    }


    /**
     * 得到签名字符串
     * @param map map
     * @return 得到不包含sign的字符串
     */
    public static String genSignString(Map<String, Object> map) {
        Iterator<String> iter = map.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        String name;
        while(iter.hasNext()) {
            name = iter.next();
            String value = String.valueOf(map.get(name));
            if (!"sign".equalsIgnoreCase(name) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                sb.append(name).append("=").append(value).append("&");
            }
        }

        return sb.toString();
    }

    /**
     * 主要用于微信支付转换
     * @param map map
     * @return map转换为XML格式
     */
    public static String getXml(Map<String, Object> map) {
        Iterator<String> iter = map.keySet().iterator();
        StringBuilder sb = new StringBuilder("<xml>");
        while(iter.hasNext()) {
            String name = iter.next();
            String value = String.valueOf(map.get(name));
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                sb.append("<").append(name).append(">").append(value).append("</").append(name).append(">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
}