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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.utils.EncryptUtil;

import java.io.*;
import java.util.zip.*;


/**
 * Provides useful methods dealing with zip compression
 */
public class ZipUtil {

    /**
     * Compresses data
     *
     * @param aData 压缩
     * @return Byte array representation of the compressed data
     */
    public static byte[] zip(byte[] aData) {
        if (aData == null) {
            return null;
        }
        try {
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            GZIPOutputStream zipout = new GZIPOutputStream(byteout);
            zipout.write(aData);
            zipout.finish();
            byte[] result = byteout.toByteArray();
            zipout.close();
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 使用zip进行解压缩
     *
     * @param compressed 压缩后的文本
     * @return 解压后的字符串
     */
    public static byte[] unZip(byte[] compressed) {
        if (compressed == null) {
            return null;
        }
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        GZIPInputStream zin = null;
        try {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }


    /**
     * 这个算法并不好,一般越压越大,只是保留为了方便
     * 利用重复字符出现的次数，编写一个方法，实现基本的字符串压缩功能。
     *
     * @param str 例如，字符串aabcccccaaa会变为a2b1c5a3。
     * @return 串压后的字符串
     */
    public static String compressChar(String str) {
        StringBuilder result = new StringBuilder();
        int count = 1;
        char last = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (last == str.charAt(i)) {
                count++;
            } else {
                result.append(last).append(count);
                last = str.charAt(i);
                count = 1;
            }
        }
        result.append(last).append(count);
        return result.toString();
    }

    /**
     * 压缩后转base64
     *
     * @param data 数据
     * @return base64
     */
    public static String getZipBase64Encode(String data) {
        if (data == null) {
            return "";
        }
        try {
            return EncryptUtil.getBase64Encode(zip(data.getBytes(Environment.defaultEncode)), EncryptUtil.URL_SAFE + EncryptUtil.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

    /**
     * 压缩的base64还原
     *
     * @param txt 压缩数据
     * @return 压缩的base64还原
     */
    public static String getZipBase64Decode(String txt) {
        if (txt == null) {
            return null;
        }
        try {
            return new String(unZip(EncryptUtil.getBase64Decode(txt, EncryptUtil.URL_SAFE + EncryptUtil.NO_WRAP)), Environment.defaultEncode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

}