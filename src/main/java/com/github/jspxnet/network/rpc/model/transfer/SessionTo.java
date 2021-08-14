package com.github.jspxnet.network.rpc.model.transfer;

import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/7/2 23:47
 * description: 只是为了RPC传输调用兼容 HttpSession
 **/
public class SessionTo extends HashMap<String,Object> implements HttpSession, Serializable {
    public SessionTo(Map<String,Object> map)
    {
        if (map!=null)
        {
            super.putAll(map);
        }
    }
    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return (String)super.get(RequestUtil.SESSION+".id");
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int i) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return super.get(RequestUtil.SESSION+ StringUtil.DOT + s);
    }

    @Override
    public Object getValue(String s) {
        return super.get(RequestUtil.SESSION+StringUtil.DOT + s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Vector<String> names = new Vector<>();
        for (String key:super.keySet())
        {
            if (key==null)
            {
                continue;
            }
            if (key.startsWith(RequestUtil.SESSION))
            {
                names.add(key);
            }
        }
        return names.elements();
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        super.put(RequestUtil.SESSION+StringUtil.DOT+s,o);
    }

    @Override
    public void putValue(String s, Object o) {
        super.put(RequestUtil.SESSION+StringUtil.DOT+s,o);
    }

    @Override
    public void removeAttribute(String s) {
        super.remove(RequestUtil.SESSION+StringUtil.DOT+s);
    }

    @Override
    public void removeValue(String s) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
