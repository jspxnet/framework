package sdk.security;




import com.github.jspxnet.utils.DateUtil;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.security.auth.x500.X500PrivateCredential;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

/**
 * Created by yuan on 2014/9/6 0006.
 *
 */
public class CreateKeyStore {
    static {
        Security.addProvider(new BouncyCastleProvider());

        // CN commonName 一般名字
        // L localityName 地方名
        // ST stateOrProvinceName 州省名
        // O organizationName 组织名
        // OU organizationalUnitName 组织单位名
        // C countryName 国家
        // STREET streetAddress 街道地址
        // DC domainComponent 领域
        // UID user id 用户ID

        Hashtable attrs = new Hashtable();
        Vector order = new Vector();
        attrs.put(X509Name.C, "CN");    //country code
        attrs.put(X509Name.ST, "贵州");   //province name
        attrs.put(X509Name.L, "贵阳");    //locality name
        attrs.put(X509Name.O, "jspx.net"); //organization
        attrs.put(X509Name.OU, "jspx.net");    //organizational unit name
        attrs.put(X509Name.CN, "192.168.0.200");   //common name
        attrs.put(X509Name.E, "jspxnet@qq.com");
        //attrs.put(X509Name.DC, "jspxnet@qq.com");
        order.add(X509Name.C);
        order.add(X509Name.ST);
        order.add(X509Name.L);
        order.add(X509Name.O);
        order.add(X509Name.OU);
        order.add(X509Name.CN);
        order.add(X509Name.E);
        issuerDN = new X509Name(order, attrs);

    }
    static X509Name issuerDN;

    public static char[] keyPassword = "456789".toCharArray();
    /**
     * Create a random 1024 bit RSA key pair
     * 创建随机1024位密码
     */
    public static KeyPair generateRSAKeyPair()
            throws Exception
    {
        KeyPairGenerator  kpGen = KeyPairGenerator.getInstance("RSA","BC");
        kpGen.initialize(1024, new SecureRandom());
        return kpGen.generateKeyPair();
    }

    private static final long VALIDITY_PERIOD = DateUtil.YEAR * 10;

    public static String ROOT_ALIAS = "root.jspx.net";
    public static String INTERMEDIATE_ALIAS = "app.jspx.net";
    public static String END_ENTITY_ALIAS = "user.jspx.net";

