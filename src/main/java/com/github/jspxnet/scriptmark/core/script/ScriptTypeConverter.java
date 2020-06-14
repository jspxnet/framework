/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.script;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.scriptmark.core.block.CommentBlock;
import com.github.jspxnet.scriptmark.core.type.*;
import com.github.jspxnet.scriptmark.core.iterator.*;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.*;
import com.github.jspxnet.scriptmark.ListIterator;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ValidUtil;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.javascript.NativeArray;
import java.util.*;
import java.io.Writer;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-30
 * Time: 5:24:06
 */
@Slf4j
public final class ScriptTypeConverter {
    final private static Map<String, AbstractType> TYPE_MAP = new HashMap<>();

    static {
        Configurable config = TemplateConfigurable.getInstance();
        DateProvider typeSerializer = new DateProvider();
        typeSerializer.setFormat(config.getString(ScriptmarkEnv.DateTimeFormat));
        TYPE_MAP.put(Date.class.getName(), typeSerializer);

        DoubleProvider doubleProvider = new DoubleProvider();
        doubleProvider.setFormat(config.getString(ScriptmarkEnv.NumberFormat));
        TYPE_MAP.put(Double.class.getName(), doubleProvider);

        FloatProvider floatProvider = new FloatProvider();
        floatProvider.setFormat(config.getString(ScriptmarkEnv.NumberFormat));
        TYPE_MAP.put(Float.class.getName(), floatProvider);

        IntegerProvider integerProvider = new IntegerProvider();
        TYPE_MAP.put(Integer.class.getName(), integerProvider);

        BooleanProvider booleanProvider = new BooleanProvider();
        TYPE_MAP.put(Boolean.class.getName(), booleanProvider);
        ///////////////////////////////////////
    }

    private ScriptTypeConverter() {

    }

    static public boolean isDouble(Object o) {
        return o != null && (o.getClass().getName().toLowerCase().contains("number") || o.getClass().getName().toLowerCase().contains("integer") || isStandardNumber(o.toString()));
    }

