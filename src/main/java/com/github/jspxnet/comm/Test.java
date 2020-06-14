package com.github.jspxnet.comm;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.comm.service.SerialConfig;
import com.github.jspxnet.comm.service.SmsService;
import com.github.jspxnet.utils.BeanUtil;

import java.lang.Thread;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by chenyuan on 2015/8/23.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        BigDecimal a = BeanUtil.getTypeValue(1, BigDecimal.class);
        System.out.println("-----isAlive=" + a);


    /*
        JspxNetApplication.autoRun();
       SmsService smsService = SmsService.getInstance();
        smsService.startService();

        SerialConfig settings = new SerialConfig();
        settings.name = "default";
        settings.serialModem = "SIM900A";
        settings.portName = "COM5";*/

        // SerialComm serialComm = smsService.open(settings);


/*
        System.out.println("-----isAlive=" + serialComm.isAlive());

        System.out.println("-----getSignal=" + serialComm.getSignal());

       // System.out.println("-----信息数量=" + serialComm.getMessageNumber());


        SmsMessageIn messageIn = serialComm.getReadMessage(5);
        System.out.println("read message 5----------begin");
        System.out.println(BeanUtil.toXml(messageIn));
        System.out.println("-----------------------------------end");



       /*




        list = FormatParsing.getJoinMessage(list);

        for (SmsReceive smsMessageIn:list)
        {
            System.out.println("合并.in----------begin");
            System.out.println(BeanUtil.toXml(smsMessageIn));
            System.out.println("----------end");
        }


  //getMessageIn(String response)
        Thread.sleep(2000);
        serialComm.write("AT+CMGL=\"ALL\"");
        serialComm.writeR();

        Thread.sleep(3000);
        String txt = serialComm.getResponse();
        List<SmsMessageIn> list = FormatParsing.getMessageInList(txt);
        for (SmsMessageIn smsMessageIn:list)
        {
            System.out.println("SmsMessageIn----------begin");
            System.out.println(BeanUtil.toXml(smsMessageIn));
            System.out.println("----------end");
        }



        Thread.sleep(1000);
         for (int i=0;i<4;i++)
         {
             boolean send = serialComm.sendSms("13984415037","["+i+"]测试发送中文短信,时间:" + DateUtil.toString(DateUtil.UTC_ST_FORMAT) + ",english");
             System.out.println("------send=" + send);

         }
                 serialComm.write("AT+CPMS?");
        serialComm.writeR();
        while (true)
        {
            if (serialComm.hasChanged()||serialComm.isOk())
            {
                serialComm.write("AT+CMGR=1");
                serialComm.writeR();
                break;
            }
            Thread.sleep(100);
        }
        AT+CPMS?
        +CPMS: "SM",3,50,"SM",3,50,"SM",3,50
        OK

//////判断是否有新短信,当前有6条
AT+CPMS="SM"
+CPMS: 6,50,6,50,6,50

OK

得到新短信条数
        serialComm.write("AT+CPMS=\"SM\"");
        serialComm.writeR();
        Thread.sleep(1000);
        String txt = serialComm.getResponse();
        int newMsgNum = FormatParsing.getMessageNumber(txt);
        System.out.println("----------newMsgNum=" + newMsgNum);


        Thread.sleep(2000);
        serialComm.write("AT+CSQ");
        serialComm.writeR();

        Thread.sleep(2000);
        serialComm.write("AT+CIPMUX=0");
        serialComm.writeR();

        Thread.sleep(2000);
        serialComm.write("AT+CIPRXGET=1");
        serialComm.writeR();

        Thread.sleep(2000);
        serialComm.write("AT+CIPQRCLOSE=1");
        serialComm.writeR();

        Thread.sleep(2000);
        serialComm.write("AT+CIPMODE=0");
        serialComm.writeR();

        Thread.sleep(6000);
        serialComm.write("AT+CIPSTART=\"TCP\",\"wap.baidu.com\",80");
        serialComm.writeR();

        Thread.sleep(2000);
        serialComm.write("AT+CIPSEND");
        serialComm.writeR();

        Thread.sleep(2000);
        serialComm.write("SIM900A");
        serialComm.write1A();

        Thread.sleep(2000);

        serialComm.write("AT+CPMS=\"SM\"");
        serialComm.writeR();
        //--+CPMS: 2,50,2,50,2,50--
        Thread.sleep(2000);


        serialComm.write("AT+CMGL=\"ALL\"");
        serialComm.writeR();

        */

        Thread.sleep(10000);


        //smsService.shutdown();
        //txt--NO CARRIER--


    }


}

