package com.github.jspxnet.component.k3cloud;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/30 0:28
 * description:
 *
 * {@code
 *     <kingdee>
 *         <acctId>20210421180424181</acctId>
 *         <userName>demo</userName>
 *         <appId>214196_33dJ5wtIRvkXRX8Ew+7rzY/vRrXWWtmJ</appId>
 *         <appSec>xxxxxxxxxxx</appSec>
 *         <serverUrl>https://xxx.ik3cloud.com/k3cloud/</serverUrl>
 *         <lcid>2052</lcid>
 *         <pwd>sy123456-</pwd>
 *     </kingdee>
 * }
 **/
@Data
public class KingdeeAccount implements Serializable {

    private String acctId;
    private String userName;
    private String appId;
    private String appSec;
    private String serverUrl;
    private int lcid;
    private String pwd;

}