    /**
     * Generate a sample V3 certificate to use as an intermediate CA certificate
     */
    public static X509Certificate generateIntermediateCert(PublicKey intKey, PrivateKey caKey, X509Certificate caCert)
            throws Exception
    {
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(1));
        certGen.setIssuerDN(caCert.getSubjectX500Principal());
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
        certGen.setSubjectDN(issuerDN);
        certGen.setPublicKey(intKey);
        certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));

        //certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(intKey));
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(0));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

        return certGen.generateX509Certificate(caKey);
    }

    /**
     * Generate a X500PrivateCredential for the intermediate entity.
     */
    public static X500PrivateCredential createIntermediateCredential(
            PrivateKey      caKey,
            X509Certificate caCert)
            throws Exception
    {
        KeyPair         interPair = generateRSAKeyPair();
        X509Certificate interCert = generateIntermediateCert(interPair.getPublic(), caKey, caCert);
        return new X500PrivateCredential(interCert, interPair.getPrivate(), INTERMEDIATE_ALIAS);
    }


    //-------------------------------------
    /**
     * Generate a X500PrivateCredential for the end entity.
     */
    public static X500PrivateCredential createEndEntityCredential(
            PrivateKey      caKey,
            X509Certificate caCert)
            throws Exception
    {
        KeyPair         endPair = generateRSAKeyPair();
        X509Certificate endCert = generateEndEntityCert(endPair.getPublic(), caKey, caCert);
        return new X500PrivateCredential(endCert, endPair.getPrivate(), END_ENTITY_ALIAS);
    }

    /**
     * Generate a sample V3 certificate to use as an end entity certificate
     */
    public static X509Certificate generateEndEntityCert(PublicKey entityKey, PrivateKey caKey, X509Certificate caCert)
            throws Exception
    {
        X509V3CertificateGenerator  certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(1));
        certGen.setIssuerDN(caCert.getSubjectX500Principal());
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
        certGen.setSubjectDN(issuerDN);
        certGen.setPublicKey(entityKey);
        certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));

       // certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(entityKey));
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

        return certGen.generateX509Certificate(caKey);
    }

    public static KeyStore createKeyStore()
            throws Exception
    {
        KeyStore store = KeyStore.getInstance("JKS");
        // initialize
        store.load(null, null);

        /**
         * 创建root 证书 v3
         */
        //根证书创建
        KeyPair rootPair = generateRSAKeyPair();

        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(BigInteger.valueOf(1));
        //发布正式

        certGen.setIssuerDN(issuerDN);
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
        //子证书
        certGen.setSubjectDN(issuerDN);
        certGen.setPublicKey(rootPair.getPublic());

        certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
        //X509Certificate rootCert =  certGen.generateX509Certificate(rootPair.getPrivate());
        X509Certificate rootCert =  certGen.generate(rootPair.getPrivate());

        X500PrivateCredential  rootCredential = new X500PrivateCredential(rootCert, rootPair.getPrivate(), ROOT_ALIAS);
        X500PrivateCredential    interCredential = createIntermediateCredential(rootCredential.getPrivateKey(), rootCredential.getCertificate());
        X500PrivateCredential    endCredential = createEndEntityCredential(interCredential.getPrivateKey(), interCredential.getCertificate());

        Certificate[]  chain = new Certificate[3];
        chain[0] = rootCredential.getCertificate();
        chain[1] = interCredential.getCertificate();
        chain[2] = endCredential.getCertificate();

        // set the entries
        store.setCertificateEntry(rootCredential.getAlias(), rootCredential.getCertificate());
        store.setCertificateEntry(interCredential.getAlias(), interCredential.getCertificate());
        store.setKeyEntry(endCredential.getAlias(), endCredential.getPrivateKey(), keyPassword, chain);
        return store;
    }


    public static void sig(String KeystoreAlias,byte[] sigText, String outFileName,String  KeyPassword,String KeyStorePath){
        char[] kpass;
        int i;
        try{
            KeyStore ks = KeyStore.getInstance("JKS","BC");
            FileInputStream ksfis = new FileInputStream(KeyStorePath);
            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
            kpass=new char[KeyPassword.length()];
            for(i=0;i<KeyPassword.length();i++)
                kpass[i]=KeyPassword.charAt(i);
            ks.load(ksbufin, kpass);
            PrivateKey priv = (PrivateKey) ks.getKey(KeystoreAlias,kpass );
            Signature rsa=Signature.getInstance("MD5withRSA");
            rsa.initSign(priv);
            rsa.update(sigText);
            byte[] sig=rsa.sign();
            System.out.println("sig is done");
            try{
                FileOutputStream out=new FileOutputStream(outFileName);
                out.write(sig);
                out.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    static byte[] desKeyData = { (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04,
            (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08 };
    public static void crypt(byte[] cipherText,String outFileName){
        try{
            DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            Cipher cdes = Cipher.getInstance("DES");
            cdes.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] ct = cdes.doFinal(cipherText);
            try{
                FileOutputStream out=new FileOutputStream(outFileName);
                out.write(ct);
                out.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String keystoreFile = "e:\\temp\\server.keystore";
        try {
            KeyStore store = createKeyStore();
            char[] storePassword = "456789".toCharArray();
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            // save the store
            store.store(bOut, storePassword);
            // reload from scratch
            store = KeyStore.getInstance("JKS");

            store.load(new ByteArrayInputStream(bOut.toByteArray()), null);
            store.store(new FileOutputStream(keystoreFile), storePassword);

            Enumeration<String> enums = store.aliases();
            while (enums.hasMoreElements())
            {
                String keyAlias = enums.nextElement();
                if (!store.isCertificateEntry(keyAlias)) continue;

                Certificate certificate = store.getCertificate(keyAlias);
                KeyStore pkcsStore = KeyStore.getInstance("PKCS12");
                pkcsStore.load(null, null);
                pkcsStore.setCertificateEntry(keyAlias,certificate);
                pkcsStore.store(new FileOutputStream("e:\\temp\\" + keyAlias+ ".pfx"),storePassword);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //keytool -list -keystore  e:\temp\server.keystore -storepass 456789
    //keytool -export -v -alias root.jspx.net -keystore e:\temp\server.keystore -storepass 456789 -rfc -file e:\temp\server.cer
    //keytool -export -v -alias user.jspx.net -keystore e:\temp\server.keystore -storepass 456789 -rfc -file e:\temp\client.cer
    //keytool -export -v -alias user.jspx.net -keystore  e:\temp\client.p12 -storetype PKCS12 -storepass 456789 -rfc -file e:\temp\client.cer

    //keytool -export -v -alias user.jspx.net -keystore  e:\temp\server.keystore -storetype PKCS12 -storepass 123456 -rfc -file e:\temp\client.cer

    /**
     * 列出x509Certificate的基本信息
     *
     * @param t
     */
   static private void printX509Certificate(X509Certificate t) {
        System.out.println(t);
        System.out.println("输出证书信息:\n" + t.toString());
        System.out.println("版本号:" + t.getVersion());
        System.out.println("序列号:" + t.getSerialNumber().toString(16));
        System.out.println("主体名：" + t.getSubjectDN());
        System.out.println("签发者：" + t.getIssuerDN());
        System.out.println("有效期：" + t.getNotBefore());
        System.out.println("签名算法：" + t.getSigAlgName());
        byte[] sig = t.getSignature();// 签名值
        PublicKey pk = t.getPublicKey();
        byte[] pkenc = pk.getEncoded();
        System.out.println("签名 ：");
        for (int i = 0; i < sig.length; i++)
            System.out.print(sig[i] + ",");
        System.out.println();
        System.out.println("公钥： ");
        for (int i = 0; i < pkenc.length; i++)
            System.out.print(pkenc[i] + ",");
        System.out.println("-----------------------------------------------------------------------");
    }
}
