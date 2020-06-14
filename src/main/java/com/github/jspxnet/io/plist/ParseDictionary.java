package com.github.jspxnet.io.plist;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.github.jspxnet.utils.StringUtil;

import com.dd.plist.*;
import com.github.jspxnet.utils.ArrayUtil;

import java.util.*;

/**
 * 苹果的配置文件解析
 */
public class ParseDictionary {
    final static public String start_KEY = "start";
    final static public String duration_KEY = "duration";
    final static public String end_KEY = "end";

    private NSDictionary rootDictionary;

    public ParseDictionary(NSDictionary rootDictionary) {
        this.rootDictionary = rootDictionary;
    }

    public NSDictionary findDictionary(String name) {
        if (rootDictionary == null) {
            return null;
        }
        searchDictionary(rootDictionary, name);
        return findDict;
    }

    private NSDictionary findDict = null;

    private void searchDictionary(NSDictionary startDict, String name) {
        if (startDict == null) {
            return;
        }
        HashMap<String, NSObject> framesMap = startDict.getHashMap();
        for (String key : framesMap.keySet()) {
            NSObject nsObj = framesMap.get(key);
            if (nsObj instanceof NSDictionary) {
                if (name.equalsIgnoreCase(StringUtil.trim(key))) {
                    findDict = (NSDictionary) nsObj;
                    return;
                } else {
                    searchDictionary((NSDictionary) nsObj, name);
                }
            }
        }
    }

    public Set<String> getFileNameList() {
        NSDictionary listDict = findDictionary("frames");
        return listDict.getHashMap().keySet();
    }

    public LinkedList<String> getFileNameSortList() {
        Set<String> nameSet = getFileNameList();
        LinkedList<String> listStart = new LinkedList();
        for (String name : nameSet) {
            if (name.contains(start_KEY)) {
                listStart.add(name);
            }
        }
        LinkedList<String> listDuration = new LinkedList();
        for (String name : nameSet) {
            if (name.contains(duration_KEY)) {
                listDuration.add(name);
            }
        }
        LinkedList<String> listEnd = new LinkedList();
        for (String name : nameSet) {
            if (name.contains(end_KEY)) {
                listEnd.add(name);
            }
        }
        PlistFileComparator comparator = new PlistFileComparator();
        Collections.sort(listStart, comparator); // 排序
        Collections.sort(listDuration, comparator); // 排序
        Collections.sort(listEnd, comparator); // 排序

        LinkedList<String> list = new LinkedList();
        list.addAll(listStart);
        list.addAll(listDuration);
        list.addAll(listEnd);

        listStart.clear();
        listDuration.clear();
        listEnd.clear();
        return list;
    }

    public static Rect getFrameRect(HashMap<String, NSObject> hashMap) {

        Rect rect = new Rect();
        if (hashMap.containsKey("frame")) {
            NSString frame = (NSString) hashMap.get("frame");
            String txt = StringUtil.replace(frame.getContent(), "{", StringUtil.empty);
            txt = StringUtil.replace(txt, "}", StringUtil.empty);
            String[] txtArray = StringUtil.split(txt, StringUtil.COMMAS);
            if (ArrayUtil.isEmpty(txtArray) || txtArray.length < 4) {
                return null;
            }

            rect.setX(StringUtil.toInt(txtArray[0]));
            rect.setY(StringUtil.toInt(txtArray[1]));
            rect.setWidth(StringUtil.toInt(txtArray[2]));
            rect.setHeight(StringUtil.toInt(txtArray[3]));
        } else {

            if (hashMap.containsKey("x")) {
                NSNumber number = (NSNumber) hashMap.get("x");
                rect.setX(number.intValue());
            } else {
                return null;
            }
            if (hashMap.containsKey("y")) {
                NSNumber number = (NSNumber) hashMap.get("y");
                rect.setY(number.intValue());
            } else {
                return null;
            }
            if (hashMap.containsKey("width")) {
                NSNumber number = (NSNumber) hashMap.get("width");
                rect.setWidth(number.intValue());
            } else {
                return null;
            }
            if (hashMap.containsKey("height")) {
                NSNumber number = (NSNumber) hashMap.get("height");
                rect.setHeight(number.intValue());
            } else {
                return null;
            }
        }

        return rect;
    }

