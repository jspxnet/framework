package com.github.jspxnet.security.symmetry.impl;

/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
   DES加密解密 1.0
  不能使用字符串方式，必须是byte[] 方式
  密码只能是8位
   PKCS5Padding 和 PKCS7Padding 基本一样
 */
import com.github.jspxnet.security.symmetry.AbstractEncrypt;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
@Slf4j
public class DESEncrypt extends AbstractEncrypt {
    //随机数，会使加密的key和数据不一样,
    public DESEncrypt() {
        algorithm = "DES";
        //设置  DESede  就是 3DES加密算法
        cipherIv = "12345678";
        cipherAlgorithm = "DES/CBC/PKCS5Padding";

    }

    /**
     * 加密
     *
     * @param classData 数据
     * @return 密文
     * @throws Exception 异常
     */
    @Override
    public byte[] getEncode(byte[] classData) throws Exception {
        byte[] rawKey = getSecretKeyBytes();
        SecretKey key = new SecretKeySpec(rawKey, algorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key, getCipherIV());
        return cipher.doFinal(classData);
    }

    /**
     * @param classData 密文
     * @return 解密
     * @throws Exception 异常
     */
    @Override
    public byte[] getDecode(byte[] classData) throws Exception {
        byte[] rawKey = getSecretKeyBytes();
        SecretKeySpec key = new SecretKeySpec(rawKey, algorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm); //"算法/模式/补码方式"
        cipher.init(Cipher.DECRYPT_MODE, key, getCipherIV());
        return cipher.doFinal(classData);
    }

}