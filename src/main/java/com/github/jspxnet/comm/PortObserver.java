package com.github.jspxnet.comm;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.comm.table.SmsReceive;
import com.github.jspxnet.comm.utils.FormatParsing;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.*;

/**
 * Created by chenyuan on 2015/8/25.
 * 串口事件触发器
 */
public abstract class PortObserver implements Observer {

    //过滤重复的电话响铃，结果只要一次，周期为 1分半
    private static final Map<String, Long> RING_LIST = new HashMap<>();
    public static final String CAN_SEND_NOTIFICATION = "canSendNotification";
    public static long lastSendTimeMillis = System.currentTimeMillis();
    public static long lastMessageInTimeMillis = System.currentTimeMillis();
    public static long sendWaitSecond = DateUtil.SECOND * 12;
    private boolean waiting = false;
    private boolean waitingMessageIn = false;


    /**
     * 发送循环促发
     *
     * @param serialComm 串口
     */
    abstract public void canSendNotification(SerialComm serialComm);

    /**
     * 来电事件
     *
     * @param serialComm 来源接口
     * @param phone      电话号码
     */
    abstract public void callNotification(SerialComm serialComm, String phone);

    /**
     * 新消息促发事件
     *
     * @param serialComm 来源接口
     * @param messageId  消息id
     */
    abstract public void messageInNotification(SerialComm serialComm, int messageId);

    /**
     * 收到的消息列表
     *
     * @param serialComm 串口
     * @param list       消息列表
     */
    abstract public void messageListNotification(SerialComm serialComm, List<SmsReceive> list);

    /**
     * @param serialComm 串口
     * @param smsReceive 接受到消息
     */
    abstract public void oneMessageInNotification(SerialComm serialComm, SmsReceive smsReceive);

    @Override
    public void update(Observable o, Object arg) {
        SerialComm serialComm = (SerialComm) o;
        String cmd = StringUtil.trim((String) arg);
        if (!waiting && CAN_SEND_NOTIFICATION.equalsIgnoreCase(cmd) && (System.currentTimeMillis() - lastSendTimeMillis > sendWaitSecond)) {
            try {
                waiting = true;
                lastSendTimeMillis = System.currentTimeMillis();
                canSendNotification(serialComm);
            } finally {
                waiting = false;
            }
        } else if (cmd.contains("+CSQ")) {
            try {
                serialComm.getStatus().setSignal(FormatParsing.getSignal(cmd));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cmd.contains("+CMTI")) {
            //收到短信
            //避免频繁接收,当收到后，等3秒在处理
            try {
                serialComm.getStatus().updateReceiveCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (waitingMessageIn) {
                return;
            }
            try {
                waitingMessageIn = true;
                lastMessageInTimeMillis = System.currentTimeMillis();
                messageInNotification(serialComm, FormatParsing.getNewMessageId(cmd));

            } finally {
                waitingMessageIn = false;
            }

        } else if (cmd.contains("+CMGR")) {
            //读取一条信息
            oneMessageInNotification(serialComm, FormatParsing.getMessageIn(cmd));
        } else if (cmd.contains("+CMGL")) {
            //收到短信
            List<SmsReceive> list = FormatParsing.getMessageInList(cmd);
            for (SmsReceive smsMessageIn : list) {
                smsMessageIn.setGatewayName(serialComm.getName());
                smsMessageIn.setPortName(serialComm.getPortName());
                smsMessageIn.setEncoding(serialComm.getEncoding());
                smsMessageIn.setPutUid(Environment.GUEST_ID);
                smsMessageIn.setPutName(Environment.GUEST_NAME);
                smsMessageIn.setIp(Environment.localeIP);
            }
            messageListNotification(serialComm, list);
        } else if (cmd.startsWith("RING") && cmd.contains("+CLIP")) {
            String phone = FormatParsing.getRingCode(cmd);
            synchronized (RING_LIST) {
                for (String key : RING_LIST.keySet()) {
                    long timeMillis = RING_LIST.get(key);
                    if ((Math.abs(System.currentTimeMillis() - timeMillis) > DateUtil.MINUTE)) {
                        RING_LIST.remove(key);
                    }
                }
            }
            if (!RING_LIST.containsKey(phone)) {
                RING_LIST.put(phone, System.currentTimeMillis());
                callNotification(serialComm, phone);

            }
        }
    }

}
