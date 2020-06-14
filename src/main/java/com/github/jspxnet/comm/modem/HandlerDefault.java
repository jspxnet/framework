package com.github.jspxnet.comm.modem;

import com.github.jspxnet.comm.SerialComm;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by chenyuan on 2015/8/24.
 */
@Slf4j
public class HandlerDefault extends SerialComm {

    @Override
    public void init() {
        try {
            //设置来电显示
            write("AT+CLIP=1");
            writeR();
            Thread.sleep(getSettings().AT_WAIT);
            writeCSQ();
            Thread.sleep(getSettings().AT_WAIT);
        } catch (Exception e) {
            e.printStackTrace();
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
