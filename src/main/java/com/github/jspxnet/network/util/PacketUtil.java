/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.network.util;

import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.symmetry.SymmetryEncryptFactory;
import com.github.jspxnet.security.utils.EncryptUtil;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ZipUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * 这里不是socket的分包,而是比tcp通信包上一层的加密压缩包,是tcp包已经传输成功后组合出来的包,二次分析
 * 网络传输封包单元,加密,压缩
 * 先压缩,再加密,最后转换为编码对应字符串
 * [Zip-1][Encrypt-1][Charset-1][hash-4][第8位自定义]
 */
public class PacketUtil {
    public PacketUtil() {

    }

    public static final Character Charset_UTF_8 = '0';
    public static final Character Charset_UTF_16 = '1'; //Charset.forName("UTF-16");
    public static final Character Charset_UTF_32 = '2'; //Charset.forName("UTF-32");
    public static final Character Charset_GB2312 = '3'; //Charset.forName("GB2312");
    public static final Character Charset_GBK = '4'; //Charset.forName("GBK");
    public static final Character Charset_GB18030 = '5'; //Charset.forName("GB18030");
    public static final Character Charset_Big5 = '6'; //Charset.forName("Big5");
    public static final Character Charset_WINDOWS_1251 = '7'; //Charset.forName("WINDOWS-1251");
    public static final Character Charset_CP866 = '8'; //Charset.forName("CP866");

    public static final Character Charset_ISO_8859_1 = 'a';
    public static final Character Charset_ISO_8859_2 = 'b';
    public static final Character Charset_ISO_8859_3 = 'c';
    public static final Character Charset_ISO_8859_4 = 'd';
    public static final Character Charset_ISO_8859_5 = 'e';
    public static final Character Charset_ISO_8859_6 = 'f';
    public static final Character Charset_ISO_8859_7 = 'g';
    public static final Character Charset_ISO_8859_8 = 'h';
    public static final Character Charset_ISO_8859_9 = 'i';
    public static final Character Charset_ISO_8859_10 = 'j';
    public static final Character Charset_ISO_8859_11 = 'k';
    public static final Character Charset_ISO_8859_12 = 'l';
    public static final Character Charset_ISO_8859_13 = 'm';
    public static final Character Charset_ISO_8859_14 = 'n';
    public static final Character Charset_ISO_8859_15 = 'o';
    public static final Character Charset_ISO_8859_16 = 'p';
    public static final Character Charset_ISO_2022_KR = 'q';
    public static final Character Charset_ISO_2022_JP = 'x';
    public static final Character Charset_US_ASCII = 'y';


    public static final Map<Character, Charset> charsetMap = new HashMap<Character, Charset>();
    public static final Charset defaultCharset = StandardCharsets.UTF_8;

    static {
        charsetMap.put(Charset_UTF_8, defaultCharset);
        charsetMap.put(Charset_UTF_16, StandardCharsets.UTF_16);
        charsetMap.put(Charset_UTF_32, Charset.forName("UTF-32"));
        charsetMap.put(Charset_GB2312, Charset.forName("GB2312"));
        charsetMap.put(Charset_GBK, Charset.forName("GBK"));
        charsetMap.put(Charset_GB18030, Charset.forName("GB18030"));
        charsetMap.put(Charset_Big5, Charset.forName("Big5"));
        charsetMap.put(Charset_WINDOWS_1251, Charset.forName("WINDOWS-1251"));
        charsetMap.put(Charset_CP866, Charset.forName("CP866"));
        charsetMap.put(Charset_ISO_8859_1, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_2, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_3, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_4, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_5, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_6, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_7, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_8, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_9, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_10, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_11, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_12, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_13, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_14, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_15, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_8859_16, StandardCharsets.ISO_8859_1);
        charsetMap.put(Charset_ISO_2022_KR, Charset.forName("ISO-2022-KR"));
        charsetMap.put(Charset_ISO_2022_JP, Charset.forName("ISO-2022-JP"));
        charsetMap.put(Charset_US_ASCII, StandardCharsets.US_ASCII);
    }

    public static final Character NONE = '0';  //表示非压缩

    public static final Character ZIP = '1';  //zip压缩的

    /**
     传输密钥才使用非对称加密,其他情况统一使用对称加密
     包的头部格式定义:一共8位,先压缩,再加密,最后转换为编码
     [Zip-1][Encrypt-1][Charset-1][hash-4][第8位自定义]

     如果头为00X0000  就表示为普通的文本,不能满足条件的包丢弃
     */

    /**
     symmetryAlgorithm=com.github.jspxnet.sdk.security.symmetry.impl.AESEncrypt
     cipherAlgorithm

     AES/CBC/PKCS7Padding
     DES/CBC/PKCS5Padding

     cipherIv=1919369657728823
     */
    /**
     * secretKey
     * 校验算法,为了减少解密时候的错误发生,确保系统健壮行和安全性,采用MD5验证,
     * 但Md5有32位,我们起第一位 1,12,22,32一共4位来验证, 计算机从0开始,所有起数都-1
     * 验证密钥为登陆后服务器动态分发给客户
     * 格式为       4位[MD5(加密后文件+密钥)]
     * 0000 表示验证失败
     *
     * @param origin 来源
     * @return hash
     */
    public static String getHash(String origin) {
        String hash = EncryptUtil.getMd5(origin);
        if (hash.length() == 32) {
            return String.valueOf(hash.charAt(0)) + hash.charAt(11) + hash.charAt(21) + hash.charAt(31);
        }
        return "0000";
    }


