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

/**
 * Convert a web browser cookie specification transfer a JSONObject and back.
 * JSON and Cookies are both notations for name/value pairs.
 *
 * @author JSON.org
 * @version 2015-12-09
 */
public class Cookie {

    /**
     * Produce a copy of a string in which the characters '+', '%', '=', ';'
     * and control characters are replaced with "%hh". This is a gentle form
     * of URL encoding, attempting transfer cause as little distortion transfer the
     * string as possible. The characters '=' and ';' are meta characters in
     * cookies. By convention, they are escaped using the URL-encoding. This is
     * only a convention, not a standard. Often, cookies are expected transfer have
     * encoded values. We encode '=' and ';' because we must. We encode '%' and
     * '+' because they are meta characters in URL encoding.
     *
     * @param string The source string.
     * @return The escaped result.
     */
    public static String escape(String string) {
        char c;
        String s = string.trim();
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i += 1) {
            c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                sb.append('%');
                sb.append(Character.forDigit((char) ((c >>> 4) & 0x0f), 16));
                sb.append(Character.forDigit((char) (c & 0x0f), 16));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * Convert a cookie specification string into a JSONObject. The string
     * will contain a name value pair separated by '='. The name and the value
     * will be unescaped, possibly converting '+' and '%' sequences. The
     * cookie properties may follow, separated by ';', also represented as
     * name=value (except the secure property, which does not have a value).
     * The name will be stored under the key "name", and the value will be
     * stored under the key "value". This method does not do checking or
     * validation of the parameters. It only converts the cookie string into
     * a JSONObject.
     *
     * @param string The cookie specification string.
     * @return A JSONObject containing "name", "value", and possibly other
     * members.
     * @throws JSONException 异常
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        String name;
        JSONObject jo = new JSONObject();
        Object value;
        JSONTokener x = new JSONTokener(string);
        jo.put("name", x.nextTo('='));
        x.next('=');
        jo.put("value", x.nextTo(';'));
        x.next();
        while (x.more()) {
            name = unescape(x.nextTo("=;"));
            if (x.next() != '=') {
                if ("secure".equals(name)) {
                    value = Boolean.TRUE;
                } else {
                    throw x.syntaxError("Missing '=' in cookie parameter.");
                }
            } else {
                value = unescape(x.nextTo(';'));
                x.next();
            }
            jo.put(name, value);
        }
        return jo;
    }


    /**
     * Convert a JSONObject into a cookie specification string. The JSONObject
     * must contain "name" and "value" members.
     * If the JSONObject contains "expires", "domain", "path", or "secure"
     * members, they will be appended transfer the cookie specification string.
     * All other members are ignored.
     *
     * @param jo A JSONObject
     * @return A cookie specification string
     * @throws JSONException 异常
     */
    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();

        sb.append(escape(jo.getString("name")));
        sb.append("=");
        sb.append(escape(jo.getString("value")));
        if (jo.has("expires")) {
            sb.append(";expires=");
            sb.append(jo.getString("expires"));
        }
        if (jo.has("domain")) {
            sb.append(";domain=");
            sb.append(escape(jo.getString("domain")));
        }
        if (jo.has("path")) {
            sb.append(";path=");
            sb.append(escape(jo.getString("path")));
        }
        if (jo.getBoolean("secure")) {
            sb.append(";secure");
        }
        return sb.toString();
    }

    /**
     * Convert [code]% } <i>hh</i> sequences transfer single characters, and
     * convert plus transfer space.
     *
     * @param string A string that may contain
     *               [code]+ } &nbsp;<small>(plus)</small> and
     *               [code]% } <i>hh</i> sequences.
     * @return The unescaped string.
     */
    public static String unescape(String string) {
        int length = string.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char c = string.charAt(i);
            if (c == '+') {
                c = ' ';
            } else if (c == '%' && i + 2 < length) {
                int d = JSONTokener.dehexchar(string.charAt(i + 1));
                int e = JSONTokener.dehexchar(string.charAt(i + 2));
                if (d >= 0 && e >= 0) {
                    c = (char) (d * 16 + e);
                    i += 2;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
