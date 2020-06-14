package com.github.jspxnet.txweb.evasive.condition;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public interface Decide {
    void setContent(String content);

    void setRequest(HttpServletRequest request);

    void setResponse(HttpServletResponse response);


    boolean execute();

}
