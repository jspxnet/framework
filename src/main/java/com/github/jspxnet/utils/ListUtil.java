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

import com.github.jspxnet.json.JSONObject;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-7-19
 * Time: 21:54:27
 */
public final class ListUtil {
    public static final List<?> EMPTY = new ArrayList<>(0);
    private ListUtil() {

    }

    /**
     * @param list 列表
     * @param fen  分割
     * @return 切分后转换为字符串
     */
    public static String toString(Collection<?> list, String fen) {
        if (list == null || list.isEmpty()) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (Object aList : list) {
            if (ClassUtil.isStandardProperty(aList.getClass())) {
                sb.append(aList).append(fen);
            } else {
                sb.append(new JSONObject(aList)).append(fen);
            }

        }

        if (sb.toString().endsWith(fen)) {
            sb.setLength(sb.length() - fen.length());
        }
        return sb.toString();
    }

    public static String toString(Set<?> set, String fen) {
        if (set == null || set.isEmpty()) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        for (Object aList : set) {
            if (ClassUtil.isStandardProperty(aList.getClass())) {
                sb.append(aList).append(fen);
            } else {
                sb.append(new JSONObject(aList)).append(fen);
            }
        }
        if (sb.toString().endsWith(fen)) {
            sb.setLength(sb.length() - fen.length());
        }
        return sb.toString();
    }

    /**
     * @param array list列表
     * @return String[]  List 转换为 String[] 类型
     */
    public static String[] toArray(Collection<?> array) {
        String[] result = new String[array.size()];
        int i = 0;
        for (Object obj : array) {
            if (obj instanceof String) {
                result[i] = (String) obj;
            } else {
                result[i] = obj.toString();
            }
            i++;
        }
        return result;
    }

}