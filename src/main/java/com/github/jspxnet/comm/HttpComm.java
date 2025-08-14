package com.github.jspxnet.comm;

import com.github.jspxnet.comm.service.SerialConfig;
import com.github.jspxnet.comm.service.SerialStatus;
import com.github.jspxnet.utils.StringUtil;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import static com.github.jspxnet.comm.PortObserver.CAN_SEND_NOTIFICATION;

/**
 * Created by chenyuan on 2015/9/2.
 * http 挂接发送接口
 * <pre>
 * {@code
 * http://utf8.sms.webchinese.cn/?Uid=本站用户名&Key=接口安全秘钥&smsMob=手机号码&smsText=验证码:8888
 * }</pre>
 */
@Slf4j
public abstract class HttpComm extends Observable implements SerialPortEventListener {
    public String name = StringUtil.empty;
    protected String portName = StringUtil.empty;
    protected String encoding = StandardCharsets.UTF_8.name();
    protected String urlTemplate = " http://utf8.sms.webchinese.cn";
    private final Map<String, Object> params = new HashMap<String, Object>();
    private final String response = StringUtil.empty;
    private SerialConfig settings = new SerialConfig();
    private final SerialStatus status = new SerialStatus();

    public abstract void init();

    protected abstract void echoOff() throws Exception;

    public SerialConfig getSettings() {
        return settings;
    }

    public void setSettings(SerialConfig settings) {
        this.settings = settings;
        name = this.settings.name;
        portName = this.settings.portName;
        encoding = this.settings.encoding;
    }

    public String getName() {
        return name;
    }

    public String getPortName() {
        return portName;
    }

    public void write(String cmd) throws IOException {

    }

    public String getResponse() {
        return response;
    }

    public boolean isOk() {
        return getResponse().toUpperCase().contains("OK");
    }

    public void connect() {

    }

    /**
     * @param phone   电话
     * @param content 内容
     * @return 等待的时间短了不稳定
     */
    public boolean sendSms(String phone, String content) {

        return false;
    }

    public void close() {

    }


    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            Thread.sleep(settings.AT_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setChanged();
        notifyObservers(response);
    }

    public void canSendNotification() {
        setChanged();
        notifyObservers(CAN_SEND_NOTIFICATION);
    }

    //------------------------------------------------------------------------------------------------------------------
    public void reset() throws IOException, InterruptedException {
    }

    public boolean isAlive() throws IOException, InterruptedException {
        return isOk();
    }

    public String getEncoding() {
        return encoding;
    }

    /**
     * @return 得到统计数据
     */
    public SerialStatus getStatus() {
        return status;
    }

}
