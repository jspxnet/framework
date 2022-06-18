/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.dao.IpLocationDAO;
import com.github.jspxnet.txweb.table.IpLocation;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-6-23
 * Time: 17:43:29
 * jspx.juser.dao.aop.IpLocationDAOImpl
 * BeanFactory beanFactory = EnvFactory.getBeanFactory();
 * IpLocationDAO ipLocationDAO = (IpLocationDAO)beanFactory.getBean(Environment.ipLocationDAO);
 * //ipLocationDAO.deleteAll();
 * //System.out.println(ipLocationDAO.fileToDataBase());
 * long p1 = 0,p2 = 0,p3 = 0,p4 = 0;
 * <p>
 * BeanFactory beanFactory = EnvFactory.getBeanFactory();
 * IpLocationDAO ipLocationDAO = (IpLocationDAO)beanFactory.getBean(Environment.ipLocationDAO);
 * // System.out.println("-----" + ipLocationDAO.fileToDataBase());   导入数据库
 * IpLocation ipLocation = ipLocationDAO.getIpLocation("220.181.111.147");
 * System.out.println("-----" + ipLocation.getCity() + " " + ipLocation.getCountry());
 */

public class IpLocationDAOImpl extends JdbcOperations implements IpLocationDAO {


    private String fileName;


    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public IpLocationDAOImpl() {

    }


    @Override
    public boolean deleteAll() {
        return createCriteria(IpLocation.class).delete(false) > 0;
    }

    @Override
    public int fileToDataBase() throws Exception {

        String myFile = StringUtil.substringBefore(fileName, "!");
        File f = new File(myFile);
        if (!f.isFile() || !f.canRead()) {
            return 0;
        }

        String partName = StringUtil.substringAfterLast(fileName, "!");
        if (partName.startsWith("/")) {
            partName = StringUtil.substringAfterLast(partName, "/");
        }
        ZipFile file = new ZipFile(myFile);
        InputStream inputStream = file.getInputStream(file.getEntry(partName));
        BufferedReader in = null;
        List<IpLocation> saveList = new ArrayList<IpLocation>();
        int i = 0;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream, Environment.defaultEncode));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                i++;
                String startIp = StringUtil.trim(inputLine.substring(0, 16));
                String endIp = StringUtil.trim(inputLine.substring(16, 32));

                String area = StringUtil.trim(inputLine.substring(32));
                area = StringUtil.replace(area, "CZ88.NET", " ");
                area = StringUtil.replace(area, "jspx.net", " ");

                IpLocation ipLocation = new IpLocation();
                ipLocation.setLineNumber(i);
                ipLocation.setBeginIp(StringUtil.toIpNumber(startIp));
                ipLocation.setEndIp(StringUtil.toIpNumber(endIp));
                if (area.contains(" ")) {
                    ipLocation.setCountry(StringUtil.substringBefore(area, " "));
                    ipLocation.setCity(StringUtil.substringAfterLast(area, " "));
                } else {
                    ipLocation.setCountry(area);
                    ipLocation.setCity("");
                }
                saveList.add(ipLocation);
                if (saveList.size() > 500) {

                    super.save(saveList);
                    saveList.clear();
                }
            }
            if (saveList.size() > 0) {

                super.save(saveList);
                saveList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return i;
    }


    /**
     * @param ipName ip数
     * @return IpLocation ip地址说明
     */
    @Override
    public IpLocation getIpLocation(String ipName) {
        long ipLong = ObjectUtil.toLong(StringUtil.toIpNumber(ipName));
        IpLocation ipLocation = createCriteria(IpLocation.class)
                .add(Expression.le("beginIp", ipLong))
                .add(Expression.ge("endIp", ipLong))
                .addOrder(Order.asc("lineNumber")).objectUniqueResult(false);
        if (ipLocation == null) {
            return new IpLocation();
        }
        return ipLocation;
    }


}