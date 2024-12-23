package com.github.jspxnet.util;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;

/**
 * Created by chenyuan on 2016-06-06.
 * 简单的对话留言,统一使用json格式保存
 */
public class ScopeNoteUtil {


    /**
     * 解析出交流便签列表
     *
     * @param str 字符串
     * @return json数组
     */
    public static JSONArray decode(String str) {
        return new JSONArray(str);
    }

    /**
     * 删除操作
     *
     * @param xml xml
     * @param id  id
     * @return json数组
     */
    public static JSONArray delete(String xml, long id) {
        JSONArray list = decode(xml);
        for (int i = list.length()-1; i>=0; i--) {
            JSONObject jsonNote = list.getJSONObject(i);
            if (jsonNote != null && id == jsonNote.getLong("id")) {
                list.remove(i);
            }
        }
        return list;
    }

    /**
     * 添加操作
     *
     * @param str  上下文内容
     * @param note 添加对象
     * @return 添加操作
     */
    public static JSONArray addNote(String str, ScopeNote note) {
        JSONArray list = decode(str);
        list.add(0,note);
        return list;
    }
}
