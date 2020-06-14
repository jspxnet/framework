package sdk.security;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AbstractWrite;
import com.github.jspxnet.io.SecurityReadFile;
import com.github.jspxnet.io.SecurityWriteFile;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.KeyPairGen;
import com.github.jspxnet.security.asymmetric.AsyEncrypt;
import com.github.jspxnet.security.asymmetric.impl.RSAEncrypt;
import com.github.jspxnet.security.asymmetric.impl.SM2Encrypt;
import com.github.jspxnet.security.sm.SM3Digest;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.symmetry.impl.*;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.dispatcher.handle.RsaRocHandle;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ZipUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.encoders.Hex;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.github.jspxnet.boot.environment.Environment.defaultEncode;


/**
 * Created by Administrator on 2017/4/27.
 */
public class TestEncrypt {
    @BeforeClass
    public static void init() {
        JspxNetApplication.autoRun();
        System.out.println("------------开始");
    }

    @AfterClass
    public void afterExit() {
        System.out.println("------------结束");
    }


    @Test
    public static void testSM4A() throws Exception {
        String text = "目前，SiSoftware数据库中已经出现了Coffee Lake的身影，赫然就是6核心！\n" +
                "\n" +
                "    不过线程数也是6个，按照Intel这几年的产品线划分惯例，那应该就是隶属于Core i5系列，更高的Core i7就得是6核心12线程了，可以更好地对抗AMD 6/8核心的Ryzen 5/7系列。\n" +
                "\n" +
                "    当然，也不排除6核心6线程、6核心12线程都是Core i7系列的一部分，Core i5则变为4核心8线程。\n" +
                "\n" +
                "    检测信息还显示，该处理器主频为3.5GHz，二级缓存1.5MB(每核心256Kaby Lake)、三级缓存9MB。\n" +
                "\n" +
                "    这就比较奇怪，Kaby Lake架构是每个核心2MB三级缓存，6核心应该是12MB才对，这里要么是阉割了一部分(每核心1.5MB)，要么就是检测不准确。";

        SM4Encrypt encrypt = new SM4Encrypt();
        encrypt.setSecretKey("1234567890123456");
        encrypt.setCipherAlgorithm("CBC");
        encrypt.setCipherIv("1234567890123456");

        byte[] sm = encrypt.getEncode(text.getBytes(defaultEncode));
        byte[] out = encrypt.getDecode(sm);
        Assert.assertEquals(text, new String(out, defaultEncode));
    }


    @Test
    public static void testSM4B() throws Exception {
        String text = "目前，SiSoftware数据库中已经出现了Coffee Lake的身影，赫然就是6核心！\n" +
                "\n" +
                "    不过线程数也是6个，按照Intel这几年的产品线划分惯例，那应该就是隶属于Core i5系列，更高的Core i7就得是6核心12线程了，可以更好地对抗AMD 6/8核心的Ryzen 5/7系列。\n" +
                "\n" +
                "    当然，也不排除6核心6线程、6核心12线程都是Core i7系列的一部分，Core i5则变为4核心8线程。\n" +
                "\n" +
                "    检测信息还显示，该处理器主频为3.5GHz，二级缓存1.5MB(每核心256Kaby Lake)、三级缓存9MB。\n" +
                "\n" +
                "    这就比较奇怪，Kaby Lake架构是每个核心2MB三级缓存，6核心应该是12MB才对，这里要么是阉割了一部分(每核心1.5MB)，要么就是检测不准确。";

        SM4Encrypt encrypt = new SM4Encrypt();
        encrypt.setSecretKey("1234567890123456");
        encrypt.setCipherAlgorithm("ECB");
        encrypt.setCipherIv("1234567890123456");
        String sm = encrypt.getEncode(text);
        String out = encrypt.getDecode(sm);
        Assert.assertEquals(text, out);
    }

    @Test
    public static void testSM4C() throws Exception {
        String text = "目前，SiSoftware数据库中已经出现了Coffee Lake的身影，赫然就是6核心！\n" +
                "\n" +
                "    不过线程数也是6个，按照Intel这几年的产品线划分惯例，那应该就是隶属于Core i5系列，更高的Core i7就得是6核心12线程了，可以更好地对抗AMD 6/8核心的Ryzen 5/7系列。\n" +
                "\n" +
                "    当然，也不排除6核心6线程、6核心12线程都是Core i7系列的一部分，Core i5则变为4核心8线程。\n" +
                "\n" +
                "    检测信息还显示，该处理器主频为3.5GHz，二级缓存1.5MB(每核心256Kaby Lake)、三级缓存9MB。\n" +
                "\n" +
                "    这就比较奇怪，Kaby Lake架构是每个核心2MB三级缓存，6核心应该是12MB才对，这里要么是阉割了一部分(每核心1.5MB)，要么就是检测不准确。";

        SM4Encrypt encrypt = new SM4Encrypt();
        encrypt.setSecretKey("X23456789012345E");
        encrypt.setCipherAlgorithm("CBC");
        encrypt.setCipherIv("1234567890123456");
        String sm = encrypt.getEncode(text);
        String out = encrypt.getDecode(sm);
        Assert.assertEquals(text, out);
    }


