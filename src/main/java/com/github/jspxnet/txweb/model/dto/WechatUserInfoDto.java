package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.utils.StringUtil;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 微信用户信息
 */
@Data
public class WechatUserInfoDto implements Serializable {
    @SerializedName("openid")
    private String openId;
    @SerializedName("nickname")
    private String nickName;
    private int sex = 0; // 1-男，2-女，0-未知
    private String province;
    private String country;
    private String city;
    private String headimgurl;
    private List<String> privilege;
    private String unionid;
    /*
       {
            "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
            "nickname": "Band",
            "sex": 1,
            "province": "广东",
            "city": "深圳",
            "country": "中国",
            "headimgurl": "http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
            "privilege": ["PRIVILEGE1", "PRIVILEGE2"],
            "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
        }

     */

    // 处理微信昵称特殊字符
    public String getDecodedNickname() {
        if (nickName == null) {
            return StringUtil.empty;
        }
        try {
            return new String(nickName.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return nickName;
        }
    }

    public String getSexZh() {
        if (sex == 1) {
            return "男";
        }
        if (sex == 2) {
            return "女";
        }
        return "未知";
    }
}
