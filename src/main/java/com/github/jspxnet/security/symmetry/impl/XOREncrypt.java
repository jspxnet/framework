/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.security.symmetry.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.KeyFormatEnumType;
import com.github.jspxnet.security.symmetry.AbstractEncrypt;
import com.github.jspxnet.security.utils.EncryptUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-29
 * Time: 下午9:47
 */
public class XOREncrypt extends AbstractEncrypt {
    final static private byte[] FILE_HEAD = new byte[]{'x', 'o', 'r'};

    //随机数，会使加密的key和数据不一样,
    public XOREncrypt() {
        keyFormatType = KeyFormatEnumType.HEX;
    }

    public boolean isEncrypt(String source) throws Exception {
        if (!EncryptUtil.isHex(source)) {
            return false;
        }
        byte[] data = EncryptUtil.hexToByte(source);
        byte[] head = new byte[FILE_HEAD.length];
        if (data != null) {
            System.arraycopy(data, 0, head, 0, FILE_HEAD.length);
        }
        return new String(head).equals(new String(FILE_HEAD));
    }


    //加密
    @Override
    public byte[] getEncode(byte[] aSource) throws Exception {
        byte[] fSecretKey = secretKey.getBytes(Environment.defaultEncode);
        for (int i = 0; i < aSource.length; i++) {
            aSource[i] = (byte) (aSource[i] ^ fSecretKey[i % fSecretKey.length]);
        }
        byte[] data3 = new byte[FILE_HEAD.length + aSource.length];
        System.arraycopy(FILE_HEAD, 0, data3, 0, FILE_HEAD.length);
        System.arraycopy(aSource, 0, data3, FILE_HEAD.length, aSource.length);
        return data3;
    }

    /**
     * @param source 密文
     * @return 解密原文
     * @throws Exception 异常
     */
    @Override
    public byte[] getDecode(byte[] source) throws Exception {
        if (source == null) {
            return null;
        }
        byte[] head = new byte[FILE_HEAD.length];
        System.arraycopy(source, 0, head, 0, FILE_HEAD.length);
        if (!new String(head).equals(new String(FILE_HEAD)))
        {
            throw new Exception("文件标识错误");
        }
        byte[] aSource = new byte[source.length - FILE_HEAD.length];
        System.arraycopy(source, FILE_HEAD.length, aSource, 0, aSource.length);
        byte[] fSecretKey = secretKey.getBytes(Environment.defaultEncode);
        for (int i = 0; i < aSource.length; i++) {
            aSource[i] = (byte) (aSource[i] ^ fSecretKey[i % fSecretKey.length]);
        }
        return aSource;
    }


}