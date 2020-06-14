package com.github.jspxnet.security.asymmetric;

import com.github.jspxnet.security.KeyPairGen;

/**
 * Created by ChenYuan on 2017/5/9.
 * 非对称加密接口
 */
public interface AsyEncrypt {
   /*
    String sign(byte[] data, String privateKey) throws Exception;
    boolean verify(byte[] data, String publicKey, String sign) throws Exception;
    byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)  throws Exception;
    byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception;
    byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception;
    byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception;
    */

    KeyPairGen getKeyPair() throws Exception;

    byte[] sign(byte[] data, byte[] privateKey) throws Exception;

    boolean verify(byte[] data, byte[] publicKey, byte[] sign) throws Exception;

    byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception;

    byte[] decryptByPrivateKey(byte[] encryptedData, byte[] privateKey) throws Exception;

    byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception;

    byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception;

    // byte[] encryptByPrivateKey(byte[] data, byte[] privateKey)throws Exception;

    //byte[] decryptByPublicKey(byte[] encryptedData, byte[] publicKey) throws Exception;

}
