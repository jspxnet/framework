package com.github.jspxnet.security.zssm2;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Base64;

/**
 * 招商银行sm2算法
 */
public class SM2Util {

    private static final String SM2_KEY_TITLE = "3059301306072a8648ce3d020106082a811ccf5501822d03420004";

    public static String USER_ID = "1234567812345678";

    public static String sm2Sign( String content,String privateKey ){
        try {
            //init privateKey
            BCECPrivateKey bcecPrivateKey = BCUtil.getPrivatekeyFromD(new BigInteger(privateKey,16));

            byte[] sign = BCUtil.signSm3WithSm2(content.getBytes(),USER_ID.getBytes(),bcecPrivateKey);

            return encodeBase64(signRawToAsn1(sign));
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param content 数据
     * @param rawSign 签名
     * @param publicKey 公密
     * @return 验签结果
     */
    public static boolean sm2Check( String content,String rawSign ,String publicKey ){
        try {
            //init PublicKey
            Sm2Vo sm2Vo = parseBase64TRawKey(publicKey);
            if( null == sm2Vo ){
                return false;
            }
            BCECPublicKey bcecPublicKey = BCUtil.getPublickeyFromXY(new BigInteger(sm2Vo.getSm2_x(),16),new BigInteger(sm2Vo.getSm2_y(),16));

            byte[] sign = signAsn12Raw(decodeBase64(rawSign));

            return BCUtil.verifySm3WithSm2(content.getBytes(),USER_ID.getBytes(),sign,bcecPublicKey);

        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * BASE64格式公钥转换为裸公钥
     * @param sm2Key 公钥
     * @return 裸公钥
     */
    private static Sm2Vo parseBase64TRawKey(String sm2Key){
        if( null == sm2Key ){
            return null;
        }

        String sm2_asn1 = Hex.toHexString(decodeBase64(sm2Key));
        if( !sm2_asn1.startsWith(SM2_KEY_TITLE) ){
            return null;
        }

        String sm2_xy = sm2_asn1.substring(SM2_KEY_TITLE.length(),sm2_asn1.length());
        String sm2_x = sm2_xy.substring(0,sm2_xy.length()/2 );
        String sm2_y = sm2_xy.substring(sm2_xy.length()/2 ,sm2_xy.length());

        return new Sm2Vo(SM2_KEY_TITLE,sm2_x,sm2_y);
    }

    /**
     * 将字节数组转换为Base64格式字符串
     * @param data 加密数据
     * @return 将字节数组转换为Base64格式字符串
     */
    public static String encodeBase64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 将Base64格式字符串转为字节数组
     * @param data 加密数据
     * @return 将Base64格式字符串转为字节数组
     */
    public static byte[] decodeBase64(String data){
        return Base64.getDecoder().decode(data);
    }

    /**
     * 将BC SM2 RAW签名值转化为ASN1格式签名值
     * @param bcCipTxt  将BC签名值
     * @return  ASN1格式签名值
     * @throws Exception 异常
     */
    private static byte[] signRawToAsn1(byte[] bcCipTxt) throws Exception {

        byte[] netSignCipTxt = new byte[73];

        byte[] signR = new byte[32];
        byte[] signS = new byte[32];

        System.arraycopy(bcCipTxt, 0, signR, 0, 32);
        System.arraycopy(bcCipTxt, 32, signS, 0, 32);

        //signR补位
        int wPos = 4;
        netSignCipTxt[0] = 0x30;
        netSignCipTxt[2] = 0x02;
        if( (signR[0] & 0xFF) >= 128 )
        {
            netSignCipTxt[wPos - 1] = 0x21;
            netSignCipTxt[wPos] = 0x00;
            wPos += 1;
        }
        else
        {
            netSignCipTxt[wPos - 1] = 0x20;
        }
        System.arraycopy(signR, 0, netSignCipTxt, wPos, 32);
        wPos += 32;

        //signS补位
        netSignCipTxt[wPos] = 0x02;
        wPos += 1;
        if( (signS[0] & 0xFF) >= 128 )
        {
            netSignCipTxt[wPos] = 0x21;
            wPos += 1;
            netSignCipTxt[wPos] = 0x00;
            wPos += 1;
        }
        else
        {
            netSignCipTxt[wPos] = 0x20;
            wPos += 1;
        }
        System.arraycopy(signS, 0, netSignCipTxt, wPos, 32);
        wPos += 32;

        if(70 == wPos)
        {
            netSignCipTxt[1] = 0x44;
        }
        else if(71 == wPos)
        {
            netSignCipTxt[1] = 0x45;
        }
        else if(72== wPos)
        {
            netSignCipTxt[1] = 0x46;
        }
        else
        {
            throw new Exception("signRawToAsn1 Error!");
        }

        byte[] resultBytes = new byte[wPos];
        System.arraycopy(netSignCipTxt, 0, resultBytes, 0, wPos);

        return resultBytes;
    }

    /**
     * 将ASN1格式签名值转化为BC SM2 RAW 签名值
     *
     * @param  signature Asn1格式签名值
     * @return byte[] Raw签名值
     */
    private static byte[] signAsn12Raw(byte[] signature) throws Exception {

        byte[] resultBytes = new byte[64];

        //截取signR
        int wPos = 3;
        if( (signature[wPos] & 0xFF) == 32 )
        {
            wPos += 1;
        }
        else if( (signature[wPos] & 0xFF) == 33 )
        {
            wPos += 2;
        }
        else
        {
            throw new Exception("signR length Error!");
        }
        System.arraycopy(signature, wPos, resultBytes, 0, 32);
        wPos += 32;

        //截取signS
        wPos += 1;
        if( (signature[wPos] & 0xFF) == 32 )
        {
            wPos += 1;
        }
        else if( (signature[wPos] & 0xFF) == 33 )
        {
            wPos += 2;
        }
        else
        {
            throw new Exception("signS length Error!");
        }
        System.arraycopy(signature, wPos, resultBytes, 32, 32);

        //System.out.println("\nhhh:\n" + ByteToHex(resultBytes));

        return resultBytes;
    }

}
