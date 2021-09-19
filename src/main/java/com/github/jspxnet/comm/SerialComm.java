package com.github.jspxnet.comm;

import com.github.jspxnet.comm.service.SerialConfig;
import com.github.jspxnet.comm.service.SerialStatus;
import com.github.jspxnet.utils.StringUtil;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread;
import java.util.*;

/**
 * Created by chenyuan on 2015/8/24.
 * SerialModem
 */
@Slf4j
public abstract class SerialComm extends Observable implements SerialPortEventListener {

    public String name = StringUtil.empty;
    protected InputStream in;
    protected OutputStream out;
    protected CommPort commPort;
    protected boolean open = false;
    protected String portName = StringUtil.empty;
    protected String encoding = "UCS2";
    private String response = StringUtil.empty;
    private SerialConfig settings = new SerialConfig();

    private boolean waiting = false;
    private SerialStatus status = new SerialStatus();

    //最后动作时间,用来判断是否空闲
    private long lastTimeMillis = System.currentTimeMillis();

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

    public boolean isWait() {
        return waiting;
    }

    public String getName() {
        return name;
    }

    public String getPortName() {
        return portName;
    }

    public void write(String cmd) throws IOException {
        write(cmd.getBytes());
    }

    public void write(byte[] cmd) throws IOException {
        lastTimeMillis = System.currentTimeMillis();
        waiting = true;
        out.write(cmd);
    }

    public void writeR() throws IOException {
        waiting = true;
        out.write('\r');
    }

    public void write1A() throws IOException {
        waiting = true;
        out.write(0x1A);
        out.flush();
    }

    public String getResponse() {
        return response;
    }

    public boolean isOk() {
        return getResponse().toUpperCase().contains("OK");
    }

    public void connect() throws Exception {
        if (open) {
            close();
        }
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            log.error("Error: Port is currently in use");
        } else {
            status.setGatewayName(name);
            status.setPortName(portName);
            try {
                commPort = portIdentifier.open(getClass().getName(), settings.SERIAL_TIMEOUT);
                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(settings.rate, settings.dataBits == 0 ? SerialPort.DATABITS_8 : settings.dataBits, settings.stopBits, settings.parity);
                    serialPort.enableReceiveTimeout(settings.RECEIVE_TIMEOUT);
                    in = serialPort.getInputStream();
                    out = serialPort.getOutputStream();
                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                    status.setRestartCount(status.getResetCount() + 1);
                    log.info("串口开启,name=" + name + ",port=" + portName + ",type=" + getPortTypeName(portIdentifier.getPortType()) + ",rate=" + settings.rate + ",dataBits=" + settings.dataBits);
                    open = true;
                } else {
                    log.error("Error: Only serial ports are handled by,错误的开启端口");
                }
            } catch (PortInUseException e) {
                log.error("端口" + portName + "已经被占用", e);
                e.printStackTrace();
            } catch (TooManyListenersException e) {
                log.error("端口" + portName + "监听者过多", e);
                e.printStackTrace();
            } catch (UnsupportedCommOperationException e) {
                log.error("端口操作命令不支持", e);
                //"端口操作命令不支持";
                e.printStackTrace();
            } catch (IOException e) {
                log.error("打开端口" + portName + "失败", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * @param phone   电话
     * @param content 内容
     * @return 等待的时间短了不稳定
     * @throws Exception 异常
     */
    public boolean sendSms(String phone, String content) throws Exception {
        lastTimeMillis = System.currentTimeMillis();
        //设置手机号
        write("AT+CMGS=\"" + StringUtil.toMobileUTFString(phone) + "\"");
        writeR();
        if (!isOk()) {
            Thread.sleep(settings.AT_WAIT_CMD);
        }
        int i = 0;
        waiting = true;
        while (true) {
            lastTimeMillis = System.currentTimeMillis();
            Thread.sleep(settings.AT_WAIT_CMD);
            String response = getResponse();
            if (response.contains(">")) {
                write(StringUtil.toMobileUTFString(content));
                write1A();
                Thread.sleep(settings.SEND_MESSAGE_WAIT);
                status.updateSendCount();
                return true;
            }
            i++;
            if (i > settings.RETRIES) {
                return false;
            }
        }
    }

    static public String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }


    public void close() {
        if (open) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (commPort != null) {
                commPort.close();
            }

        }
    }


    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            Thread.sleep(settings.AT_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (event.getEventType()) {
            case SerialPortEvent.BI: /*Break interrupt,通讯中断   10*/
            case SerialPortEvent.OE: /*Overrun error，溢位错误   7*/
            case SerialPortEvent.FE: /*Framing error，传帧错误   9*/
            case SerialPortEvent.PE: /*Parity error，校验错误*/
            case SerialPortEvent.CD: /*Carrier detect，载波检测*/
            case SerialPortEvent.CTS: /*Clear transfer send，清除发送*/
            case SerialPortEvent.DSR: /*Data set ready，数据设备就绪*/
            case SerialPortEvent.RI: /*Ring indicator，响铃指示*/
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: /*Output buffer is empty，输出缓冲区清空*/
                break;
            case SerialPortEvent.DATA_AVAILABLE: /*Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端*/
                //这里只是事件才会有调用
                StringBuilder buffer = new StringBuilder();
                try {
                    //一次性全部数据读取
                    int x;
                    while ((x = in.read()) != -1) {
                        buffer.append((char) x);
                    }
                    response = buffer.toString();
                    log.debug("---------at response---------\r\n{}------------------end", response);
                    waiting = false;
                    setChanged();
                    notifyObservers(response);
                } catch (IOException e) {
                    in = null;
                    response = "restart";
                    log.error("设备断开,不能链接设备", e);
                }
                break;
            default:{
                break;
            }
        }

    }

