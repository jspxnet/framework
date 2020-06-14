package com.github.jspxnet.security.asymmetric.impl;

import com.github.jspxnet.security.asymmetric.AbstractEncrypt;
import com.github.jspxnet.security.KeyPairGen;
import com.github.jspxnet.security.sm.SM2;
import com.github.jspxnet.utils.ArrayUtil;
import org.bouncycastle.asn1.*;

import java.nio.charset.StandardCharsets;

/**
 * SM2椭圆曲线公钥密码算法
 * SM2算法：SM2椭圆曲线公钥密码算法是我国自主设计的公钥密码算法，包括SM2-1椭圆曲线数字签名算法，SM2-2椭圆曲线密钥交换协议，
 * SM2-3椭圆曲线公钥加密算法，分别用于实现数字签名密钥协商和数据加密等功能。SM2算法与RSA算法不同的是，
 * SM2算法是基于椭圆曲线上点群离散对数难题，相对于RSA算法，256位的SM2密码强度已经比2048位的RSA密码强度要高。
 * <p>
 * 每种加密算法的保持格式会有不同SM2保存为Hex ,16进制字符串
 */
public class SM2Encrypt extends AbstractEncrypt {
    private String userId = "1234567812345678";
    private String IDA = "Heartbeats";
    private SM2 sm2 = new SM2();

    @Override
    public KeyPairGen getKeyPair() {
        KeyPairGen sm2KeyPair = sm2.generateKeyPair();
        return new KeyPairGen("SM2", sm2KeyPair.getPublicKey(), sm2KeyPair.getPrivateKey());
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 使用默认id计算
     *
     * @param sourceData 数据
     * @param publicKey  共密
     * @param signData   验证
     * @return 验证
     */
    @Override
    public boolean verify(byte[] sourceData, byte[] publicKey, byte[] signData) throws Exception {
        return true;
    }

    public boolean verify(byte[] publicKey, SM2.Signature signature) throws Exception {
        return sm2.verify(userId.getBytes(StandardCharsets.UTF_8), signature, IDA.getBytes(StandardCharsets.UTF_8), publicKey);
    }

    /**
     * @param sourceData 数据
     * @param privateKey 私密
     * @return 签名数据
     */
    @Override
    public byte[] sign(byte[] sourceData, byte[] privateKey) {
        return null;
    }


    public byte[] sign(byte[] userId, byte[] IDA, KeyPairGen keyPairGen) {
        if (keyPairGen != null) {
            return null;
        }
        try {
            SM2.Signature signature = sm2.sign(userId, IDA, keyPairGen);
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            ASN1Integer d_r = new ASN1Integer(signature.getR());
            ASN1Integer d_s = new ASN1Integer(signature.getS());
            v2.add(d_r);
            v2.add(d_s);
            DERSequence derObject = new DERSequence(v2);
            return derObject.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
        if (ArrayUtil.isEmpty(publicKey) || ArrayUtil.isEmpty(data)) {
            return null;
        }

        return null;
    }

    @Override
    public byte[] decryptByPrivateKey(byte[] encryptedData, byte[] privateKey) {
        if (ArrayUtil.isEmpty(encryptedData) || ArrayUtil.isEmpty(privateKey)) {
            return null;
        }

        sm2.decrypt(encryptedData, privateKey);
        return encryptedData;
    }

    /**
     * sm2没有实现
     *
     * @param data      加密数据
     * @param publicKey 公密
     * @return sm2没有实现
     */
    @Override
    public byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {

        return null;
    }

    /**
     * m2没有实现
     *
     * @param data       原文
     * @param privateKey 私密
     * @return 密文
     */
    @Override
    public byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {

        return sm2.encrypt(new String(data), null);
    }

}
