package com.github.jspxnet.network.oss.adapter;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/11/17 23:28
 * description: 阿里云sts安全token
 **/
@Data
public class OssSts implements Serializable {
    private String securityToken;
    private String accessKeySecret;
    private String accessKeyId;
    private String expiration;
    private Long expirationTimeStamp;
    private Date expirationDate;
    private String expirationLocal;

    private  String statusCode;

    private  String errorCode;

    private  String errorMessage;

    public boolean isExpired() {
        long nowStamp = System.currentTimeMillis();
        return expirationTimeStamp == null || expirationTimeStamp < nowStamp;
    }

    public OssSts setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }


    public OssSts setExpiration(String expiration) {
        this.expiration = expiration;
        expiration = expiration.replace("Z", " UTC");
        try {
            this.expirationDate = StringUtil.getDate(expiration,"yyyy-MM-dd'T'HH:mm:ss Z");
            this.expirationTimeStamp = this.expirationDate.getTime();
            this.expirationLocal = DateUtil.toString(this.expirationDate,"yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
            this.expirationTimeStamp = 0L;
        }
        return this;
    }

    public OssSts setExpirationTimeStamp(Long expirationTimeStamp) {
        this.expirationTimeStamp = expirationTimeStamp;
        return this;
    }


}
