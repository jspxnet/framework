package sdk.security;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.AlgorithmId;

import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class X509CertTest {
    static private final String sigAlg = "MD5WithRSA";
    static private final String rootCAName = "jspxNetRoot";
    static private final String subjectCAName = "jspxNetCA";

    private SecureRandom secureRandom;

    public X509CertTest() {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 颁布证书
     *
     * @param issue
     * @param subject
     * @param issueAlias
     * @param issuePfxPath
     * @param issuePassword
     * @param issueCrtPath
     * @param subjectAlias
     * @param subjectPfxPath
     * @param subjectPassword
     * @param subjectCrtPath
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws SignatureException
     */

    public void createIssueCert(X500Name issue, X500Name subject,
                                String issueAlias, String issuePfxPath, String issuePassword,
                                String issueCrtPath, String subjectAlias, String subjectPfxPath,
                                String subjectPassword, String subjectCrtPath)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, CertificateException, IOException,
            KeyStoreException, UnrecoverableKeyException, SignatureException {

        CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", sigAlg, null);
        certAndKeyGen.setRandom(secureRandom);

        certAndKeyGen.generate(1024);

        // 1年
        long validity = 3650 * 24L * 60L * 60L;

        Date firstDate = new Date();

        Date lastDate;

        lastDate = new Date();

        lastDate.setTime(firstDate.getTime() + validity * 1000);

        CertificateValidity interval = new CertificateValidity(firstDate, lastDate);

        X509CertInfo info = new X509CertInfo();

        // Add all mandatory attributes

        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));

        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));

        AlgorithmId algID = AlgorithmId.get(sigAlg);

        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algID));

        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(subject));

        info.set(X509CertInfo.KEY, new CertificateX509Key(certAndKeyGen.getPublicKey()));

        info.set(X509CertInfo.VALIDITY, interval);

        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issue));

        PrivateKey privateKey = readPrivateKey(issueAlias, issuePfxPath, issuePassword);
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(privateKey, sigAlg);

        X509Certificate issueCertificate = readX509Certificate(issueCrtPath);

        X509Certificate[] X509Certificates = new X509Certificate[]{cert, issueCertificate};

        createKeyStore(subjectAlias, certAndKeyGen.getPrivateKey(),subjectPassword.toCharArray(), X509Certificates, subjectPfxPath);

        FileOutputStream fos = new FileOutputStream(new File(subjectCrtPath));
        fos.write(cert.getEncoded());
        fos.close();
    }

    /**
     * 创建根证书（证书有效期10年，私钥保存密码“123456”，公钥算法“RSA”，签名算法“MD5WithRSA”）
     *
     * @param issuePfxPath Personal Information Exchange 路径
     * @param issueCrtPath 证书路径
     * @param issue        颁发者&接收颁发者
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws CertificateException
     * @throws SignatureException
     * @throws KeyStoreException
     */

    public void createRootCert(String issuePfxPath, String issueCrtPath,
                               X500Name issue, String password) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, IOException,CertificateException, SignatureException, KeyStoreException
    {
        CertAndKeyGen rootCertAndKeyGen = new CertAndKeyGen("RSA", sigAlg, null);
        rootCertAndKeyGen.setRandom(secureRandom);
        rootCertAndKeyGen.generate(1024);
        X509Certificate rootCertificate = rootCertAndKeyGen.getSelfCertificate(issue, 3650 * 24L * 60L * 60L);
        X509Certificate[] X509Certificates = new X509Certificate[]{rootCertificate};

        createKeyStore(rootCAName, rootCertAndKeyGen.getPrivateKey(), password.toCharArray(), X509Certificates, issuePfxPath);
        FileOutputStream fos = new FileOutputStream(new File(issueCrtPath));
        fos.write(rootCertificate.getEncoded());
        fos.close();

    }

    /**
     * 证书私钥存储设施
     *
     * @param alias    KeyStore别名
     * @param key      密钥（这里是私钥）
     * @param password 保存密码
     * @param chain    证书链
     * @param filePath PFX文件路径
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */

    private void createKeyStore(String alias, Key key, char[] password,
                                Certificate[] chain, String filePath) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(null, password);
        keyStore.setKeyEntry(alias, key, password, chain);
        FileOutputStream fos = new FileOutputStream(filePath);
        keyStore.store(fos, password);
        fos.close();
    }

    /**
     * 读取PFX文件中的私钥
     *
     * @param alias    别名
     * @param pfxPath  PFX文件路径
     * @param password 密码
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableKeyException
     */

    public PrivateKey readPrivateKey(String alias, String pfxPath,
                                     String password) throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException,
            UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        FileInputStream fis = new FileInputStream(pfxPath);
        keyStore.load(fis, password.toCharArray());
        fis.close();
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

    /**
     * 读取X.509证书
     *
     * @param crtPath 证书路径
     * @return
     * @throws CertificateException
     * @throws IOException
     */

    public X509Certificate readX509Certificate(String crtPath)
            throws CertificateException, IOException {

        InputStream inStream = new FileInputStream(crtPath);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
        inStream.close();
        return cert;
    }

    public static void main(String args[]) throws IOException {

        // CN commonName 一般名字
        // L localityName 地方名
        // ST stateOrProvinceName 州省名
        // O organizationName 组织名
        // OU organizationalUnitName 组织单位名
        // C countryName 国家
        // STREET streetAddress 街道地址
        // DC domainComponent 领域
        // UID user id 用户ID

        X500Name issue = new X500Name("CN=192.168.0.200,OU=chenYuan,O=jspx.net,L=贵阳,ST=贵州,C=zh_CN");
        String issuePfxPath = "e://temp//root_ca.pfx";
        String issueCrtPath = "e://temp//root_ca.crt";

        String rootPassword = "456789";
        String subjectPassword = "456789";

        X509CertTest test = new X509CertTest();
        try {
            test.createRootCert(issuePfxPath, issueCrtPath, issue, rootPassword);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //发行
        String subjectPfxPath = "e://temp//issue_user.pfx";
        String subjectCrtPath = "e://temp//issue_user.crt";

        X500Name subject = new X500Name("CN=192.168.0.200,OU=chenYuan,O=jspx.net,L=贵阳,ST=贵州,C=CN");
        try {
            test.createIssueCert(issue, subject, rootCAName, issuePfxPath, rootPassword, issueCrtPath, subjectCAName,
                    subjectPfxPath, subjectPassword, subjectCrtPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //openssl pkcs12 -in root_ca.pfx -nodes -out root_ca.pem
        //openssl rsa -in root_ca.pem -out root_ca.key

    }

}