package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.sober.annotation.Column;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccessTokenDto implements Serializable {
    @SerializedName(value = "access_token" )
    @Column(caption = "accessToken")
    private String token;


    @SerializedName(value = "expires_in" )
    @Column(caption = "官方返回")
    private long expires_in;

    @Column(caption = "官方动态计算是否超时")
    private long expiresAt; // 精确到毫秒的时间戳



    /**
     * 判断token是否即将过期（剩余时间小于5分钟）
     * @return 是否有效
     */
    public boolean isAboutToExpire() {
        return (expiresAt - System.currentTimeMillis()) < 300_000; // 5分钟阈值
    }

/*    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("access_token","12345678");
        json.put("expires_in",99999);
        AccessTokenDto dto = json.parseObject(AccessTokenDto.class);
        System.out.println(ObjectUtil.toString(dto));
    }*/
}
