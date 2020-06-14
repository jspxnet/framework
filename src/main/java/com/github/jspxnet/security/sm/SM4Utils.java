package com.github.jspxnet.security.sm;


/**
 * SM4分组密码算法是我国自主设计的分组对称密码算法，用于实现数据的加密/解密运算，以保证数据和信息的机密性。
 * 要保证一个对称密码算法的安全性的基本条件是其具备足够的密钥长度，SM4算法与AES算法具有相同的密钥长度分组长度128比特，因此在安全性上高于3DES算法。
 */

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.utils.EncryptUtil;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


public class SM4Utils {
    private String secretKey = "";

    private String iv = "";

    private boolean hexString = false;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public void setHexString(boolean hexString) {
        this.hexString = hexString;
    }

    public SM4Utils() {
    }

    public String encryptData_ECB(String plainText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.ENCRYPT;

            byte[] keyBytes;
            if (hexString) {
                keyBytes = EncryptUtil.getStoreToKey(secretKey);
            } else {
                keyBytes = secretKey.getBytes(Environment.defaultEncode);
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes(Environment.defaultEncode));
            String cipherText = EncryptUtil.getBase64Encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData_ECB(String cipherText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.DECRYPT;

            byte[] keyBytes;
            if (hexString) {
                keyBytes = EncryptUtil.getStoreToKey(secretKey);
            } else {
                keyBytes = secretKey.getBytes(Environment.defaultEncode);
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, EncryptUtil.getBase64Decode(cipherText));
            return new String(decrypted, Environment.defaultEncode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptData_CBC(String plainText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.ENCRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = EncryptUtil.hexToByte(secretKey);
                ivBytes = EncryptUtil.hexToByte(iv);
            } else {
                keyBytes = secretKey.getBytes(Environment.defaultEncode);
                ivBytes = iv.getBytes(Environment.defaultEncode);
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes(Environment.defaultEncode));
            String cipherText = EncryptUtil.getBase64Encode(encrypted);
            if (cipherText.trim().length() > 0) {
                Pattern p = compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData_CBC(String cipherText) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.DECRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = EncryptUtil.hexToByte(secretKey);
                ivBytes = EncryptUtil.hexToByte(iv);
            } else {
                keyBytes = secretKey.getBytes(Environment.defaultEncode);
                ivBytes = iv.getBytes(Environment.defaultEncode);
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, EncryptUtil.getBase64Decode(cipherText));
            return new String(decrypted, Environment.defaultEncode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        String plainText = "在密码学中，分组加密（英语：Block cipher），又称分块加密或块密码，是一种对称密钥算法。它将明文分成多个等长的模块（block），使用确定的算法和对称密钥对每组分别加密解密。分组加密是极其重要的加密协议组成，其中典型的如DES和AES作为美国政府核定的标准加密算法，应用领域从电子邮件加密到银行交易转帐，非常广泛。\n" +
                "国密即国家密码局认定的国产密码算法。主要有SM1，SM2，SM3，SM4。密钥长度和分组长度均为128位。\n" +
                "SM1为对称加密。其加密强度与AES相当。该算法不公开，调用该算法时，需要通过加密芯片的接口进行调用。\n" +
                "SM2为非对称加密，基于ECC。该算法已公开。由于该算法基于ECC，故其签名速度与秘钥生成速度都快于RSA。ECC 256位（SM2采用的就是ECC 256位的一种）安全强度比RSA 2048位高，但运算速度快于RSA。\n" +
                "SM3消息摘要。可以用MD5作为对比理解。该算法已公开。校验结果为256位。\n" +
                "SM4无线局域网标准的分组数据算法。对称加密，密钥长度和分组长度均为128位。";

        SM4Utils sm4 = new SM4Utils();

        sm4.secretKey = "JeF8U9wHFOMfs2Y8";
        sm4.hexString = false;

        System.out.println("ECB模式");
        String cipherText = sm4.encryptData_ECB(plainText);
        System.out.println("密文: " + cipherText);
        System.out.println();

        plainText = sm4.decryptData_ECB(cipherText);
        System.out.println("明文: " + plainText);
        System.out.println();

        System.out.println("CBC模式");
        sm4.iv = "UISwD9fW6cFh9SNS";
        cipherText = sm4.encryptData_CBC(plainText);
        System.out.println("密文: " + cipherText);
        System.out.println();

        plainText = sm4.decryptData_CBC(cipherText);
        System.out.println("明文: " + plainText);
    }
}
