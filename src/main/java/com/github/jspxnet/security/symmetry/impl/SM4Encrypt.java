package com.github.jspxnet.security.symmetry.impl;

import com.github.jspxnet.enums.KeyFormatEnumType;
import com.github.jspxnet.security.symmetry.AbstractEncrypt;
import com.github.jspxnet.utils.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * SM4 加盟方式的密钥，密码必须是128位，
 * 16进制表示是32位
 * {@code
 * byte[] key = new byte[16];
 * new Random().nextBytes(key);
 * 生成128位
 * byte[] iv = new byte[16];
 * new Random().nextBytes(iv);
 * System.out.println(StringUtil.toHexString(key).toUpperCase());
 * }
 */
public class SM4Encrypt extends AbstractEncrypt {
    /**
     * SM4算法目前只支持128位（即密钥16字节）
     */
    private static final int DEFAULT_KEY_SIZE = 128;


    public SM4Encrypt() {

        /*
        public static final String SM4_ECB_PKCS7 = "SM4/ECB/PKCS7Padding";
        public static final String SM4_CBC_PKCS7 = "SM4/CBC/PKCS7Padding";
        */

        cipherIv = "00000000000000000000000000000000";
        cipherAlgorithm = "SM4/CBC/PKCS5Padding";
        algorithm = "SM4";
        keyFormatType = KeyFormatEnumType.BASE64;
    }

    /**
     * SM4加密函数
     * @param plainText 明文
     * @return 密文
     * @throws Exception 异常
     */
    @Override
    public byte[] getEncode(byte[] plainText) throws Exception {
        byte[] rawKey = getSecretKeyBytes();
        // 创建Cipher对象

        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        // 初始化为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(rawKey, algorithm), getCipherIV());
        // 加密数据
        return cipher.doFinal(plainText);

    }

    /**
     * SM4解密函数
     * @param classData 密文
     * @return 明文
     * @throws Exception 异常
     */
    @Override
    public byte[] getDecode(byte[] classData) throws Exception {
        byte[] rawKey = getSecretKeyBytes();
        // 创建Cipher对象

        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        // 初始化为加密模式
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(rawKey, algorithm), getCipherIV());
        // 解密数据
        return cipher.doFinal(classData);

    }


    /**
     * 生成密钥
     * <p>建议使用org.bouncycastle.util.encoders.Hex将二进制转成HEX字符串</p>
     *
     * @return 密钥16位
     * @throws Exception 生成密钥异常
     */
    public  String generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE, new SecureRandom());
        return Hex.toHexString(kg.generateKey().getEncoded()).toUpperCase();
    }



}