    public void canSendNotification() {
        setChanged();
        notifyObservers(PortObserver.CAN_SEND_NOTIFICATION);
    }

    //------------------------------------------------------------------------------------------------------------------
    public void reset() throws IOException, InterruptedException {
        write("\u001b");
        Thread.sleep(settings.AT_WAIT);
        write("+++");
        Thread.sleep(settings.AT_WAIT);
        write("ATZ");
        writeR();
        Thread.sleep(settings.AT_WAIT_AFTER_RESET);
    }

    public boolean isAlive() throws IOException, InterruptedException {
        write("AT");
        writeR();
        Thread.sleep(settings.AT_WAIT);
        return isOk();
    }

    //得到型号强度
    //应在 10 到 31 之间，数值越大表明信号质量越好
    //99为无信号
    public void writeCSQ() throws IOException, InterruptedException {
        write("AT+CSQ");
        writeR();
        Thread.sleep(settings.AT_WAIT_CMD);
    }


    public void writeCMGL() throws IOException, InterruptedException {
        write("AT+CMGL=\"ALL\"");
        writeR();
        Thread.sleep(settings.AT_WAIT);
    }


    public boolean isNeedReStart() {
        return in == null || out == null || commPort == null || "RESTART".equalsIgnoreCase(response);
    }

    //------------------------------------------------------------------------------------------------------------------
/*
    //得到信息数量
    public int getMessageNumber() throws IOException, InterruptedException {
        write("AT+CPMS=\"SM\"");
        writeR();
        Thread.sleep(settings.AT_WAIT_CMD);
        return FormatParsing.getMessageNumber(getResponse());
    }

    //得到信息内容
    public SmsReceive getReadMessage(int messageId) throws IOException, InterruptedException {
        write("AT+CMGR=" + messageId);
        writeR();
        Thread.sleep(settings.AT_WAIT_CMD);
        String txt = getResponse();
        return FormatParsing.getMessageIn(txt);
    }
 */

    /**
     * @return 挂断电话
     */
    public boolean stopRing() {
        try {
            write("AT+HVOIC"); //正在通话中
            writeR();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isOk();
    }


    /**
     * 删除消息
     *
     * @param messageId 手机消息id
     * @return 得到是否删除
     * @throws IOException          异常
     * @throws InterruptedException 异常
     */
    public boolean deleteMessage(int messageId) throws IOException, InterruptedException {
        write("AT+CMGD=" + messageId);
        writeR();
        Thread.sleep(settings.AT_WAIT_CMD);
        return isOk();
    }


    public String getEncoding() {
        return encoding;
    }

    /**
     * @return 判断是否满足空闲时间
     */
    public boolean isWaitingWork() {
        return !waiting && (System.currentTimeMillis() - lastTimeMillis > settings.WAITING_WORK);
    }


    /**
     * @return 得到统计数据
     */
    public SerialStatus getStatus() {
        return status;
    }
}
