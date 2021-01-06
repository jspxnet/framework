package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * 部分用户功能抽象出来，不要方舟TXWebUtil中
 */
public class MemberUtil {

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
        if (password!=null&&password.startsWith("[")&&password.contains(".")&&password.contains("]"))
        {
            //已经格式过的就不格式了
            return password;
        }
        return "[" + EnvFactory.getHashAlgorithm() + "." + hashAlgorithmKey + "]" + getPasswordHashEncode(password,hashAlgorithmKey);
    }

    /**
     * 验证密码
     * @param password 密码
     * @param storePassword  保存的加密格式数据
     * @return 验证是否成功
     */
    public static boolean verifyPassword(String password,String storePassword)
    {
        if (storePassword!=null&&storePassword.startsWith("[")&&storePassword.contains(".")&&storePassword.contains("]"))
        {
            String hashAlgorithm = StringUtil.substringBetween(storePassword,"[",".");
            String hashAlgorithmKey = StringUtil.substringBetween(storePassword,".","]");
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
        if (storePassword!=null&&storePassword.startsWith("[")&&storePassword.contains(".")&&storePassword.contains("]"))
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
        if (storePassword!=null&&storePassword.startsWith("[")&&storePassword.contains(".")&&storePassword.contains("]"))
        {
            return StringUtil.substringBetween(storePassword,".","]");
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



}
