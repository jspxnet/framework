package com.github.jspxnet.txweb.dispatcher;


import com.github.jspxnet.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/20 0:55
 * description:
 **/
@Slf4j
public class CloseTomcatFinalizer implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Thread.sleep(DateUtil.SECOND*3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }
}
