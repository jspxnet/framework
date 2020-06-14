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
import com.github.jspxnet.security.symmetry.AbstractEncrypt;
import com.github.jspxnet.security.utils.EncryptUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-29
 * Time: 下午9:47
 */
public class XOREncrypt extends AbstractEncrypt {
    static private byte[] fileHead = new byte[]{'x', 'o', 'r'};

    //随机数，会使加密的key和数据不一样,
    public XOREncrypt() {


    }

    public boolean isEncrypt(String source) throws Exception {
        if (!EncryptUtil.isHex(source)) {
            return false;
        }
        byte[] data = EncryptUtil.hexToByte(source);
        byte[] head = new byte[fileHead.length];
        System.arraycopy(data, 0, head, 0, fileHead.length);
        return new String(head).equals(new String(fileHead));
    }


    //加密
    @Override
    public byte[] getEncode(byte[] aSource) throws Exception {
        byte[] fSecretKey = secretKey.getBytes(Environment.defaultEncode);
        for (int i = 0; i < aSource.length; i++) {
            aSource[i] = (byte) (aSource[i] ^ fSecretKey[i % fSecretKey.length]);
        }

        byte[] data3 = new byte[fileHead.length + aSource.length];
        System.arraycopy(fileHead, 0, data3, 0, fileHead.length);
        System.arraycopy(aSource, 0, data3, fileHead.length, aSource.length);

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
        byte[] head = new byte[fileHead.length];
        System.arraycopy(source, 0, head, 0, fileHead.length);
        byte[] aSource = new byte[source.length - fileHead.length];
        System.arraycopy(source, fileHead.length, aSource, 0, aSource.length);

        byte[] fSecretKey = secretKey.getBytes(Environment.defaultEncode);
        for (int i = 0; i < aSource.length; i++) {
            aSource[i] = (byte) (aSource[i] ^ fSecretKey[i % fSecretKey.length]);
        }

        return aSource;
    }

}