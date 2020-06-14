/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.mac;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-8-31
 * Time: 16:12:22
 */

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import static java.util.regex.Pattern.compile;

public abstract class NetworkInfo {
    private static final String LOCALHOST = "localhost";
    public static final String NSLOOKUP_CMD = "nslookup";

    /**
     * 返回一个字节的十六进制字符串
     *
     * @param b byte
     * @return 返回一个字节的十六进制字符串
     */
    static String hexByte(byte b) {
        String s = "000000" + Integer.toHexString(b);
        return s.substring(s.length() - 2);
    }

    public abstract String parseMacAddress() throws Exception;

    private static String localMac = null;
    /**
     * JDK1.6新特性获取网卡MAC地址
     */
    private static String getMac() throws SocketException {
        if (localMac!=null)
        {
            return localMac;
        }
        Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
        while (el.hasMoreElements()) {
            byte[] mac = el.nextElement().getHardwareAddress();
            if (mac == null || mac.length < 1) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            for (byte b : mac) {
                builder.append(hexByte(b));
                builder.append("-");
            }
            if (builder.length() > 1) {
                builder.deleteCharAt(builder.length() - 1);
            }
            if (builder.toString().toUpperCase().startsWith("00-00-00-00") || builder.toString().toUpperCase().startsWith("FF-FF-FF-FF")) {
                continue;
            }
            return localMac = builder.toString().toUpperCase();
        }
        return null;
    }

    /**
     * @return Not too sure of the ramifications here, but it just doesn't seem right
     * @throws Exception 异常
     */
    public String parseDomain()
            throws Exception {
        return parseDomain(LOCALHOST);
    }


    /**
     * @return Universal entry for retrieving MAC Address
     * @throws IOException 异常
     */
    public static String getMacAddress()
            throws IOException {
        try {
            return getMac();
        } catch (SocketException e) {
            try {
                NetworkInfo info = getNetworkInfo();
                return localMac = info.parseMacAddress();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IOException(ex.getMessage());
            }
        }
    }


    /**
     * @return Universal entry for retrieving domain info
     * @throws IOException 异常
     */
    public static String getNetworkDomain() throws IOException {
        try {
            NetworkInfo info = getNetworkInfo();
            return info.parseDomain();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException(ex.getMessage());
        }
    }

    protected String parseDomain(String hostname) throws Exception {        // get the address of the host we are looking for - verification
        java.net.InetAddress addy;
        try {
            addy = java.net.InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }
        // back out transfer the hostname - just validating
        hostname = addy.getCanonicalHostName();
        String nslookupCommand = NSLOOKUP_CMD + " " + hostname;
        // run the lookup command
        String nslookupResponse;
        try {
            nslookupResponse = runConsoleCommand(nslookupCommand);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }
        StringTokenizer tokeit = new StringTokenizer(nslookupResponse, "\n", false);
        while (tokeit.hasMoreTokens()) {
            String line = tokeit.nextToken();
            if (line.startsWith("Name:")) {
                line = line.substring(line.indexOf(":") + 1);
                line = line.trim();
                if (isDomain(line, hostname)) {
                    line = line.substring(hostname.length() + 1);
                    return line;
                }
            }
        }
        return "n.a.";
    }

    private static boolean isDomain(String domainCandidate, String hostname) {
        Pattern domainPattern = compile("[\\w-]+\\.[\\w-]+\\.[\\w-]+\\.[\\w-]+");
        Matcher m = domainPattern.matcher(domainCandidate);
        return m.matches() && domainCandidate.startsWith(hostname);
    }

    protected String runConsoleCommand(String command) throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
        StringBuilder buffer = new StringBuilder();
        for (; ; ) {
            int c = stdoutStream.read();
            if (c == -1) {
                break;
            }
            buffer.append((char) c);
        }
        String outputText = buffer.toString();
        stdoutStream.close();
        return outputText;
    }

    /**
     * Sort of like a factory...
     */
    private static NetworkInfo getNetworkInfo() throws IOException {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return new WindowsNetworkInfo();
        } else if (os.startsWith("Linux")) {
            return new LinuxNetworkInfo();
        } else {
            throw new IOException("unknown operating system: " + os);
        }
    }

    protected String getLocalHost() throws Exception {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }
    }


    /**
     * 验收
     *
     * @param args 无效
     */
    public static void main(String[] args) {
        try {
            System.out.println("Network infos");
            System.out.println("  Operating System: " + System.getProperty("os.name"));
            System.out.println("  IP/Localhost: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("  MAC Address: " + getMacAddress());
            System.out.println("  Domain: " + getNetworkDomain());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}