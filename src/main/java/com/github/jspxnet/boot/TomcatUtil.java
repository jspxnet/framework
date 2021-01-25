package com.github.jspxnet.boot;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/1/26 2:09
 * @description: jspbox
 **/
public class TomcatUtil {
    /**
     * 得到tomcat端口
     */
    public static void getHttpPort() {
        try {
            MBeanServer server = null;
            if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                server = MBeanServerFactory.findMBeanServer(null).get(0);
            }

            Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
            Iterator<ObjectName> iterator = names.iterator();
            ObjectName name = null;
            while (iterator.hasNext()) {
                name = (ObjectName) iterator.next();

                String protocol = server.getAttribute(name, "protocol").toString();
                String scheme = server.getAttribute(name, "scheme").toString();
                String port = server.getAttribute(name, "port").toString();
                System.out.println(protocol + " : " + scheme + " : " + port);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
