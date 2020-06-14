package com.github.jspxnet.txweb.evasive.condition;

import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by ChenYuan on 2017/6/15.
 */
public abstract class AbstractDecide implements Decide {
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected String content = StringUtil.empty;

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }


    @Override
    abstract public boolean execute();
}