    @Test
    public static void testSM3A() throws UnsupportedEncodingException {
        byte[] md = new byte[32];
        String text = "目前，SiSoftware数据库中已经出现了Coffee Lake的身影，赫然就是6核心！\n" +
                "\n" +
                "    不过线程数也是6个，按照Intel这几年的产品线划分惯例，那应该就是隶属于Core i5系列，更高的Core i7就得是6核心12线程了，可以更好地对抗AMD 6/8核心的Ryzen 5/7系列。\n" +
                "\n" +
                "    当然，也不排除6核心6线程、6核心12线程都是Core i7系列的一部分，Core i5则变为4核心8线程。\n" +
                "\n" +
                "    检测信息还显示，该处理器主频为3.5GHz，二级缓存1.5MB(每核心256Kaby Lake)、三级缓存9MB。\n" +
                "\n" +
                "    这就比较奇怪，Kaby Lake架构是每个核心2MB三级缓存，6核心应该是12MB才对，这里要么是阉割了一部分(每核心1.5MB)，要么就是检测不准确。";

        //e6e24691ccd450f18ddcd08dbb38d88c683ca18f2ba42e552e777aff06195c77

        byte[] msg1 = text.getBytes(defaultEncode);
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg1, 0, msg1.length);
        sm3.doFinal(md, 0);
        String s = new String(Hex.encode(md));
        System.out.println(s);
        Assert.assertEquals(EncryptUtil.getSm3(text), s);
    }

    @Test
    public static void testXOREncrypt() throws Exception {
        String text = "目前，3453245SiSoftware数据库中已经出现了Coffee Lake的身影，赫然就是6核心！\n" +
                "\n" +
                "    不过线程数也是6个，按照Intel这几年的产品线划分惯例，那应该就是隶属于Core i5系列，更高的Core i7就得是6核心12线程了，可以更好地对抗AMD 6/8核心的Ryzen 5/7系列。\n" +
                "\n" +
                "    当然，也不wetr排除6核心6线程、 6核心12线程都是Core i7系列的一部分，Core i5则变为4核心8线程。\n" +
                "\n" +
                "    检测信息还显示，该处理器主频为3.5GHz，二级缓存1.5MB(每核心256Kaby Lake)、三级缓存9MB。\n" +
                "\n" +
                "    这就比较奇怪，Kaby Lake架构是每个核心2MB三级缓存，6核心应该是12MB才对，这里要么是阉割了一部分(每核心1.5MB)，要么就是检测不准确。";

        Encrypt encrypt = new XOREncrypt();
        encrypt.setSecretKey(StringUtil.cut(EncryptUtil.getMd5(text), 16, ""));
        String sm = encrypt.getEncode(text);
        System.out.println("encrypt=" + sm);
        String out = encrypt.getDecode(sm);
        Assert.assertEquals(text, out);
    }

    @Test
    public static void testTextKeyEncrypt() throws Exception {
        String text = "目前，3453245SiSoftware数据库中已经出现了Coffee Lake的身影，赫然就是6核心！\n" +
                "\n" +
                "    不过线程数也是6个，按照Intel这几年的产品线划分惯例，那应该就是隶属于Core i5系列，更高的Core i7就得是6核心12线程了，可以更好地对抗AMD 6/8核心的Ryzen 5/7系列。\n" +
                "\n" +
                "    当然，也不wetr排除6核心6线程、 6核心12线程都是Core i7系列的一部分，Core i5则变为4核心8线程。\n" +
                "\n" +
                "    检测信息还显示，该处理器主频为3.5GHz，二级缓存1.5MB(每核心256Kaby Lake)、三级缓存9MB。\n" +
                "\n" +
                "    这就比较奇怪，Kaby Lake架构是每个核心2MB三级缓存，6核心应该是12MB才对，这里要么是阉割了一部分(每核心1.5MB)，要么就是检测不准确。";

        Encrypt encrypt = new TextKeyEncrypt();
        encrypt.setSecretKey(StringUtil.cut(EncryptUtil.getMd5(text), 16, ""));
        String sm = encrypt.getEncode(text);
        String out = encrypt.getDecode(sm);
        Assert.assertEquals(text, out);
    }

    @Test
    public static void testTextKeyEncryptTwo() throws Exception {

        Encrypt en = new TextKeyEncrypt();
        en.setSecretKey("家密我的文件");
        String txt = en.getEncode("家密我的文件");
        String out = en.getDecode(txt);
        Assert.assertEquals("家密我的文件", out);
    }

    @Test
    public static void testSecurityWriteFile() throws Exception {
        String txt = "卖家说可上四川的牌，A但我担心是套牌？B如果能在四川上牌，是否可以在山东过户？";
        File file = new File(System.getProperty("java.io.tmpdir"), "testaio.txt");
        AbstractWrite write = new SecurityWriteFile();
        write.setFile(file.getAbsolutePath());
        write.setContent(txt);

        AbstractRead read = new SecurityReadFile();
        read.setFile(file);
        String txt2 = read.getContent();

        Assert.assertEquals(txt, txt2);

        AbstractRead read2 = new SecurityReadFile();
        read2.setFile(file.getAbsolutePath());
        String txt3 = read.getContent();
        FileUtil.delete(file);
        Assert.assertEquals(txt, txt3);
    }

    @Test
    public void testDESEncrypt() throws Exception {
        String text = "1236745^$%^&$^%$(*&()&*_()KHJN<M:LIUPOULKGT中文看看,五一小长假期间，国内的各大景点都出现了游客激增的情况，不少景区因为游客实在太多，为了游客安全，不得不采取限流的措施。 ";
        DESEncrypt encrypt = new DESEncrypt();
        encrypt.setSecretKey(StringUtil.cut(EncryptUtil.getMd5(text), 8, ""));
        encrypt.setCipherIv("01234567");
        String mm = encrypt.getEncode(text);
        String out = encrypt.getDecode(mm);
        Assert.assertEquals(out, text);
    }


    @Test
    public void testAESEncrypt() throws Exception {
        String text = "1236745^$%^&$^%$(*&()&*_()KHJN<M:LIUPOULKGT中文看看,五一小长假期间，国内的各大景点都出现了游客激增的情况，不少景区因为游客实在太多，为了游客安全，不得不采取限流的措施。 ";
        AESEncrypt encrypt = new AESEncrypt();
        encrypt.setSecretKey(StringUtil.cut(EncryptUtil.getMd5(text), 20, ""));
        encrypt.setCipherIv(StringUtil.cut(EncryptUtil.getMd5(text), 20, ""));
        String mm = encrypt.getEncode(text);
        String out = encrypt.getDecode(mm);
        Assert.assertEquals(out, text);
    }

    @Test
    public void testMD5ORMD32() throws Exception {
        String text = "1236745^$%^&$^%$(*&()&*_()KHJN<M:LIUPOULKGT中文看看,五一小长假期间，国内的各大景点都出现了游客激增的情况，不少景区因为游客实在太多，为了游客安全，不得不采取限流的措施。 ";
        String md5 = EncryptUtil.getMd5(text);
        String md32 = EncryptUtil.getMd32(text, StringUtil.cut(EncryptUtil.getMd5(text), 16, ""));

        Assert.assertEquals(md5, "410a2b8f3605b9a1da6e6a59c703c9d4");
        Assert.assertEquals(md32, "0B0CD525C8569D6E78986CB047CA29D4");
    }


    @Test
    public void testBigDecimal() throws Exception {
        byte[] privateKey = EncryptUtil.hexToByte("D497FC0FD2863D9FA50BC3196FECE2770687B61541F0A22B124B17FE134A5386");
        BigInteger bigIntegerA = EncryptUtil.byteConvertInteger(privateKey);
        Assert.assertEquals(bigIntegerA.toString(), "96158857473083406276639890509186950349261855908591580727831085807919509951366");
    }
    @Test
    public void testHex() throws Exception {

        String source = "恭喜发财!当该用户发送文件时，用私钥签名，别人用他给的公钥解密，可以保证该信息是由他发送的。即数字签名 数字签名:" +
                "就是信息发送者用其私钥对从所传报文中提取出的特征数据（或称数字指纹）进行RSA算法操作，以保证发信人无法抵赖曾发过该信息（即不可抵赖 性），" +
                "同时也确保信息报文在经签名后末被篡改（即完整性）。当信息接收者收到报文后，\r\n就可以用发送者的公钥对数字签名进行验证";// 要加密的字符串

        String hexString = EncryptUtil.byteToHex(source.getBytes(defaultEncode));
        Assert.assertEquals(EncryptUtil.isHex(hexString), true);
        String source2 = "12345678986G654";
        System.out.println(hexString);
        byte[] xx = EncryptUtil.hexToByte(source2);
        Assert.assertEquals(EncryptUtil.isHex(source2), false);

    }


    @Test
    public void testSysEncrypt() throws Exception {
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        String source = "恭喜发财!当该用户发送文件时，用私钥签名，别人用他给的公钥解密，可以保证该信息是由他发送的。即数字签名 数字签名:" +
                "就是信息发送者用其私钥对从所传报文中提取出的特征数据（或称数字指纹）进行RSA算法操作，以保证发信人无法抵赖曾发过该信息（即不可抵赖 性），" +
                "同时也确保信息报文在经签名后末被篡改（即完整性）。当信息接收者收到报文后，\r\n就可以用发送者的公钥对数字签名进行验证";
        String temp = encrypt.getEncode(source);
        String last = encrypt.getDecode(temp);
        Assert.assertEquals(source, last);

    }


    @Test
    public void testZipEncrypt() throws Exception {

        String source = "恭喜发财!当该用户发送文件时，用私钥签名，别人用他给的公钥解密，可以保证该信息是由他发送的。即数字签名 数字签名:" +
                "就是信息发送者用其私钥对从所传报文中提取出的特征数据（或称数字指纹）进行RSA算法操作，以保证发信人无法抵赖曾发过该信息（即不可抵赖 性），" +
                "同时也确保信息报文在经签名后末被篡改（即完整性）。当信息接收者收到报文后，\r\n就可以用发送者的公钥对数字签名进行验证";// 要加密的字符串

        String hexString = EncryptUtil.byteToHex(source.getBytes(defaultEncode));
        System.out.println(hexString);
        byte[] zip = ZipUtil.zip(source.getBytes(defaultEncode));

        byte[] unzip = ZipUtil.unZip(zip);
        String last = new String(unzip, defaultEncode);
        System.out.println(last);
        Assert.assertEquals(last, source);

    }


    @Test(threadPoolSize = 10, invocationCount = 10)
    public static void testSm2Two() throws Exception {
        AsyEncrypt encrypt = new SM2Encrypt();
        KeyPairGen keyPair = encrypt.getKeyPair();
        Assert.assertEquals(keyPair == null, false);

        System.out.println("私钥: " + EncryptUtil.byteToHex(keyPair.getPrivateKey()));
        System.out.println("公钥: " + EncryptUtil.byteToHex(keyPair.getPublicKey()));

        String text = "1236745^$%^&$^%$(*&()&*_()KHJN<M:LIUPOULKGT中文看看,五一小长假期间，国内的各大景点都出现了游客激增的情况，不少景区因为游客实在太多，为了游客安全，不得不采取限流的措施。 ";

        byte[] sourceData = text.getBytes(defaultEncode);
        byte[] c = encrypt.sign(sourceData, keyPair.getPrivateKey());

        boolean vs = encrypt.verify(sourceData, keyPair.getPublicKey(), c);
        Assert.assertEquals(vs, true);

        //使用公密加密
        byte[] cipherText = encrypt.encryptByPublicKey(sourceData, keyPair.getPublicKey());
        System.out.println(EncryptUtil.byteToHex(cipherText));

        //解密使用私秘
        String plainText = new String(encrypt.decryptByPrivateKey(cipherText, keyPair.getPrivateKey()));
        Assert.assertEquals(text, plainText);
    }


    @Test
    public void testRSA() throws Exception {

        AsyEncrypt encrypt = new RSAEncrypt();
        KeyPairGen keyPair = encrypt.getKeyPair();
        String source = "恭喜发财!当该用户发送文件时，用私钥签名，别人名";// 要加密的字符串

        byte[] jiaMi = encrypt.encryptByPublicKey(source.getBytes(defaultEncode), keyPair.getPublicKey());
        //验证
        byte[] sign1 = encrypt.sign(jiaMi, keyPair.getPrivateKey());
        System.out.println("sign1：" + EncryptUtil.byteToHex(sign1));
        Assert.assertEquals(encrypt.verify(jiaMi, keyPair.getPublicKey(), sign1), true);

        //私密解密
        byte[] jieMi = encrypt.decryptByPrivateKey(jiaMi, keyPair.getPrivateKey());
        Assert.assertEquals(new String(jieMi, defaultEncode), source);

        //--------------------------------------------------------------------------------------------------------------


        byte[] pjiaMi = encrypt.encryptByPrivateKey(source.getBytes(defaultEncode), keyPair.getPrivateKey());

        //验证
        byte[] sign2 = encrypt.sign(pjiaMi, keyPair.getPrivateKey());
        Assert.assertEquals(encrypt.verify(pjiaMi, keyPair.getPublicKey(), sign2), true);

        //私密解密
        byte[] pjieMi = encrypt.decryptByPublicKey(pjiaMi, keyPair.getPublicKey());
        Assert.assertEquals(new String(pjieMi, defaultEncode), source);
    }
    @Test
    public void testRSA2() throws Exception {

        AsyEncrypt encrypt = new RSAEncrypt();
        KeyPairGen keyPair = encrypt.getKeyPair();
        String source = "恭喜发财!当该用户发送文件时，用私钥签名，别人名";// 要加密的字符串

        byte[] pjiaMi = encrypt.encryptByPrivateKey(source.getBytes(defaultEncode), keyPair.getPrivateKey());

        //验证
        byte[] sign2 = encrypt.sign(pjiaMi, keyPair.getPrivateKey());
        Assert.assertEquals(encrypt.verify(pjiaMi, keyPair.getPublicKey(), sign2), true);

        //私密解密
        byte[] pjieMi = encrypt.decryptByPublicKey(pjiaMi, keyPair.getPublicKey());
        Assert.assertEquals(new String(pjieMi, defaultEncode), source);
    }

    @Test
    public void testRSAAddMs2() throws Exception {
        AsyEncrypt encrypt = new RSAEncrypt();
        testAsymmetic(encrypt);

        AsyEncrypt encrypt2 = new SM2Encrypt();
        testAsymmetic(encrypt2);
    }


    private void testAsymmetic(AsyEncrypt encrypt) throws Exception {
        KeyPairGen keyPair = encrypt.getKeyPair();
        String source = "恭喜发财!当该用户发送文件时，用私钥签名";// 要加密的字符串

        byte[] jiaMi = encrypt.encryptByPublicKey(source.getBytes(defaultEncode), keyPair.getPublicKey());
        //验证
        byte[] sign1 = encrypt.sign(jiaMi, keyPair.getPrivateKey());
        System.out.println("sign1：" + EncryptUtil.byteToHex(sign1));
        Assert.assertEquals(encrypt.verify(jiaMi, keyPair.getPublicKey(), sign1), true);

        //私密解密
        byte[] jieMi = encrypt.decryptByPrivateKey(jiaMi, keyPair.getPrivateKey());
        Assert.assertEquals(new String(jieMi, defaultEncode), source);

    }


    @Test
    public void testHashEncode() throws Exception {

        System.out.println(EncryptUtil.getHashEncode("111111", EnvFactory.getHashAlgorithm()));
    }

    @Test
    public void createRSAEncrypt() throws Exception {
        AsyEncrypt encrypt = new RSAEncrypt();
        KeyPairGen keyPair = encrypt.getKeyPair();
        byte[] piPey = keyPair.getPrivateKey();
        String hex = EncryptUtil.byteToHex(piPey);
        String hexB = EncryptUtil.getBase64Encode(piPey);
        System.out.println("privateKey=" + hex);
        System.out.println("privateKey=" + hexB);

        Assert.assertEquals(EncryptUtil.getBase64Decode(hexB), EncryptUtil.hexToByte(hex));


        byte[] puKey = keyPair.getPublicKey();

        String puHex = EncryptUtil.byteToHex(puKey);
        String puB = EncryptUtil.getBase64Encode(puKey);
        System.out.println("publicKey=" + puHex);
        System.out.println("publicKey=" + puB);

        Assert.assertEquals(EncryptUtil.getBase64Decode(puB), EncryptUtil.hexToByte(puHex));
    }

    @Test
    public void testZipUn() throws Exception {

        String data = "在“合作伙伴密钥管理”那一块上传公钥";

        byte[] zipConData = ZipUtil.zip(data.getBytes(defaultEncode));

        byte[] outData = ZipUtil.unZip(zipConData);
        Assert.assertEquals(data, new String(outData, defaultEncode));
    }

    @Test
    public void createZipRSA() throws Exception {
        AsyEncrypt encrypt = EnvFactory.getAsymmetricEncrypt();
        String data = "在“合作伙伴密钥管理”那一块上传公钥";

        byte[] zipConData = ZipUtil.zip(data.getBytes(defaultEncode));
        byte[] conData = encrypt.encryptByPublicKey(zipConData, EnvFactory.getPublicKey());
        //----------------------------------------------------------
        byte[] out = encrypt.decryptByPrivateKey(conData, EnvFactory.getPrivateKey());
        byte[] outData = ZipUtil.unZip(out);
        Assert.assertEquals(data, new String(outData, defaultEncode));
    }

    @Test
    public void testZip() throws Exception {
        String data = "在“合作伙伴密钥管理”那一块上传公钥，我已经按照文档里面说的，用openssl生成了公钥和私钥，然后将公钥的”-----BEGIN PUBLIC KEY-----”、“-----END PUBLIC KEY-----”、空格、换行都去掉了，可是上传的时候一直提示我“格式错误，请输入正确的RSA公钥”，换了很多个浏览器都不行，而且我试了一下文档给的公钥示例，也是格式错误";
        byte[] zipConData = ZipUtil.zip(data.getBytes(defaultEncode));
        //----------------------------------------------------------
        byte[] outData = ZipUtil.unZip(zipConData);
        Assert.assertEquals(data, new String(outData));
        //org.apache.catalina.session.JDBCStore
    }


    @Test
    public void testGetHex() throws Exception {
        String data = "e58e9fe69da5e698afe68891e68c87e5ae9a6b6579e79a84e696b9e5bc8fe4b88de5afb92c20e79bb4e68ea5e5b086e5ad97e7aca6e4b8b2e5819ae4b8bae58f82e695b02c20e683b3e5bd93e784b6e79a84e4bba5e4b8bae8bf99e5b0b1e698af6b65792c20e585b6e5ae9ee4b88de784b62c2043727970746f4a53e4bc9ae6a0b9e68daee8bf99e4b8aae5ad97e7aca6e4b8b2e7ae97e587bae79c9fe6ada3e79a846b6579e5928c495628e59084e7a78de696b0e9b29ce5908de8af8de4b88de8a7a3e9878a2c200ae78988e69d83e5bd92e4bd9ce88085e68980e69c89efbc8ce4bbbbe4bd95e5bda2e5bc8fe8bdace8bdbde8afb7e88194e7b3bbe4bd9ce88085e38082e58e9fe69da5e698afe68891e68c87e5ae9a6b6579e79a84e696b9e5bc8fe4b88de5afb92c20e79bb4e68ea5e5b086e5ad97e7aca6e4b8b2e5819ae4b8bae58f82e695b02c20e683b3e5bd93e784b6e79a84e4bba5e4b8bae8bf99e5b0b1e698af6b65792c20e585b6e5ae9ee4b88de784b62c2043727970746f4a53e4bc9ae6a0b9e68daee8bf99e4b8aae5ad97e7aca6e4b8b2e7ae97e587bae79c9fe6ada3e79a846b6579e5928c495628e59084e7a78de696b0e9b29ce5908de8af8de4b88de8a7a3e9878a2c20e997aee68891e4b99fe6b2a1e794a82c20e68891e4b99fe4b88de68782e982a3e4b988e68891e4bbace58faae99c80e8a681e5b0866b6579e5928c6976e5afb9e5ba94e79a84e5ad97e7aca6e4b8b2e8bdace6889043727970746f4a53e79a84576f72644172726179e7b1bbe59e8b2c20e59ca8444553e58aa0e5af86e697b6e5819ae4b8bae58f82e695b0e4bca0e585a5e58db3e58faf2c20e8bf99e6a0b7e5afb94d657373616765e8bf99e4b8aae5ad97e7aca6e4b8b2e58aa0e5af862c20e6af8fe6aca1e5be97e588b0e79a84e5af86e69687e983bde698af0ae8bf99e6a0b7e698afe4b88de698afe5b0b1e4b887e4ba8b4f4be4ba863f20e593aae69c892c20e8b081e79fa5e98193e8bf99e59d91e698afe4b880e4b8aae68ea5e4b880e4b8aae5958a0a";

        byte[] outByte = EncryptUtil.hexToByte(data);
        System.out.println(new String(outByte, defaultEncode));
    }

    @Test
    public void testAESdes() throws Exception {
        String txt = "原来是我指定key的方式不对, 直接将字符串做为参数, 想当然的以为这就是key, 其实不然, CryptoJS会根据这个字符串算出真正的key和IV(各种新鲜名词不解释, \n" +
                "版权归作者所有，任何形式转载请联系作者。原来是我指定key的方式不对, 直接将字符串做为参数, 想当然的以为这就是key, 其实不然, CryptoJS会根据这个字符串算出真正的key和IV(各种新鲜名词不解释, 问我也没用, 我也不懂那么我们只需要将key和iv对应的字符串转成CryptoJS的WordArray类型, 在DES加密时做为参数传入即可, 这样对Message这个字符串加密, 每次得到的密文都是\n" +
                "这样是不是就万事OK了? 哪有, 谁知道这坑是一个接一个啊\n";

        AESEncrypt encrypt = new AESEncrypt();
        encrypt.setCipherAlgorithm("AES/CBC/ISO10126Padding");
        encrypt.setSecretKey("dufy20170329java");
        encrypt.setCipherIv("dufy20170329java");
        String mm = EncryptUtil.getBase64EncodeString(encrypt.getEncode(txt));
        String out = encrypt.getDecode(EncryptUtil.getBase64DecodeString(mm));
        Assert.assertEquals(txt, out);


    }

    @Test
    public void testAESdes7() throws Exception {
        String txt = "原来是我指定key的方式不对, 直接将字符串做为参数, 想当然的以为这就是key, 其实不然, CryptoJS会根据这个字符串算出真正的key和IV(各种新鲜名词不解释, \n" +
                "版权归作者所有，任何形式转载请联系作者。原来是我指定key的方式不对, 直接将字符串做为参数, 想当然的以为这就是key, 其实不然, CryptoJS会根据这个字符串算出真正的key和IV(各种新鲜名词不解释, 问我也没用, 我也不懂那么我们只需要将key和iv对应的字符串转成CryptoJS的WordArray类型, 在DES加密时做为参数传入即可, 这样对Message这个字符串加密, 每次得到的密文都是\n" +
                "这样是不是就万事OK了? 哪有, 谁知道这坑是一个接一个啊\n";
        AESEncrypt encrypt = new AESEncrypt();
        encrypt.setSecretKey("dufy20170329java");
        //encrypt.setCipherIv("dufy20170329java");
        byte[] jiaMbytes = encrypt.getEncode(txt.getBytes(defaultEncode));
        byte[] myDesByte = encrypt.getDecode(jiaMbytes);
        String out = new String(myDesByte, defaultEncode);
        Assert.assertEquals(txt, out);
    }


    @Test
    public void testDES7() throws Exception {
        String txt = "原来是我指定key的方式不对, 直接将字符串做为参数, 想当然的以为这就是key, 其实不然, CryptoJS会根据这个字符串算出真正的key和IV(各种新鲜名词不解释, \n" +
                "版权归作者所有，任何形式转载请联系作者。原来是我指定key的方式不对, 直接将字符串做为参数, 想当然的以为这就是key, 其实不然, CryptoJS会根据这个字符串算出真正的key和IV(各种新鲜名词不解释, 问我也没用, 我也不懂那么我们只需要将key和iv对应的字符串转成CryptoJS的WordArray类型, 在DES加密时做为参数传入即可, 这样对Message这个字符串加密, 每次得到的密文都是\n" +
                "这样是不是就万事OK了? 哪有, 谁知道这坑是一个接一个啊\n";
        String miBase64 = "DC41790B31D6A578B14997F3AA193E276B550B77FC4945C052D78F55146CADEE9E262F994D0CBC4C549A3305EE175FFCF3D553357B179795FA86178B9247757CB808DF1AB876870C5B5C8B8ED57E506DB844D7A5B4443DFBCFD05BD9A2EA0DFA908053C645E60B8022D4FBDEC5C6735146B3DEB0712ACCDB7951AB2930E3520B287281C1159C7CEEABA71E453272410404861445E28D9EB28C05153B80946058217880829A95A42030B3673D6733A0D372D103AA22FEF0D558134679B4036A6C3D1D247E119391827FA199EFA006534362CEF0D6119000632A38143B87F4AA9288DE6088BDB6F5643E73840BEA07628E367F24A113E7778F811CF6F6B050049F6D79DC60D04534115B6176BC01277EC8923DC99E6903AAB1FB4579F5E74F9B32815211F8678AD8E6CD380B2DD00A87240181D8CDBB19AE5E515AEF41035D0D7B805F1E4ABC2AC29BBB7DAF8002CA1BC51477C6F4BD4DBC2C5617B771451987AC7E17CDE77C1A0F0F54F6DE78E8D1F27741746636AE7460BFE7D6C84F4E168828E8A601745E26A74610E6CF98B5F05A37B7D5A8DFCD12E24ECBDB1DABEE1A4880AD1D170BAF67B6927C711090C1D7FB21F2DBE9BC7D3E09395F4E3E3FF3460F39BA91A5D79286D945A7F9138CC18AAA731F8697E832E4CCA4E3F2BA0EE38134B10A6E105825A5B20B77B066D5722610BC64DB5FA88BCD6D64CD4728248764EB540C0B50889D8F6DA9C87AF0A731967BAFD3C93C3018496A7A99C5FBE60283F99D53C9925F5B7E0813C577738960903A10C77056C2F562ED57582D6DFD1044C640CD658CB552332805AA79FC9C94480BF14DC5D63D466521C6DE6004EAFEF49A9E65A3DA7CB27045C5E44A28204BE8C2565ACCBF058A197D06B76ABEAA325288E2AFA2B3FE2DC426E3310CC20E3E5CAB826C197CC09652E5134EC41681487F215BE33B75351EAE7737011948A3BFD9B53F0B1D9176535837A33809F41ABE6C78A254F281D14DF7870EA89CCB189BE55F2411FA00509DC73E8D54F13FAC017D05ECB830A8C0DF765BB1E884DAD2E566189ACDA2B240592DFD95C20B3B219BD25B5F";

        DESEncrypt encrypt = new DESEncrypt();
        encrypt.setSecretKey("01234567");
        encrypt.setCipherIv("01234567");
        byte[] myDesByte = encrypt.getDecode(EncryptUtil.hexToByte(miBase64));
        String out = new String(myDesByte, defaultEncode);
        Assert.assertEquals(txt, out);
    }

    @Test
    public void testHexString() throws Exception {
        String text = "1236745^$%^&$^%$(*&()&*_()KHJN<M:LIUPOULKGT中文看看,五一231432小长假期间，国内的各大景点都出14432534534现了游客激增的情况，不少景区因为游客实在太多，为了游客安全，不得不采取限流的措施。 ";
        String hexString = EncryptUtil.byteToHex(text.getBytes(defaultEncode));
        String hexStr = EncryptUtil.byteToHex(text.getBytes(defaultEncode));
        Assert.assertEquals(hexString, hexStr);
    }


    @Test
    public static void testHexSave() throws Exception {

        RSAEncrypt encrypt = new RSAEncrypt();
        KeyPairGen keyPair = encrypt.getKeyPair();

        String hexPri = EncryptUtil.byteToHex(keyPair.getPrivateKey());
        String hexPub = EncryptUtil.byteToHex(keyPair.getPublicKey());

        System.out.println("私钥: " + hexPri);
        System.out.println("公钥: " + hexPub);
        System.out.println("---------------------------------------");

        byte[] bytePri = EncryptUtil.hexToByte(hexPri);
        byte[] bytePub = EncryptUtil.hexToByte(hexPub);

        Assert.assertEquals(keyPair.getPrivateKey(), bytePri);
        Assert.assertEquals(keyPair.getPublicKey(), bytePub);

        String basePri = EncryptUtil.getBase64Encode(bytePri);
        String basePut = EncryptUtil.getBase64Encode(bytePub);

        System.out.println("私钥: " + basePri);
        System.out.println("公钥: " + basePut);

        byte[] bytePri1 = EncryptUtil.getBase64Decode(basePri);
        byte[] bytePput1 = EncryptUtil.getBase64Decode(basePut);

        Assert.assertEquals(bytePri1, bytePri);
        Assert.assertEquals(bytePput1, bytePub);


        //File outPri = new File("d:/tmp/privateKey.key");
        //File outPub = new File("d:/tmp/publicKey.key");
        //FileUtil.writeFile(outPri,keyPair.getPrivateKey());
        //FileUtil.writeFile(outPub,keyPair.getPublicKey());

    }

    @Test
    public static void testHexSave2() throws Exception {


    /*
        File outPri = new File("d:/tmp/privateKey.key");
        File outPub = new File("d:/tmp/publicKey.key");

        byte[] pri = FileUtil.readFileByte(outPri);
        byte[] put = FileUtil.readFileByte(outPub);

                Assert.assertEquals(hexPri,EncryptUtil.byteToHex(pri));
        Assert.assertEquals(hexPub,EncryptUtil.byteToHex(put));
*/

        String hexPri = "30820275020100300d06092a864886f70d01010105000482025f3082025b0201000281810094a99b6de6b85c68dc5aa9247357e131353f1fe3690dd6bfaf0ef1f10e1736617c3a49b1554585bb7d318af30ac9cd7b9384ca35699cd328414531c68a8bf6d3eee395b6bd99c856879864f1e58a2b5a0810b5989fdabcdd9e5c124ff6713d280a78e5094ab93c5a4df557f6835fa92760e66f9a39401f1fdc3768d2ae8cb713020301000102818022fa7533baa18e3cadfc36e7f8aba8b4a74e9ef626f5b69d8b56fcde36ee874110b5b6391f53285020f8d334b4491f2c005db226371f4b1fa19be24650baf5ec33e7b2c46d74e4c9e1f3ba22565b10ca29cd936c9c06f1aa2a2b5bdbefd3c14d20b425edac38f2777335c35f18a52d4d4bb9905fd01dd3036c442baf1efe91c1024100c8399e7ca4edf7507b96550cc8842ebfb2f2b1f385859b715488058cd3bd0ab3dca86b1a883b9a52bbc0e17f051745600393f6ec544b2a5fb61cdb2e8b859d1b024100be12fa4792c5ad951beb48708f7c78b2983162812f861c6c8e88d7d8ec1f4b1385ea65d8f143ae5fee6caab70b75c7ae3edc32cbc4fcdf7ac3e4e5a47da54569024025a53fb92bc15a3895a40445ad05bd4c69b6dbe26f7e59658b812f99f4cd9c4f88f3597fd9916b4896e882fd40d5c9436e28c9ca5166f8869a54fe98d7bebd75024076d5caa0a104f247bd52d6d179042b3a0da9229afe296ceffa15584e09226f7fdf43f3353572493d3505bf9dfd8e3faf93217ca8a59a2be6c73efecc5eb9cbb102404309e12fb394e6975d3fc50b996f5733f02a71a02e6791cacf60b537520970b73488f25e466bc635a92fabb8ad96d6a2828b8c9ba4aab3b174a2cf8c8889b4da";
        String hexPub = "30819f300d06092a864886f70d010101050003818d003081890281810094a99b6de6b85c68dc5aa9247357e131353f1fe3690dd6bfaf0ef1f10e1736617c3a49b1554585bb7d318af30ac9cd7b9384ca35699cd328414531c68a8bf6d3eee395b6bd99c856879864f1e58a2b5a0810b5989fdabcdd9e5c124ff6713d280a78e5094ab93c5a4df557f6835fa92760e66f9a39401f1fdc3768d2ae8cb7130203010001";


        //比较配置begin

         //比较配置end

        String basePut = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUqZtt5rhcaNxaqSRzV+ExNT8f42kN1r+vDvHx\n" +
                "Dhc2YXw6SbFVRYW7fTGK8wrJzXuThMo1aZzTKEFFMcaKi/bT7uOVtr2ZyFaHmGTx5YorWggQtZif\n" +
                "2rzdnlwST/ZxPSgKeOUJSrk8Wk31V/aDX6knYOZvmjlAHx/cN2jSroy3EwIDAQAB";

        String basePri = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJSpm23muFxo3FqpJHNX4TE1Px/j\n" +
                "aQ3Wv68O8fEOFzZhfDpJsVVFhbt9MYrzCsnNe5OEyjVpnNMoQUUxxoqL9tPu45W2vZnIVoeYZPHl\n" +
                "iitaCBC1mJ/avN2eXBJP9nE9KAp45QlKuTxaTfVX9oNfqSdg5m+aOUAfH9w3aNKujLcTAgMBAAEC\n" +
                "gYAi+nUzuqGOPK38Nuf4q6i0p06e9ib1tp2LVvzeNu6HQRC1tjkfUyhQIPjTNLRJHywAXbImNx9L\n" +
                "H6Gb4kZQuvXsM+eyxG105Mnh87oiVlsQyinNk2ycBvGqKitb2+/TwU0gtCXtrDjyd3M1w18YpS1N\n" +
                "S7mQX9Ad0wNsRCuvHv6RwQJBAMg5nnyk7fdQe5ZVDMiELr+y8rHzhYWbcVSIBYzTvQqz3KhrGog7\n" +
                "mlK7wOF/BRdFYAOT9uxUSypfthzbLouFnRsCQQC+EvpHksWtlRvrSHCPfHiymDFigS+GHGyOiNfY\n" +
                "7B9LE4XqZdjxQ65f7myqtwt1x64+3DLLxPzfesPk5aR9pUVpAkAlpT+5K8FaOJWkBEWtBb1Mabbb\n" +
                "4m9+WWWLgS+Z9M2cT4jzWX/ZkWtIluiC/UDVyUNuKMnKUWb4hppU/pjXvr11AkB21cqgoQTyR71S\n" +
                "1tF5BCs6Dakimv4pbO/6FVhOCSJvf99D8zU1ckk9NQW/nf2OP6+TIXyopZor5sc+/sxeucuxAkBD\n" +
                "CeEvs5Tml10/xQuZb1cz8CpxoC5nkcrPYLU3UglwtzSI8l5Ga8Y1qS+ruK2W1qKCi4ybpKqzsXSi\n" +
                "z4yIibTa";


        byte[] bytePri1 = EnvFactory.getPrivateKey();
        byte[] bytePput1 = EnvFactory.getPublicKey();

        Assert.assertEquals(bytePri1, EncryptUtil.hexToByte(hexPri));
        Assert.assertEquals(bytePput1, EncryptUtil.hexToByte(hexPub));
        String jm = "苹果系列基本都会在150左右上";


        AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();
        byte[] mw = asyEncrypt.encryptByPublicKey(jm.getBytes(defaultEncode), bytePput1);

        byte[] out = asyEncrypt.decryptByPrivateKey(mw, bytePri1);
        Assert.assertEquals(jm, new String(out, defaultEncode));


    }