    /**
     * 非加密打包,第一次连接方式用
     *
     * @param data 原文
     * @return 非加密打包
     */
    static public String getEncodePacket(String data) {
        return getEncodePacket(data, ' ', PacketUtil.Charset_UTF_8, PacketUtil.ZIP, SymmetryEncryptFactory.NONE, "");
    }

    /**
     * @param data      数据
     * @param secretKey key
     * @return 最简化
     */
    static public String getEncodePacket(String data, String secretKey) {
        return getEncodePacket(data, NONE, secretKey);
    }

    /**
     * 简化
     *
     * @param data      数据
     * @param exp       分割？
     * @param secretKey key
     * @return 简化
     */
    static public String getEncodePacket(String data, Character exp, String secretKey) {
        return getEncodePacket(data, exp, Charset_UTF_8, ZIP, SymmetryEncryptFactory.AES, secretKey);
    }
    //先压缩,再加密,最后转换为编码对应字符串
    //UTF-8、UTF-16、UTF-32编码方式_百度文库
    //AZB:AES ZIP Base64
    //AZH:AES ZIP HEX

    //RZB:RSA ZIP Base64

    //private byte encode;// 数据编码格式。已定义：0：UTF-8，1：GBK，2：GB2312，3：ISO8859-1
    // private byte encrypt;// 加密类型。0表示不加密

    /**
     * 完整版
     *
     * @param data         数据
     * @param exp          扩展
     * @param character    字符
     * @param zip          是否压缩
     * @param encryptClass 实体对象
     * @param secretKey    key
     * @return 解压
     */
    static public String getEncodePacket(String data, Character exp, Character character, Character zip, Character encryptClass, String secretKey)
    {
        if (data == null) {
            data = StringUtil.empty;
        }
        Charset charset = charsetMap.get(character);
        if (charset == null) {
            charset = defaultCharset;
        }
        byte[] byteData = data.getBytes(charset);
        //压缩
        if (ZIP.equals(zip) && byteData!=null) {
            byteData = ZipUtil.zip(data.getBytes(charset));
            zip = ZIP;
        } else {
            zip = NONE;
        }

        //加密
        if (SymmetryEncryptFactory.contains(encryptClass) && !StringUtil.isNull(secretKey)) {
            Encrypt encrypt = SymmetryEncryptFactory.createEncrypt(encryptClass);
            encrypt.setCipherIv(StringUtil.cut(secretKey, 16, ""));
            try {
                byteData = encrypt.getEncode(byteData);
            } catch (Exception e) {
                e.printStackTrace();
                encryptClass = NONE;
            }
        } else {
            encryptClass = NONE;
        }
        // [Zip-1][Encrypt-1][Charset-1][hash-4][第8位自定义]
        //传输的是base64
        String data64Data = EncryptUtil.getBase64Encode(byteData);
        String hash = getHash(data64Data);
        return String.valueOf(zip) + encryptClass + character + hash + exp + data64Data;
    }


    /**
     * 得到包的自定义字符
     *
     * @param data 数据
     * @return 得到包的自定义字符
     */
    static public Character getPacketExtended(String data) {
        if (data.length() > 8) {
            return data.charAt(7);
        }
        return NONE;
    }

    static public String getDecodePacket(String data, String secretKey) throws Exception {
        if (data == null || data.length() < 9) {
            return null;
        }
        Character zip = data.charAt(0);
        Character encryptClass = data.charAt(1);
        Character character = data.charAt(2);
        String dataHash = data.substring(3, 7);
        String dataBase = data.substring(8);

        //验证
        String hash = getHash(dataBase);
        if (!dataHash.equalsIgnoreCase(hash)) {
            throw new Exception("非法的数据格式");
        }

        byte[] byteData = EncryptUtil.getBase64Decode(dataBase);
        //解密
        if (!StringUtil.isNull(secretKey) && SymmetryEncryptFactory.contains(encryptClass)) {
            Encrypt encrypt = SymmetryEncryptFactory.createEncrypt(encryptClass);
            encrypt.setCipherIv(StringUtil.cut(secretKey, 16, ""));
            try {
                byteData = encrypt.getDecode(byteData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //解缩
        if (ZIP.equals(zip)&&byteData!=null) {
            byteData = ZipUtil.unZip(byteData);
        }

        Charset charset = charsetMap.get(character);
        if (charset==null)
        {
            charset = defaultCharset;
        }
        if (byteData==null)
        {
            return StringUtil.empty;
        }
        //  [Zip-1][Encrypt-1][Charset-1][hash-4][第8位自定义]
        //传输的是base64
        return new String(byteData, charset);
    }

}
