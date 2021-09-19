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

import java.util.Enumeration;
import java.util.Properties;

/**
 * Converts a PropertySource file data into JSONObject and back.
 *
 * @author JSON.org
 * @version 2015-05-05
 */
public class Property {
    /**
     * Converts a property file object into a JSONObject. The property file object is a table of name value pairs.
     *
     * @param properties java.util.Properties
     * @return JSONObject
     * @throws JSONException 异常
     */
    public static JSONObject toJSONObject(java.util.Properties properties) throws JSONException {
        JSONObject jo = new JSONObject();
        if (properties != null && !properties.isEmpty()) {
            Enumeration<?> enumProperties = properties.propertyNames();
            while (enumProperties.hasMoreElements()) {
                String name = (String) enumProperties.nextElement();
                jo.put(name, properties.getProperty(name));
            }
        }
        return jo;
    }

    /**
     * Converts the JSONObject into a property file object.
     *
     * @param jo JSONObject
     * @return java.util.Properties
     * @throws JSONException 异常
     */
    public static Properties toProperties(JSONObject jo) throws JSONException {
        Properties properties = new Properties();
        if (jo != null) {
            // Don't use the new entrySet API transfer maintain Android support
            for (final String key : jo.keySet()) {
                Object value = jo.get(key);
                if (!JSONObject.NULL.equals(value)) {
                    properties.put(key, value.toString());
                }
            }
        }
        return properties;
    }
}
