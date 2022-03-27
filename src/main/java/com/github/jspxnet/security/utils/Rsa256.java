package com.github.jspxnet.security.utils;


import com.github.jspxnet.boot.environment.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/1/20 16:59
 * description: jspxpro
 */
@Slf4j
public class Rsa256   {

    static final String SIGN_ALGORITHMS = "SHA256WithRSA";
    public static final String NAME = "RSA256";
    public static final String RSA_NAME = "RSA";
    private String encode = Environment.defaultEncode;

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String sign(String data, String key) {

        try {
            PKCS8EncodedKeySpec priPkcs8 = new PKCS8EncodedKeySpec(
                    Base64.decode(key,Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance(RSA_NAME);
            PrivateKey priKey = keyf.generatePrivate(priPkcs8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(data.getBytes(encode));

            byte[] signed = signature.sign();

            return EncryptUtil.getBase64Encode(signed,Base64.DEFAULT);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return null;
    }


    public Boolean verify(String sign, String data, String key) {


        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_NAME);
            byte[] encodedKey = Base64.decode(key,Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(data.getBytes(encode));
            return signature.verify(Base64.decode(sign,Base64.DEFAULT));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return false;
    }


    public String encrypt(String data, String key) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(key,Base64.DEFAULT));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_NAME);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return new String(cipher.doFinal(Base64.decode(data,Base64.DEFAULT)),encode);
    }


    public String decrypt(String data, String key, String encode) throws Exception {
        if(StringUtils.isBlank(encode)){
            encode = Environment.defaultEncode;
        }
        PrivateKey prikey = getPrivateKey(key);
        Cipher cipher = Cipher.getInstance(RSA_NAME);
        cipher.init(Cipher.DECRYPT_MODE, prikey);
        InputStream ins = new ByteArrayInputStream(Base64.decode(data,Base64.DEFAULT));
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buf = new byte[128];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return new String(writer.toByteArray(), encode);
    }


    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key,Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

/*	public static void main(String[] args) {
//		String pkey = AlipayHelper.readText("e:/opt/sdk.security/keys/alipay/alipay_app_rsa_pubkey.pem");
//		String signStr = "body=熊猫道具&buyer_email=jishizhaidong@sina.com&buyer_id=2088202300159963&discount=0.00&gmt_create=2015-09-22 19:01:42&gmt_payment=2015-09-22 19:01:43&is_total_fee_adjust=N&notify_id=ee954f0c29916f85471066a509cece2f7c&notify_time=2015-09-22 19:01:43&notify_type=trade_status_sync&out_trade_no=20150922-2004-007-0000401583&payment_type=1&price=0.01&quantity=1&seller_email=snail.account10@snailgame.net&seller_id=2088901698590302&subject=熊猫道具&total_fee=0.01&trade_no=2015092200001000960059965060&trade_status=TRADE_SUCCESS&use_coupon=N";
//		String sign = "WQGPbKhnX5mXb3ewsmk3WukyzbogtESyDI5ywpKVCILB7RDYPjLnWdtQjXlj2N0I1yMe3nRsNmDDkMxq8R5cBiJFl5a+I/XWs3iiegM8woIOwq465G4DkOc/hsXdSTVoKn03hEflHPeETLYG3Yf0P9CyIqB8hd16QBl/RzfdH0o=";
//		System.out.println(EncryptFactory.getInstance(Rsa.NAME).verify(sign, signStr, pkey, "utf-8"));
	}*/

}
