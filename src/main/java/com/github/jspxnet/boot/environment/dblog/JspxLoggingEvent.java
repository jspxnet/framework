package com.github.jspxnet.boot.environment.dblog;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "jspx_logging_event",cache = false)
public class JspxLoggingEvent implements Serializable {
    @Id
    @Column(caption = "id", notNull = true)
    private long id = 0;

    @Column(caption = "线程",length = 255)
    private String threadName;

    @Column(caption = "类名",length = 255)
    private String loggerName;


    @Column(caption = "类名",length = 50)
    private String level;

    @Column(caption = "日志上下文",length = 1000)
    private String loggerContext;

    @Column(caption = "日志",length = 10000)
    private String message;

    @Column(caption = "callerDataArray",length = 10000)
    private String callerDataArray;

    @Column(caption = "marker",length = 10000)
    private String marker;

    @Column(caption = "mdcPropertyMap",length = 10000)
    private String mdcPropertyMap;

    @Column(caption = "时间")
    private Date createDate = new Date();

  //  {"threadName":"main","loggerName":"com.github.jspxnet.boot.JspxCoreListener","loggerContext":"ch.qos.logback.classic.LoggerContext[default]","loggerContextVO":{"name":"default","propertyMap":{},"birthTime":1654539343493},"message":"-jspx.net framework 6.64 AGPLv3 Powered By chenYuan  start completed  J2SDK","throwableProxy":null,"callerDataArray":null,"marker":null,"mdcPropertyMap":{},"timeStamp":1654539348833}

}
