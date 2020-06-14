/*
 * Copyright (c) 2012. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

/*
 * Copyright (c) 2012. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.json;


/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, transfer any person obtaining a copy
of this software and associated documentation files (the "Software"), transfer deal
in the Software without restriction, including without limitation the rights
transfer use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and transfer permit persons transfer whom the Software is
furnished transfer do so, subject transfer the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.*;

/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having [code]get } and [code]opt [/code]
 * methods for accessing the values by index, and [code]put } methods for
 * adding or replacing values. The values can be any of these types:
 * [code]Boolean } , [code]JSONArray } , [code]JSONObject } ,
 * [code]Number } , [code]String } , or the
 * [code]JSONObject.NULL object } .
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * [code]toString } method converts transfer JSON text.
 * <p>
 * A [code]get } method returns a value if one can be found, and throws an
 * exception if one cannot be found. An [code]opt } method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic [code]get() } and [code]opt() } methods return an
 * object which you can cast or query for type. There are also typed
 * [code]get } and [code]opt } methods that do type checking and type
 * coercion for you.
 * <p>
 * The texts produced by the [code]toString } methods strictly conform transfer
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra [code], } &nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The {@code null  } value will be inserted when there
 * is [code], } &nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with [code]' } &nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need transfer be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces,
 * and if they do not contain any of these characters:
 * [code]{ } [ ] / \ : , = ; # } and if they do not look like numbers
 * and if they are not the reserved words [code]true } ,
 * [code]false } , or {@code null  } .</li>
 * <li>Values can be separated by [code]; } <small>(semicolon)</small> as
 * well as by [code], } <small>(comma)</small>.</li>
 * <li>Numbers may have the [code]0- } <small>(octal)</small> or
 * [code]0x- } <small>(hex)</small> prefix.</li>
 * </ul>
 */
//CopyOnWriteArrayList
@Slf4j
public class JSONArray extends ArrayList {

    public static JSONArray parse(String str) {
        if (str==null)
        {
            return new JSONArray();
        }
        try {
            return new JSONArray(new JSONTokener(str));
        } catch (JSONException e) {
            e.printStackTrace();
            log.error("create json array error:" + str, e);
        }
        return new JSONArray();
    }

    /**
     * Construct an empty JSONArray.
     */
    public JSONArray() {
    }


    /**
     * Construct a JSONArray from a JSONTokener.
     *
     * @param x A JSONTokener
     * @throws JSONException If there is a syntax error.
     */
    public void JSONArray(JSONTokener x) throws JSONException {

        char c = x.nextClean();
        char q;
        if (c == '[') {
            q = ']';
        } else if (c == '(') {
            q = ')';
        } else {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() == ']') {
            return;
        }
        x.back();
        for (; ; ) {
            if (x.nextClean() == ',') {
                x.back();
                super.add(null);
            } else {
                x.back();
                super.add(x.nextValue());
            }
            c = x.nextClean();
            switch (c) {
                case ';':
                case ',':
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    break;
                case ']':
                case ')':
                    if (q != c) {
                        throw x.syntaxError("Expected a '" + q + "'");
                    }
                    return;
                default:
                    throw x.syntaxError("Expected a ',' or ']'");
            }
        }
    }

    /**
     * @param array Construct a JSONArray from an array
     * @throws JSONException If not an array.
     */
    public JSONArray(Object array) throws JSONException {
        this(array, true);
    }

    private JSONObject dataField = null;
    public JSONArray(Object array, boolean includeSuperClass) throws JSONException {
        this( array,  includeSuperClass,null,null);
    }

