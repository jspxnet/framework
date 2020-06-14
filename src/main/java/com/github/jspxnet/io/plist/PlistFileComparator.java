package com.github.jspxnet.io.plist;

import com.github.jspxnet.utils.StringUtil;

import java.util.Comparator;

public class PlistFileComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        int a1 = 0;
        int a2 = 0;
        if (o1.contains(com.github.jspxnet.io.plist.ParseDictionary.start_KEY)) {
            a1 = StringUtil.toInt(StringUtil.substringAfter(o1, ParseDictionary.start_KEY));
        }
        if (o2.contains(com.github.jspxnet.io.plist.ParseDictionary.start_KEY)) {
            a2 = StringUtil.toInt(StringUtil.substringAfter(o2, ParseDictionary.start_KEY));
        }

        if (o1.contains(com.github.jspxnet.io.plist.ParseDictionary.duration_KEY)) {
            a1 = StringUtil.toInt(StringUtil.substringAfter(o1, ParseDictionary.start_KEY));
        }
        if (o2.contains(com.github.jspxnet.io.plist.ParseDictionary.duration_KEY)) {
            a2 = StringUtil.toInt(StringUtil.substringAfter(o2, ParseDictionary.start_KEY));
        }

        if (o1.contains(com.github.jspxnet.io.plist.ParseDictionary.end_KEY)) {
            a1 = StringUtil.toInt(StringUtil.substringAfter(o1, ParseDictionary.start_KEY));
        }
        if (o2.contains(com.github.jspxnet.io.plist.ParseDictionary.end_KEY)) {
            a2 = StringUtil.toInt(StringUtil.substringAfter(o2, ParseDictionary.start_KEY));
        }
        return Integer.compare(a1, a2);
    }
}