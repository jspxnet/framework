package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class IService {
    final public static int REQUEST_MAX_LENGTH = 5242880; //最大json请求不超过5M
    protected static WebConfigManager webConfigManager = TXWebConfigManager.getInstance();

    abstract public String doing(HttpServletRequest request, HttpServletResponse response, String call) throws Exception;
}
