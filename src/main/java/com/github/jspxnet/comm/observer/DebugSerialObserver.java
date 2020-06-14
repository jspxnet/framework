package com.github.jspxnet.comm.observer;

import com.github.jspxnet.comm.PortObserver;
import com.github.jspxnet.comm.SerialComm;
import com.github.jspxnet.comm.table.SmsReceive;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by chenyuan on 2015/8/25.
 */
@Slf4j
public class DebugSerialObserver extends PortObserver {
    public DebugSerialObserver() {

    }

    @Override
    public void canSendNotification(SerialComm serialComm) {
        log.debug("----------------可以发送短信");
    }

    @Override
    public void callNotification(SerialComm serialComm, String phone) {
        log.debug("----------------来电:" + phone);
    }

    @Override
    public void messageInNotification(SerialComm serialComm, int messageId) {
        log.debug("----------------接收到新短信:" + messageId);
    }

    @Override
    public void messageListNotification(SerialComm serialComm, List<SmsReceive> list) {
        log.debug("----------------接收到新短信列表");
    }

    @Override
    public void oneMessageInNotification(SerialComm serialComm, SmsReceive smsReceive) {
        log.debug("----------------当前收到最新的短信");
    }


}
