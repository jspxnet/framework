package com.github.jspxnet.comm.modem;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by chenyuan on 2015/8/23.
 * 串口通讯接口
 */
@Slf4j
public class HandlerSIM900A extends HandlerDefault {

    @Override
    public void init() {
        try {
            //设置来电显示
            write("AT+CLIP=1");
            writeR();
            Thread.sleep(getSettings().AT_WAIT);
            echoOff();
            Thread.sleep(getSettings().AT_WAIT);
            writeCSQ();
            Thread.sleep(getSettings().AT_WAIT);

            //设置短信为文本模式
            write("AT+CMGF=1");
            writeR();
            if (!isOk()) {
                Thread.sleep(getSettings().AT_WAIT);
            }

            //文本模式参数  中英文混用
            write("AT+CSMP=17,167,2,25");
            writeR();
            if (!isOk()) {
                Thread.sleep(getSettings().AT_WAIT);
            }

            //UTF编码
            write("AT+CSCS=\"" + encoding + "\"");
            writeR();
            if (!isOk()) {
                Thread.sleep(getSettings().AT_WAIT);
            }

            getStatus().setActive(1);
            Thread.sleep(getSettings().AT_WAIT);
        } catch (Exception e) {
            log.error("初始化端口错误", e);
            getStatus().setActive(0);
        }
    }

    @Override
    public void echoOff() throws Exception {
        write("ATV1");
        writeR();

        write("ATQ0");
        writeR();

        write("ATE0");
        writeR();

    }

}
