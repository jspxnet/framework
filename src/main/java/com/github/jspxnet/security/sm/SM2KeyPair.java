package com.github.jspxnet.security.sm;

import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;

/**
 * 说 明:SM2公私钥实体类
 */
public class SM2KeyPair {

    /**
     * 公钥
     */
    private ECPoint publicKey;

    /**
     * 私钥
     */
    private BigInteger privateKey;

    SM2KeyPair(ECPoint publicKey, BigInteger privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

}