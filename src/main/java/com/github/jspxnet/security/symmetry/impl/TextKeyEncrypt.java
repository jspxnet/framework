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

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-16
 * Time: 11:52:02
 * 自己写的一个加密方法,主要对文件加密保存
 * 使用明文密钥
 * 1.0
 * 不能使用字符串方式，必须是byte[] 方式
 */

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.symmetry.AbstractEncrypt;

import java.io.UnsupportedEncodingException;

public class TextKeyEncrypt extends AbstractEncrypt {

    public TextKeyEncrypt() {

    }

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 解密
     *
     * @param b 密文
     * @return byte[]  解密数据
     */
    @Override
    public byte[] getDecode(byte[] b) throws UnsupportedEncodingException {
        if (b == null || b.length == 0) {
            return "".getBytes();
        }
        byte[] key = secretKey.getBytes(Environment.defaultEncode);
        byte[] ins;
        ins = b;
        int keyIndex = key.length;
        int insIndex = ins.length;
        byte[] orgs = new byte[insIndex / 2];
        for (int i = 0; i < insIndex; i += 2) {
            int index = (i / 2) % keyIndex;
            int k = ins[i] - key[index];
            if (k < 0) {
                k = k + 256;
            }
            orgs[i / 2] = (byte) k;
        }
        return orgs;
    }

    /**
     * 加密
     *
     * @param b 要加密数据
     * @return byte[]  密文
     */
    @Override
    public byte[] getEncode(byte[] b) throws UnsupportedEncodingException {
        if (b == null || b.length == 0) {
            return "".getBytes();
        }
        byte[] key = secretKey.getBytes(Environment.defaultEncode);
        int keyIndex = key.length;
        int insIndex = b.length;
        byte[] outs = new byte[2 * insIndex];
        for (int i = 0; i < insIndex; i++) {
            int index = i % keyIndex;
            int k = b[i] + key[index];
            int d = (int) (255 * Math.random());
            if (k > 255) {
                k = k - 255;
            }
            outs[2 * i] = (byte) k;
            outs[2 * i + 1] = (byte) d;
        }
        return outs;
    }

}