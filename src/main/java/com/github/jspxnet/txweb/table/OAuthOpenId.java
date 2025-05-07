package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.SexEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用于微信，QQ等系统登陆
 *
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_oauth_openid", caption = "第三方登陆数据", cache = true)
public class OAuthOpenId extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    //本站的用户UID
    @Column(caption = "UID", notNull = true)
    private long uid;

    //作为唯一标识
    @Column(caption = "openId", length = 64, dataType = "isLengthBetween(1,64)", notNull = true)
    private String openId = StringUtil.empty;

    //微信 认证企业才有这
    @Column(caption = "unionId", length = 120, dataType = "isLengthBetween(1,120)")
    private String unionId = StringUtil.empty;

    //第三方软件的昵称
    @Column(caption = "昵称", length = 50, dataType = "isLengthBetween(2,50)", notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "性别", length = 8, dataType = "isLengthBetween(1,8)", notNull = true)
    private String sex = StringUtil.empty;

    @Column(caption = "城市", length = 50, dataType = "isLengthBetween(2,60)")
    private String city = StringUtil.empty;

    @Column(caption = "国家", length = 50, dataType = "isLengthBetween(2,60)")
    private String country = StringUtil.empty;

    @Column(caption = "省份", length = 50, dataType = "isLengthBetween(2,60)")
    private String province = StringUtil.empty;

    //头像
    @Column(caption = "头像URL", length = 240)
    private String faceImage = StringUtil.empty;

    //qq;wx;sinaK
    @Column(caption = "命名空间", option = "", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    public Member createMember() {
        Member member = new Member();
        member.setName(name);
        member.setSex(SexEnumType.valueOf(sex).getName());
        member.setFaceImage(faceImage);
        member.setCity(city);
        member.setCountry(country);
        member.setProvince(province);
        return member;
    }

    public OAuthOpenId() {

    }

    public OAuthOpenId(JSONObject json) {
        if (json == null) {
            return;
        }
        //为了方便移动应用调用
        openId = json.getString("openId");
        unionId = json.getIgnoreString("unionid");
        name = json.getString("name");
        sex = json.getString("sex");
        country = json.getString("country");
        province = json.getString("province");
        faceImage = json.getString("faceImage");
        namespace = json.getString("namespace");
    }

    public JSONObject toJson() {
        //为了方便移动应用调用
        JSONObject json = new JSONObject();
        json.put("openId", openId);
        json.put("unionid", unionId);
        json.put("name", name);
        json.put("sex", sex);
        json.put("country", country);
        json.put("province", province);
        json.put("faceImage", faceImage);
        json.put("namespace", namespace);
        return json;
    }
}
