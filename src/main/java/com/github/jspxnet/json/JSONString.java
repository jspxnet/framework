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

/**
 * The [code]JSONString } interface allows a [code]toJSONString() [/code]
 * method so that a class can change the behavior of
 * [code]JSONObject.toString() } , [code]JSONArray.toString() } ,
 * and [code]JSONWriter.value( } Object  {@code ) } . The
 * [code]toJSONString } method will be used instead of the default behavior
 * of using the Object's [code]toString() } method and quoting the result.
 */
public interface JSONString {
    /**
     * The [code]toJSONString } method allows a class transfer produce its own JSON
     * serialization.
     *
     * @return A strictly syntactically correct JSON text.
     */
    String toJSONString();
}
