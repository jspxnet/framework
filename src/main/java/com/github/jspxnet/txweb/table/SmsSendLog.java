package com.github.jspxnet.txweb.table;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "jspx_sms_send_log", caption = "短信发送日志")
public class SmsSendLog implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "短信服务器ID", length = 30, notNull = true)
    private String regionId = StringUtil.empty;

    @Column(caption = "手机号", length = 50)
    private String phoneNumbers = StringUtil.empty;

    @Column(caption = "签名", length = 40, notNull = true)
    private String signName = StringUtil.empty;

    @Column(caption = "模板ID", length = 30, notNull = true)
    private String templateCodeId = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "验证号", length = 250, notNull = true)
    private String templateValue = StringUtil.empty;

    @Column(caption = "返回消息", length = 100, notNull = true)
    private String message = StringUtil.empty;

    @Column(caption = "请求ID", length = 40, notNull = true)
    private String requestId = StringUtil.empty;

    @Column(caption = "bizId", length = 40)
    private String bizId = StringUtil.empty;

    @Column(caption = "状态", length = 200, notNull = true)
    private String resultCode = StringUtil.empty;

    @Column(caption = "创建时间", notNull = true)
    private Date sendDate = new Date();

}