    public static Rect getSourceColorRect(HashMap<String, NSObject> hashMap) {

        Rect rect = new Rect();
        if (hashMap.containsKey("sourceColorRect")) {
            NSString frame = (NSString) hashMap.get("sourceColorRect");
            String txt = StringUtil.replace(frame.getContent(), "{", StringUtil.empty);
            txt = StringUtil.replace(txt, "}", StringUtil.empty);
            String[] txtArray = StringUtil.split(txt, StringUtil.COMMAS);
            if (ArrayUtil.isEmpty(txtArray) || txtArray.length < 4) {
                return null;
            }

            rect.setX(StringUtil.toInt(txtArray[0]));
            rect.setY(StringUtil.toInt(txtArray[1]));
            rect.setWidth(StringUtil.toInt(txtArray[2]));
            rect.setHeight(StringUtil.toInt(txtArray[3]));
        } else {
            if (hashMap.containsKey("x")) {
                NSNumber number = (NSNumber) hashMap.get("x");
                rect.setX(number.intValue());
            } else {
                return null;
            }
            if (hashMap.containsKey("y")) {
                NSNumber number = (NSNumber) hashMap.get("y");
                rect.setY(number.intValue());
            } else {
                return null;
            }
            if (hashMap.containsKey("width")) {
                NSNumber number = (NSNumber) hashMap.get("width");
                rect.setWidth(number.intValue());
            } else {
                return null;
            }
            if (hashMap.containsKey("height")) {
                NSNumber number = (NSNumber) hashMap.get("height");
                rect.setHeight(number.intValue());
            } else {
                return null;
            }

        }
        return rect;
    }

    public static int[] getOffset(HashMap<String, NSObject> hashMap) {
        if (hashMap.containsKey("offset")) {
            NSString offsetStr = (NSString) hashMap.get("offset");
            String txt = StringUtil.replace(offsetStr.getContent(), "{", StringUtil.empty);
            txt = StringUtil.replace(txt, "}", StringUtil.empty);
            String[] txtArray = StringUtil.split(txt, StringUtil.COMMAS);
            if (ArrayUtil.isEmpty(txtArray) || txtArray.length < 2) {
                return null;
            }
            int[] result = new int[txtArray.length];
            for (int i = 0; i < txtArray.length; i++) {
                result[i] = StringUtil.toInt(txtArray[i]);
            }
            return result;
        }

        int offsetX = 0;
        int offsetY = 0;
        if (hashMap.containsKey("offsetX")) {
            NSNumber number = (NSNumber) hashMap.get("offsetX");
            offsetX = number.intValue();
        }

        if (hashMap.containsKey("offsetY")) {
            NSNumber number = (NSNumber) hashMap.get("offsetY");
            offsetY = number.intValue();
        }
        return new int[]{offsetX, offsetY};
    }

    public static int[] getSourceSize(HashMap<String, NSObject> hashMap) {
        if (hashMap.containsKey("sourceSize")) {
            NSString offsetStr = (NSString) hashMap.get("sourceSize");
            String txt = StringUtil.replace(offsetStr.getContent(), "{", StringUtil.empty);
            txt = StringUtil.replace(txt, "}", StringUtil.empty);
            String[] txtArray = StringUtil.split(txt, StringUtil.COMMAS);
            if (ArrayUtil.isEmpty(txtArray) || txtArray.length < 2) {
                return null;
            }
            int[] result = new int[txtArray.length];
            for (int i = 0; i < txtArray.length; i++) {
                result[i] = StringUtil.toInt(txtArray[i]);
            }
            return result;
        }

        int offsetX = 0;
        int offsetY = 0;
        if (hashMap.containsKey("originalWidth")) {
            NSNumber number = (NSNumber) hashMap.get("originalWidth");
            offsetX = number.intValue();
        } else {
            return null;
        }

        if (hashMap.containsKey("originalHeight")) {
            NSNumber number = (NSNumber) hashMap.get("originalHeight");
            offsetY = number.intValue();
        } else {
            return null;
        }
        return new int[]{offsetX, offsetY};
    }


    public static boolean getRotated(HashMap<String, NSObject> hashMap) {
        if (hashMap.containsKey("rotated")) {
            NSNumber number = (NSNumber) hashMap.get("rotated");
            if (number.isBoolean()) {
                return number.boolValue();
            }
        }
        return false;
    }

    public static String getString(HashMap<String, NSObject> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            NSString str = (NSString) hashMap.get(key);
            return str.getContent();
        }
        return StringUtil.empty;
    }

    public static int getInt(HashMap<String, NSObject> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            NSNumber str = (NSNumber) hashMap.get(key);
            return str.intValue();
        }
        return 0;
    }

    public static boolean getBoolean(HashMap<String, NSObject> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            NSNumber str = (NSNumber) hashMap.get(key);
            return str.boolValue();
        }
        return false;
    }


    public static long getLong(HashMap<String, NSObject> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            NSNumber str = (NSNumber) hashMap.get(key);
            return str.longValue();
        }
        return 0;
    }

    public static Date getDate(HashMap<String, NSObject> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            NSDate str = (NSDate) hashMap.get(key);
            return str.getDate();
        }
        return null;
    }

    public static String getBase64(HashMap<String, NSObject> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            NSData str = (NSData) hashMap.get(key);
            return str.getBase64EncodedData();
        }
        return null;
    }

}