/*
私钥: 30820275020100300d06092a864886f70d01010105000482025f3082025b0201000281810094a99b6de6b85c68dc5aa9247357e131353f1fe3690dd6bfaf0ef1f10e1736617c3a49b1554585bb7d318af30ac9cd7b9384ca35699cd328414531c68a8bf6d3eee395b6bd99c856879864f1e58a2b5a0810b5989fdabcdd9e5c124ff6713d280a78e5094ab93c5a4df557f6835fa92760e66f9a39401f1fdc3768d2ae8cb713020301000102818022fa7533baa18e3cadfc36e7f8aba8b4a74e9ef626f5b69d8b56fcde36ee874110b5b6391f53285020f8d334b4491f2c005db226371f4b1fa19be24650baf5ec33e7b2c46d74e4c9e1f3ba22565b10ca29cd936c9c06f1aa2a2b5bdbefd3c14d20b425edac38f2777335c35f18a52d4d4bb9905fd01dd3036c442baf1efe91c1024100c8399e7ca4edf7507b96550cc8842ebfb2f2b1f385859b715488058cd3bd0ab3dca86b1a883b9a52bbc0e17f051745600393f6ec544b2a5fb61cdb2e8b859d1b024100be12fa4792c5ad951beb48708f7c78b2983162812f861c6c8e88d7d8ec1f4b1385ea65d8f143ae5fee6caab70b75c7ae3edc32cbc4fcdf7ac3e4e5a47da54569024025a53fb92bc15a3895a40445ad05bd4c69b6dbe26f7e59658b812f99f4cd9c4f88f3597fd9916b4896e882fd40d5c9436e28c9ca5166f8869a54fe98d7bebd75024076d5caa0a104f247bd52d6d179042b3a0da9229afe296ceffa15584e09226f7fdf43f3353572493d3505bf9dfd8e3faf93217ca8a59a2be6c73efecc5eb9cbb102404309e12fb394e6975d3fc50b996f5733f02a71a02e6791cacf60b537520970b73488f25e466bc635a92fabb8ad96d6a2828b8c9ba4aab3b174a2cf8c8889b4da
公钥: 30819f300d06092a864886f70d010101050003818d003081890281810094a99b6de6b85c68dc5aa9247357e131353f1fe3690dd6bfaf0ef1f10e1736617c3a49b1554585bb7d318af30ac9cd7b9384ca35699cd328414531c68a8bf6d3eee395b6bd99c856879864f1e58a2b5a0810b5989fdabcdd9e5c124ff6713d280a78e5094ab93c5a4df557f6835fa92760e66f9a39401f1fdc3768d2ae8cb7130203010001
---------------------------------------
私钥: MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJSpm23muFxo3FqpJHNX4TE1Px/j
aQ3Wv68O8fEOFzZhfDpJsVVFhbt9MYrzCsnNe5OEyjVpnNMoQUUxxoqL9tPu45W2vZnIVoeYZPHl
iitaCBC1mJ/avN2eXBJP9nE9KAp45QlKuTxaTfVX9oNfqSdg5m+aOUAfH9w3aNKujLcTAgMBAAEC
gYAi+nUzuqGOPK38Nuf4q6i0p06e9ib1tp2LVvzeNu6HQRC1tjkfUyhQIPjTNLRJHywAXbImNx9L
H6Gb4kZQuvXsM+eyxG105Mnh87oiVlsQyinNk2ycBvGqKitb2+/TwU0gtCXtrDjyd3M1w18YpS1N
S7mQX9Ad0wNsRCuvHv6RwQJBAMg5nnyk7fdQe5ZVDMiELr+y8rHzhYWbcVSIBYzTvQqz3KhrGog7
mlK7wOF/BRdFYAOT9uxUSypfthzbLouFnRsCQQC+EvpHksWtlRvrSHCPfHiymDFigS+GHGyOiNfY
7B9LE4XqZdjxQ65f7myqtwt1x64+3DLLxPzfesPk5aR9pUVpAkAlpT+5K8FaOJWkBEWtBb1Mabbb
4m9+WWWLgS+Z9M2cT4jzWX/ZkWtIluiC/UDVyUNuKMnKUWb4hppU/pjXvr11AkB21cqgoQTyR71S
1tF5BCs6Dakimv4pbO/6FVhOCSJvf99D8zU1ckk9NQW/nf2OP6+TIXyopZor5sc+/sxeucuxAkBD
CeEvs5Tml10/xQuZb1cz8CpxoC5nkcrPYLU3UglwtzSI8l5Ga8Y1qS+ruK2W1qKCi4ybpKqzsXSi
z4yIibTa

公钥: MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUqZtt5rhcaNxaqSRzV+ExNT8f42kN1r+vDvHx
Dhc2YXw6SbFVRYW7fTGK8wrJzXuThMo1aZzTKEFFMcaKi/bT7uOVtr2ZyFaHmGTx5YorWggQtZif
2rzdnlwST/ZxPSgKeOUJSrk8Wk31V/aDX6knYOZvmjlAHx/cN2jSroy3EwIDAQAB

*/

    @Test
    public static void testWeb() throws Exception {
        String pass = "6622510976100240";
        String iv = "0804001403470363";

        String data = "B4BEDDA594846256CBBFCBD34E487FE6AFA36158FABCCA12A216B2E4F7DE984801178BE1BF39581923280B35F8B263C852460AD23CE2DB3ACD16F089D63AF3863C1321738763F3E6841613FF2519AB45A936D3E0792D7195A4C1448E021A31D6D4B66B91BDD31C7EB6D70DFCCD6F3E9B0ABF278A24D008C0D18E909B5EE272581DEEEF6D07B9FCCEDB5B4AD4BB8D0F5EF3A7243AC47B2999447DA0CE12035C71C348DDA7C5B8AE2828750D51061BC556E5A9EF08ECAC5EF17D8A6519F118D15EBDFEF28A497646813626FDF69ED424B3";

        AESEncrypt encrypt = new AESEncrypt();
        encrypt.setSecretKey(pass);
        encrypt.setCipherIv(iv);

        byte[] outByte = encrypt.getDecode(EncryptUtil.hexToByte(data));
        System.out.println(new String(outByte, "UTF-8"));
    }

    @Test
    public static void testApacheCode() throws Exception {

        /*
        String string1 = "中文poui34wlkq5j3434095";
        String out1 = DigestUtils.sha1Hex(string1.getBytes("UTF-8"));

        String out2 = EncryptUtil.getSha(string1);

        System.out.println("---------------"+ out1) ;
        System.out.println("---------------"+ out2) ;


        String signA = DigestUtils.md5Hex(string1).toUpperCase();
        String signB =EncryptUtil.getMd5(string1).toUpperCase();
        System.out.println("---------------"+ signA) ;
        System.out.println("---------------"+ signB) ;
*/
    }

    //私钥: 30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100a084cac2ce16d9212c0454ecc3ef0df59b02af0c246935529a97192e8b183b1eb1d8d129b1cf2425c7b926e89ed8cbcdccf7f8b1e5d47e8a51e61ef596dababf52551943f4c42a33761465965c143de8cc1d1c46e541d36c678633cab397a6ecfef6e0e11abb4ffd9e0c288bad138e7e509ccddb61ff25321ca8198440547f5f02030100010281806d4bfe03c6f027c83e8074930296b39a9881ab4f493b99e334ffcd55b3c03c011a77d773dc6e7ede7aafc79e7d0a813b1065882119ad26bc682798cd007acecc8273c6247a64d381e05f32bab9d09bc6e5322a1a420e74ff0b9cdf81d4690ef8b7cd31def5062f37417142fd08b0629d8c6e4943e77d89fd44109105e0a2eb01024100d677642d017b9605a0dbc16c019d5c6295c55cb2e1795904bb7ad858a75308e330f005657720c3c36dbfa108dd6773e0fab31ba01bc26afc580074d1d23e1929024100bf9ad5e72005f3b16fb23e7f76362333c7af51ba211b0c45b4e12985069b3f35bba2916e61c2e921f3da5174f9d17d35408cc5dca2a91f353a864b70e20efd4702403d67799b192d954a8f3fdc255c60fdae8c4b31bb55ab34bd92201e0963fef630763682950682af0a29461089453af58698c0e74bf426ba12fdef43833fbadfe102402d9dfc7d3967915c563e7698bc19298a63693f28f446cf462d494a2c4ca55b883c72f8f2f1bd88e9b777ebdad0bb6a76ea3659c92cc4dd143c29cafe4623d173024100871019df9c174f1e6563ad057d1602b55324212682a4b9a84f3ee7b6057ffeab6d6040eb0fbb8e640b8d6d7b6436ff539d9edd8be3bf723af710ade38a34b020
    //公钥: 30819f300d06092a864886f70d010101050003818d0030818902818100a084cac2ce16d9212c0454ecc3ef0df59b02af0c246935529a97192e8b183b1eb1d8d129b1cf2425c7b926e89ed8cbcdccf7f8b1e5d47e8a51e61ef596dababf52551943f4c42a33761465965c143de8cc1d1c46e541d36c678633cab397a6ecfef6e0e11abb4ffd9e0c288bad138e7e509ccddb61ff25321ca8198440547f5f0203010001


    /*
    ----------payData=1f8b0800000000000000cd90314fc24014c7f74bee3bf8094a412168720b8b71d01891c1f1da3e69b5b4cdddd5c8260e58138926cac482934606d9442de1c3d82b71e22bd882206e8e2e77f7bff7cbfbffdff339b02d474095519b14307285098cc8a023dfdfa2c159347892eda6bc6d44a3fbb8f13c09afe4f579dceecb9b5ef47a2983a60c7ad1e06dc658c6240ca2e1687cf7283bc3b87b31092f3fce1ab3a2bc698d1ffa0b19bdbfc4dd30e165ab1d0d5b698fa0b9cc27269f17d7ff26084616dff4810b72486d0e18d54098ae4134bf8e11d575d777c40ead01d14d700e7cea60e427abdd761da8933c46c2123690e99908b792aedd20f3475a374836b7ba962f2cda6dfdfc30d0c13a815d5aaf309b9842781b994c763da7640b45455572aaba51548b6a26b5ccfc8231f268bd6c551d224c06d4a8944a18394950ee511dc811f74e95234de38aa09a0d4ad9037abc3f25e731a7530937bd303280ebccd2be275999cba9cb1e089f397f08b8cc262e8c1a90cc9ac3e80b811899538d020000
        ----------sign=49ae95d63a5581d35cb272d1fde385723387d2d367d3771db355b8b7383cbdbcc56359607ddc9568a6126336a3cbcfae7884e9dc37453e13d887b55585fec0bd6cbcdae1d8f04e02f7fe1284eef0cf2a9a99d254419d72dd08087059d5394d836e86c2a3f53e700e443cb5824d5a1f60771fd8aeb486832f3ed1e7f0071b3702
base64
----------payData=H4sIAAAAAAAAAM2QMU/CQBTH90vuO/gJSkEhaHILi3HQGJHB8do+abW0zd3VyCYOWBOJJsrEgpNGBtlELeHD2Ctx4ivYgiBuji5397/3y/v/3/M5sC1HQJVRmxQwcoUJjMigI9/fosFZNHiS7aa8bUSj+7jxPAmv5PV53O7Lm170eimDpgx60eBtxljGJAyi4Wh89yg7w7h7MQkvP84as6K8aY0f+gsZvb/E3TDhZasdDVtpj6C5zCcmnxfX/yYIRhbf9IELckhtDhjVQJiuQTS/jhHVddd3xA6tAdFNcA586mDkJ6vddh2okzxGwhI2kOmZCLeSrt0g80daN0g2t7qWLyzabf38MNDBOoFdWq8wm5hCeBuZTHY9p2QLRUVVcqq6UVSLaia1zPyCMfJovWxVHSJMBtSolEoYOUlQ7lEdyBH3TpUjTeOKoJoNStkDerw/Jecxp1MJN70wMoDrzNK+J1mZy6nLHgifOX8IuMwmLowakMyaw+gLgRiZU40CAAA=
    ----------sign=15856ab2e2552ac7a990c8013ca7d7745b76e59185b678d412536325bcc4bc9ae53decf446c9e9ba4c3f438be87bae3a734c11ac1348268032482efd1fc81065c9574cbad9d1bac9ce29cef325962148983a5427729d6115def26b9df652da1009f8369175f9dcb5dcf8cf5738a2fffc068bd2fc836bd6f44f6d4376c0c75eb2
H4sIAAAAAAAAAI2Vy45bRRCG95b8DmjWyFN9q+6OdBasUARBURIeoLuqesaJxzM69gQMYoM
     */

    @Test
    public static void testGetJVMV() throws Exception {

        System.out.println(System.getProperty("java.version"));

    }

    @Test
    public static void testMD5() throws Exception {
        String txt = "中的发生来的快放假； 辣的是打发了就大的发生的拉夫科技大厦";
        String md5A = DigestUtils.md5Hex(txt);
        String md5B = EncryptUtil.getMd5(txt);

        System.out.println(md5A);
        System.out.println(md5B);
        System.out.println(md5A.equals(md5B));
    }


    @Test
    public static void testJsencrypt() throws Exception {
        //这里是Jsencrypt的密码
        RSAEncrypt encrypt = new RSAEncrypt();
        String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKhGvN9qvQ9k/Ffjkt0CiFGO9OIJ\n" +
                "OJsJezKGZLI7up2owRHSl67Q61YzlSIfdCTyugY/k8zplCjpe57MTaDzQ/uryQ4z209+/JdCKgFh\n" +
                "o5E6UPzalpETo9QxSkPPyinyrWpGQ+gI1TIUdCVdUcBm/f9RiQZLI5PX+kAavt9jRfF9AgMBAAEC\n" +
                "gYA5Oa0bUUIwDQifnt2f2S+ghLwUbkxYtPW3beqqjhNdcDkQJYy9v4B4lRwfOZEWy5CldVN++OQQ\n" +
                "sa3/h7zSgkfYrSw0DxdZ4kWfU+wlqY+0SF86ctgQe85AEKNnZTujetaXAoFW2KOTIKED8HXyuqgV\n" +
                "c/moRvE+DsiPva42Ob8NgQJBAOjvX3+uRj1XzZo/K3mi60RLHRBEa3MgMr5eDd2HWeq4IQE/kjCX\n" +
                "rvBz4/7cqIBIxgJimopsn/yY8XgZrSzp/p0CQQC48Fe5el0Z0GsJBNWWhkLgoRIQ414EDEOmu6U1\n" +
                "es0bqB8c7Mugr8hm6Id3OT8WFJhcA4KvOekMP4Tw2JaAaNhhAkEAn5qM2+akmBmWdi78cf7Qp120\n" +
                "T1YiLaMNQeUeKQE5+hsp+jn9yC/WnUk7GoFFL4ktq6q2a0ycnq6JHZqyTwkR/QJAda/0k0KsBmZc\n" +
                "BsQ9y2krG9lELt0Rkg/f6TWWH59C8OueyQBjYKEcLWih9BvI6ps2ja9qNWnrxPCS/9VEiPivYQJA\n" +
                "A8v0YCKz36RGMln86YT6E5RMMZ/WhxMLDwy2yijC4mS84ksr1KHCtZv7FFP4UlNIeMlZS0d6Es1r\n" +
                "XgRF946E6Q==";
        String data = "bjQ+VVSMlVYfAqVQqyOGdgDNdkzJn95elKgUbe+LpTqI9l8yPc4MequPsmj0a8F+rt11FjyEMWe73zVam/FKbhPATLqa2YIlWdiBF6Mzh0rNbJhga0s6ZXnZ246x2hYMzkTErE+wfMiyMieeO5fY7yPeevD9eQ6ypIoe10Rey7Y=";

        String txt = "This is a test!";
        String out = encrypt.decryptByPrivateKey(data, priKey,EncryptUtil.DEFAULT);
        Assert.assertEquals(txt, EncryptUtil.getBase64DecodeString(out,EncryptUtil.DEFAULT));

    }


    @Test
    public void createRSAZip() throws Exception {
        //先加密,在压缩   A,D
        //AZB:AES ZIP Base64
        //AZH:AES ZIP HEX

        //RZB:RSA ZIP Base64



        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();


        String data = "在“合作伙伴密钥管理”那一块上传公钥,compress压缩解压文件测试,<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
                "<link id=\"RSSLink\" title=\"RSS\" type=\"application/rss+xml\" rel=\"alternate\" href=\"/yo746862873/rss/list\" />\n" +
                "    <link rel=\"shortcut icon\" href=\"http://c.csdnimg.cn/public/favicon.ico\" />" +
                "<meta http-equiv=\"Cache-Control\" content=\"no-siteapp\" /><link rel=\"alternate\" media=\"handheld\" href=\"#\" />";

        //这里是先加密,在压缩
        byte[] dataE = encrypt.getEncode(data.getBytes(defaultEncode));
        byte[] zipConData = ZipUtil.zip(dataE);
        System.out.println("HEXA:"+ EncryptUtil.byteToHex(zipConData));
        String data64 = EncryptUtil.getBase64Encode(zipConData);
        System.out.println("B64A:"+ data64);
        //System.out.println("Z64:"+  ZipUtil.compressChar(data64));

        //这里是先压缩,在加密
        byte[] zipData = ZipUtil.zip(data.getBytes(defaultEncode));
        byte[] dataEA = encrypt.getEncode(zipData);

        System.out.println("HEXB:"+ EncryptUtil.byteToHex(dataEA));
        String data64A = EncryptUtil.getBase64Encode(dataEA);
        System.out.println("B64B:"+ data64A);


        //对比结果,先压缩在加密,比较短小一点,而且压缩后数据比较小,加密也比较快一点

        //----------------------------------------------------------
        byte[] zipConData64 = EncryptUtil.getBase64Decode(data64);
        byte[] zData = ZipUtil.unZip(zipConData64);
        byte[] dataD = encrypt.getDecode(zData);
        Assert.assertEquals(data, new String(dataD, defaultEncode));

        ZipUtil.zip("".getBytes(defaultEncode));
    }

    @Test
    public void testSysSign() throws Exception {

       // String privateKey =  EnvFactory.getPrivateKey();

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();

        String secretKey = envTemplate.getString(Environment.secretKey, Environment.defaultDrug);
       // String  cipherIv = envTemplate.getString(Environment.cipherIv);

        AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();

        byte[] sendKey = asyEncrypt.encryptByPrivateKey(secretKey.getBytes("UTF-8"), EnvFactory.getPrivateKey());
        String sendSecretKey = EncryptUtil.byteToHex(sendKey);
        System.out.println("-----------sendSecretKey=" + sendSecretKey);
        String sign = EncryptUtil.byteToHex(asyEncrypt.sign(sendKey, EnvFactory.getPrivateKey()));
        System.out.println("-----------sign=" + sign);
        boolean v = asyEncrypt.verify(sendKey, EnvFactory.getPublicKey(), EncryptUtil.hexToByte(sign));
        System.out.println("-----------verify=" + v);
    }

    @Test
    public void shaHex() throws Exception {
      String str ="accountId=10000\n" +
              "accountName=chenyuan\n" +
              "currency=money\n" +
              "describe=冻结悬赏报酬金\n" +
              "integral=0\n" +
              "lockEnd=0\n" +
              "lockRunningId=0\n" +
              "markPrice=2.0\n" +
              "method=lock\n" +
              "money=2.0\n" +
              "namespace=jspx.jbbs.table.SpeakThread\n" +
              "organizeId=10000\n" +
              "payChannels=\n" +
              "paySign=rewardLock\n" +
              "putName=chenyuan\n" +
              "putUid=10000\n" +
              "remark=\n" +
              "replacePay=0\n" +
              "runningId=10\n" +
              "sellerId=\n" +
              "sellerName=\n" +
              "tid=25\n" +
              "title=ssssssssssss\n" +
              "toUserId=10000\n" +
              "toUserName=system\n" +
              "tradeId=25";
        StringMap stringMap = new StringMap();
        stringMap.setString(str);

        //验证
        AsyEncrypt encrypt = new RSAEncrypt();
        byte[] sign = encrypt.sign(stringMap.toString().getBytes(defaultEncode), EnvFactory.getPrivateKey());
        String baseSign = EncryptUtil.getBase64Encode(sign);
        System.out.println("sign：" + EncryptUtil.getBase64Encode(sign));
        Assert.assertEquals(encrypt.verify(stringMap.toString().getBytes(defaultEncode), EnvFactory.getPublicKey(), EncryptUtil.getBase64Decode(baseSign)), true);


    }

    @Test
    public void testRocAes() throws Exception {
        String str = "{\"keyType\":\"rsa\",\"dataType\":\"aes\",\"key\":\"KAXK9EfxakkS1zvmt1s3VohcArlth9SO037Yc3ytw0JKWYYXiRF5VKTAfoKvRcYI2OAVtLiQVYWI91BaZzTKAb1Led7BlEft3xPg4zC+Vdug05PG0XBzF+Fb7SVjPp0w8l3r0hXBpF2qBTy8hCbIBR2XYMwbgIaqwTmJPPd+3Dk: \",\"data\":\"FB3406460398D3D76D864D4FEB7B365B8E167211D59C25FED857032325625BD85342D19E03FD6196CEFC0BBD8B7218D7AA00F64A18727F90C3703E91FB61941079D3BFF8F578A7AA5917F6AF9CEBE6666127C1879690116527FEDA1E32DA0A68688EC016FF06702B13FD2E54BEC6784473D9D3210A52000B7708ADBE84770510\"}";
        JSONObject json = new JSONObject(str);
        String data =  RsaRocHandle.getSecretDecode(json);
        System.out.println("-----------data=" + data);
    }
}
