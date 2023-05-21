package com.github.jspxnet.security.sm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.KeyPairGen;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

/**
 * SM2公钥加密算法实现 包括 -签名,验签 -密钥交换 -公钥加密,私钥解密
 */
public class SM2 {
    private static BigInteger n = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16);
    private static BigInteger p = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16);
    private static BigInteger a = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16);
    private static BigInteger b = new BigInteger(
            "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16);
    private static BigInteger gx = new BigInteger(
            "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16);
    private static BigInteger gy = new BigInteger(
            "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16);
    private static ECDomainParameters ecc_bc_spec;
    private static int w = (int) Math.ceil(n.bitLength() * 1.0 / 2) - 1;
    private static BigInteger BIG_2W = new BigInteger("2").pow(w);
    private static final int DIGEST_LENGTH = 32;

    private static SecureRandom random = new SecureRandom();
    private static ECCurve.Fp curve;
    private static ECPoint G;


    /**
     * @param b 以16进制打印字节数组
     */
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }
        System.out.println();
    }

    /**
     * 随机数生成器
     *
     * @param max 最大数
     * @return 随机数生成器
     */
    private static BigInteger random(BigInteger max) {

        BigInteger r = new BigInteger(256, random);
        // int count = 1;

        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, random);
            // count++;
        }

        // System.out.println("count: " + count);
        return r;
    }

    /**
     * @param buffer byte数据
     * @return 判断字节数组是否全0
     */
    private boolean allZero(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 公钥加密
     *
     * @param input   加密原文
     * @param publicK 公钥
     * @return 公钥加密
     */
    public byte[] encrypt(String input, byte[] publicK) {
        ECPoint publicKey = curve.decodePoint(publicK);
        byte[] inputBuffer = input.getBytes();
        byte[] C1Buffer;
        ECPoint kpb;
        byte[] t;
        do {
            /* 1 产生随机数k，k属于[1, n-1] */
            BigInteger k = random(n);
            /* 2 计算椭圆曲线点C1 = [k]G = (x1, y1) */
            ECPoint C1 = G.multiply(k);
            C1Buffer = C1.getEncoded(false);

            /*
             * 3 计算椭圆曲线点 S = [h]Pb
             */
            BigInteger h = ecc_bc_spec.getH();
            if (h != null) {
                ECPoint S = publicKey.multiply(h);
                if (S.isInfinity()) {
                    throw new IllegalStateException();
                }
            }

            /* 4 计算 [k]PB = (x2, y2) */
            kpb = publicKey.multiply(k).normalize();

            /* 5 计算 t = KDF(x2||y2, klen) */
            byte[] kpbBytes = kpb.getEncoded(false);
            t = KDF(kpbBytes, inputBuffer.length);
            // DerivationFunction kdf = new KDF1BytesGenerator(new
            // ShortenedDigest(new SHA256Digest(), DIGEST_LENGTH));
            //
            // t = new byte[inputBuffer.length];
            // kdf.init(new ISO18033KDFParameters(kpbBytes));
            // kdf.generateBytes(t, 0, t.length);
        } while (allZero(t));

        /* 6 计算C2=M^t */
        byte[] C2 = new byte[inputBuffer.length];
        for (int i = 0; i < inputBuffer.length; i++) {
            C2[i] = (byte) (inputBuffer[i] ^ t[i]);
        }

        /* 7 计算C3 = Hash(x2 || M || y2) */
        byte[] C3 = sm3hash(kpb.getXCoord().toBigInteger().toByteArray(), inputBuffer,
                kpb.getYCoord().toBigInteger().toByteArray());

        /* 8 输出密文 C=C1 || C2 || C3 */

        byte[] encryptResult = new byte[C1Buffer.length + C2.length + C3.length];

        System.arraycopy(C1Buffer, 0, encryptResult, 0, C1Buffer.length);
        System.arraycopy(C2, 0, encryptResult, C1Buffer.length, C2.length);
        System.arraycopy(C3, 0, encryptResult, C1Buffer.length + C2.length, C3.length);
        return encryptResult;
    }

    /**
     * 私钥解密
     *
     * @param encryptData 密文数据字节数组
     * @param privateK    解密私钥
     * @return 解密文件
     */
    public String decrypt(byte[] encryptData, byte[] privateK) {

        BigInteger privateKey = new BigInteger(privateK);
        byte[] C1Byte = new byte[65];
        System.arraycopy(encryptData, 0, C1Byte, 0, C1Byte.length);
        ECPoint C1 = curve.decodePoint(C1Byte).normalize();
        /*
         * 计算椭圆曲线点 S = [h]C1 是否为无穷点
         */
        BigInteger h = ecc_bc_spec.getH();
        if (h != null) {
            ECPoint S = C1.multiply(h);
            if (S.isInfinity()) {
                throw new IllegalStateException();
            }
        }
        /* 计算[dB]C1 = (x2, y2) */
        ECPoint dBC1 = C1.multiply(privateKey).normalize();

        /* 计算t = KDF(x2 || y2, klen) */
        byte[] dBC1Bytes = dBC1.getEncoded(false);
        int klen = encryptData.length - 65 - DIGEST_LENGTH;
        byte[] t = KDF(dBC1Bytes, klen);
        // DerivationFunction kdf = new KDF1BytesGenerator(new
        // ShortenedDigest(new SHA256Digest(), DIGEST_LENGTH));
        // if (debug)
        // System.out.println("klen = " + klen);
        // kdf.init(new ISO18033KDFParameters(dBC1Bytes));
        // kdf.generateBytes(t, 0, t.length);

        if (allZero(t)) {
            System.err.println("all zero");
            throw new IllegalStateException();
        }

        /* 5 计算M'=C2^t */
        byte[] M = new byte[klen];
        for (int i = 0; i < M.length; i++) {
            M[i] = (byte) (encryptData[C1Byte.length + i] ^ t[i]);
        }
        /* 6 计算 u = Hash(x2 || M' || y2) 判断 u == C3是否成立 */
        byte[] C3 = new byte[DIGEST_LENGTH];


        System.arraycopy(encryptData, encryptData.length - DIGEST_LENGTH, C3, 0, DIGEST_LENGTH);
        byte[] u = sm3hash(dBC1.getXCoord().toBigInteger().toByteArray(), M,
                dBC1.getYCoord().toBigInteger().toByteArray());
        if (Arrays.equals(u, C3)) {
            try {
                return new String(M, Environment.defaultEncode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 判断是否在范围内
     *
     * @param param 参数
     * @param min   最小
     * @param max   最大
     * @return 期间数据
     */
    private boolean between(BigInteger param, BigInteger min, BigInteger max) {
        return param.compareTo(min) >= 0 && param.compareTo(max) < 0;
    }

    /**
     * 判断生成的公钥是否合法
     *
     * @param publicK 公密
     * @return 判断生成的公钥是否合法
     */
    private boolean checkPublicKey(byte[] publicK) {

        ECPoint publicKey = curve.decodePoint(publicK);
        if (!publicKey.isInfinity()) {

            BigInteger x = publicKey.getXCoord().toBigInteger();
            BigInteger y = publicKey.getYCoord().toBigInteger();

            if (between(x, new BigInteger("0"), p) && between(y, new BigInteger("0"), p)) {

                BigInteger xResult = x.pow(3).add(a.multiply(x)).add(b).mod(p);
                BigInteger yResult = y.pow(2).mod(p);
                return yResult.equals(xResult) && publicKey.multiply(n).isInfinity();
            }
        }
        return false;
    }

    /**
     * @return 生成密钥对
     */
    public KeyPairGen generateKeyPair() {

        BigInteger d = random(n.subtract(new BigInteger("1")));
        ECPoint publicKey = G.multiply(d).normalize();
        KeyPairGen keyPairGen = new KeyPairGen("sm2", publicKey.getEncoded(false), d.toByteArray());

        if (checkPublicKey(keyPairGen.getPublicKey())) {
            return keyPairGen;
        }
        return null;
    }

    public SM2() {
        curve = new ECCurve.Fp(p, // q
                a, // a
                b); // b
        G = curve.createPoint(gx, gy);
        ecc_bc_spec = new ECDomainParameters(curve, G, n);
    }


    /**
     * @param params 参数
     * @return 字节数组拼接
     */
    private static byte[] join(byte[]... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] res = null;
        try {
            for (int i = 0; i < params.length; i++) {
                baos.write(params[i]);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @param params 参数
     * @return sm3摘要
     */
    private static byte[] sm3hash(byte[]... params) {
        byte[] res = null;
        try {
            res = SM3Util.hash(join(params));
        } catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @param idaBytes ida头数据
     * @param publicK  公密
     * @return 取得用户标识字节数组
     */
    private static byte[] ZA(byte[] idaBytes, byte[] publicK) {
        ECPoint publicKey = curve.decodePoint(publicK);
        return ZA(idaBytes, publicKey);
    }


    private static byte[] ZA(byte[] idaBytes, ECPoint publicKey) {
        int entlenA = idaBytes.length * 8;
        byte[] ENTLA = new byte[]{(byte) (entlenA & 0xFF00), (byte) (entlenA & 0x00FF)};
        byte[] ZA = sm3hash(ENTLA, idaBytes, a.toByteArray(), b.toByteArray(), gx.toByteArray(), gy.toByteArray(),
                publicKey.getXCoord().toBigInteger().toByteArray(),
                publicKey.getYCoord().toBigInteger().toByteArray());
        return ZA;
    }



    /**
     * 签名
     *
     * @param userId   签名信息
     * @param idaBytes 签名方唯一标识
     * @param keyPair  签名方密钥对
     * @return 签名
     */
    public Signature sign(byte[] userId, byte[] idaBytes, KeyPairGen keyPair)
    {
        byte[] ZA = ZA(idaBytes, keyPair.getPublicKey());
        byte[] M_ = join(ZA, userId);
        BigInteger e = new BigInteger(1, sm3hash(M_));
        BigInteger k;
        BigInteger r;
        do {
            k = random(n);
            ECPoint p1 = G.multiply(k).normalize();
            BigInteger x1 = p1.getXCoord().toBigInteger();
            r = e.add(x1);
            r = r.mod(n);
        } while (r.equals(BigInteger.ZERO) || r.add(k).equals(n));
        BigInteger privateKey = new BigInteger(keyPair.getPrivateKey());
        BigInteger s = ((privateKey.add(BigInteger.ONE).modInverse(n))
                .multiply((k.subtract(r.multiply(privateKey))).mod(n))).mod(n);
        return new Signature(r, s);
    }

    /**
     * 验签
     *
     * @param userId    签名信息
     * @param signature 签名
     * @param idaBytes  签名方唯一标识
     * @param publicK   签名方公钥
     * @return true or false
     */
    public boolean verify(byte[] userId, Signature signature, byte[] idaBytes, byte[] publicK) {
        ECPoint publicKey = curve.decodePoint(publicK);
        if (!between(signature.r, BigInteger.ONE, n)) {
            return false;
        }
        if (!between(signature.s, BigInteger.ONE, n)) {
            return false;
        }

        byte[] M_ = join(ZA(idaBytes, publicK), userId);
        BigInteger e = new BigInteger(1, sm3hash(M_));
        BigInteger t = signature.r.add(signature.s).mod(n);
        if (t.equals(BigInteger.ZERO)) {
            return false;
        }

        ECPoint p1 = G.multiply(signature.s).normalize();
        ECPoint p2 = publicKey.multiply(t).normalize();
        BigInteger x1 = p1.add(p2).normalize().getXCoord().toBigInteger();
        BigInteger R = e.add(x1).mod(n);
        return R.equals(signature.r);
    }

    /**
     * 密钥派生函数
     *
     * @param Z    数据
     * @param klen 生成klen字节数长度的密钥
     * @return 密钥派生函数
     */
    private static byte[] KDF(byte[] Z, int klen) {
        int ct = 1;
        int end = (int) Math.ceil(klen * 1.0 / 32);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (int i = 1; i < end; i++) {
                baos.write(sm3hash(Z, SM3Util.toByteArray(ct)));
                ct++;
            }
            byte[] last = sm3hash(Z, SM3Util.toByteArray(ct));
            if (klen % 32 == 0) {
                baos.write(last);
            } else {
                baos.write(last, 0, klen % 32);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传输实体类
     *
     * @author Potato
     */
    private static class TransportEntity implements Serializable {
        final byte[] R; //R点
        final byte[] S; //验证S
        final byte[] Z; //用户标识
        final byte[] K; //公钥

        public TransportEntity(byte[] r, byte[] s, byte[] z, ECPoint pKey) {
            R = r;
            S = s;
            Z = z;
            K = pKey.getEncoded(false);
        }

        public TransportEntity(byte[] r, byte[] s, byte[] z, byte[] pKey) {
            R = r;
            S = s;
            Z = z;
            K = pKey;
        }
    }

    /**
     * 密钥协商辅助类
     *
     * @author Potato
     */
    public static class KeyExchange {
        BigInteger rA;
        ECPoint RA;
        ECPoint V;
        byte[] Z;
        byte[] key;

        byte[] ID;
        KeyPairGen keyPair;

        public KeyExchange(byte[] idaBytes, KeyPairGen keyPair) {
            this.ID = idaBytes;
            this.keyPair = keyPair;

            this.Z = ZA(idaBytes, keyPair.getPublicKey());
        }

        /**
         * @return 密钥协商发起第一步
         */
        public TransportEntity keyExchange_1() {
            rA = random(n);
            // rA=new BigInteger("83A2C9C8 B96E5AF7 0BD480B4 72409A9A 327257F1
            // EBB73F5B 073354B2 48668563".replace(" ", ""),16);
            RA = G.multiply(rA).normalize();
            return new TransportEntity(RA.getEncoded(false), null, Z, keyPair.getPublicKey());
        }

        /**
         * @param entity 传输实体
         * @return 密钥协商响应方
         */
        public TransportEntity keyExchange_2(TransportEntity entity) {
            BigInteger rB = random(n);
            // BigInteger rB=new BigInteger("33FE2194 0342161C 55619C4A 0C060293
            // D543C80A F19748CE 176D8347 7DE71C80".replace(" ", ""),16);
            ECPoint RB = G.multiply(rB).normalize();

            this.rA = rB;
            this.RA = RB;

            BigInteger x2 = RB.getXCoord().toBigInteger();
            x2 = BIG_2W.add(x2.and(BIG_2W.subtract(BigInteger.ONE)));

            BigInteger privateKey = new BigInteger(keyPair.getPrivateKey());
            BigInteger tB = privateKey.add(x2.multiply(rB)).mod(n);
            ECPoint RA = curve.decodePoint(entity.R).normalize();

            BigInteger x1 = RA.getXCoord().toBigInteger();
            x1 = BIG_2W.add(x1.and(BIG_2W.subtract(BigInteger.ONE)));

            ECPoint aPublicKey = curve.decodePoint(entity.K).normalize();
            ECPoint temp = aPublicKey.add(RA.multiply(x1).normalize()).normalize();
            ECPoint V = temp.multiply(ecc_bc_spec.getH().multiply(tB)).normalize();
            if (V.isInfinity()) {
                throw new IllegalStateException();
            }
            this.V = V;

            byte[] xV = V.getXCoord().toBigInteger().toByteArray();
            byte[] yV = V.getYCoord().toBigInteger().toByteArray();
            byte[] KB = KDF(join(xV, yV, entity.Z, this.Z), 16);
            key = KB;
            System.out.print("协商得B密钥:");
            printHexString(KB);
            byte[] sB = sm3hash(new byte[]{0x02}, yV,
                    sm3hash(xV, entity.Z, this.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), RB.getXCoord().toBigInteger().toByteArray(),
                            RB.getYCoord().toBigInteger().toByteArray()));
            return new TransportEntity(RB.getEncoded(false), sB, this.Z, keyPair.getPublicKey());
        }


        /**
         * 密钥协商发起方第二步
         *
         * @param entity 传输实体
         * @return 密钥协商发起方第二步
         */
        public TransportEntity keyExchange_3(TransportEntity entity) {
            BigInteger x1 = RA.getXCoord().toBigInteger();
            x1 = BIG_2W.add(x1.and(BIG_2W.subtract(BigInteger.ONE)));
            BigInteger privateKey = new BigInteger(keyPair.getPrivateKey());
            BigInteger tA = privateKey.add(x1.multiply(rA)).mod(n);
            ECPoint RB = curve.decodePoint(entity.R).normalize();

            BigInteger x2 = RB.getXCoord().toBigInteger();
            x2 = BIG_2W.add(x2.and(BIG_2W.subtract(BigInteger.ONE)));

            ECPoint bPublicKey = curve.decodePoint(entity.K).normalize();
            ECPoint temp = bPublicKey.add(RB.multiply(x2).normalize()).normalize();
            ECPoint U = temp.multiply(ecc_bc_spec.getH().multiply(tA)).normalize();
            if (U.isInfinity()) {
                throw new IllegalStateException();
            }
            this.V = U;

            byte[] xU = U.getXCoord().toBigInteger().toByteArray();
            byte[] yU = U.getYCoord().toBigInteger().toByteArray();
            byte[] KA = KDF(join(xU, yU,
                    this.Z, entity.Z), 16);
            key = KA;
            System.out.print("协商得A密钥:");
            printHexString(KA);
            byte[] s1 = sm3hash(new byte[]{0x02}, yU,
                    sm3hash(xU, this.Z, entity.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), RB.getXCoord().toBigInteger().toByteArray(),
                            RB.getYCoord().toBigInteger().toByteArray()));
            if (Arrays.equals(entity.S, s1)) {
                System.out.println("B->A 密钥确认成功");
            } else {
                System.out.println("B->A 密钥确认失败");
            }
            byte[] sA = sm3hash(new byte[]{0x03}, yU,
                    sm3hash(xU, this.Z, entity.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), RB.getXCoord().toBigInteger().toByteArray(),
                            RB.getYCoord().toBigInteger().toByteArray()));

            return new TransportEntity(RA.getEncoded(false), sA, this.Z, keyPair.getPublicKey());
        }

        /**
         * 密钥确认最后一步
         *
         * @param entity 传输实体
         */
        public void keyExchange_4(TransportEntity entity) {
            byte[] xV = V.getXCoord().toBigInteger().toByteArray();
            byte[] yV = V.getYCoord().toBigInteger().toByteArray();
            ECPoint RA = curve.decodePoint(entity.R).normalize();
            byte[] s2 = sm3hash(new byte[]{0x03}, yV,
                    sm3hash(xV, entity.Z, this.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), this.RA.getXCoord().toBigInteger().toByteArray(),
                            this.RA.getYCoord().toBigInteger().toByteArray()));
            if (Arrays.equals(entity.S, s2)) {
                System.out.println("A->B 密钥确认成功");
            } else {
                System.out.println("A->B 密钥确认失败");
            }
        }
    }


    public static class Signature {
        BigInteger r;
        BigInteger s;

        public Signature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }

        public BigInteger getR() {
            return r;
        }

        public BigInteger getS() {
            return s;
        }

        @Override
        public String toString() {
            return r.toString(16) + "," + s.toString(16);
        }
    }


    public static void main(String[] args) throws UnsupportedEncodingException {

        //头信息
        String SM2_KEY_TITLE = "3059301306072a8648ce3d020106082a811ccf5501822d03420004";

        //签名信息用户
        String USER_ID = "1234567812345678";

        SM2 sm02 = new SM2();
        // BigInteger px = new BigInteger(
        // "0AE4C779 8AA0F119 471BEE11 825BE462 02BB79E2 A5844495 E97C04FF
        // 4DF2548A".replace(" ", ""), 16);
        // BigInteger py = new BigInteger(
        // "7C0240F8 8F1CD4E1 6352A73C 17B7F16F 07353E53 A176D684 A9FE0C6B
        // B798E857".replace(" ", ""), 16);
        // ECPoint publicKey = sm02.curve.createPoint(px, py);
        // BigInteger privateKey = new BigInteger(
        // "128B2FA8 BD433C6C 068C8D80 3DFF7979 2A519A55 171B1B65 0C23661D
        // 15897263".replace(" ", ""), 16);

        // SM2KeyPair keyPair = sm02.generateKeyPair();
        // ECPoint publicKey=keyPair.getPublicKey();
        // BigInteger privateKey=keyPair.getPrivateKey();
        // sm02.exportPublicKey(publicKey, "E:/publickey.pem");
        // sm02.exportPrivateKey(privateKey, "E:/privatekey.pem");
        KeyPairGen aKeyPair = sm02.generateKeyPair();

        System.out.println("-----------------公钥加密与解密-----------------");
        byte[] data = sm02.encrypt("测试加密aaaaaaaaaaa123aabb", aKeyPair.getPublicKey());
        System.out.print("密文:");
        SM2.printHexString(data);
        System.out.println("解密后明文:" + sm02.decrypt(data, aKeyPair.getPrivateKey()));

        System.out.println("-----------------签名与验签-----------------");

        byte[] IDA = SM2_KEY_TITLE.getBytes(StandardCharsets.UTF_8);
        byte[] M = USER_ID.getBytes(StandardCharsets.UTF_8);
        Signature signature = sm02.sign(M, IDA, aKeyPair);
        System.out.println("用户标识:" + new String(IDA, StandardCharsets.UTF_8));
        System.out.println("签名信息:" + new String(M, StandardCharsets.UTF_8));
        System.out.println("数字签名:" + signature);
        System.out.println("验证签名:" + sm02.verify(M, signature, IDA, aKeyPair.getPublicKey()));

        System.out.println("-----------------密钥协商-----------------");
        byte[] aID = "AAAAAAAAAAAAA".getBytes(StandardCharsets.UTF_8);
        aKeyPair = sm02.generateKeyPair();
        KeyExchange aKeyExchange = new KeyExchange(aID, aKeyPair);
        TransportEntity entity1 = aKeyExchange.keyExchange_1();

        byte[] bID = "BBBBBBBBBBBBB".getBytes(StandardCharsets.UTF_8);
        KeyPairGen bKeyPair = sm02.generateKeyPair();
        KeyExchange bKeyExchange = new KeyExchange(bID, bKeyPair);
        TransportEntity entity2 = bKeyExchange.keyExchange_2(entity1);
        TransportEntity entity3 = aKeyExchange.keyExchange_3(entity2);
        bKeyExchange.keyExchange_4(entity3);
    }
}