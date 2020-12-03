/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.security.utils;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.sm.SM3Digest;
import com.github.jspxnet.utils.StringUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-8-30
 * Time: 9:26:04
 * 加密单元,提供不同的加密方式
 */
public class EncryptUtil {
    //base64编码使用 begin
    /***
     * Default values for encoder/decoder flags.
     * 76个字符自动换行
     */
    public static final int DEFAULT = 0;

    /***
     * Encoder flag bit transfer omit the padding '=' characters at the end
     * of the output (if any).
     *
     */
    public static final int NO_PADDING = 1;

    /***
     * Encoder flag bit transfer omit all line terminators (i.e., the output
     * will be on one long line).
     * 不换行 输出将在一行
     */
    public static final int NO_WRAP = 2;

    /***
     * Encoder flag bit transfer indicate lines should be terminated with a
     * CRLF pair instead of just an LF.  Has no effect if {@code
     * NO_WRAP} is specified as well.
     */
    public static final int CRLF = 4;

    /***
     * Encoder/decoder flag bit transfer indicate using the "URL and
     * filename safe" variant of Base64 (see RFC 3548 section 4) where
     * {@code -} and {@code _} are used in place of {@code +} and
     * {@code /}.
     * 使用在文件名换URL
     */
    public static final int URL_SAFE = 8;

    /***
     * should not close the output stream it is wrapping when it
     * itself is closed.
     * 不要关闭它所包裹的输出流
     */
    public static final int NO_CLOSE = 16;
    //base64编码使用 end

    /*

        public static final String RSA = "RSA";
        private static final String SHA1WithRSA = "SHA1WithRSA";
        public static final String MD5withRSA = "MD5withRSA";

     */
    private EncryptUtil() {
    }

