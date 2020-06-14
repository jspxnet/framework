package com.github.jspxnet.comm.service;


import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by yuan on 2015/8/15 0015.
 */
public class SerialStatus {
    public SerialStatus() {

    }

    @Column(caption = "网关", length = 64)
    private String gatewayName = StringUtil.empty;

    @Column(caption = "来源端口", length = 64)
    private String portName = StringUtil.empty;

    @Column(caption = "信号")
    private int signal = 99;

    @Column(caption = "接收短信数量")
    private int receiveCount = 0;

    @Column(caption = "发送短信数量")
    private int sendCount = 0;

    @Column(caption = "重启次数")
    private int restartCount = 0;

    @Column(caption = "重置次数")
    private int resetCount = 0;

    @Column(caption = "激活")
    private int active = 1;

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getReceiveCount() {
        return receiveCount;
    }

    public void updateReceiveCount() {
        receiveCount++;
    }

    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public void updateSendCount() {
        sendCount++;
    }


    public int getRestartCount() {
        return restartCount;
    }

    public void setRestartCount(int restartCount) {
        this.restartCount = restartCount;
    }

    public int getResetCount() {
        return resetCount;
    }

    public void setResetCount(int resetCount) {
        this.resetCount = resetCount;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
