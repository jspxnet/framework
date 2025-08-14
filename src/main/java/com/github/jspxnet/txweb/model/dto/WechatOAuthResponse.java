package com.github.jspxnet.txweb.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

/**
 * {@code
 *  https://api.weixin.qq.com/sns/oauth2/refresh_token
 *  https://api.weixin.qq.com/sns/oauth2/access_token
 * } 上边这两个方法返回都是这个类
 */
@Data
public class WechatOAuthResponse implements Serializable {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private Integer expiresIn;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("openid")
    private String openId;

    @SerializedName("scope")
    private String scope;

    @SerializedName("unionid")
    private String unionId;

    // 错误相关字段（与成功响应互斥）
    @SerializedName("errcode")
    private Integer errCode;

    @SerializedName("errmsg")
    private String errMsg;


    /**
     *
     * @return 判断响应是否成功
     */
    public boolean isSuccess() {
        return errCode == null || errCode == 0;
    }


}
