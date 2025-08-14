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

import com.github.jspxnet.enums.KeyFormatEnumType;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-1-5
 * Time: 14:49:03
 */
public interface Encrypt {
    KeyFormatEnumType getKeyFormatType();

    void setKeyFormatType(KeyFormatEnumType keyFormatType);

    void setSecretKey(String secretKey);

    void setCipherAlgorithm(String cipherAlgorithm);

    String getAlgorithm();

    void setCipherIv(String cipherIv);

    String sign(String data, String key) throws Exception;

    boolean verify(String data, String key, String sign) throws Exception;

    boolean fileDecode(File fin, File fout) throws Exception;

    byte[] fileEncode(File fin) throws Exception;

    boolean fileEncode(File fin, File fout) throws Exception;

    byte[] fileDecode(File fin) throws Exception;

    //改密
    byte[] getDecode(byte[] classData) throws Exception;

    byte[] getEncode(byte[] b) throws Exception;

    String getEncode(String classData) throws Exception;

    String getDecode(String classData) throws Exception;

    String getBase64Decode(String classData) throws Exception;

}