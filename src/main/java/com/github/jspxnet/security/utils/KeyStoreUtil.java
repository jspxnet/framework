/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.security.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-8-30
 * Time: 9:18:52
 */
public class KeyStoreUtil {
    private KeyStoreUtil() {

    }

    /**
     * 从PKCS12格式转换为保存到JKS格式，要先有jks文件
     *
     * @param srcFile String PKCS12格式的证书库
     * @param srcPass String PKCS12格式的证书库密码
     * @param desFile String JKS格式的证书库
     * @param desPass String  JKS格式的证书库密码
     */
    public static void PKCS12ToJKS(String srcFile, String srcPass, String desFile, String desPass) {
        try {
            KeyStore inputKeyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(srcFile);
            char[] srcPwd, destPwd;

            if ((srcPass == null) || "".equals(srcPass.trim())) {
                srcPwd = null;
            } else {
                srcPwd = srcPass.toCharArray();
            }

            if (!com.github.jspxnet.utils.StringUtil.hasLength(desPass)) {
                destPwd = null;
            } else {
                destPwd = desPass.toCharArray();
            }

            inputKeyStore.load(fis, srcPwd);
            fis.close();

            KeyStore outputKeyStore = KeyStore.getInstance("JKS");
            outputKeyStore.load(null, destPwd);
            Enumeration enumeration = inputKeyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String keyAlias = (String) enumeration.nextElement();
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, srcPwd);
                    Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, destPwd, certChain);
                }
            }

            FileOutputStream out = new FileOutputStream(desFile);
            outputKeyStore.store(out, destPwd);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 从JKS格式转换为PKCS12格式
     * 方便导入到usbkey
     *
     * @param srcFile String JKS格式证书库
     * @param srcPass String JKS格式证书库密码
     * @param desFile String PKCS12格式证书库,后缀 pfx
     * @param desPass String PKCS12格式证书库密码
     */
    public static void JSKToPfx(String srcFile, String srcPass, String desFile, String desPass) {
        try {
            KeyStore inputKeyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(srcFile);
            char[] srcPwd, destPwd;

            if ((srcPass == null) || "".equals(srcPass.trim())) {
                srcPwd = null;
            } else {
                srcPwd = srcPass.toCharArray();
            }
            if ((desPass == null) || "".equals(desPass.trim())) {
                destPwd = null;
            } else {
                destPwd = desPass.toCharArray();
            }

            inputKeyStore.load(fis, srcPwd);
            fis.close();

            KeyStore outputKeyStore = KeyStore.getInstance("PKCS12");
            Enumeration enumeration = inputKeyStore.aliases();
            while (enumeration.hasMoreElements()) {
                String keyAlias = (String) enumeration.nextElement();
                System.out.println("alias=[" + keyAlias + "]");

                outputKeyStore.load(null, destPwd);
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, srcPwd);
                    Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, destPwd, certChain);
                }

                FileOutputStream out = new FileOutputStream(desFile);
                outputKeyStore.store(out, destPwd);
                out.close();
                outputKeyStore.deleteEntry(keyAlias);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * PFX证书转换为JKS(Java Key IStore)
     *
     * @param pfxPassword PFX证书密码
     * @param pfxFilePath PFX证书路径
     * @param jksPassword JKS证书密码
     * @param jksFilePath JKS证书路径
     */
    public static void covertPFXtoJKS(String pfxPassword, String pfxFilePath,
                                      String jksPassword, String jksFilePath) {
        FileInputStream fis = null;
        FileOutputStream out = null;
        try {
            // 加载PFX证书
            KeyStore inputKeyStore = KeyStore.getInstance("PKCS12");
            fis = new FileInputStream(pfxFilePath);
            char[] inPassword = pfxPassword == null ? null : pfxPassword
                    .toCharArray();
            char[] outPassword = jksPassword == null ? null : jksPassword
                    .toCharArray();
            inputKeyStore.load(fis, inPassword);

            KeyStore outputKeyStore = KeyStore.getInstance("JKS");
            outputKeyStore.load(null, outPassword);
            Enumeration<String> enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) {
                String keyAlias = enums.nextElement();
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, inPassword);
                    Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key,
                            pfxPassword.toCharArray(), certChain);
                }
            }
            out = new FileOutputStream(jksFilePath);
            outputKeyStore.store(out, outPassword);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从JKS格式转换为PKCS12格式
     *
     * @param jksFilePath   String JKS格式证书库路径
     * @param jksPasswd     String JKS格式证书库密码
     * @param pfxFolderPath String PKCS12格式证书库保存文件夹
     * @param pfxPasswd     String PKCS12格式证书库密码
     * @throws Throwable 异常
     */
    public void covertJSKToPFX(String jksFilePath, String jksPasswd,
                               String pfxFolderPath, String pfxPasswd) throws Throwable {
        FileInputStream fis = null;
        try {
            KeyStore inputKeyStore = KeyStore.getInstance("JKS");
            fis = new FileInputStream(jksFilePath);
            char[] srcPwd = jksPasswd == null ? null : jksPasswd.toCharArray();
            char[] destPwd = pfxPasswd == null ? null : pfxPasswd.toCharArray();
            inputKeyStore.load(fis, srcPwd);

            KeyStore outputKeyStore = KeyStore.getInstance("PKCS12");
            Enumeration<String> enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) {
                String keyAlias = enums.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
                outputKeyStore.load(null, destPwd);
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, srcPwd);
                    java.security.cert.Certificate[] certChain = inputKeyStore
                            .getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, destPwd,
                            certChain);
                }
                String fName = pfxFolderPath + "_" + keyAlias + ".pfx";
                FileOutputStream out = new FileOutputStream(fName);
                outputKeyStore.store(out, destPwd);
                out.close();
                outputKeyStore.deleteEntry(keyAlias);
            }
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从BKS格式转换为PKCS12格式
     *
     * @param jksFilePath   String JKS格式证书库路径
     * @param jksPasswd     String JKS格式证书库密码
     * @param pfxFolderPath String PKCS12格式证书库保存文件夹
     * @param pfxPasswd     String PKCS12格式证书库密码
     * @throws Throwable 异常
     */
    public void covertBKSToPFX(String jksFilePath, String jksPasswd,
                               String pfxFolderPath, String pfxPasswd) throws Throwable {
        FileInputStream fis = null;
        try {
            KeyStore inputKeyStore = KeyStore.getInstance("BKS",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            fis = new FileInputStream(jksFilePath);
            char[] srcPwd = jksPasswd == null ? null : jksPasswd.toCharArray();
            char[] destPwd = pfxPasswd == null ? null : pfxPasswd.toCharArray();
            inputKeyStore.load(fis, srcPwd);

            KeyStore outputKeyStore = KeyStore.getInstance("PKCS12");
            Enumeration<String> enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) {
                String keyAlias = enums.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
                outputKeyStore.load(null, destPwd);
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, srcPwd);
                    java.security.cert.Certificate[] certChain = inputKeyStore
                            .getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, destPwd,
                            certChain);
                }
                String fName = pfxFolderPath + "_" + keyAlias + ".pfx";
                FileOutputStream out = new FileOutputStream(fName);
                outputKeyStore.store(out, destPwd);
                out.close();
                outputKeyStore.deleteEntry(keyAlias);
            }
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 列出JKS库内所有X509证书的属性
     *
     * @param jksFilePath 证书库路径
     * @param jksPasswd   证书库密码
     * @param algName     库类型
     */
    public static void listAllCerts(String jksFilePath, String jksPasswd,
                                    String algName) {
        try {
            char[] srcPwd = jksPasswd == null ? null : jksPasswd.toCharArray();
            FileInputStream in = new FileInputStream(jksFilePath);
            KeyStore ks = KeyStore.getInstance(algName);
            ks.load(in, srcPwd);
            Enumeration<String> e = ks.aliases();
            while (e.hasMoreElements()) {
                String alias = e.nextElement();
                java.security.cert.Certificate cert = ks.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    X509Certificate X509Cert = (X509Certificate) cert;
                    System.out.println("*********************************");
                    System.out.println("版本号:" + X509Cert.getVersion());
                    System.out.println("序列号:" + X509Cert.getSerialNumber().toString(16));
                    System.out.println("主体名：" + X509Cert.getSubjectDN());
                    System.out.println("签发者：" + X509Cert.getIssuerDN());
                    System.out.println("有效期：" + X509Cert.getNotBefore());
                    System.out.println("签名算法：" + X509Cert.getSigAlgName());
                    System.out.println("输出证书信息:\n" + X509Cert.toString());
                    System.out.println("**************************************");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 列出BKS库内所有X509证书的属性
     * @param jksFilePath 证书库路径
     * @param jksPasswd 证书库密码
     * @param algName 库类型
     */
    public static void listAllCertsBks(String jksFilePath, String jksPasswd,
                                       String algName) {
        try {
            char[] srcPwd = jksPasswd == null ? null : jksPasswd.toCharArray();
            FileInputStream in = new FileInputStream(jksFilePath);
            KeyStore ks = KeyStore.getInstance(algName,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            ks.load(in, srcPwd);
            Enumeration<String> e = ks.aliases();
            while (e.hasMoreElements()) {
                String alias = e.nextElement();
                java.security.cert.Certificate cert = ks.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    X509Certificate X509Cert = (X509Certificate) cert;
                    System.out.println("*********************************");
                    System.out.println("版本号:" + X509Cert.getVersion());
                    System.out.println("序列号:" + X509Cert.getSerialNumber().toString(16));
                    System.out.println("主体名：" + X509Cert.getSubjectDN());
                    System.out.println("签发者：" + X509Cert.getIssuerDN());
                    System.out.println("有效期：" + X509Cert.getNotBefore());
                    System.out.println("签名算法：" + X509Cert.getSigAlgName());
                    System.out.println("输出证书信息:\n" + X509Cert.toString());
                    System.out.println("**************************************");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}