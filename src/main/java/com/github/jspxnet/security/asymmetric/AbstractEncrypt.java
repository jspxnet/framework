package com.github.jspxnet.security.asymmetric;

import com.github.jspxnet.security.utils.EncryptUtil;

/**
 * Created by ChenYuan on 2017/5/9.
 */
public abstract class AbstractEncrypt implements AsyEncrypt {

    //SHA1WithRSA  MD5WithRSA
    protected String signAlgorithm = "MD5WithRSA";

    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到1024位之间
     */
    protected static final int KEY_SIZE = 1024;

    /**
     * @param signAlgorithm MD5withRSA SHA1WithRSA 签名算法
     */
    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    //字符串模式为16进制字符串

    public String sign(String data, String privateKey) throws Exception {
        return sign(data, privateKey, EncryptUtil.URL_SAFE + EncryptUtil.NO_WRAP);
    }


    public String sign(String data, String privateKey, int flag) throws Exception {
        return EncryptUtil.getBase64Encode(sign(EncryptUtil.getBase64Decode(data, flag), EncryptUtil.getBase64Decode(privateKey, flag)), flag);
    }

    public boolean verify(String data, String publicKey, String sign) throws Exception {
        return verify(EncryptUtil.getBase64Decode(data), EncryptUtil.getBase64Decode(publicKey), EncryptUtil.getBase64Decode(sign));
    }

    public String encryptByPublicKey(String data, String publicKey) throws Exception {
        return encryptByPublicKey(data, publicKey, EncryptUtil.URL_SAFE + EncryptUtil.NO_WRAP);
    }

    public String encryptByPublicKey(String data, String publicKey, int flag) throws Exception {
        return EncryptUtil.getBase64Encode(encryptByPublicKey(EncryptUtil.getBase64Decode(data, flag), EncryptUtil.getBase64Decode(publicKey, flag)), flag);
    }

    public String decryptByPrivateKey(String data, String privateKey) throws Exception {
        return decryptByPrivateKey(data, privateKey, EncryptUtil.URL_SAFE + EncryptUtil.NO_WRAP);
    }

    public String decryptByPrivateKey(String data, String privateKey, int flag) throws Exception {
        return EncryptUtil.getBase64Encode(decryptByPrivateKey(EncryptUtil.getBase64Decode(data, flag), EncryptUtil.getBase64Decode(privateKey, flag)), flag);
    }

}
