/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.security.symmetry;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.utils.Base64;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-12-30
 * Time: 5:39:53
 */
public abstract class AbstractEncrypt implements Encrypt {
    //密钥规则 左边必须是大于F的字母

    protected String secretKey = Environment.defaultDrug;
    protected String algorithm = StringUtil.empty;
    //加解密算法/模式/填充方式
    protected String cipherAlgorithm = StringUtil.empty;
    protected String cipherIv = StringUtil.empty;

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void setCipherAlgorithm(String cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    @Override
    public String getAlgorithm()
    {
        return algorithm;
    }

    @Override
    public void setCipherIv(String cipherIv) {
        this.cipherIv = cipherIv;
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 已加密数据
     * @param key  私钥(BASE64编码)
     * @return 用私钥对信息生成数字签名
     */
    @Override
    public String sign(String data, String key)  {
        return EncryptUtil.getMd5(data + key);
    }

    /**
     * @param data 已加密数据
     * @param key  公钥(BASE64编码)
     * @param sign 数字签名
     * @return 校验数字签名
     */
    @Override
    public boolean verify(String data, String key, String sign) {
        return !(StringUtil.isNull(data) || StringUtil.isNull(key) || StringUtil.isNull(sign)) && sign.equals(sign(data, key));
    }

    /**
     * @param classData 数据
     * @return 部分加密为byte[] 后是不能转换为字符串的加密方式不能使用
     * @throws Exception 异常
     */
    @Override
    public String getEncode(String classData) throws Exception {

        return EncryptUtil.getBase64Encode(getEncode(classData.getBytes(Environment.defaultEncode)));
    }

    //生成iv
    public AlgorithmParameterSpec getCipherIV() throws Exception {

        if ("AES".equalsIgnoreCase(algorithm) && !StringUtil.hasLength(cipherIv)) {
            //iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
            cipherIv = StringUtil.cut(NumberUtil.LING_STRING, 16, "");
        } else if ("DES".equalsIgnoreCase(algorithm) && !StringUtil.hasLength(cipherIv)) {
            //iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
            cipherIv = StringUtil.cut(NumberUtil.LING_STRING, 8, "");
        }
        return new IvParameterSpec(cipherIv.getBytes(Environment.defaultEncode));
    }

    public byte[] getIvBytes() throws Exception {
        return cipherIv.getBytes(Environment.defaultEncode);
    }

    /**
     * @param data 数据
     * @return 部分加密为byte[] 后是不能转换为字符串的加密方式不能使用
     * @throws Exception 异常
     */
    @Override
    public String getDecode(String data) throws Exception
    {
        if (EncryptUtil.isHex(data))
        {
            return new String(getDecode(EncryptUtil.hexToByte(data)), Environment.defaultEncode);
        }
        return new String(getDecode(EncryptUtil.getBase64Decode(data)), Environment.defaultEncode);
    }

    @Override
    public String getBase64Decode(String classData) throws Exception {
        if (!EncryptUtil.isHex(classData)) {
            return StringUtil.empty;
        }
        return new String(getDecode(EncryptUtil.getBase64Decode(classData)), Environment.defaultEncode);
    }


    @Override
    public byte[] fileDecode(File fin) throws Exception {
        if (!fin.canRead()) {
            return null;
        }
        return getDecode(FileUtil.readFileByte(fin));
    }

    @Override
    public boolean fileEncode(File fin, File fOut) throws Exception {
        return fin.canWrite() && FileUtil.writeFile(fOut, getEncode(FileUtil.readFileByte(fin)));
    }

    /**
     * @param file 文件
     * @return 加密的byte  只适合 二进制文件
     * @throws Exception 异常
     */
    @Override
    public byte[] fileEncode(File file) throws Exception {
        if (!file.canRead()) {
            return null;
        }
        return getEncode(FileUtil.readFileByte(file));
    }

    @Override
    public boolean fileDecode(File fin, File fOut) throws Exception {
        return fin.canWrite() && FileUtil.writeFile(fOut, getDecode(FileUtil.readFileByte(fin)));
    }

/*
    public boolean saveSecretKey(File keyFilename) throws Exception
    {
        if (keyFilename == null) return false;
        //加密一下在保持

        return FileUtil.writeFile(keyFilename, secretKey.getBytes(Environment.defaultEncode));
    }
    public String readSecretKey(File keyFilename) throws Exception
    {
        if (keyFilename == null) return StringUtil.empty;
        return new String(FileUtil.readFileByte(keyFilename),Environment.defaultEncode);
    }
 */
}