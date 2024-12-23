package com.github.jspxnet.security.symmetry.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.KeyFormatEnumType;
import com.github.jspxnet.security.symmetry.AbstractEncrypt;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.Provider;
import java.security.Security;

/**
 * Created by ChenYuan on 2017/5/9.
 * <p>
 * secretKey AES可以使用128、192、和256位密钥，并且用128位分组加密和解密数据，相对来说安全很多
 * AES 加密算法有很多种模式 AES/CBC/PKCS7Padding 模式相对标准,但默认JDK不支持,需要载入 bcprov-jdk15.jar   org.bouncycastle.jce.provider.BouncyCastleProvider
 * 默认,因为ios使用这种方式
 * <p>
 * //
 * //可以任意选择，为了方便后面与iOS端的加密解密，采用与其相同的模式与填充方式
 * //ECB模式只用密钥即可对数据进行加密解密，CBC模式需要添加一个参数iv
 * <p>
 * JDK默认支持方式  AES/CBC/ISO10126Padding
 */
@Slf4j
public class AESEncrypt extends AbstractEncrypt {

    //AES/CBC/ISO10126Padding
    //AES/PCBC/PKCS7Padding
    //随机数，会使加密的key和数据不一样,


    public AESEncrypt() {

        algorithm = "AES";
        cipherAlgorithm = "AES/CBC/PKCS7Padding";
        keyFormatType = KeyFormatEnumType.STRING;
        cipherIv = "0123456789ABCDEF";
        try {
            //判断log4j是否存在
            Security.addProvider((Provider) Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").newInstance());
        } catch (Exception e) {
            log.error("bcprov-jdk15.jar or use jdk1.8 not find,can not transfer use PKCS7Padding");
        }
    }


    /**
     * 加密
     *
     * @param classData 数据
     * @return 密文
     * @throws Exception 异常
     */
    @Override
    public byte[] getEncode(byte[] classData) throws Exception {

        byte[] rawKey = getSecretKeyBytes();
        SecretKey key = new SecretKeySpec(rawKey, algorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key, getCipherIV());
        return cipher.doFinal(classData);
    }

    /**
     * @param data 数据
     * @return 解密
     * @throws Exception 异常
     */
    @Override
    public byte[] getDecode(byte[] data) throws Exception {
        byte[] rawKey = getSecretKeyBytes();
        SecretKeySpec key = new SecretKeySpec(rawKey, algorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm); //"算法/模式/补码方式"
        cipher.init(Cipher.DECRYPT_MODE, key, getCipherIV());
        return cipher.doFinal(data);

    }

}