    /**
     * 得到小写的MD5编码
     *
     * @param origin 要加密的数据
     * @return md5
     */
    public static String getMd5(String origin) {
        if (origin == null) {
            return StringUtil.empty;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return toHex(md.digest(origin.getBytes(Environment.defaultEncode))).toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return StringUtil.empty;
    }


    /**
     * @param origin 来源数据
     * @return 国密类似MD5的算法
     */
    public static String getSm3(String origin) {
        if (origin == null) {
            return StringUtil.empty;
        }
        byte[] md = new byte[32];
        try {
            byte[] msg1 = origin.getBytes(Environment.defaultEncode);
            SM3Digest sm3 = new SM3Digest();
            sm3.update(msg1, 0, msg1.length);
            sm3.doFinal(md, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return toHex(md);
    }


    /**
     * MD32和MD5不同的地方是加入了密钥
     *
     * @param dm 数据
     * @param kl 密钥
     * @return 返回  加密后是一个32位大写字母组合形式，并且本加密方法不可逆
     */
    static public String getMd32(String dm, String kl) {
        if (null == dm) {
            return StringUtil.empty;
        }
        if (null == kl) {
            return StringUtil.empty;
        }
        String mw, cmm;
        int k, i, a, hf, lf, bytes = 16;
        int[] mm;
        int l;
        int rand_seed;

        rand_seed = 12345;

        int len = kl.length();
        if (len >= 12) {
            len = 12;
        }

        mw = kl.substring(0, len) + dm.trim();
        l = mw.length();

        if (bytes > 100) {
            bytes = 100;
        }
        if (l < bytes) {
            for (; l <= bytes; l++) {
                rand_seed = (31527 * rand_seed + 3) % 32768;
                a = rand_seed % 256;
                if (a < 32 || a > 127) {
                    a = 'a';
                }
                mw = mw + (char) (a);
            }
        }

        mm = new int[100];

        for (l = 0; l < 100; l++) {
            mm[l] = 0;
        }

        for (l = 0; l < bytes; l++) {
            a = mw.charAt(l);
            for (i = 1; i <= 8; i++) {
                if (a >= 128) {
                    a -= 128;
                    for (k = 0; k < bytes; k++) {
                        rand_seed = (31527 * rand_seed + 3) % 32768;
                        mm[k] += rand_seed % 256;
                    }
                } else {
                    for (k = 1; k <= bytes; k++) {
                        rand_seed = (31527 * rand_seed + 3) % 32768;
                    }
                }
                a *= 2;
            }
        }

        for (k = bytes - 1; k >= 0; k--) {
            if (k >= 1) {
                mm[k - 1] += mm[k] / 256;
            }
            mm[k] = mm[k] % 256;
        }

        cmm = StringUtil.empty;
        for (k = 0; k < bytes; k++) {
            hf = mm[k] / 16;
            if (hf < 10) {
                cmm = cmm + (char) (hf + (short) ('0'));
            } else {
                cmm = cmm + (char) (hf + (short) ('A') - 10);
            }
            lf = mm[k] % 16;
            if (lf < 10) {
                cmm = cmm + (char) (lf + (short) ('0'));
            } else {
                cmm = cmm + (char) (lf + (short) ('A') - 10);
            }
        }
        return cmm;
    }

    public static String getBase64Encode(byte[] bytes) {

        return Base64.encodeToString(bytes, URL_SAFE + NO_WRAP);
    }


    /**
     * @param bytes 编码
     * @param flags 格式类型
     * @return 改为jdk rt.jar 里边的包使用
     */
    public static String getBase64Encode(byte[] bytes, int flags) {

        return Base64.encodeToString(bytes, flags);
    }


    /**
     * @param str 解码
     * @return 原文
     */
    public static byte[] getBase64Decode(String str) {
        //有没有换行都可以正确解析
        return getBase64Decode(str, URL_SAFE + NO_WRAP);
    }

    public static byte[] getBase64Decode(String str, int flags) {
        //有没有换行都可以正确解析
        if (str == null) {
            str = StringUtil.empty;
        }
        return Base64.decode(str, flags);
    }


    public static String getBase64DecodeString(String str) throws UnsupportedEncodingException {
        if (StringUtil.isNull(str)) {
            return StringUtil.empty;
        }
        return getBase64DecodeString(str, URL_SAFE + NO_WRAP);
    }

    public static String getBase64DecodeString(String str, int flags) throws UnsupportedEncodingException {
        //有没有换行都可以正确解析
        if (str == null) {
            str = StringUtil.empty;
        }
        return new String(Base64.decode(str, flags), Environment.defaultEncode);
    }

    /**
     * @param str 字符串加密返回
     * @return base64编码
     */
    public static String getBase64EncodeString(String str) {
        return getBase64EncodeString(str, URL_SAFE + NO_WRAP);
    }


    /**
     * @param str   字符串加密返回
     * @param flags 类型
     * @return base64编码
     */
    public static String getBase64EncodeString(String str, int flags) {
        try {
            return getBase64Encode(str.getBytes(Environment.defaultEncode), flags);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }


    /**
     * et99 et299电子钥匙会用到
     *
     * @param userKey    密钥
     * @param randomData 随机数
     * @return 返回验证对比数据, 摘要
     * @throws Exception 异常
     */
    public static String getHmac_md5(String userKey, String randomData) throws Exception {
        if (userKey == null || randomData == null) {
            return StringUtil.empty;
        }
        HMAC_MD5 hm = new HMAC_MD5(userKey.getBytes(Environment.defaultEncode));
        hm.addData(randomData.getBytes(Environment.defaultEncode));
        hm.sign();
        return hm.toString();
    }

    /**
     * 替代 DigestUtils.shaHex(txt);
     *
     * @param src 数据
     * @return sha sha编码
     */
    public static String getSha(String src) {
        if (src == null) {
            return StringUtil.empty;
        }
        try {
            return getSha(src.getBytes(Environment.defaultEncode));
        } catch (Exception e) {
            return StringUtil.empty;
        }
    }

    /**
     * getSha 和 Sha1 是一样的
     *
     * @param src 数据
     * @return sha加密
     */
    public static String getSha(byte[] src) {
        if (src == null) {
            return StringUtil.empty;
        }
        return toHex(encrypt(src, "SHA"));
    }


    public static String toHex(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            if (((int) aByte & 0xff) < 0x10) {
                buffer.append("0");
            }
            buffer.append(Long.toString((int) aByte & 0xff, 16));
        }
        return buffer.toString();
    }

    public static String getSha256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(byte[] convert, String encrypt) {
        try {
            MessageDigest md = MessageDigest.getInstance(encrypt);
            return md.digest(convert);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    static public String getHashEncode(String txt, String signType) {
        if ("sha".equalsIgnoreCase(signType)) {
            return getSha(txt);
        }
        if ("Sha256".equalsIgnoreCase(signType)) {
            return getSha256(txt);
        }
        if ("md5".equalsIgnoreCase(signType)) {
            return getMd5(txt);
        }
        if ("sm3".equalsIgnoreCase(signType)) {
            return getSm3(txt);
        }
        return "";
    }

    /**
     * 整形转换成网络传输的字节流（字节数组）型数据
     *
     * @param num 一个整型数据
     * @return 4个字节的自己数组
     */
    public static byte[] intToBytes(int num) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (0xff & (num >> 0));
        bytes[1] = (byte) (0xff & (num >> 8));
        bytes[2] = (byte) (0xff & (num >> 16));
        bytes[3] = (byte) (0xff & (num >> 24));
        return bytes;
    }

    /**
     * 四个字节的字节数据转换成一个整形数据
     *
     * @param bytes 4个字节的字节数组
     * @return 一个整型数据
     */
    public static int byteToInt(byte[] bytes) {
        int num = 0;
        int temp;
        temp = (0x000000ff & (bytes[0])) << 0;
        num = num | temp;
        temp = (0x000000ff & (bytes[1])) << 8;
        num = num | temp;
        temp = (0x000000ff & (bytes[2])) << 16;
        num = num | temp;
        temp = (0x000000ff & (bytes[3])) << 24;
        num = num | temp;
        return num;
    }

    /**
     * 长整形转换成网络传输的字节流（字节数组）型数据
     *
     * @param num 一个长整型数据
     * @return 4个字节的自己数组
     */
    public static byte[] longToBytes(long num) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (0xff & (num >> (i * 8)));
        }
        return bytes;
    }

    /**
     * 大数字转换字节流（字节数组）型数据
     *
     * @param n 数据
     * @return 32位byte数据
     */
    public static byte[] byteConvert32Bytes(BigInteger n) {
        if (n == null) {
            return null;
        }
        byte[] tmp = null;
        if (n.toByteArray().length == 33) {
            tmp = new byte[32];
            System.arraycopy(n.toByteArray(), 1, tmp, 0, 32);
        } else if (n.toByteArray().length == 32) {
            tmp = n.toByteArray();
        } else {
            tmp = new byte[32];
            for (int i = 0; i < 32 - n.toByteArray().length; i++) {
                tmp[i] = 0;
            }
            System.arraycopy(n.toByteArray(), 0, tmp, 32 - n.toByteArray().length, n.toByteArray().length);
        }
        return tmp;
    }

    /**
     * 换字节流（字节数组）型数据转大数字
     *
     * @param b byte数据
     * @return 换字节流（字节数组）型数据转大数字
     */
    public static BigInteger byteConvertInteger(byte[] b) {
        if (b[0] < 0) {
            byte[] temp = new byte[b.length + 1];
            temp[0] = 0;
            System.arraycopy(b, 0, temp, 1, b.length);
            return new BigInteger(temp);
        }
        return new BigInteger(b);
    }

    /**
     * 打印十六进制字符串
     *
     * @param bytes 数据
     */
    public static void printHexString(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print("0x" + hex.toUpperCase() + ",");
        }
        System.out.println();
    }


    /**
     * Convert char transfer byte
     *
     * @param c char要转换的数据
     * @return byte类型
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 将十六进制字符转换成一个整数
     *
     * @param ch    十六进制char
     * @param index 十六进制字符在字符数组中的位置
     * @return 一个整数
     * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
     */
    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }


