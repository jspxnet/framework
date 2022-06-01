package com.github.jspxnet.security.symmetry;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.security.symmetry.impl.AESEncrypt;
import com.github.jspxnet.security.symmetry.impl.DESEncrypt;
import com.github.jspxnet.security.symmetry.impl.DESedeEncrypt;
import com.github.jspxnet.security.symmetry.impl.SM4Encrypt;
import com.github.jspxnet.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 修改添加如下配置 java.sdk.security
 * sdk.security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
 * 放入包
 * jce_policy-8.zip
 */
public class SymmetryEncryptFactory {
    public static EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
    static private String defaultCipherIv = envTemplate.getString(Environment.cipherIv);

    //对称加密算法
    public static final Character NONE = '0'; //非加密
    public static final Character AES = '1';
    public static final Character DES = '2';
    public static final Character DES3 = '3';
    public static final Character DESede = '4';
    public static final Character SM4 = '5';

    public static final Map<Character, Class> symmetryMap = new HashMap<Character, Class>();

    static {
        symmetryMap.put(AES, AESEncrypt.class);
        symmetryMap.put(DES, DESEncrypt.class);
        symmetryMap.put(DES3, DESedeEncrypt.class);
        symmetryMap.put(DESede, DESedeEncrypt.class);
        symmetryMap.put(SM4, SM4Encrypt.class);
    }


    /**
     * @param type 加密类型,看常量
     * @return 判断是否支持的加密类型
     */
    public static boolean contains(Character type) {
        return symmetryMap.containsKey(type);
    }

    /**
     * 简化版的，cipherIv  偏移量为系统默认配置，cipherAlgorithm为默认的配置，模式太多，这里主要为了简化调用方式
     *
     * @param type 加密类型,看常量
     * @return 实体
     */
    public static Encrypt createEncrypt(Character type) {
        return createEncrypt(type, null, defaultCipherIv);
    }

    /**
     * @param type            加密方式
     * @param cipherAlgorithm 算法模型
     * @param cipherIv        偏移量
     * @return 创建一个加密实体
     */
    public static Encrypt createEncrypt(Character type, String cipherAlgorithm, String cipherIv) {
        Class className = symmetryMap.get(type);
        if (className == null) {
            return null;
        }
        try {
            AbstractEncrypt encrypt = (AbstractEncrypt) ClassUtil.newInstance(className.getName());
            if (cipherAlgorithm != null && cipherAlgorithm.length() > 3) {
                encrypt.setCipherAlgorithm(cipherAlgorithm);
            }
            encrypt.setCipherIv(cipherIv);
            return encrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
