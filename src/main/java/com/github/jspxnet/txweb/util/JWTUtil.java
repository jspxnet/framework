package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;

public class JWTUtil {

    static public final String JWT_TYPE = "type";
    static public final String JWT = "JWT";
    static public final String JWT_ID = "id";
    static public final String JWT_UID = "uid";
    static public final String JWT_IP = "ip";
    static public final String JWT_ALG = "alg";

    private JWTUtil()
    {

    }

    /**
     * 修复tomcat分布式 session 多余符号
     * @param ip IP验证
     * @param uid  用户ID
     * @param sessionId sessionId
     * @return 创建一个token
     */
    public static String createToken(String ip,String uid,String sessionId) {
        if (sessionId != null && sessionId.length() < 60) {
            //不识别ip默认
            if (StringUtil.isEmpty(ip)) {
                ip = "127.0.0.1";
            }
            //游客默认
            if (StringUtil.isEmpty(uid))
            {
                uid = "0";
            }
            JSONObject json = new JSONObject();
            json.put(JWT_TYPE,JWT);
            json.put(JWT_ID,sessionId);
            json.put(JWT_UID,uid);
            json.put(JWT_IP,ip);
            try {
                Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
                json.put(JWT_ALG,encrypt.getAlgorithm());
                String strMiWen = encrypt.getEncode(json.toString());
                String sign = encrypt.sign(strMiWen,EnvFactory.getHashAlgorithmKey());
                //格式,签名加密文
                return sign + StringUtil.DOT + strMiWen;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionId;
    }

    /**
     *
     * @param token token数据
     * @param ip ip地址
     * @param uid 用户id
     * @return 验证 token 的合法性
     */
    public static boolean tokenVerify(String token,String ip,long uid)
    {
        return tokenVerify( token, ip, NumberUtil.toString(uid));
    }

    /**
     *
     * @param token token数据
     * @param ip  ip地址
     * @param uid  用户id
     * @return 验证 token 的合法性
     */
    public static boolean tokenVerify(String token,String ip,String uid)
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
        try {
            Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
            boolean verify = encrypt.verify(strMiWen,EnvFactory.getHashAlgorithmKey(),sign);
            if (verify)
            {
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

                if (!encrypt.getAlgorithm().equals(json.getString(JWT_ALG)))
                {
                    return false;
                }

                //验证ip,防止钓鱼
                if (!StringUtil.isEmpty(ip)&&!"127.0.0.1".equals(ip))
                {
                    if (!ip.equalsIgnoreCase(json.getString(JWT_IP)))
                    {
                        return false;
                    }
                }
                //验证用户id
                return uid == null || "0".equals(uid) || uid.equals(json.getString(JWT_UID));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] arg)
    {
        String token = "e7e82de44bc77791410be46292049e07.OsmUInzzEoZJZeojgOwXJ1TE_FOwwMcg3I_Jjs32UxskhbDNmrvNODUL-mKflBKozO6RbWoCosubD-n84j5TshsY3FWc6jWjR54SoNQ7izy9-kEVDrjDq_ECsUTTrnNM58-Fugox6-NwgoXXeohMng==";

        boolean b = tokenVerify( token,null,null);
        System.out.println("---------------b="+ b);

    }

}
