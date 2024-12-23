package com.github.jspxnet.security.asymmetric.impl;

import com.github.jspxnet.security.KeyPairGen;
import com.github.jspxnet.security.asymmetric.AbstractEncrypt;
import com.github.jspxnet.security.utils.RSACoder;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by ChenYuan on 2017/5/9.
 * RSA 为非对称加密，公密加密，
 * 使用 私密加密就要用公密解密，
 * 使用 公密加密就要使用私密解密
 * com.github.jspxnet.sdk.security.asymmetric.impl.RSAEncrypt
 * 每种加密算法的保持格式会有不同RSA保存为Base64 字符串
 */
public class RSAEncrypt extends AbstractEncrypt {
    /**
     * 加密算法RSA
     */
    public static final String ALGORITHM = "RSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public RSAEncrypt() {

    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding
     *
     * @return 生成密钥对(公钥和私钥)
     * @throws Exception 异常
     */
    @Override
    public KeyPairGen getKeyPair() throws Exception {
        return RSACoder.initKey();
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 用私钥对信息生成数字签名
     * @throws Exception 异常
     */
    @Override
    public byte[] sign(byte[] data, byte[] privateKey) throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(privateKey)) {
            return StringUtil.empty.getBytes();
        }
        return RSACoder.sign(data, privateKey);
    }

    /**
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return 校验数字签名
     * @throws Exception 异常
     */
    @Override
    public boolean verify(byte[] data, byte[] publicKey, byte[] sign)
            throws Exception {

        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(publicKey) || ArrayUtil.isEmpty(sign)) {
            return false;
        }
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(sign);
    }

    /**
     * 私钥解密
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 原文
     */
    @Override
    public byte[] decryptByPrivateKey(byte[] data, byte[] privateKey)
            throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(privateKey)) {
            return StringUtil.empty.getBytes();
        }
        return RSACoder.decryptByPrivateKey(data, privateKey);

    }

    /**
     * 公钥解密
     *
     * param data      已加密数据
     * param publicKey 公钥(BASE64编码)
     * return 原文
     */
    /*@Override
    public byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(publicKey)) {
            return StringUtil.empty.getBytes();
        }
        return RSACoder.decryptByPublicKey(data, publicKey);
    }*/

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return 密文
     */
    @Override
    public byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(publicKey)) {
            return StringUtil.empty.getBytes();
        }
        return RSACoder.encryptByPublicKey(data, publicKey);
    }



/*    public static void main(String[] args) throws Exception {
        RSAEncrypt encrypt = new RSAEncrypt();

        KeyPairGen keyPair = encrypt.getKeyPair();
        String source = "恭喜发财!当该用户发送文件时，用私钥签名";// 要加密的字符串

        byte[] jiaMi  = encrypt.encryptByPublicKey(source.getBytes(Environment.defaultEncode),keyPair.getPublicKey());
        //验证
        byte[] sign1 = encrypt.sign(jiaMi,keyPair.getPrivateKey());
        System.out.println("sign1：" +  EncryptUtil.getBase64Encode(sign1));
        System.out.println("verify1：" +  encrypt.verify(jiaMi,keyPair.getPublicKey(),sign1));
        System.out.println("公钥加密的字符串为：" + new String(jiaMi,Environment.defaultEncode) );
        byte[] jieMi  = encrypt.decryptByPrivateKey(jiaMi,keyPair.getPrivateKey());
        System.out.println("私密解密：" + new String(jieMi,Environment.defaultEncode));

    }*/

}
