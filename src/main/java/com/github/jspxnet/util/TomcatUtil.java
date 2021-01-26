package com.github.jspxnet.util;

import com.github.jspxnet.utils.StringUtil;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/26 2:09
 * description: tomcat端口
 **/
public class TomcatUtil {
    /**
     *
     * @return tomcat的端口信息
     */
    public static List<PortInfo> getPortList() {
        try {
            MBeanServer server = null;
            if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                server = MBeanServerFactory.findMBeanServer(null).get(0);
            }

            List<PortInfo> result = new ArrayList<>();
            Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
            Iterator<ObjectName> iterator = names.iterator();
            ObjectName name = null;
            while (iterator.hasNext()) {
                name = iterator.next();

                PortInfo portInfo = new PortInfo();
                portInfo.setProtocol(server.getAttribute(name, "protocol").toString());
                portInfo.setScheme(server.getAttribute(name, "scheme").toString());
                portInfo.setPort(StringUtil.toInt(server.getAttribute(name, "port").toString()));
                result.add(portInfo);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
