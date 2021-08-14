package com.github.jspxnet.comm.service;

import com.github.jspxnet.comm.Router;
import com.github.jspxnet.comm.SerialComm;
import com.github.jspxnet.comm.router.DefaultRouter;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyuan on 2015/8/24.
 */
@Slf4j
public class SmsService extends Thread {
    private static final Map<String, SerialComm> GATEWAY_LIST = new HashMap<String, SerialComm>();
    private static SmsService INSTANCE;
    private static boolean quit = false;

    public synchronized static SmsService getInstance() {
        if (INSTANCE==null)
        {
            INSTANCE = new SmsService();
        }
        return INSTANCE;
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

            if (!serialModem.contains(StringUtil.DOT)) {
                serialModem = "com.jspx.comm.modem.Handler" + serialModem;
            }
            //加到路由表
            if (GATEWAY_LIST.containsKey(name)) {
                return GATEWAY_LIST.get(name);
            }
            SerialComm serialComm = (SerialComm) ClassUtil.newInstance(serialModem);
            serialComm.setSettings(settings);
            serialComm.connect();
            serialComm.init();
            GATEWAY_LIST.put(name, serialComm);
            return serialComm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SerialComm getGateway(String name) {
        return GATEWAY_LIST.get(name);
    }

    /**
     * @return 得到路由计算
     */
    public SerialComm getRouterComm() {
        router.setGatewayList(GATEWAY_LIST);
        return router.getRouter();
    }

    public List<SerialStatus> getSerialStatus()  {
        List<SerialStatus> list = new ArrayList<>();
        for (SerialComm serialComm : GATEWAY_LIST.values()) {
            list.add(serialComm.getStatus());
        }
        return list;
    }


    //synchronized
    @Override
    synchronized public void run() {
        while (!quit) {
            try {
                for (SerialComm serialComm : GATEWAY_LIST.values()) {
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
        for (SerialComm serialComm : GATEWAY_LIST.values()) {
            log.info("关闭设备 {},串口 {}", serialComm.getName(), serialComm.getPortName());
            serialComm.close();
        }
        GATEWAY_LIST.clear();
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
