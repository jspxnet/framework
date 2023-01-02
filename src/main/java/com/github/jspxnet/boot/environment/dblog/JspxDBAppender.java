package com.github.jspxnet.boot.environment.dblog;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.dao.impl.GenericDAOImpl;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 目前测试 在tomcat下lib是隔离的,加载不进来
 * 如果是用打包为单一jar的时候可以正常是用
 *
 */
public class JspxDBAppender<E> extends UnsynchronizedAppenderBase<E> {
    private final GenericDAO genericDAO = new GenericDAOImpl();
    private final List<JspxLoggingEvent> CACHE =  new ArrayList<>();

    @Override
    public void start() {
        String soberFactoryName = EnvFactory.getEnvironmentTemplate().getString("soberFactory","jspxSoberFactory");
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        SoberFactory soberFactory = (SoberFactory)beanFactory.getBean(soberFactoryName);
        genericDAO.setSoberFactory(soberFactory);
        super.start();
    }

    @Override
    public void stop() {
        try {
            Thread.sleep(6* DateUtil.SECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.stop();
    }

    @Override
    protected void append(E o) {
        ILoggingEvent vo = (ILoggingEvent)o;
        /*
        {"threadName":"main","loggerName":"com.github.jspxnet.boot.JspxCoreListener","loggerContext":"ch.qos.logback.classic.LoggerContext[default]","loggerContextVO":{"name":"default","propertyMap":{},"birthTime":1654539343493},"message":"-jspx.net framework 6.64 AGPLv3 Powered By chenYuan  start completed  J2SDK","throwableProxy":null,"callerDataArray":null,"marker":null,"mdcPropertyMap":{},"timeStamp":1654539348833}
         */
        JspxLoggingEvent jspxLoggingEvent = BeanUtil.copy(vo,JspxLoggingEvent.class);
        jspxLoggingEvent.setLevel(vo.getLevel().levelStr);
        jspxLoggingEvent.setLoggerContext(ObjectUtil.toString(vo.getLoggerContextVO()));
        if (vo.hasCallerData())
        {
            jspxLoggingEvent.setCallerDataArray(getStackTraceString(vo.getCallerData()));
        }
        jspxLoggingEvent.setMessage(vo.getFormattedMessage());
        jspxLoggingEvent.setCreateDate(new Date(vo.getTimeStamp()));
        CACHE.add(jspxLoggingEvent);
        if (CACHE.size()>5)
        {
            batchSave();
        }
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void batchSave()
    {
        if (CACHE.isEmpty())
        {
            return;
        }
        synchronized (CACHE)
        {
            try {
                genericDAO.batchSave(CACHE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CACHE.clear();
            }
        }
    }

    static private String getStackTraceString(StackTraceElement[] callerData)
    {
        if (ObjectUtil.isEmpty(callerData))
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el:callerData)
        {
            sb.append(el.toString()).append("\r\n");
        }
        return sb.toString();
    }

}
