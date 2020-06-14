package com.github.jspxnet.weixin.table;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;


/**
 * 这里是一个临时到表，并不保存到数据库
 */
@Table(name = "weixin_user", caption = "联系方式")
public class WeiXinUser {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "公众号标识", length = 40)
    private String subscribe = StringUtil.empty;//用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。

    @Column(caption = "openId", length = 64)
    private String openid = StringUtil.empty;//用户的标识，对当前公众号唯一

    @Column(caption = "accessToken", length = 240)
    private String accessToken = StringUtil.empty;

    @Column(caption = "昵称", length = 32)
    private String nickname = StringUtil.empty;//用户的昵称

    @Column(caption = "性别", option = "0:未知;1:男;2:女", length = 20)
    private int sex;//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知

    @Column(caption = "城市", length = 40)
    private String city = StringUtil.empty;//用户所在城市

    @Column(caption = "国家", length = 40)
    private String country = StringUtil.empty;//用户所在国家

    @Column(caption = "城市", length = 40)
    private String province = StringUtil.empty;//用户所在省份

    @Column(caption = "用户特权信息", length = 250)
    private String privileges = StringUtil.empty;//用户特权信息 json 数组，如微信沃卡用户为（chinaunicom）

    @Column(caption = "语言", length = 10)
    private String language = StringUtil.empty;//用户的语言，简体中文为zh_CN

    @Column(caption = "头像", length = 240)
    private String headimgurl = StringUtil.empty;//用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空

    @Column(caption = "头像大小", length = 40)
    private int headImgSize;//用户头像大小

    @Column(caption = "关注时间")
    private long subscribe_time = 0;//用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间

    @Column(caption = "UnionID", length = 64)
    private String unionid = StringUtil.empty;//只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）

    @Column(caption = "备注", length = 250)
    private String remark = StringUtil.empty;//用户备注

    @Column(caption = "用户分组", length = 50)
    private String groupid;  //用户分组

    @Column(caption = "tagid_list", length = 250)
    private String tagid_list;  //用户被打上的标签ID列表

    //返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他
    @Column(caption = "用户关注的渠道来源", length = 50)
    private String subscribe_scene = StringUtil.empty;  //用户被打上的标签ID列表

    @Column(caption = "二维码扫码场景", length = 100)
    private String qr_scene = StringUtil.empty;  //二维码扫码场景（开发者自定义）

    @Column(caption = "二维码扫码场景描述", length = 100)
    private String qr_scene_str = StringUtil.empty;  //二维码扫码场景描述（开发者自定义）


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public int getHeadImgSize() {
        return headImgSize;
    }

    public void setHeadImgSize(int headImgSize) {
        this.headImgSize = headImgSize;
    }

    public long getSubscribe_time() {
        return subscribe_time;
    }

    public void setSubscribe_time(long subscribe_time) {
        this.subscribe_time = subscribe_time;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getTagid_list() {
        return tagid_list;
    }

    public void setTagid_list(String tagid_list) {
        this.tagid_list = tagid_list;
    }

    public String getSubscribe_scene() {
        return subscribe_scene;
    }

    public void setSubscribe_scene(String subscribe_scene) {
        this.subscribe_scene = subscribe_scene;
    }

    public String getQr_scene() {
        return qr_scene;
    }

    public void setQr_scene(String qr_scene) {
        this.qr_scene = qr_scene;
    }

    public String getQr_scene_str() {
        return qr_scene_str;
    }

    public void setQr_scene_str(String qr_scene_str) {
        this.qr_scene_str = qr_scene_str;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("subscribe", subscribe);
        json.put("openid", openid);
        json.put("nickname", nickname);
        json.put("sex", sex);
        json.put("city", city);
        json.put("country", country);
        json.put("province", province);
        json.put("language", language);
        json.put("headImgUrl", headimgurl);
        json.put("headImgSize", headImgSize);
        json.put("subscribe_time", subscribe_time);
        json.put("privileges", privileges);
        json.put("unionid", unionid);
        json.put("accessToken", accessToken);
        return json.toString();
    }


}