    /**
     * 数字字符串转ASCII码字符串
     *
     * @param content 字符串
     * @return ASCII字符串
     */
    public static String StringToAsciiString(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            String b = Integer.toHexString(c);
            result = result + b;
        }
        return result;
    }


    /**
     * 十六进制字符串装十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

    /**
     * 十六转二进制
     *
     * @param hex 十六进制字符串
     * @return 二进制字符串
     */
    public static String hexStringToBinary(String hex) {
        hex = hex.toUpperCase();
        StringBuilder result = new StringBuilder();
        int max = hex.length();
        for (int i = 0; i < max; i++) {
            char c = hex.charAt(i);
            switch (c) {
                case '0':
                    result.append("0000");
                    break;
                case '1':
                    result.append("0001");
                    break;
                case '2':
                    result.append("0010");
                    break;
                case '3':
                    result.append("0011");
                    break;
                case '4':
                    result.append("0100");
                    break;
                case '5':
                    result.append("0101");
                    break;
                case '6':
                    result.append("0110");
                    break;
                case '7':
                    result.append("0111");
                    break;
                case '8':
                    result.append("1000");
                    break;
                case '9':
                    result.append("1001");
                    break;
                case 'A':
                    result.append("1010");
                    break;
                case 'B':
                    result.append("1011");
                    break;
                case 'C':
                    result.append("1100");
                    break;
                case 'D':
                    result.append("1101");
                    break;
                case 'E':
                    result.append("1110");
                    break;
                case 'F':
                    result.append("1111");
                    break;
            }
        }
        return result.toString();
    }

    /**
     * ASCII码字符串转数字字符串
     *
     * @param content ASCII字符串
     * @return 字符串
     */
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);
            char b = (char) a;
            result += String.valueOf(b);
        }
        return result;
    }

    /**
     * 将十进制转换为指定长度的十六进制字符串
     *
     * @param algorism  int 十进制数字
     * @param maxLength int 转换后的十六进制字符串长度
     * @return String 转换后的十六进制字符串
     */
    public static String algorismToHexString(int algorism, int maxLength) {
        String result = "";
        result = Integer.toHexString(algorism);
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }
        return patchHexString(result.toUpperCase(), maxLength);
    }


    /**
     * 二进制字符串转十进制
     *
     * @param binary 二进制字符串
     * @return 十进制数值
     */
    public static int binaryToAlgorism(String binary) {
        int max = binary.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = binary.charAt(i - 1);
            int algorism = c - '0';
            result += Math.pow(2, max - i) * algorism;
        }
        return result;
    }

    /**
     * 十进制转换为十六进制字符串
     *
     * @param algorism int 十进制的数字
     * @return String 对应的十六进制字符串
     */
    public static String algorismToHexString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);
        if (result.length() % 2 == 1) {
            result = '0' + result;
        }
        return result.toUpperCase();
    }

    /**
     * HEX字符串前补0，主要用于长度位数不足。
     *
     * @param str       String 需要补充长度的十六进制字符串
     * @param maxLength int 补充后十六进制字符串的长度
     * @return 补充结果
     */
    public static String patchHexString(String str, int maxLength) {
        String temp = "";
        for (int i = 0; i < maxLength - str.length(); i++) {
            temp = '0' + temp;
        }
        str = (temp + str).substring(0, maxLength);
        return str;
    }

    /**
     * 将一个字符串转换为int
     *
     * @param s          String 要转换的字符串
     * @param defaultInt int 如果出现异常,默认返回的数字
     * @param radix      int 要转换的字符串是什么进制的,如16 8 10.
     * @return int 转换后的数字
     */
    public static int parseToInt(String s, int defaultInt, int radix) {
        int i = 0;
        try {
            i = Integer.parseInt(s, radix);
        } catch (NumberFormatException ex) {
            i = defaultInt;
        }
        return i;
    }

    /**
     * 将一个十进制形式的数字字符串转换为int
     *
     * @param s          String 要转换的字符串
     * @param defaultInt int 如果出现异常,默认返回的数字
     * @return int 转换后的数字
     */
    public static int parseToInt(String s, int defaultInt) {
        int i = 0;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            i = defaultInt;
        }
        return i;
    }


    /**
     * 十六进制串转化为byte数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexToByte(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;


    }


    /**
     * 字节数组转换为十六进制字符串
     *
     * @param src byte[] 需要转换的字节数组
     * @return String 十六进制字符串
     */
    public static String byteToHex(byte[] src) {

        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 字节数组转为普通字符串（ASCII对应的字符）
     *
     * @param bytearray byte[]
     * @return String
     */
    public static String byteToAscii(byte[] bytearray) {
        String result = "";
        char temp;
        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }


    /*
    截取字节数组
     */
    public static byte[] subByte(byte[] input, int startIndex, int length) {
        byte[] bt = new byte[length];
        for (int i = 0; i < length; i++) {
            bt[i] = input[i + startIndex];
        }
        return bt;
    }


    public static String encrypt(String str, String hashType) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashType);
            return toHex(md.digest(str.getBytes(Environment.defaultEncode)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return StringUtil.empty;
    }

    /**
     * 判断是否为十六进制字符串，避免解码错误
     *
     * @param str 字符串
     * @return 判断是否为十六进制字符串
     */
    public static boolean isHex(String str) {
        String validate = "(?i)[0-9a-f]+";
        return str.matches(validate);
    }


    /**
     *
     * @param str 数据
     * @return 判断是否为base64编码
     */
    public static boolean isBase64(String str) {

        if (StringUtil.isNull(str)) {
            return false;
        }
        if (str.length() % 4 != 0) {
            return false;
        }
        char[] strChars = str.toCharArray();
        for (char c : strChars) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == '+' || c == '/' || c == '=' || c == '_' | c == '-' | c == ' ')) {
                return false;
            }
        }
        return true;
    }


    /**
     * 得到存储的密钥，默认支持16进制和base64两种格式
     *
     * @param key 16进制或者base64
     * @return 得到加密密钥
     */
    public static byte[] getStoreToKey(String key)
    {
        if (EncryptUtil.isHex(key)) {
            return hexToByte(key);
        } else {
            return getBase64Decode(key, URL_SAFE + NO_WRAP);
        }
    }

    /**
     * 用于微信解密
     * @param content 正文
     * @param keyByte 密钥
     * @param ivByte iv
     * @return 解密数据
     * @throws Exception 异常
     */
    public static byte[] aesDecrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateAesIV(ivByte));// 初始化
        return cipher.doFinal(content);
    }

    private static AlgorithmParameters generateAesIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }

}