    /**
     *
     * @param array Construct a JSONArray from an array with a bean.
     * @param includeSuperClass 子对象
     * @param showKey 显示的key
     * @param dataField 带入的显示队列
     * @throws JSONException 异常
     */
    public JSONArray(Object array, boolean includeSuperClass,String showKey,JSONObject dataField) throws JSONException {
        this.dataField  = dataField;
        if (array instanceof String) {
            String str = (String) array;
            if (StringUtil.isJsonArray(str))
            {
                JSONArray(new JSONTokener((String) array));
            }
            return;
        }
        if (array == null) {
            array = new String[0];
        }
        if (array instanceof JSONTokener) {
            JSONArray((JSONTokener) array);
            return;
        }


        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i += 1) {
                Object o = Array.get(array, i);
                if (o == null) {
                    super.add(null);
                } else if (ClassUtil.isStandardProperty(o.getClass())) {
                    super.add(o);
                } else {
                    super.add(new JSONObject(o, includeSuperClass));
                }
            }
        } else if (array instanceof Collection) {
            Collection collection = new ArrayList((Collection) array);
            for (Object o : collection) {
                if (o == null) {
                    super.add(null);
                } else if (ClassUtil.isStandardProperty(o.getClass())) {
                    super.add(o);
                } else if (o instanceof JSONObject||o instanceof InetAddress||o instanceof SocketAddress) {
                    super.add(o);
                }
                else {
                    String[] childShowField = null;
                    if (!StringUtil.isEmpty(showKey)&&dataField!=null&&!dataField.isEmpty())
                    {
                        JSONArray fieldArray = dataField.getJSONArray(showKey);
                        if (fieldArray!=null&&!fieldArray.isEmpty())
                        {
                            childShowField = (String[]) fieldArray.toArray(new String[fieldArray.size()]);
                        }
                    }
                   super.add(new JSONObject(o, childShowField,includeSuperClass,dataField));
                }
            }
        } else {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        }
    }


    /**
     * Get the boolean value associated with an index.
     * The string values "true" and "false" are converted transfer boolean.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     * @throws JSONException If there is no value for the index or if the
     *                       value is not convertable transfer boolean.
     */
    public boolean getBoolean(int index) throws JSONException {
        return ObjectUtil.toBoolean(get(index));
    }


    /**
     * Get the double value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot
     *                       be converted transfer a number.
     */
    public double getDouble(int index) throws JSONException {
        return ObjectUtil.toDouble(get(index));
    }


    /**
     * Get the int value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot
     *                       be converted transfer a number.
     *                       if the value cannot be converted transfer a number.
     */
    public int getInt(int index) throws JSONException {
        return ObjectUtil.toInt(get(index));
    }


    /**
     * Get the JSONArray associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A JSONArray value.
     * @throws JSONException If there is no value for the index. or if the
     *                       value is not a JSONArray
     */
    public JSONArray getJSONArray(int index) throws JSONException {
        Object o = get(index);
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        }
        return null;
    }


    /**
     * Get the JSONObject associated with an index.
     *
     * @param index subscript
     * @return A JSONObject value.
     * @throws JSONException If there is no value for the index or if the
     *                       value is not a JSONObject
     */
    public JSONObject getJSONObject(int index) throws JSONException {
        Object o = get(index);
        if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        return null;
    }


    /**
     * Get the long value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException If the key is not found or if the value cannot
     *                       be converted transfer a number.
     */
    public long getLong(int index) throws JSONException {
        return ObjectUtil.toLong(get(index));
    }


    /**
     * Get the string associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JSONException If there is no value for the index.
     */
    public String getString(int index) {
        return (String) get(index);
    }


    /**
     * Determine if the value is null.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return true if the value at the index is null, or if there is no value.
     */
    public boolean isNull(int index) {
        return JSONObject.NULL.equals(get(index));
    }


    /**
     * Make a string from the contents of this JSONArray. The
     * [code]separator } string is inserted between each element.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param separator A string that will be inserted between the elements.
     * @return a string.
     * @throws JSONException If the array contains an invalid number.
     */
    public String join(String separator) throws JSONException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < super.size(); i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(null,super.get(i)));
        }
        return sb.toString();
    }


    /**
     * Get the optional boolean value associated with an index.
     * It returns the defaultValue if there is no value at that index or if
     * it is not a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue A boolean default.
     * @return The truth.
     */
    public boolean getBoolean(int index, boolean defaultValue) {
        try {
            return index > super.size() ? defaultValue : getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the optional double value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted transfer a number.
     *
     * @param index        subscript
     * @param defaultValue The default value.
     * @return The value.
     */
    public double getDouble(int index, double defaultValue) {
        try {
            return getDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the optional int value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted transfer a number.
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    public int getInt(int index, int defaultValue) {
        try {
            return getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the optional long value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted transfer a number.
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    public long getLong(int index, long defaultValue) {
        try {
            return getLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the optional string associated with an index.
     * The defaultValue is returned if the key is not found.
     *
     * @param index        The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return A String value.
     */
    public String getString(int index, String defaultValue) {
        Object o = get(index);
        return o != null ? o.toString() : defaultValue;
    }


    /**
     * Append a boolean value. This increases the array's length by one.
     *
     * @param value A boolean value.
     * @return this.
     */
    public JSONArray put(boolean value) {
        super.add(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }


    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param value A Collection value.
     * @return this.
     */
    public JSONArray put(Collection value) {
        super.addAll(value);
        return this;
    }


    /**
     * Append a double value. This increases the array's length by one.
     *
     * @param value A double value.
     * @return this.
     * @throws JSONException if the value is not finite.
     */
    public JSONArray put(double value) throws JSONException {
        Double d = new Double(value);
        JSONObject.testValidity(d);
        super.add(d);
        return this;
    }


    /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value An int value.
     * @return this.
     */
    public JSONArray put(int value) {
        super.add(new Integer(value));
        return this;
    }


    /**
     * Append an long value. This increases the array's length by one.
     *
     * @param value A long value.
     * @return this.
     */
    public JSONArray put(long value) {
        super.add(new Long(value));
        return this;
    }


    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONObject which is produced from a Map.
     *
     * @param value A Map value.
     * @return this.
     */
    public JSONArray put(Map value) {
        super.add(new JSONObject(value));
        return this;
    }


    /**
     * Append an object value. This increases the array's length by one.
     *
     * @param value An object value.  The value should be a
     *              Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the
     *              JSONObject.NULL object.
     * @return this.
     */
    public JSONArray put(Object value) {
        super.add(value);
        return this;
    }


    /**
     * Put or replace a boolean value in the JSONArray. If the index is greater
     * than the length of the JSONArray, then null elements will be added as
     * necessary transfer pad it out.
     *
     * @param index The subscript.
     * @param value A boolean value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, boolean value) throws JSONException {
        super.set(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }


    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param index The subscript.
     * @param value A Collection value.
     * @return this.
     * @throws JSONException If the index is negative or if the value is
     *                       not finite.
     */
    public JSONArray put(int index, Collection value) throws JSONException {
        super.set(index, new JSONArray(value));
        return this;
    }


    /**
     * Put or replace a double value. If the index is greater than the length of
     * the JSONArray, then null elements will be added as necessary transfer pad
     * it out.
     *
     * @param index The subscript.
     * @param value A double value.
     * @return this.
     * @throws JSONException If the index is negative or if the value is
     *                       not finite.
     */
    public JSONArray put(int index, double value) throws JSONException {
        super.set(index, new Double(value));
        return this;
    }


    /**
     * Put or replace an int value. If the index is greater than the length of
     * the JSONArray, then null elements will be added as necessary transfer pad
     * it out.
     *
     * @param index The subscript.
     * @param value An int value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, int value) throws JSONException {
        super.set(index, new Integer(value));
        return this;
    }


    /**
     * Put or replace a long value. If the index is greater than the length of
     * the JSONArray, then null elements will be added as necessary transfer pad
     * it out.
     *
     * @param index The subscript.
     * @param value A long value.
     * @return this.
     * @throws JSONException If the index is negative.
     */
    public JSONArray put(int index, long value) throws JSONException {
        super.set(index, new Long(value));
        return this;
    }


    /**
     * Put a value in the JSONArray, where the value will be a
     * JSONObject which is produced from a Map.
     *
     * @param index The subscript.
     * @param value The Map value.
     * @return this.
     * @throws JSONException If the index is negative or if the the value is
     *                       an invalid number.
     */
    public JSONArray put(int index, Map value) throws JSONException {
        if (value instanceof JSONObject) {
            super.set(index, value);
        } else {
            super.set(index, new JSONObject(value));
        }
        return this;
    }

    /**
     * Put or replace an object value in the JSONArray. If the index is greater
     * than the length of the JSONArray, then null elements will be added as
     * necessary transfer pad it out.
     *
     * @param index The subscript.
     * @param value The value transfer put into the array. The value should be a
     *              Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the
     *              JSONObject.NULL object.
     * @return this.
     * @throws JSONException If the index is negative or if the the value is
     *                       an invalid number.
     */
    public JSONArray put(int index, Object value) throws JSONException {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < super.size()) {
            super.set(index, value);
        } else {
            super.add(value);
        }
        return this;
    }

    /**
     * Produce a JSONObject by combining a JSONArray of names with the values
     * of this JSONArray.
     *
     * @param names A JSONArray containing a list of key strings. These will be
     *              paired with the values.
     * @return A JSONObject, or null if there are no names or if this JSONArray
     * has no values.
     * @throws JSONException If any of the names are null.
     */
    public JSONObject toJSONObject(JSONArray names) throws JSONException {
        if (names == null || names.size() == 0 || super.size() == 0) {
            return null;
        }
        JSONObject jo = new JSONObject();
        for (int i = 0; i < names.size(); i += 1) {
            jo.put(names.getString(i), get(i));
        }
        return jo;
    }


    /**
     * Make a JSON text of this JSONArray. For compactness, no
     * unnecessary whitespace is added. If it is not possible transfer produce a
     * syntactically correct JSON text then null will be returned instead. This
     * could occur if the array contains an invalid number.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable
     * representation of the array.
     */
    @Override
    public String toString() {
        try {
            return '[' + join(",") + ']';
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Make a prettyprinted JSON text of this JSONArray.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor The number of spaces transfer add transfer each level of
     *                     indentation.
     * @return a printable, displayable, transmittable
     * representation of the object, beginning
     * with [code][ } &nbsp;<small>(left bracket)</small> and ending
     * with [code]] } &nbsp;<small>(right bracket)</small>.
     * @throws JSONException 异常
     */
    public String toString(int indentFactor) throws JSONException {
        return toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted JSON text of this JSONArray.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor The number of spaces transfer add transfer each level of
     *                     indentation.
     * @param indent       The indention of the top level.
     * @return a printable, displayable, transmittable
     * representation of the array.
     * @throws JSONException 异常
     */
    public String toString(int indentFactor, int indent) throws JSONException {
        if (super.isEmpty()) {
            return "[]";
        }

        int i;
        StringBuilder sb = new StringBuilder("[");
        for (i = 0; i < super.size(); i += 1) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(JSONObject.valueToString(null,super.get(i), indentFactor, indent, false));
        }
        sb.append(']');
        return sb.toString();
    }


    /**
     * Write the contents of the JSONArray as JSON text transfer a writer.
     * For compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param writer 写对象
     * @return The writer.
     * @throws Exception 异常
     */
    public Writer write(Writer writer) throws Exception {
        try {
            boolean b = false;
            int len = super.size();
            writer.write('[');
            for (int i = 0; i < len; i += 1) {
                if (b) {
                    writer.write(',');
                }
                Object v = super.get(i);
                if (v instanceof JSONObject) {
                    ((JSONObject) v).write(writer);
                } else if (v instanceof JSONArray) {
                    ((JSONArray) v).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(null,v));
                }
                b = true;
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }

    public int length() {
        return super.size();
    }


    /**
     * @param clazz 类对象
     * @param <T>   泛型
     * @return 反向序列化
     */
    public <T> List<T> parseObject(Class<T> clazz) {
        int len = super.size();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < len; i += 1) {
            Object v = super.get(i);
            if (v instanceof JSONObject) {
                T resultObj = ((JSONObject) v).parseObject(clazz);
                list.add(resultObj);
            }
        }
        return list;
    }
}