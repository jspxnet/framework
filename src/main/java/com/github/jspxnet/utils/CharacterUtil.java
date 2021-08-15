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

import com.github.jspxnet.io.cpdetector.ASCIIDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-17
 * Time: 9:22:16
 * 字符编码
 */

public final class CharacterUtil {
    final private static com.github.jspxnet.io.cpdetector.CodePageDetectorProxy DETECTOR = com.github.jspxnet.io.cpdetector.CodePageDetectorProxy.getInstance();
    private static final com.github.jspxnet.io.cpdetector.ByteOrderMarkDetector BYTE_ORDER_MARK_DETECTOR = new com.github.jspxnet.io.cpdetector.ByteOrderMarkDetector();
    final private static com.github.jspxnet.io.cpdetector.ParsingDetector PARSING_DETECTOR = new com.github.jspxnet.io.cpdetector.ParsingDetector(false);

    static {
        DETECTOR.add(com.github.jspxnet.io.cpdetector.JChardetFacade.getInstance());
        DETECTOR.add(ASCIIDetector.getInstance());
        DETECTOR.add(BYTE_ORDER_MARK_DETECTOR);
        DETECTOR.add(PARSING_DETECTOR);
        DETECTOR.add(com.github.jspxnet.io.cpdetector.UnicodeDetector.getInstance());


    }


    public static boolean equals(String encode1, String encode2) {
        return !(encode1 == null || encode2 == null) && (encode1.equalsIgnoreCase(encode2) || Charset.forName(encode1).equals(Charset.forName(encode2)));
    }

    public static String fixEncode(String encode, String defaultEncode) {
        if (encode == null) {
            return SystemUtil.encode;
        }
        if (Charset.isSupported(encode)) {
            return Charset.forName(encode).displayName();
        }
        return defaultEncode;
    }

    public static String encoding(String s, String encodeOld, String encode2) {
        try {
            return new String(s.getBytes(encodeOld), encode2);
        } catch (UnsupportedEncodingException uee) {
            return s;
        }
    }

    public static String native2Unicode(String s) {
        if (s == null || s.length() == 0) {
            return StringUtil.empty;
        }
        byte[] buffer = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            buffer[i] = (byte) s.charAt(i);
        }
        return new String(buffer);
    }

    public static String unicode2Native(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        char[] buffer = new char[s.length() * 2];
        char c;
        int j = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) >= 0x100) {
                c = s.charAt(i);
                byte[] buf = ("" + c).getBytes();
                buffer[j++] = (char) buf[0];
                buffer[j++] = (char) buf[1];
            } else {
                buffer[j++] = s.charAt(i);
            }
        }
        return new String(buffer, 0, j);
    }

    /**
     * @param file          文件
     * @param defaultEncode 编码
     * @return 得到文件的编码
     */
    public static String getFileCharacterEnding(File file, String defaultEncode) {
        String fileCharacterEnding = defaultEncode;
        Charset charset = null;
        try {
            charset = DETECTOR.detectCodepage(file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
            return defaultEncode;
        }
        if (charset != null) {
            fileCharacterEnding = charset.name();
        }
        return fileCharacterEnding;

    }

    public static String getStreamCharacterEnding(InputStream inputStream, int size, String defaultEncode) {
        String fileCharacterEnding = defaultEncode;
        Charset charset = null;
        try {
            charset = DETECTOR.detectCodepage(inputStream, size);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultEncode;
        }
        if (charset != null) {
            fileCharacterEnding = charset.name();
        }
        return fileCharacterEnding;
    }

    public static String getBytesEnding(byte[] str, String defaultEncode) {
        String fileCharacterEnding = defaultEncode;
        Charset charset = null;
        try {
            ByteArrayInputStream byteArrIn = new ByteArrayInputStream(str, 0, str.length);
            charset = DETECTOR.detectCodepage(byteArrIn, str.length);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultEncode;
        }
        if (charset != null) {
            fileCharacterEnding = charset.name();
        }
        return fileCharacterEnding;
    }


    public static boolean isGBK(String str) {
        char[] chars = str.toCharArray();
        boolean isGBK = false;
        for (char aChar : chars) {
            byte[] bytes = ("" + aChar).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
                        && ints[1] <= 0xFE) {
                    isGBK = true;
                    break;
                }
            }
        }
        return isGBK && java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(str);
    }

    final static public Map<String, String> CHAR_SET_MAP = new HashMap<String, String>();

    static {

        CHAR_SET_MAP.put("usa7", "US-ASCII");
        CHAR_SET_MAP.put("big5", "Big5");
        CHAR_SET_MAP.put("gbk", "GBK");
        CHAR_SET_MAP.put("sjis", "SJIS");
        CHAR_SET_MAP.put("gb2312", "EUC_CN");
        CHAR_SET_MAP.put("ujis", "EUC_JP");
        CHAR_SET_MAP.put("euc_kr", "EUC_KR");
        CHAR_SET_MAP.put("latin1", "ISO8859_1");
        CHAR_SET_MAP.put("latin1_de", "ISO8859_1");
        CHAR_SET_MAP.put("german1", "ISO8859_1");
        CHAR_SET_MAP.put("danish", "ISO8859_1");
        CHAR_SET_MAP.put("latin2", "ISO8859_2");
        CHAR_SET_MAP.put("czech", "ISO8859_2");
        CHAR_SET_MAP.put("hungarian", "ISO8859_2");
        CHAR_SET_MAP.put("croat", "ISO8859_2");
        CHAR_SET_MAP.put("greek", "ISO8859_7");
        CHAR_SET_MAP.put("hebrew", "ISO8859_8");
        CHAR_SET_MAP.put("latin5", "ISO8859_9");
        CHAR_SET_MAP.put("latvian", "ISO8859_13");
        CHAR_SET_MAP.put("latvian1", "ISO8859_13");
        CHAR_SET_MAP.put("estonia", "ISO8859_13");
        CHAR_SET_MAP.put("dos", "Cp437");
        CHAR_SET_MAP.put("pclatin2", "Cp852");
        CHAR_SET_MAP.put("cp866", "Cp866");
        CHAR_SET_MAP.put("koi8_ru", "KOI8_R");
        CHAR_SET_MAP.put("tis620", "TIS620");
        CHAR_SET_MAP.put("win1250", "Cp1250");
        CHAR_SET_MAP.put("win1250ch", "Cp1250");
        CHAR_SET_MAP.put("win1251", "Cp1251");
        CHAR_SET_MAP.put("cp1251", "Cp1251");
        CHAR_SET_MAP.put("win1251ukr", "Cp1251");
        CHAR_SET_MAP.put("cp1257", "Cp1257");
        CHAR_SET_MAP.put("macroman", "MacRoman");
        CHAR_SET_MAP.put("macce", "MacCentralEurope");
        CHAR_SET_MAP.put("utf8", StandardCharsets.UTF_8.name());
        CHAR_SET_MAP.put("utf-8", StandardCharsets.UTF_8.name());
        CHAR_SET_MAP.put("ucs2", "UnicodeBig");
    }

    public static boolean haveEncode(String encode) {
        return encode != null && CHAR_SET_MAP.containsKey(encode.toLowerCase());
    }
}