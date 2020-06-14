/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.impl.BaseConfigurationImpl;
import com.github.jspxnet.boot.environment.impl.EnvironmentTemplateImpl;
import com.github.jspxnet.boot.environment.impl.PlaceholderImpl;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.boot.environment.impl.SqlMapPlaceholderImpl;
import com.github.jspxnet.security.asymmetric.AsyEncrypt;
import com.github.jspxnet.security.asymmetric.impl.RSAEncrypt;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.symmetry.impl.SM4Encrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.factory.EntryFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-7
 * Time: 10:20:34
 */

public abstract class EnvFactory {

    private EnvFactory() {

    }

    /**
     * 最基本的配置，得到默认路径
     */
    static private JspxConfiguration jspxConfiguration = new BaseConfigurationImpl();

    public static JspxConfiguration getBaseConfiguration() {
        return jspxConfiguration;
    }


    /**
     * 提供载入文件名
     *
     * @param configuration 配置
     */
    public static void setConfiguration(JspxConfiguration configuration) {
        jspxConfiguration = configuration;
    }

    /**
     * SIoc 上下文
     */
    static private BeanFactory beanFactory = new EntryFactory();

    static public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    static private EnvironmentTemplate envTemplate = new EnvironmentTemplateImpl();

    /**
     * 环境变量和环境模版
     *
     * @return 得到环境变量
     */
    static public EnvironmentTemplate getEnvironmentTemplate() {
        return envTemplate;
    }

    final static private Placeholder PLACEHOLDER = new PlaceholderImpl();
    static public Placeholder getPlaceholder() {
        return PLACEHOLDER;
    }

    final static private Placeholder SQL_PLACEHOLDER = new SqlMapPlaceholderImpl();
    static public Placeholder getSqlPlaceholder() {
        return SQL_PLACEHOLDER;
    }

    static public String getHashAlgorithm() {
        return envTemplate.getString(Environment.hashAlgorithm, "Md5");
    }

    static public String getHashAlgorithmKey() {
        return envTemplate.getString(Environment.hashAlgorithmKey, StringUtil.empty);
    }

    static public String getSecretKey() {
        return envTemplate.getString(Environment.secretKey, "chenYuan");
    }



    //对称加密实例
    static private Encrypt symmetryEncrypt = null;

    /**
     *
     * @return 对称加密实例
     */
    static public Encrypt getSymmetryEncrypt() {
        if (symmetryEncrypt != null) {
            return symmetryEncrypt;
        }
        String encryptionAlgorithmClass = envTemplate.getString(Environment.symmetryAlgorithm, SM4Encrypt.class.getName());
        try {
            symmetryEncrypt = (Encrypt) ClassUtil.newInstance(encryptionAlgorithmClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String secretKey = envTemplate.getString(Environment.secretKey, Environment.defaultDrug);
        if (secretKey.length() > 16) {
            secretKey = StringUtil.cut(secretKey, 16, StringUtil.empty);
        }
        symmetryEncrypt.setSecretKey(secretKey);
        symmetryEncrypt.setCipherAlgorithm(envTemplate.getString(Environment.cipherAlgorithm));
        symmetryEncrypt.setCipherIv(envTemplate.getString(Environment.cipherIv));
        return symmetryEncrypt;
    }


    /**
     * 非对称加密算法
     */
    static private AsyEncrypt asymmetricEncrypt = null;

    /**
     *
     * @return 非对称加密算法
     */
    static public AsyEncrypt getAsymmetricEncrypt() {
        if (asymmetricEncrypt != null) {
            return asymmetricEncrypt;
        }
        String encryptionAlgorithmClass = envTemplate.getString(Environment.asymmetricAlgorithm, RSAEncrypt.class.getName());
        try {
            asymmetricEncrypt = (AsyEncrypt) ClassUtil.newInstance(encryptionAlgorithmClass);
        } catch (Exception e) {
            e.printStackTrace();
            return new RSAEncrypt();
        }
        return asymmetricEncrypt;
    }

    static byte[] privateKey = null;

    /**
     * 配置文件 jspx.properties
     *
     * @return 私密
     */
    static public byte[] getPrivateKey() {
        if (privateKey != null) {
            return privateKey;
        }
        String key = StringUtil.trim(envTemplate.getString(Environment.privateKey));
        return privateKey = EncryptUtil.getStoreToKey(key);
    }

    static byte[] publicKey = null;

    /**
     * 配置文件 jspx.properties
     *
     * @return 公密
     */
    static public byte[] getPublicKey() {
        if (publicKey != null) {
            return publicKey;
        }
        String key = StringUtil.trim(envTemplate.getString(Environment.publicKey));
        return publicKey = EncryptUtil.getStoreToKey(key);
    }

    /**
     * 为了方便得到配置的文件信息，这里提供一个方法函数，能够自动匹配文件路径
     *
     * @param loadFile 需要载入的文件名称，支持通配符
     * @return 得到文件，如果为空，表示没有找到文件
     */
    static public File getFile(String loadFile) {
        String[] findDirs = new String[]{envTemplate.getString(Environment.defaultPath), envTemplate.getString(Environment.templatePath), envTemplate.getString(Environment.resPath)};
        return FileUtil.scanFile(findDirs, loadFile);
    }


}