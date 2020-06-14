package com.github.jspxnet.comm.service;

/**
 * Created by chenyuan on 2015/8/25.
 * 端口配置
 */


import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import gnu.io.SerialPort;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信接口配置,等待的时间和设备性能有很大关系
 */
public class SerialConfig {
    public int RETRIES = 5; //重新次数

    //端口名称 COM4
    public String portName = StringUtil.empty;

    public String encoding = "UCS2";

    // 波特率
    public int rate = 9600;

    // 数据位
    public int dataBits = SerialPort.DATABITS_8;

    // 停止位
    public int stopBits = SerialPort.STOPBITS_1;

    // 奇偶校验
    public int parity = 0;

    //串口设备名称，也是ID,一个设备一个ID
    public String name = "default";

    //串口设备的名称例如SIM900A 涉及到调研模块
    public String serialModem = "Default";


    public Map<String, Object> params = new HashMap<String, Object>();
    /**
     * 串口启动超时Specifies the serial ports' timeout (milliseconds).
     */
    public int SERIAL_TIMEOUT = 15000;


    /**
     * 接收数据超时
     */
    public int RECEIVE_TIMEOUT = 2000;

    /**
     * Specifies the serial ports' keep-alive interval (seconds).
     */
    public int WAITING_WORK = DateUtil.SECOND * 8;

    /**
     * Wait time for generic AT commands (milliseconds).
     */
    public int AT_WAIT = 300;

    /**
     * Wait time after issuing a RESET command (milliseconds).
     */
    public int AT_WAIT_AFTER_RESET = 10000;

    /**
     * Wait time transfer give the modem after entring COMMAND mode (milliseconds).
     */
    public int AT_WAIT_CMD = 1100;

    public int READ_MESSAGE_WAIT = 10000;

    public int SEND_MESSAGE_WAIT = 2500;


}
