package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.json.JsonField;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.io.Serializable;

@Data
public class JSApiPaymentResponseDto implements Serializable {
    //付款单id
    @SerializedName("payId")
    private String payId;
    @SerializedName("appId")
    private String appId;
    @JsonField(name = "timeStamp")
    @SerializedName("timeStamp")
    private String timestamp;
    @SerializedName("nonceStr")
    private String nonceStr;
    @JsonField(name = "package")
    @SerializedName("package")
    private String packageVal;
    @SerializedName("signType")
    private String signType;
    @SerializedName("paySign")
    private String paySign;
}
