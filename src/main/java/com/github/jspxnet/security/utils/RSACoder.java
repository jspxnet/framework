package com.github.jspxnet.security.utils;

/**
 * Created by ChenYuan on 2017/6/8.
 */

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.KeyPairGen;
import com.github.jspxnet.utils.StringUtil;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by lake on 17-4-12.
 */
public class RSACoder {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    public static final String split = "#";


    /**
     * 用私钥对信息生成数字签名
     *
     * @param data     加密数据
     * @param keyBytes 私钥
     * @return 签名
     * @throws Exception 异常
     */
    public static byte[] sign(byte[] data, byte[] keyBytes) throws Exception {

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception 异常
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] sign) throws Exception {
        // 解密由base64编码的公钥
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        // 验证签名是否正常
        return signature.verify(sign);
    }

    public static byte[] decryptByPrivateKey(byte[] data, byte[] keyBytes) throws Exception {

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }


    public static byte[] decryptByPrivateKeyLong(byte[] data, byte[] keyBytes)
            throws Exception {
        String str = new String(data, Environment.defaultEncode);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] lines = StringUtil.split(str, split);
        for (String line : lines) {
            out.write(decryptByPrivateKey(EncryptUtil.getBase64Decode(line), keyBytes));
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;

    }

    /**
     * 解密
     * 用公钥解密
     *
     * @param data     数据
     * @param keyBytes 公密
     * @return 原文
     * @throws Exception 异常
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] keyBytes)
            throws Exception {

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);

    }

    public static byte[] decryptByPublicKeyLong(byte[] data, byte[] keyBytes)
            throws Exception {

        String str = new String(data, Environment.defaultEncode);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] lines = StringUtil.split(str, split);
        for (String line : lines) {
            out.write(decryptByPublicKey(EncryptUtil.getBase64Decode(line), keyBytes));
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data     原文
     * @param keyBytes 公钥
     * @return 密文
     * @throws Exception 异常
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] keyBytes)
            throws Exception {
        if (data.length > 117) {
            return encryptByPublicKeyLong(data, keyBytes).getBytes(Environment.defaultEncode);
        }
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static String encryptByPublicKeyLong(byte[] data, byte[] keyBytes)
            throws Exception {

        StringBuilder out = new StringBuilder();
        int totalLen = data.length;
        int blockLength = 117;
        int totalPage = (totalLen - 1) / blockLength + 1;
        int pos = blockLength;
        for (int i = 0; i < totalPage; i++) {
            if (totalLen - i * blockLength < blockLength) {
                pos = totalLen - (i * blockLength);
            }
            byte[] block = new byte[pos];
            System.arraycopy(data, i * blockLength, block, 0, pos);
            out.append(EncryptUtil.getBase64Encode(encryptByPublicKey(block, keyBytes), EncryptUtil.NO_WRAP));
            out.append(split);
        }
        out.setLength(out.length() - 1);
        return out.toString();

    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data     数据
     * @param keyBytes 用私
     * @return 密文
     * @throws Exception 异常
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] keyBytes)
            throws Exception {
        if (data.length > 117) {
            return encryptByPrivateKeyLong(data, keyBytes).getBytes(Environment.defaultEncode);
        }
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);

    }

    public static String encryptByPrivateKeyLong(byte[] data, byte[] keyBytes)
            throws Exception {
        StringBuilder out = new StringBuilder();
        int totalLen = data.length;
        int blockLength = 117;
        int totalPage = (totalLen - 1) / blockLength + 1;
        int pos = blockLength;
        for (int i = 0; i < totalPage; i++) {
            if (totalLen - i * blockLength < blockLength) {
                pos = totalLen - (i * blockLength);
            }
            byte[] block = new byte[pos];
            System.arraycopy(data, i * blockLength, block, 0, pos);
            out.append(EncryptUtil.getBase64Encode(encryptByPrivateKey(block, keyBytes), EncryptUtil.NO_WRAP));
            out.append(split);
        }
        out.setLength(out.length() - 1);
        return out.toString();
    }


    /**
     * 初始化密钥
     *
     * @return 初始化密钥
     * @throws Exception 异常
     */
    public static KeyPairGen initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);

        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        return new KeyPairGen(keyPairGen.getAlgorithm(), keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
    }
}