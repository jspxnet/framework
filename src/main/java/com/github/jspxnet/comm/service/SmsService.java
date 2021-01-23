package com.github.jspxnet.comm.service;

import com.github.jspxnet.comm.Router;
import com.github.jspxnet.comm.SerialComm;
import com.github.jspxnet.comm.router.DefaultRouter;
import com.github.jspxnet.utils.StringUtil;

import com.github.jspxnet.utils.ClassUtil;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyuan on 2015/8/24.
 */
public class SmsService extends Thread {

    final static private org.slf4j.Logger log = LoggerFactory.getLogger(SmsService.class);
    private static final Map<String, SerialComm> gatewayList = new HashMap<String, SerialComm>();
    private static SmsService instance = new SmsService();
    private static boolean quit = false;

    public static SmsService getInstance() {
        return instance;
    }

    private Router router = new DefaultRouter();


    private SmsService() {

    }

    public void startService() {
        super.setDaemon(true);
        super.start();
        ShutDownHook hook = new ShutDownHook();
        hook.doShutDownWork();
    }

    public SerialComm open(SerialConfig settings) {
        try {
            String name = settings.name;
            String serialModem = settings.serialModem;
            if (StringUtil.isNull(serialModem)) {
                serialModem = "Default";
            }
            if (!serialModem.contains(".")) {
                serialModem = "com.jspx.comm.modem.Handler" + serialModem;
            }
            //加到路由表
            if (gatewayList.containsKey(name)) {
                return gatewayList.get(name);
            }
            SerialComm serialComm = (SerialComm) ClassUtil.newInstance(serialModem);
            serialComm.setSettings(settings);
            serialComm.connect();
            serialComm.init();
            gatewayList.put(name, serialComm);
            return serialComm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SerialComm getGateway(String name) {
        return gatewayList.get(name);
    }

    /**
     * @return 得到路由计算
     */
    public SerialComm getRouterComm() {
        router.setGatewayList(gatewayList);
        return router.getRouter();
    }

    public List<SerialStatus> getSerialStatus()  {
        List<SerialStatus> list = new ArrayList<>();
        for (SerialComm serialComm : gatewayList.values()) {
            list.add(serialComm.getStatus());
        }
        return list;
    }


    //synchronized
    @Override
    synchronized public void run() {
        while (!quit) {
            try {
                for (SerialComm serialComm : gatewayList.values()) {
                    if (serialComm.isNeedReStart()) {
                        //如果发生错误,重置设备
                        serialComm.close();
                        sleep(15000);
                        serialComm.connect();
                        sleep(15000);

                    } else {
                        String response = serialComm.getResponse();
                        if (response != null && response.contains("ERROR")) {
                            //如果发生错误,重置设备
                            serialComm.reset();
                        }
                    }
                    //--------------------------------------------------------------------------------------------------
                    //如果连续
                    if (serialComm.isWaitingWork()) {
                        serialComm.canSendNotification();
                    }
                }
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        interrupt();//重新设置中断标示位
    }


    public void shutdown() {
        quit = true;
        for (SerialComm serialComm : gatewayList.values()) {
            log.info("关闭设备 {},串口 {}", serialComm.getName(), serialComm.getPortName());
            serialComm.close();
        }
        gatewayList.clear();
        log.info("关闭短信设备");
        System.out.println("-------------关闭短信设备-----------");
    }


    class ShutDownHook {
        public ShutDownHook() {
            doShutDownWork();
        }

        private void doShutDownWork() {
            Runtime run = Runtime.getRuntime();//当前 Java 应用程序相关的运行时对象。
            run.addShutdownHook(new Thread() { //注册新的虚拟机来关闭钩子
                @Override
                public void run() {
                    //程序结束时进行的操作
                    shutdown();
                }
            });
        }
    }

}