    /**
     * 创建相应的类型数组
     *
     * @param o 对象
     * @return 数组字符串
     */
    static public String toString(Object o) {
        if (o == null || "undefined".equals(o)) {
            return StringUtil.empty;
        }
        if (o instanceof String) {
            return (String) o;
        }
        //转换匹配
        AbstractType type = TYPE_MAP.get(o.getClass().getName());
        if (type != null) {
            return type.toString(o);
        }
        if (o instanceof NativeArray) {
            return o.toString();
        }
        if (o.getClass().isArray()) {
            Object[] o1 = (Object[]) o;
            try {
                return new JSONArray(o).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < o1.length; i++) {
                if (isDouble(o1[i])) {
                    sb.append(toString(o1[i]));
                } else {
                    sb.append(StringUtil.quote(toString(o1[i]), true));
                }
                if (i + 1 < o1.length) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString();
        }

        if (o instanceof Collection) {
            try {
                return new JSONArray(o).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "[]";
        }
        if (o instanceof Iterator) {
            StringBuilder sb = new StringBuilder();
            Iterator iterator = (Iterator) o;
            while (iterator.hasNext()) {
                sb.append(toString(iterator.next()));
            }
            return sb.toString();
        }
        if (o instanceof Map) {
            return new JSONObject(o, true).toString();
        }
        return o.toString();
    }

    /**
     * 相应对象转化给系列对象
     *
     * @param object 对象
     * @return 列表
     */
    static public ListIterator getCollection(Object object) {
        if (object == null) {
            return new NullIterator();
        }
        if (object instanceof Object[] || object.getClass().isArray()) {
            return new ArrayIterator(object);
        }
        if (object instanceof Set) {
            return new SetIterator(object);
        }
        if (object instanceof Collection) {
            return new CollectionIterator(object);
        }
        if (object instanceof Map) {
            return new MapIterator(object);
        }
        if (object.getClass().getName().contains("NativeArray")) {
            NativeArray nativeArray = (NativeArray) object;
            return new NativeArrayIterator(nativeArray);
        }
        if (object instanceof String && isArray(object.toString())) {
            return new ArrayIterator(toArray(object.toString()));
        }
        return new NullIterator();
    }

    static public boolean isArray(String s) {
        if (s == null) {
            return false;
        }
        s = s.trim();
        if (s.length() < 1) {
            return false;
        }
        char c = s.charAt(0);
        return c == '[' && s.charAt(s.length() - 1) == ']';
    }

    static public boolean isString(String s) {
        if (s == null) {
            return false;
        }
        s = s.trim();
        if (s.length() < 1) {
            return false;
        }
        char c = s.charAt(0);
        return (c == '\"' && s.charAt(s.length() - 1) == '\"') || c == '\'' && s.charAt(s.length() - 1) == '\'';
    }

    static public Object[] toArray(String s) {
        if (s == null) {
            return new Object[0];
        }
        s = s.trim();
        if (s.length() < 1) {
            return new Object[]{s};
        }
        s = s.substring(1, s.length() - 1);
        if (s.contains(",")) {
            //////枚举的方式
            String[] array = s.split(",");
            Object[] result = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                String v = array[i].trim();
                if (v.charAt(0) == v.charAt(v.length() - 1) && (v.charAt(0) == '\"' || v.charAt(0) == '\'')) {
                    v = v.substring(1, v.length() - 1);
                    result[i] = v;
                } else if (isStandardNumber(v)) {
                    result[i] = new Double(v);
                }
            }
            return result;
        }
        ///[1..3] 或 [a..z]
        if (s.contains("..")) {
            //////枚举的方式
            String sb = s.substring(0, s.indexOf(".."));
            String se = s.substring(s.indexOf("..") + 2);
            if (ValidUtil.isNumber(sb) && ValidUtil.isNumber(se)) {
                if (StringUtil.toInt(se) == StringUtil.toInt(sb)) {
                    return new Object[0];
                }
                int length = (StringUtil.toInt(se) - StringUtil.toInt(sb)) + 1;
                return ScriptMarkUtil.getInitIntArray(length, StringUtil.toInt(sb));
            } else if (sb.length() == 1 && se.length() == 1) {
                char b = sb.charAt(0);
                char e = se.charAt(se.length() - 1);
                List<String> list = new LinkedList<String>();
                for (int i = b; i <= e; i++) {
                    list.add("" + (char) i);
                }
                return list.toArray();
            }
        }
        return new Object[]{s};
    }

    /**
     * 判断是否为标准的数字格式
     *
     * @param str 字符串,可能是高级的数字
     * @return 是否为数组字符串
     */
    static public boolean isStandardNumber(String str) {
        return StringUtil.isStandardNumber(str);
    }

    /**
     * 注入变量   tagNode 要插入的代码块 代码
     * @param scriptEngine 当前的脚本引擎
     * @param tagNode 节点
     * @param out 输出
     * @param variableBegin 开始标识
     * @param variableSafeBegin 安全的开始标识
     * @param variableEnd 结束标识
     * @param escapeVariable 通配符号
     * @throws Exception 异常
     */
    static public void getInjectVariables(ScriptRunner scriptEngine, TagNode tagNode, Writer out, String variableBegin,String variableSafeBegin, String variableEnd, char escapeVariable) throws Exception {
        if (tagNode == null || tagNode.getTagName().equals(CommentBlock.noteTagBegin)) {
            return;
        }
        String str = tagNode.getBody();
        if (str == null) {
            return;
        }

        int i = 0;
        int ivb = -1;
        int ys = 0;
        int yd = 0;
        boolean quote = false;
        int lineNumber = 0;
        while (i < str.length()) {
            char c = str.charAt(i);
            if (c == '\n') {
                lineNumber++;
            }
            char old = ' ';
            if (i > 0) {
                old = str.charAt(i - 1);
            }

            if (ivb == -1 && escapeVariable != old && (str.startsWith(variableBegin, i)||str.startsWith(variableSafeBegin, i))) {
                ys = 0;
                yd = 0;
                ivb = i + variableBegin.length();
                quote = str.startsWith(variableSafeBegin, i);
            } else if (ivb != -1) {
                if (c == '\"' && yd % 2 == 0) {
                    ys++;
                }
                if (c == '\'' && ys % 2 == 0) {
                    yd++;
                }
                if (ys % 2 == 0 && yd % 2 == 0 && old != escapeVariable && variableEnd.equalsIgnoreCase(str.substring(i, i + variableEnd.length()))) {
                    String varName = str.substring(ivb, i);
                    try {
                        if (quote)
                        {
                            out.write(StringUtil.quoteSql(toString(scriptEngine.eval(varName, tagNode.getLineNumber()))));
                        }
                        else
                        {
                            out.write( toString(scriptEngine.eval(varName, tagNode.getLineNumber())) );
                        }
                    } finally {
                        ivb = -1;
                    }
                }
            } else {
                if (!(escapeVariable == c && (str.startsWith(variableBegin, i + 1)||str.startsWith(variableSafeBegin, i + 1)))) {
                    out.write(c);
                }
            }
            i++;
        }
    }
}