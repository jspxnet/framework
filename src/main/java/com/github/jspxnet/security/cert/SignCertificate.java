/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.security.cert;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-7-26
 * Time: 22:29:20
 * <p>
 * 1.
 * keytool -genkey -alias jspx.net -keypass 79fada3a1762f29f80997d23b3304947 -keyalg SHA1WithRSA -keysize 1024 -validity 3650 -keystore d:\jspx.net.keystore
 *
 * <p>
 * 2、keystore信息的查看：
 * keytool -list  -v -keystore d:\jspx.net.keystore -storepass 123456
 * <p>
 * 3、证书的导出：
 * <p>
 * keytool -export -alias jspx.net -keystore d:\jspx.net.keystore -file d:\jspx.net.crt -storepass 123456
 * <p>
 * JKS的Provider是SUN 的格式
 */

import java.io.*;
import java.security.*;
import java.util.*;

import com.github.jspxnet.security.utils.EncryptUtil;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X500Name;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateAlgorithmId;

public class SignCertificate {


    // 你用来签名的算法
    private static final String SIG_ALG_NAME = "SHA1WithRSA";

    // 有效期
    private static final int VALIDITY = 365 * 10;

    /**
     * Usage: SignCertificate keystore CAAlias certToSignAlias newAlias
     * <p>
     * 本地测试
     *
     * @param args 无效
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {
        System.out.println(EncryptUtil.getMd5("jspx.net chenYuan 13511993665"));
        //keystore ca mykey mykey_signed
        String keystoreFile = "d:\\jspx.net.keystore";
        String caAlias = "jspx.net";
        String certToSignAlias = "jspx.net_signed";
        String newAlias = "new_jspx.netkey";


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Keystore password: ");
        char[] password = in.readLine().toCharArray();
        System.out.print("CA (" + caAlias + ") password: ");
        char[] caPassword = in.readLine().toCharArray();
        System.out.print("Cert (" + certToSignAlias + ") password: ");
        char[] certPassword = in.readLine().toCharArray();


        FileInputStream input = new FileInputStream(keystoreFile);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(input, password);
        input.close();

        // 得到CA的私钥来签名
        PrivateKey caPrivateKey = (PrivateKey) keyStore.getKey(caAlias, caPassword);
        // 得到CA的证书
        java.security.cert.Certificate caCert = keyStore.getCertificate(caAlias);
        // 创建 X509CertImpl 对象

        byte[] encoded = caCert.getEncoded();
        X509CertImpl caCertImpl = new X509CertImpl(encoded);
        X509CertInfo caCertInfo = (X509CertInfo) caCertImpl.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);

        X500Name issuer = (X500Name) caCertInfo.get(X509CertInfo.SUBJECT + "." + CertificateIssuerName.DN_NAME);

        //得到用CA签名的证书
        java.security.cert.Certificate cert = keyStore.getCertificate(certToSignAlias);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(certToSignAlias, certPassword);
        encoded = cert.getEncoded();
        X509CertImpl certImpl = new X509CertImpl(encoded);
        X509CertInfo certInfo = (X509CertInfo) certImpl.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);


        Date firstDate = new Date();
        Date lastDate = new Date(firstDate.getTime() + VALIDITY * 24 * 60 * 60 * 1000L);
        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        certInfo.set(X509CertInfo.VALIDITY, interval);

        // 序列号
        certInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber((int) (firstDate.getTime() / 1000)));

        // 发行者
        certInfo.set(X509CertInfo.ISSUER + "." + CertificateSubjectName.DN_NAME, issuer);

        AlgorithmId algorithm = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
        certInfo.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algorithm);
        X509CertImpl newCert = new X509CertImpl(certInfo);

        // 签名此证书
        newCert.sign(caPrivateKey, SIG_ALG_NAME);

        keyStore.setKeyEntry(newAlias, privateKey, certPassword, new java.security.cert.Certificate[]{newCert});

        // 保存在秘钥库种
        FileOutputStream output = new FileOutputStream(keystoreFile);
        keyStore.store(output, password);
        output.close();

    }
}