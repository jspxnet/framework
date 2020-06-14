package com.github.jspxnet.security.symmetry.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.sm.SM4_Context;
import com.github.jspxnet.security.symmetry.AbstractEncrypt;
import com.github.jspxnet.security.sm.SM4;

/**
 * Created by ChenYuan on 2017/4/27.
 */
public class SM4Encrypt extends AbstractEncrypt {

    private SM4 sm4 = new SM4();
    private SM4_Context ctx = new SM4_Context();

    public SM4Encrypt() {
        ctx.isPadding = true;
        algorithm = "SM4";
        setCipherAlgorithm("CBC"); //ECB
        setCipherIv("1234567890123456");
    }


    @Override
    public byte[] getEncode(byte[] plainText) throws Exception {
        ctx.mode = SM4.ENCRYPT;
        byte[] keyBytes = secretKey.getBytes(Environment.defaultEncode);
        byte[] ivBytes = getIvBytes();
        sm4.sm4_setkey_enc(ctx, keyBytes);
        if ("CBC".equalsIgnoreCase(cipherAlgorithm)) {
            return sm4.sm4_crypt_cbc(ctx, ivBytes, plainText);
        } else {
            return sm4.sm4_crypt_ecb(ctx, plainText);
        }
    }

    @Override
    public byte[] getDecode(byte[] classData) throws Exception {
        ctx.mode = SM4.DECRYPT;
        byte[] keyBytes = secretKey.getBytes(Environment.defaultEncode);
        byte[] ivBytes = getIvBytes();
        sm4.sm4_setkey_dec(ctx, keyBytes);
        if ("CBC".equalsIgnoreCase(cipherAlgorithm)) {
            return sm4.sm4_crypt_cbc(ctx, ivBytes, classData);
        } else {
            return sm4.sm4_crypt_ecb(ctx, classData);
        }
    }


}
