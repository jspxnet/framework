package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JWTUtil {
    static public final String JWT_TYPE = "type";

    static public final String JWT = "JSPX";
    //签发日期,用来判断是否已经到期
    static public final String JWT_DATE = "d";
    //用户id
    static public final String JWT_UID = "u";
    //用户ip 判断位置
    static public final String JWT_IP = "ip";

    //如果是游客，创建一个随机数确保唯一性
    static public final String JWT_RD = "r";

    final static public String JWT_DATE_FORMAT = "yyMMddHHmmss";

    private JWTUtil()
    {

    }

    /**
     * 修复tomcat分布式 session 多余符号
     * @param ip IP验证
     * @param uid  用户ID
     * @return 创建一个token
     */
    public static String createToken(String ip,String uid) {
        //不识别ip默认
        if (StringUtil.isEmpty(ip)) {
            ip = StringUtil.empty;
        }
        //游客默认
        if (StringUtil.isEmpty(uid))
        {
            uid = "0";
        }
        JSONObject json = new JSONObject();
        json.put(JWT_TYPE,JWT);
        json.put(JWT_DATE, DateUtil.toString(new Date(),JWT_DATE_FORMAT));
        json.put(JWT_UID,uid);
        json.put(JWT_IP, IpUtil.toLong(ip));
        json.put(JWT_RD, RandomUtil.getRandomGUID(4));
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        try {
            String strMiWen = encrypt.getEncode(json.toString());
            String sign = encrypt.sign(strMiWen,EnvFactory.getHashAlgorithmKey());
            //格式,签名加密文
            return sign + StringUtil.DOT + EncryptUtil.getBase58EncodeString(strMiWen);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }


    public static JSONObject getTokenJson(String token) {
        if (StringUtil.isEmpty(token)) {
            return null;
        }
        if (!token.contains(StringUtil.DOT))
        {
            return null;
        }
        //签名
        String sign = StringUtil.substringBefore(token,StringUtil.DOT);
        //密文
        String strMiWen = StringUtil.substringAfter(token,StringUtil.DOT);
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        try {
            strMiWen = EncryptUtil.getBase58DecodeString(strMiWen);
            boolean isVerifyOk = encrypt.verify(strMiWen,EnvFactory.getHashAlgorithmKey(),sign);
            if (!isVerifyOk)
            {
                return new JSONObject();
            }
            String data = encrypt.getDecode(strMiWen);
            if (StringUtil.isJsonObject(data))
            {
                return new JSONObject(data);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
        return null;
    }



    /**
     * 只验证签名
     * @param token token
     * @return 快速验证
     */
    public static boolean tokenVerify(String token)
    {
        if (token == null || !token.contains(StringUtil.DOT)) {
            return false;
        }
        String sign = StringUtil.substringBefore(token,StringUtil.DOT);
        String strMiWen = StringUtil.substringAfter(token,StringUtil.DOT);
        if (StringUtil.isNull(sign)||StringUtil.isNull(strMiWen))
        {
            return false;
        }
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        try {
            strMiWen = EncryptUtil.getBase58DecodeString(strMiWen);
            return encrypt.verify(strMiWen,EnvFactory.getHashAlgorithmKey(),sign);
        } catch (Exception e) {
            return false;
            //e.printStackTrace();
        }
    }

    /**
     * ip为了确保正确行，只验证ip的前3位，实际应用场景中验证不通过的需要重新登录，重新签发
     * @param token  token数据
     * @param ip ip
     * @param hour 有效期几小时
     * @return 验证是否满足条件重新登录，重新登录的uid来自token
     */
    public static boolean tokenVerify(String token,String ip,int hour)
    {
        if (token == null || !token.contains(StringUtil.DOT)) {
            return false;
        }
        String sign = StringUtil.substringBefore(token,StringUtil.DOT);
        String strMiWen = StringUtil.substringAfter(token,StringUtil.DOT);
        if (StringUtil.isNull(sign)||StringUtil.isNull(strMiWen))
        {
            return false;
        }
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        try {
            strMiWen = EncryptUtil.getBase58DecodeString(strMiWen);
            boolean verify = encrypt.verify(strMiWen,EnvFactory.getHashAlgorithmKey(),sign);
            if (!verify)
            {
                return false;
            }
            String text = encrypt.getDecode(strMiWen);
            if (!StringUtil.isJsonObject(text))
            {
                return false;
            }

            JSONObject json = new JSONObject(text);
            if (!JWT.equals(json.getString(JWT_TYPE)))
            {
                return false;
            }

            //验证ip,防止钓鱼
            if (!StringUtil.isEmpty(ip)&&!"127.0.0.1".equals(ip))
            {
                long dIp = StringUtil.toLong(json.getString(JWT_IP));
                String strIp = IpUtil.getIPForLong(dIp);
                String ipa = StringUtil.substringBeforeLast(strIp,StringUtil.DOT);
                String ipb = StringUtil.substringBeforeLast(ip,StringUtil.DOT);
                if (!ipa.equalsIgnoreCase(ipb))
                {
                    return false;
                }
            }
            String strDate = json.getString(JWT_DATE);
            Date createDate  = StringUtil.getDate(strDate,JWT_DATE_FORMAT);
            long h = DateUtil.compareHour(createDate,new Date());
            if (h > hour)
            {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
