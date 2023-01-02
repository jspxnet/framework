package com.github.jspxnet.sioc;

import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

//只是防止tomcat 签入异常
@Data
public class CatalinaObject implements Serializable {
    private String base = System.getProperty("CATALINA_HOME", System.getProperty("user.dir"));
    private String home = System.getProperty("CATALINA_HOME", System.getProperty("user.dir"));
}
