package com.github.jspxnet.security.asymmetric.impl;


import com.github.jspxnet.security.KeyPairGen;
import com.github.jspxnet.security.asymmetric.AbstractEncrypt;
import com.github.jspxnet.security.utils.RSACoder;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by ChenYuan on 2017/6/23.
 * DSA加密解密
 */
public class DSAEncrypt extends AbstractEncrypt {
    /**
     * 加密算法DSA
     */
    public static final String ALGORITHM = "DSA";


    public DSAEncrypt() {
        signAlgorithm = "SHA1withDSA";
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

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey priKey = factory.generatePrivate(keySpec);//生成 私钥

        //用私钥对信息进行数字签名
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initSign(priKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return 校验数字签名
     */
    @Override
    public boolean verify(byte[] data, byte[] publicKey, byte[] sign)
            throws Exception {

        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(publicKey) || ArrayUtil.isEmpty(sign)) {
            return false;
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initVerify(pubKey);
        signature.update(data);
        return signature.verify(sign); //验证签名
    }


    /**
     * 私钥解密
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return 解密数据
     */
    @Override
    public byte[] decryptByPrivateKey(byte[] data, byte[] privateKey)
            throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(privateKey)) {
            return StringUtil.empty.getBytes();
        }
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);

    }

    /**
     * 公钥解密
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return 解密数据
     */
    @Override
    public byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(publicKey)) {
            return StringUtil.empty.getBytes();
        }
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

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
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key pubKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return 密文
     */
    @Override
    public byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
        if (ArrayUtil.isEmpty(data) || ArrayUtil.isEmpty(privateKey)) {
            return StringUtil.empty.getBytes();
        }
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        return cipher.doFinal(data);
    }


}
