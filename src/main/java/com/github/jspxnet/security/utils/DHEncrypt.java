package com.github.jspxnet.security.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by ChenYuan on 2017/5/9.
 * DH 算法是怎么加密的呢？ 过程比较复杂，首先我们假设发送方是 A，接受方是 B。A 首先生成公钥和密钥，将公钥发送出去，B 接收到 A发送的公钥，然后利用该公钥生成自己的公钥和密钥，
 * 再将自己的公钥 发送给 A，这个时候 A 拥有了自己的公钥，密钥和 B 的公钥，B 拥有了自己的公钥密钥和 A 的公钥。
 * 之后， A 就可以使用 A自己的密钥 + B的公钥 获取到本地的密钥，B也是如此，这个时候 A 和 B 生成的本地密钥其实是相同的，
 * 这样的话也就变成了用相同的密钥加密，用相同的密钥解密。而且这样的话，我们数据传递过程中传递的是 A 和 B 的公钥，就算被黑客截取了也无济于事，
 * 他们不可能凭借着公钥将数据解密，从而保证了数据的安全性。
 */
public class DHEncrypt {

    //非对称密钥算法
    public static final String KEY_ALGORITHM = "DH";

    //本地密钥算法，即对称加密算法。可选des，aes，desede
    //public static final String SECRET_ALGORITHM = "DESEDE";
    // public static final String SECRET_ALGORITHM = "DES";


    protected String signAlgorithm = "SHA1WithRSA";

    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到1024位之间
     */
    protected static final int KEY_SIZE = 1024;

    /**
     * 签名算法
     * SHA1WithRSA
     * MD5withRSA
     *
     * @param signAlgorithm 签名算法
     */
    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    /**
     * @param keyPair 密钥对
     * @return 获取私钥
     */
    public static String getPrivateKey(KeyPair keyPair) {
        return EncryptUtil.getBase64Encode(keyPair.getPrivate().getEncoded(), EncryptUtil.DEFAULT);
    }

    /**
     * @param keyPair 密钥对
     * @return 获取公钥
     * @throws Exception 异常
     */
    public static String getPublicKey(KeyPair keyPair)
            throws Exception {
        return EncryptUtil.getBase64Encode(keyPair.getPublic().getEncoded(), EncryptUtil.DEFAULT);
    }

    public DHEncrypt() {
        this.signAlgorithm = "DESEDE";
    }

    /**
     * 初始化甲方密钥
     *
     * @return Map 甲方密钥的Map
     * @throws Exception 异常
     */
    public static KeyPair getKeyPair() throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 初始化乙方密钥
     *
     * @param key 甲方密钥（这个密钥是通过第三方途径传递的）
     * @return Map 乙方密钥的Map
     * @throws Exception 异常
     */
    public static KeyPair getKeyPair(byte[] key) throws Exception {
        //解析甲方的公钥
        //转换公钥的材料
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //由甲方的公钥构造乙方密钥
        DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
        //初始化密钥生成器
        keyPairGenerator.initialize(dhParamSpec);
        //产生密钥对
        return keyPairGenerator.genKeyPair();
        //乙方公钥
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     * @throws Exception 异常
     */
    public byte[] encrypt(byte[] data, byte[] key) throws Exception {
        //生成本地密钥
        SecretKey secretKey = new SecretKeySpec(key, signAlgorithm);
        //数据加密
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @throws Exception 异常
     */
    public byte[] decrypt(byte[] data, byte[] key) throws Exception {
        //生成本地密钥
        SecretKey secretKey = new SecretKeySpec(key, signAlgorithm);
        //数据解密
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 构建密钥
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return byte[] 本地密钥
     * @throws Exception 异常
     */
    public byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //初始化私钥
        //密钥材料转换
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        //产生私钥
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //实例化
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        //初始化
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);
        //生成本地密钥
        SecretKey secretKey = keyAgree.generateSecret(signAlgorithm);
        return secretKey.getEncoded();
    }

    /**
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {
        DHEncrypt encrypt = new DHEncrypt();
        //生成甲方的密钥对
        KeyPair keyPair = getKeyPair();
        //甲方的公钥

        String publicKey1 = DHEncrypt.getPublicKey(keyPair);

        //甲方的私钥
        String privateKey1 = DHEncrypt.getPrivateKey(keyPair);

        System.out.println("甲方公钥：" + publicKey1);
        System.out.println("甲方私钥：" + privateKey1);

        //由甲方的公钥产生的密钥对
        KeyPair keyPair2 = DHEncrypt.getKeyPair(EncryptUtil.getBase64Decode(publicKey1));
        String publicKey2 = DHEncrypt.getPublicKey(keyPair2);
        String privateKey2 = DHEncrypt.getPrivateKey(keyPair2);

        System.out.println("乙方公钥：" + publicKey2);
        System.out.println("乙方私钥：" + privateKey2);

        //组装甲方的本地加密密钥,由乙方的公钥和甲方的私钥组合而成
        byte[] key1 = encrypt.getSecretKey(EncryptUtil.getBase64Decode(publicKey2), EncryptUtil.getBase64Decode(privateKey1));
        System.out.println("甲方的本地密钥：" + EncryptUtil.getBase64Encode(key1, EncryptUtil.DEFAULT));

        //组装乙方的本地加密密钥，由甲方的公钥和乙方的私钥组合而成
        byte[] key2 = encrypt.getSecretKey(EncryptUtil.getBase64Decode(publicKey1), EncryptUtil.getBase64Decode(privateKey2));
        System.out.println("乙方的本地密钥：" + EncryptUtil.getBase64Encode(key2, EncryptUtil.DEFAULT));

        System.out.println("================密钥对构造完毕，开始进行加密数据的传输=============");
        String str = "密码交换算法，测试的要加密的数据";
        System.out.println("===========甲方向乙方发送加密数据==============");
        System.out.println("原文:" + str);
        System.out.println("===========使用甲方本地密钥对进行数据加密==============");
        //甲方进行数据的加密
        byte[] code1 = encrypt.encrypt(str.getBytes(), key1);
        System.out.println("加密后的数据：" + EncryptUtil.getBase64Encode(code1, EncryptUtil.DEFAULT));

        System.out.println("===========使用乙方本地密钥对数据进行解密==============");
        //乙方进行数据的解密
        byte[] decode1 = encrypt.decrypt(code1, key2);
        System.out.println("乙方解密后的数据：" + new String(decode1));

        System.out.println("===========反向进行操作，乙方向甲方发送数据==============");

        str = "乙方向甲方发送数据DH";

        System.out.println("原文:" + str);

        //使用乙方本地密钥对数据进行加密
        byte[] code2 = encrypt.encrypt(str.getBytes(), key2);
        System.out.println("===========使用乙方本地密钥对进行数据加密==============");
        System.out.println("加密后的数据：" + EncryptUtil.getBase64Encode(code2, EncryptUtil.DEFAULT));

        System.out.println("=============乙方将数据传送给甲方======================");
        System.out.println("===========使用甲方本地密钥对数据进行解密==============");

        //甲方使用本地密钥对数据进行解密
        byte[] decode2 = encrypt.decrypt(code2, key1);

        System.out.println("甲方解密后的数据：" + new String(decode2));
        //
        //加密后的数据：5SWL2xkjQ/DqFzOdcQjTKIZOhajH5NRtUv+dHwKRwbU=
        //加密后的数据：dAmBBGmvs0R0MrAUKkqvmj9dNthuTFTy5d8a+xorJtM=
    }
}
