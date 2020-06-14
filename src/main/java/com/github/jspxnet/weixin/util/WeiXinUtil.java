package com.github.jspxnet.weixin.util;

import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.weixin.env.WeiXinEnv;
import com.github.jspxnet.weixin.table.WeiXinUser;
import org.weixin4j.model.sns.SnsUser;
import org.weixin4j.model.user.User;


/**
 * Created by ChenYuan on 2017/8/8.
 */
public class WeiXinUtil {
    private WeiXinUtil() {

    }

    /**
     * SnsUser 为页面授权信息,没有关注的信息
     *
     * @param user 将weixin4j 用户转换为 自己的WeiXinUser  方便保存,查询
     * @return 微信对象
     */

    public static WeiXinUser toWeiXinUser(SnsUser user) {
        WeiXinUser weiXinUser = new WeiXinUser();
        weiXinUser.setOpenid(user.getOpenid());
        weiXinUser.setNickname(user.getNickname());
        weiXinUser.setSex(user.getSex());
        weiXinUser.setProvince(user.getProvince());
        weiXinUser.setCity(user.getCity());
        weiXinUser.setCountry(user.getCountry());
        weiXinUser.setHeadimgurl(user.getHeadimgurl());
        weiXinUser.setPrivileges(ArrayUtil.toString(user.getPrivilege(), "|"));
        weiXinUser.setUnionid(user.getUnionid());
        return weiXinUser;
    }

    public static WeiXinUser toWeiXinUser(User user) {

        /*
    private String openid;          //用户的标识，对当前公众号唯一
    private String subscribe;       //用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
    private String nickname;        //用户的昵称
    private int sex;                //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    private String city;            //用户所在城市
    private String country;         //用户所在国家
    private String province;        //用户所在省份
    private String language;        //用户的语言，简体中文为zh_CN
    private String headimgurl;      //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
    private long subscribe_time;    //用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
    private String unionid;         //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
    private String remark;          //用户备注
    private String groupid;         //用户分组
    private int[] tagid_list;       //用户被打上的标签ID列表
    private String subscribe_scene; //返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他
    private String qr_scene;        //二维码扫码场景（开发者自定义）
    private String qr_scene_str;    //二维码扫码场景描述（开发者自定义）

         */
        WeiXinUser weiXinUser = new WeiXinUser();
        weiXinUser.setOpenid(user.getOpenid());
        weiXinUser.setSubscribe(user.getSubscribe());
        weiXinUser.setNickname(user.getNickname());
        weiXinUser.setSex(user.getSex());
        weiXinUser.setCity(user.getCity());
        weiXinUser.setProvince(user.getProvince());
        weiXinUser.setCountry(user.getCountry());
        weiXinUser.setHeadimgurl(user.getHeadimgurl());
        weiXinUser.setSubscribe_time(user.getSubscribe_time());
        weiXinUser.setUnionid(user.getProvince());
        weiXinUser.setRemark(user.getRemark());
        weiXinUser.setGroupid(user.getGroupid());
        weiXinUser.setTagid_list(ArrayUtil.toString(user.getTagid_list(), "|"));
        weiXinUser.setSubscribe_scene(user.getSubscribe_scene());
        weiXinUser.setQr_scene(user.getQr_scene());
        weiXinUser.setQr_scene_str(user.getQr_scene_str());
        return weiXinUser;
    }

    public static void putCacheWeiXinState(String sessionId,String state,String scope)
    {
        JSCacheManager.put(DefaultCache .class,WeiXinEnv.weiXinState+"_"+sessionId,state);
        JSCacheManager.put(DefaultCache.class,WeiXinEnv.weiXinScope+"_" + state,scope);
    }

    public static String getCacheWeiXinState(String sessionId)
    {
        return (String)   JSCacheManager.get(DefaultCache .class,WeiXinEnv.weiXinState+"_"+sessionId);
    }

    public static String getCacheWeiXinScope(String state)
    {
        return (String) JSCacheManager.get(DefaultCache.class,WeiXinEnv.weiXinScope+"_" + state);
    }

}
