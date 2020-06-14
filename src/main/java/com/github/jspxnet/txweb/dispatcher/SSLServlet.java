package com.github.jspxnet.txweb.dispatcher;

/**
 * Created by yuan on 2014/9/6 0006.
 */

import com.github.jspxnet.boot.environment.Environment;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SSL Servlet
 *
 * @author IceWee
 * @version 1.0
 */
public class SSLServlet extends HttpServlet {


    private static final String ATTR_CER = "javax.servlet.request.X509Certificate";
    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";
    private static final String DEFAULT_ENCODING = Environment.defaultEncode;
    private static final String SCHEME_HTTPS = "https";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(DEFAULT_ENCODING);
        PrintWriter out = response.getWriter();
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute(ATTR_CER);
        if (certs != null) {
            int count = certs.length;
            out.println("共检测到[" + count + "]个客户端证书");
            for (int i = 0; i < count; i++) {
                out.println("客户端证书 [" + (++i) + "]： ");
                out.println("校验结果：" + verifyCertificate(certs[--i]));
                out.println("证书详细：\r" + certs[i].toString());
            }
        } else {
            if (SCHEME_HTTPS.equalsIgnoreCase(request.getScheme())) {
                out.println("这是一个HTTPS请求，但是没有可用的客户端证书");
            } else {
                out.println("这不是一个HTTPS请求，因此无法获得客户端证书列表 ");
            }
        }
        out.close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * @param certificate 证书
     * @return 校验证书是否过期
     */
    private boolean verifyCertificate(X509Certificate certificate) {
        boolean valid = true;
        try {
            certificate.checkValidity();
        } catch (Exception e) {
            e.printStackTrace();
            valid = false;
        }
        return valid;
    }

}
