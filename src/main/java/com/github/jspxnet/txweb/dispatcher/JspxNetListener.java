package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.boot.JspxNetApplication;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by chenyuan on 2015-7-30.
 *
 */
public class JspxNetListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Dispatcher.init(servletContextEvent.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        JspxNetApplication.destroy();
    }
}
