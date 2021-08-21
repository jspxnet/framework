package com.github.jspxnet.comm.table;


import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Created by chenyuan on 2015-8-14.
 * 短消息接收后将合并后保存在这里，并且会删除设备上的短信信息
 */

@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "talk_sms_receive", caption = "短信接收库")
public class SmsReceive extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "内存消息ID")
    private int messageId = 0;

    //A: M
    @Column(caption = "标识", option = "A:独立短信;M:合并", length = 10, notNull = true)
    private String markType = "A";

    @Column(caption = "合并的ID", length = 200)
    private String messageIds = StringUtil.empty;

    @Column(caption = "编码", dataType = "isLengthBetween(1,10)", length = 10)
    private String encoding = "UCS2";

    @Column(caption = "消息日期")
    private Date messageDate = new Date();

    @Column(caption = "接收日期")
    private Date receiveDate = new Date();

    @Column(caption = "内容", dataType = "isLengthBetween(1,500)", length = 500, notNull = true)
    private String content = StringUtil.empty;

    @Column(caption = "来源号码", dataType = "isMobile()", length = 64)
    private String originalNo = StringUtil.empty;

    @Column(caption = "来源网关", dataType = "isLengthBetween(1,64)", length = 64)
    private String gatewayName = StringUtil.empty;

    @Column(caption = "来源端口", dataType = "isLengthBetween(1,64)", length = 64)
    private String portName = StringUtil.empty;

    //保留给外部处理使用
    @Column(caption = "外部标识", notNull = true)
    private int process = 0;

    @Column(caption = "是否回复", notNull = true)
    private int replyType = 0;

    @Column(caption = "回复内容", dataType = "isLengthBetween(1,500)", length = 500, notNull = true)
    private String replyContent = StringUtil.empty;

    public void addMessageIds(String messageId) {

        if (this.messageIds.endsWith(StringUtil.COLON)) {
            this.messageIds = this.messageIds + messageId + StringUtil.COLON;
        } else {
            this.messageIds = this.messageIds + StringUtil.COLON + messageId;
        }

        if (this.messageIds.endsWith(StringUtil.COLON)) {
            this.messageIds = this.messageIds.substring(0, this.messageIds.length() - 1);
        }
    }

    public void addMessageIds(long messageId) {
        this.messageIds = this.messageIds + messageId + StringUtil.COLON;
        if (this.messageIds.endsWith(StringUtil.COLON)) {
            this.messageIds = this.messageIds.substring(0, this.messageIds.length() - 1);
        }
    }

    public int[] getMessageIdArray() {
        if (StringUtil.isNull(messageIds)) {
            return new int[0];
        }
        String[] ids = StringUtil.split(messageIds, StringUtil.COLON);
        int[] messageIds = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            messageIds[i] = StringUtil.toInt(ids[i]);
        }
        return messageIds;
    }
}
