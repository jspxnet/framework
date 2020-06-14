/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io.zip;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-9
 * Time: 16:48:03
 */

import com.github.jspxnet.utils.DateUtil;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.zip.*;
import java.net.*;

public class zipServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String temp;
        ServletOutputStream out = res.getOutputStream();
        String fileName = DateUtil.toString(DateUtil.DATA_FORMAT);
        res.setContentType("application/zip");
        res.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".zip;");
        if ((temp = req.getParameter("url")) != null) {
            ZipOutputStream zout = new ZipOutputStream(out);
            HttpURLConnection con = null;
            URL url;
            try {
                url = new URL(temp);
                con = (HttpURLConnection) url.openConnection();
                DataInputStream input = new DataInputStream(con.getInputStream());
                zout.setMethod(ZipOutputStream.DEFLATED);
                //设置压缩方法               
                zout.putNextEntry(new ZipEntry(fileName + ".html"));//生成一个ZIP entry，
                byte[] data = new byte[1024];
                int nbRead = 0;
                while (nbRead >= 0) {
                    try {
                        nbRead = input.read(data);
                        if (nbRead >= 0) {
                            zout.write(data, 0, nbRead);
                        }
                    } catch (Exception e) {
                        nbRead = -1;
                    }
                }
                zout.closeEntry();
                out.flush();
            } catch (Exception e) {
                res.setContentType("text/html");
                out.println("<html><head><title>Error</title></head>");
                out.println("<body><b>");
                out.println("An error has occured while processing " + temp + "<br>");
                out.println("Here is the exception: <br>" + e + "<br>");
                e.printStackTrace(new PrintWriter(out));
                out.println("</body>");
                out.println("</html>");
            }
            try {
                zout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (con != null) {
                try {
                    con.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}