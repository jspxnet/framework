package com.github.jspxnet.util;

import java.util.Hashtable;
import java.util.StringTokenizer;

public final
class HttpUtil {
    private HttpUtil()
    {

    }
    public static Hashtable<String, String[]> parseQueryString(String s) {
        if (s==null)
        {
            return new Hashtable<>(0);
        }
        Hashtable<String, String[]> ht = new Hashtable<>();
        StringBuilder sb = new StringBuilder();
        String[] valArray;
        String key;
        for(StringTokenizer st = new StringTokenizer(s, "&"); st.hasMoreTokens(); ht.put(key, valArray)) {
            String pair = st.nextToken();
            int pos = pair.indexOf(61);
            if (pos == -1) {
                break;
            }
            key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1), sb);
            if (!ht.containsKey(key)) {
                valArray = new String[]{val};
            } else {
                String[] oldVals = ht.get(key);
                valArray = new String[oldVals.length + 1];
                System.arraycopy(oldVals, 0, valArray, 0, oldVals.length);
                valArray[oldVals.length] = val;
            }
        }
        return ht;
    }


    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);

        for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch(c) {
                case '%':
                    try {
                        sb.append((char)Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException var6) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException var7) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2) {
                            ++i;
                        }
                    }
                    break;
                case '+':
                    sb.append(' ');
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

}
