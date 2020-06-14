package com.github.jspxnet.security.sm;

import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import static com.github.jspxnet.boot.environment.Environment.defaultEncode;

public class SM3Digest {
    private static BigInteger p = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);
    private static BigInteger a = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16);
    private static BigInteger b = new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16);
    private static BigInteger n = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
    private static BigInteger Gx = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    private static BigInteger Gy = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);

    /**
     * SM3值的长度
     */
    private static final int BYTE_LENGTH = 32;

    /**
     * SM3分组长度
     */
    private static final int BLOCK_LENGTH = 64;

    /**
     * 缓冲区长度
     */
    private static final int BUFFER_LENGTH = BLOCK_LENGTH * 1;

    /**
     * 缓冲区
     */
    private byte[] xBuf = new byte[BUFFER_LENGTH];

    /**
     * 缓冲区偏移量
     */
    private int xBufOff;

    /**
     * 初始向量
     */
    private byte[] V = SM3.iv.clone();

    private int cntBlock = 0;

    public SM3Digest() {
    }

    public SM3Digest(SM3Digest t) {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        System.arraycopy(t.V, 0, this.V, 0, t.V.length);
    }

    /**
     * @param out    保存SM3结构的缓冲区
     * @param outOff 缓冲区偏移量
     * @return SM3结果输出
     */
    public int doFinal(byte[] out, int outOff) {
        byte[] tmp = doFinal();
        System.arraycopy(tmp, 0, out, 0, tmp.length);
        return BYTE_LENGTH;
    }

    public void reset() {
        xBufOff = 0;
        cntBlock = 0;
        V = SM3.iv.clone();
    }

    /**
     * 明文输入
     *
     * @param in    明文输入缓冲区
     * @param inOff 缓冲区偏移量
     * @param len   明文长度
     */
    public void update(byte[] in, int inOff, int len) {
        int partLen = BUFFER_LENGTH - xBufOff;
        int inputLen = len;
        int dPos = inOff;
        if (partLen < inputLen) {
            System.arraycopy(in, dPos, xBuf, xBufOff, partLen);
            inputLen -= partLen;
            dPos += partLen;
            doUpdate();
            while (inputLen > BUFFER_LENGTH) {
                System.arraycopy(in, dPos, xBuf, 0, BUFFER_LENGTH);
                inputLen -= BUFFER_LENGTH;
                dPos += BUFFER_LENGTH;
                doUpdate();
            }
        }

        System.arraycopy(in, dPos, xBuf, xBufOff, inputLen);
        xBufOff += inputLen;
    }

    private void doUpdate() {
        byte[] B = new byte[BLOCK_LENGTH];
        for (int i = 0; i < BUFFER_LENGTH; i += BLOCK_LENGTH) {
            System.arraycopy(xBuf, i, B, 0, B.length);
            doHash(B);
        }
        xBufOff = 0;
    }

    private void doHash(byte[] B) {
        byte[] tmp = SM3.CF(V, B);
        System.arraycopy(tmp, 0, V, 0, V.length);
        cntBlock++;
    }

    private byte[] doFinal() {
        byte[] B = new byte[BLOCK_LENGTH];
        byte[] buffer = new byte[xBufOff];
        System.arraycopy(xBuf, 0, buffer, 0, buffer.length);
        byte[] tmp = SM3.padding(buffer, cntBlock);
        for (int i = 0; i < tmp.length; i += BLOCK_LENGTH) {
            System.arraycopy(tmp, i, B, 0, B.length);
            doHash(B);
        }
        return V;
    }

    public void update(byte in) {
        byte[] buffer = new byte[]{in};
        update(buffer, 0, 1);
    }

    public int getDigestSize() {
        return BYTE_LENGTH;
    }


    public byte[] getSM2Za(byte[] x, byte[] y, byte[] id) {
        byte[] tmp = IntToByte(id.length * 8);
        byte[] buffer = new byte[32 * 6 + 2 + id.length];
        buffer[0] = tmp[1];
        buffer[1] = tmp[0];
        byte[] a = getA();
        // printHexString(a);
        System.out.println();
        byte[] b = getB();
        // printHexString(b);
        System.out.println();
        byte[] gx = getGx();
        // printHexString(gx);
        byte[] gy = getGy();
        System.out.println();
        // printHexString(gy);
        int dPos = 2;
        System.arraycopy(id, 0, buffer, dPos, id.length);
        dPos += id.length;
        System.arraycopy(a, 0, buffer, dPos, 32);
        dPos += 32;
        System.arraycopy(b, 0, buffer, dPos, 32);
        dPos += 32;
        System.arraycopy(gx, 0, buffer, dPos, 32);
        dPos += 32;
        System.arraycopy(gy, 0, buffer, dPos, 32);
        dPos += 32;
        System.arraycopy(x, 0, buffer, dPos, 32);
        dPos += 32;
        System.arraycopy(y, 0, buffer, dPos, 32);
        dPos += 32;

        //
        // PrintUtil.printWithHex(buffer);
        SM3Digest digest = new SM3Digest();
        System.out.println();
        // printHexString(buffer);
        digest.update(buffer, 0, buffer.length);
        System.out.println();
        // printHexString(buffer);
        byte[] out = new byte[32];
        digest.doFinal(out, 0);

        return out;
    }

    public static byte[] getP() {
        return asUnsigned32ByteArray(p);
    }

    public static byte[] getA() {
        return asUnsigned32ByteArray(a);
    }

    public static byte[] getB() {
        return asUnsigned32ByteArray(b);
    }

    public static byte[] getN() {
        return asUnsigned32ByteArray(n);
    }

    public static byte[] getGx() {
        return asUnsigned32ByteArray(Gx);
    }

    public static byte[] getGy() {
        return asUnsigned32ByteArray(Gy);
    }

    public static byte[] asUnsigned32ByteArray(BigInteger n) {
        return asUnsignedNByteArray(n, 32);
    }

    public static byte[] asUnsignedNByteArray(BigInteger x, int length) {
        if (x == null) {
            return null;
        }

        byte[] tmp = new byte[length];
        int len = x.toByteArray().length;
        if (len > length + 1) {
            return null;
        }

        if (len == length + 1) {
            if (x.toByteArray()[0] != 0) {
                return null;
            } else {
                System.arraycopy(x.toByteArray(), 1, tmp, 0, length);
                return tmp;
            }
        } else {
            System.arraycopy(x.toByteArray(), 0, tmp, length - len, len);
            return tmp;
        }

    }

    /**
     * 整形转换成网络传输的字节流（字节数组）型数据
     *
     * @param num 一个整型数据
     * @return 4个字节的自己数组
     */
    public static byte[] IntToByte(int num) {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) (0xff & (num >> 0));
        bytes[1] = (byte) (0xff & (num >> 8));
        bytes[2] = (byte) (0xff & (num >> 16));
        bytes[3] = (byte) (0xff & (num >> 24));

        return bytes;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        byte[] md = new byte[32];
        byte[] msg1 = "abc".getBytes(defaultEncode);
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg1, 0, msg1.length);
        sm3.doFinal(md, 0);
        String s = new String(Hex.encode(md));
        System.out.println(s);
    }
}
