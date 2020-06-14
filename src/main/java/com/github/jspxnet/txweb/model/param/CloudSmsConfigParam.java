package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.CloudServiceEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import java.io.Serializable;

@Data
public class CloudSmsConfigParam implements Serializable {
    @Param(caption = "ID",min = 1,max = 32)
    private int id;

    @Param(caption = "枚举标识",min = 1,max = 10000)
    private int enumId = 0;

    @Param(caption = "账号Key", max = 64, required = true, message = "不能为空")
    private String accessKey;

    @Param(caption = "KeySecret", max = 128, required = true,  message = "不能为空")
    private String accessKeySecret;

    @Param(caption = "短信模版号",max = 128, required = true,  message = "不能为空")
    private String templateNo;

    @Param(caption = "模版名称",max = 128, required = true,  message = "不能为空")
    private String name;

    @Param(caption = "短信签名", required = true,  message = "不能为空")
    private String sign;

    //华为云才用
    @Param(caption = "签名通道号")
    private String sender;

    //华为云才用
    @Param(caption = "回调URL")
    private String callBackUrl;


    @Param(caption = "短信平台")
    private  int cloudType = CloudServiceEnumType.Ali.getValue();
}
