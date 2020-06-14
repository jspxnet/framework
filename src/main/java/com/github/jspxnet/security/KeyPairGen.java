package com.github.jspxnet.security;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by ChenYuan on 2017/5/12.
 */
public class KeyPairGen implements java.io.Serializable {
    private String algorithm = StringUtil.empty;
    private byte[] privateKey;
    private byte[] publicKey;

    public KeyPairGen(String algorithm, byte[] publicKey, byte[] privateKe) {
        this.algorithm = algorithm;
        this.publicKey = publicKey;
        this.privateKey = privateKe;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
