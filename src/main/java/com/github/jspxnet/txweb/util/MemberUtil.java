package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * 部分用户功能抽象出来，不要方舟TXWebUtil中
 */
public final class MemberUtil {

    private MemberUtil() {

    }

    /**
     *
     * @param password 密码
     * @param hashAlgorithmKey 密钥
     * @return 用户密码加密算法
     */
    public static String getPasswordHashEncode(String password,String hashAlgorithmKey) {
        ///////////////////判断是否有游客帐号，没有就创建一个
        return EncryptUtil.getHashEncode(password + hashAlgorithmKey, EnvFactory.getHashAlgorithm());
        ///////////////////
    }

    /**
     *
     * @param password 密码
     * @param hashAlgorithmKey 密钥
     * @return 升级后的密码加密保存格式,创建一个数据库密码
     */
    public static String createPasswordSaveFormat(String password,String hashAlgorithmKey) {
        if (password!=null&&password.startsWith("[")&&password.contains(StringUtil.DOT)&&password.contains("]"))
        {
            //已经格式过的就不格式了
            return password;
        }
        return "[" + EnvFactory.getHashAlgorithm() + StringUtil.DOT + hashAlgorithmKey + "]" + getPasswordHashEncode(password,hashAlgorithmKey);
    }

    /**
     * 验证密码
     * @param password 密码
     * @param storePassword  保存的加密格式数据
     * @return 验证是否成功
     */
    public static boolean verifyPassword(String password,String storePassword)
    {
        if (storePassword!=null&&storePassword.startsWith("[")&&storePassword.contains(StringUtil.DOT)&&storePassword.contains("]"))
        {
            String hashAlgorithm = StringUtil.substringBetween(storePassword,"[",StringUtil.DOT);
            String hashAlgorithmKey = StringUtil.substringBetween(storePassword,StringUtil.DOT,"]");
            String passwordHash = StringUtil.substringAfter(storePassword,"]");
            if (EncryptUtil.getHashEncode(password + hashAlgorithmKey, hashAlgorithm).equalsIgnoreCase(passwordHash))
            {
                return true;
            }
        } else 
        {
            //兼容老密码
            return EncryptUtil.getHashEncode(password, EnvFactory.getHashAlgorithm()).equalsIgnoreCase(storePassword);
        }
        return getPasswordHashEncode(password,storePassword).equalsIgnoreCase(storePassword);
    }

    /**
     *
     * @param storePassword 数据里边密码串
     * @return 加密的md5 hash
     */
    public static String getPasswordHash(String storePassword) {
        if (storePassword!=null&&storePassword.startsWith("[")&&storePassword.contains(StringUtil.DOT)&&storePassword.contains("]"))
        {
            return StringUtil.substringAfter(storePassword,"]");
        }
        return storePassword;
    }

    /**
     *
     * @param storePassword 数据库里边保存的密码字符串
     * @return 得到密码的密钥
     */
    public static String getHashAlgorithmKey(String storePassword) {
        if (storePassword!=null&&storePassword.startsWith("[")&&storePassword.contains(StringUtil.DOT)&&storePassword.contains("]"))
        {
            return StringUtil.substringBetween(storePassword,StringUtil.DOT,"]");
        }
        return StringUtil.empty;
    }
    /**
     * 修改用户账号，钱和积分都要重新生成token
     *
     * @param member 用户信息
     * @return 修改用户账号，钱和积分都要重新生成token
     */
    public static String builderToken(Member member) {
        String sb = member.getId() + member.getPhone() + member.getMail() + member.getPayPassword() +
                NumberUtil.getNumberStdFormat(member.getStoreMoney()) +
                member.getPoints();
        return EncryptUtil.getHashEncode(sb + EnvFactory.getHashAlgorithmKey(),EnvFactory.getHashAlgorithm());
    }

    /**
     * 验证token是否正常,是否被非法修改
     *
     * @param member 用户信息
     * @return 是否正常, 是否被非法修改
     */
    public static boolean tokenVerify(Member member) {
        if (StringUtil.isNull(member.getToken())) {
            return true;
        }
        String sb = member.getId() + member.getPhone() + member.getMail() + member.getPayPassword() +
                NumberUtil.getNumberStdFormat(member.getStoreMoney()) +
                member.getPoints();
        return EncryptUtil.getHashEncode(sb + EnvFactory.getHashAlgorithmKey(),EnvFactory.getHashAlgorithm()).equalsIgnoreCase(member.getToken());
    }

    /**
     *
     * @param userId 用户ID
     * @param userName 用户名
     * @return 返回用户格式
     */
    public static String makeUser(String userId,String userName) {
        return Environment.marker_user_startTag + userId + Environment.marker_user_centerTag + userName + Environment.marker_user_endTag;
    }

    public static String getUserId(String str) {
        return StringUtil.substringBetween(str,Environment.marker_user_startTag,Environment.marker_user_centerTag);
    }

    public static boolean isUser(String data) {
        return !StringUtil.isNull(data) && data.startsWith(Environment.marker_user_startTag) && data.contains(Environment.marker_user_centerTag) && data.endsWith(Environment.marker_user_endTag);
    }

    public static  boolean isGroup(String data) {
        return !StringUtil.isNull(data) && data.startsWith(Environment.marker_group_startTag) && data.contains(Environment.marker_group_centerTag) && data.endsWith(Environment.marker_group_endTag);
    }

    public static  boolean isContacts(String data) {
        return !StringUtil.isNull(data) && data.startsWith(Environment.marker_contacts_startTag) && data.contains(Environment.marker_contacts_centerTag) && data.endsWith(Environment.marker_contacts_endTag);
    }

    public static  boolean isFollow(String data) {
        return !StringUtil.isNull(data) && data.startsWith(Environment.marker_follow_startTag) && data.contains(Environment.marker_follow_centerTag) && data.endsWith(Environment.marker_follow_endTag);
    }


}
