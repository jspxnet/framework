package com.github.jspxnet.network.rpc.model.transfer;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 20:23
 * description: 应答容器
 **/
public class ResponseTo extends HashMap<String,Object>  implements HttpServletResponse, Serializable {


    final private StringWriter writer = new StringWriter();

    public ResponseTo(Map<String,Object> map)
    {
        if (map!=null)
        {
            super.putAll(map);
        }
    }


    public ResponseTo(HttpServletResponse response)
    {
        super.putAll(RequestUtil.getResponseMap(response));
    }

    @Override
    public String getCharacterEncoding() {
        return (String) super.get("characterEncoding");
    }

    @Override
    public String getContentType() {
        return (String) super.get("contentType");
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(writer);
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return ObjectUtil.toInt(super.get("bufferSize"));
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return Locale.CHINA;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return super.containsKey(s);
    }

    @Override
    public String encodeURL(String s) {
        return URLUtil.getEncoder(s, Environment.defaultEncode);
    }

    @Override
    public String encodeRedirectURL(String s) {
        return URLUtil.getEncoder(s, Environment.defaultEncode);
    }

    @Override
    public String encodeUrl(String s) {
        return URLUtil.getEncoder(s, Environment.defaultEncode);
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return URLUtil.getEncoder(s, Environment.defaultEncode);
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    @Override
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        Object value  = super.get(s);
        return value==null? StringUtil.empty:(String)value;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return new HashSet<>(0);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return new HashSet<>(0);
    }
}
