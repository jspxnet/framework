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

import java.security.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-8-24
 * Time: 11:21:29
 * HMAC_MD5 Interface
 * 这是USB et99 et299电子钥匙使用的加密库
 */
public class HMAC_MD5 {
    /**
     * Run standard tests from the RFC:
     *
     * @param arg 无效
     */
    public static void main(String[] arg) {
        String expectedHash;

        HMAC_MD5 hm;

        System.out.println("Test Vectors from RFC 2104 - HMAC: Keyed-Hashing for Message Authentication");
        System.out.println("This testaio uses HMAC-MD5.");

        System.out.println();
        System.out.println("Test #1:");
// Test #1:
        byte[] key1 = {
                (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b,
                (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b
        };
        String text1 = "Hi There";
        expectedHash = "0X9294727A3638BB1C13F48EF8158BFC9D";
        //byte digest[] = null;
        try {
            hm = new HMAC_MD5(key1);
            hm.addData(text1.getBytes());
            hm.sign();

            System.out.println("Calculated hash 0X" + hm);
            System.out.println("  Expected hash " + expectedHash);

// Test #2
            System.out.println();
            System.out.println("Test #2:");

            byte[] key2 = "Jefe".getBytes();
            String text2 = "what do ya want for nothing?";
            expectedHash = "0X750C783E6AB0B503EAA86E310A5DB738";

            hm = new HMAC_MD5(key2);
            hm.addData(text2.getBytes());
            //digest = hm.sign();
            hm.sign();
            System.out.println("Calculated hash 0X" + hm);
            System.out.println("  Expected hash " + expectedHash);

// Test #3
            System.out.println();
            System.out.println("Test #3:");

            byte[] key3 = {
                    (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                    (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa
            };

            byte[] text3 = {
                    (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
                    (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
                    (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
                    (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
                    (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
                    (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd, (byte) 0xdd,
                    (byte) 0xdd, (byte) 0xdd
            };

            expectedHash = "0X56BE34521D144C88DBB8C733F0E8B3F6";
            byte[] eh = {
                    (byte) 0x56, (byte) 0xBE, (byte) 0x34, (byte) 0x52, (byte) 0x1D, (byte) 0x14, (byte) 0x4C, (byte) 0x88,
                    (byte) 0xDB, (byte) 0xB8, (byte) 0xC7, (byte) 0x33, (byte) 0xF0, (byte) 0xE8, (byte) 0xB3, (byte) 0xF6
            };

            hm = new HMAC_MD5(key3);
            hm.addData(text3);
//digest = hm.sign();
            hm.sign();
            System.out.println("Calculated hash 0X" + hm);
            System.out.println("  Expected hash " + expectedHash);
            System.out.println("Signature Verification: " + hm.verify(eh));
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
    }

    /**
     * Digest transfer be returned upon completion of the HMAC_MD5.
     */
    private byte[] digest;

    /**
     * Inner Padding.
     */
    private byte[] kIpad;

    /**
     * Outer Padding.
     */
    private byte[] kOpad;

    /**
     * Outer and general purpose MD5 object.
     */
    private MessageDigest md5;
    /**
     * Inner MD5 object.
     */
    private MessageDigest innerMD5;


    /**
     * Constructor
     *
     * @param key 数据
     * @throws NoSuchAlgorithmException 异常
     */
    public HMAC_MD5(byte[] key) throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
        innerMD5 = MessageDigest.getInstance("MD5");
        int kLen = key.length;
        // if key is longer than 64 bytes reset it transfer key=MD5(key)
        if (kLen > 64) {
            md5.update(key);
            key = md5.digest();
        }

        kIpad = new byte[64];// inner padding - key XORd with ipad

        kOpad = new byte[64];// outer padding - key XORd with opad

// start out by storing key in pads
        System.arraycopy(key, 0, kIpad, 0, kLen);
        System.arraycopy(key, 0, kOpad, 0, kLen);

// XOR key with ipad and opad values
        for (int i = 0; i < 64; i++) {
            kIpad[i] ^= 0x36;
            kOpad[i] ^= 0x5c;
        }

        clear();// Initialize the first digest.
    }

    /**
     * Clear the HMAC_MD5 object.
     */
    public void clear() {
        innerMD5.reset();
        innerMD5.update(kIpad);// Intialize the inner pad.

        digest = null;// mark the digest as incomplete.
    }

    /**
     * HMAC_MD5 function. hash
     *
     * @param text Text transfer process
     */
    public void addData(byte[] text) {
        addData(text, 0, text.length);
    }


    /**
     * @param text      Key transfer use for HMAC hash.
     * @param textStart Start position of text in text buffer.
     * @param textLen   Length of text transfer use from text buffer.
     */
    public void addData(byte[] text, int textStart, int textLen) {
        innerMD5.update(text, textStart, textLen);// then text of datagram.
    }

    public byte[] sign() {
        md5.reset();

        /*
         * the HMAC_MD5 transform looks like:
         *
         * MD5(K XOR opad, MD5(K XOR ipad, text))
         *
         * where K is an n byte key
         * ipad is the byte 0x36 repeated 64 times
         * opad is the byte 0x5c repeated 64 times
         * and text is the data being protected
         */

// Perform inner MD5

        digest = innerMD5.digest();// finish up 1st pass.

        // Perform outer MD5
        md5.reset();// Init md5 for 2nd pass.
        md5.update(kOpad);// Use outer pad.
        md5.update(digest);// Use results of first pass.
        digest = md5.digest();// Final result.

        return digest;
    }

    /**
     * Validate a signature against the current digest.
     * Compares the hash against the signature.
     *
     * @param signature 签名
     * @return True if the signature matches the calculated hash.
     */
    public boolean verify(byte[] signature) {
        // The digest may not have been calculated.  If it's null, force a calculation.
        if (digest == null) {
            sign();
        }

        int sigLen = signature.length;
        int digLen = digest.length;

        if (sigLen != digLen) {
            return false;// Different lengths, not a good sign.
        }

        for (int i = 0; i < sigLen; i++) {
            if (signature[i] != digest[i]) {
                return false;// Mismatch. Misfortune.
            }
        }

        return true;// Signatures matched. Perseverance furthers.
    }

    /**
     * Return the digest as a HEX string.
     *
     * @return a hex representation of the MD5 digest.
     */
    @Override
    public String toString() {
// If not already calculated, do so.
        if (digest == null) {
            sign();
        }

        StringBuilder r = new StringBuilder();
        final String hex = "0123456789ABCDEF";
        byte[] b = digest;

        for (int i = 0; i < 16; i++) {
            int c = ((b[i]) >>> 4) & 0xf;
            r.append(hex.charAt(c));
            c = ((int) b[i] & 0xf);
            r.append(hex.charAt(c));
        }

        return r.toString();
    }
}