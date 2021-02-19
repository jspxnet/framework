package com.github.jspxnet.txweb.dispatcher;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/20 0:55
 * description:
 **/
@Slf4j
public class CloseContextFinalizer implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //卸载jdbc驱动begin
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver d = null;
        while (drivers.hasMoreElements()) {
            try {
                d = drivers.nextElement();
                DriverManager.deregisterDriver(d);
                log.debug(String.format("jdbc driver %s deregister", d));
            } catch (SQLException ex) {
                log.error(String.format("Error deregistering driver %s", d));
            }
        }
        AbandonedConnectionCleanupThread.getThread().interrupt();
        if (AbandonedConnectionCleanupThread.getThread().isAlive())
        {
            AbandonedConnectionCleanupThread.getThread().stop();
        }
        AbandonedConnectionCleanupThread.uncheckedShutdown();
        System.exit(1);
    }
}
