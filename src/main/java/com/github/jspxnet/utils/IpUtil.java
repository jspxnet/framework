/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-26
 * Time: 0:58:26
 * String ipf = "211.92.137.1-211.92.137.55;211.92.137.*";
 * String ipaddress = "211.92.137.66";
 * <p>
 * bindip.setExpression(ipf);
 * println("bindip.isIPFitter()=" + bindip.isIpExpression());
 * println("bindip.isIP()=" + bindip.isIpExpression(ipaddress);
 * println("bindip.interiorly()=" + bindip.interiorly(ipaddress));
 */
@Slf4j
public final class IpUtil {
    final static private char[] EXP_CHAR = new char[]{'@', '#', '$', '%', '^', '<', '>', '&', '+', '\\', '\'', '\"'};

    private IpUtil() {

    }

    /**
     * @param expression 绑定ip地址
     * @return boolean 判断是否为一个合法 IP  绑定表达式
     */
    public static boolean isIpExpression(String expression) {
        if (expression == null) {
            return false;
        }
        if (StringUtil.ASTERISK.equals(expression)) {
            return true;
        }
        if (expression.length() < 8) {
            return false;
        }
        for (char c : EXP_CHAR) {
            if (expression.indexOf(c) != -1) {
                return false;
            }
        }
        return !(expression.startsWith("-") || expression.endsWith("-")) && StringUtil.split(expression, "\\.").length >= 3;
    }


    /**
     * @param aexp  判断一个表达式
     * @param theip ip地址
     * @return boolean   绑定IP地址用来绑定用户自己的IP
     */
    static private boolean oneInteriorly(String aexp, String theip) {
        if (StringUtil.isNull(aexp) || StringUtil.ASTERISK.equals(aexp)) {
            return true;
        }
        if (!StringUtil.isIPAddress(theip)) {
            return false;
        }
        if (aexp.equals(theip)) {
            return true;
        }
        String[] ipList = StringUtil.split(theip, "\\.");
        if (!aexp.contains("-")) {
            String[] sList = StringUtil.split(aexp, "\\.");
            for (int i = 0; i < ipList.length; i++) {
                if (!StringUtil.ASTERISK.equals(sList[i]) && StringUtil.toInt(sList[i]) != StringUtil.toInt(ipList[i])) {
                    return false;
                }
            }
            return true;
        } else {
            // 127.0.0.1-127.0.0.9
            String[] ips = StringUtil.split(aexp, "-");
            String[] alist1 = StringUtil.split(ips[0], "\\.");
            String[] blist2 = StringUtil.split(ips[1], "\\.");
            for (int i = 0; i < ipList.length; i++) {
                if (StringUtil.ASTERISK.equals(alist1[i]) || (StringUtil.toInt(ipList[i]) >= 0 && StringUtil.toInt(ipList[i]) >= StringUtil.toInt(alist1[i]))) {
                    //...
                } else {
                    return false;
                }
            }
            for (int i = 0; i < ipList.length; i++) {
                if (StringUtil.ASTERISK.equals(blist2[i]) || (StringUtil.toInt(ipList[i]) >= 0 && StringUtil.toInt(ipList[i]) <= StringUtil.toInt(blist2[i]))) {
                    //..
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * @param expression 表达式
     * @param ip         ip地址
     * @return boolean 判断一个复合表达式
     */
    static public boolean interiorly(String expression, String ip) {
        if (!StringUtil.hasLength(expression)) {
            return false;
        }
        String fen = null;
        if (expression.contains(StringUtil.SEMICOLON)) {
            fen = StringUtil.SEMICOLON;
        } else if (expression.contains(",")) {
            fen = ",";
        } else if (expression.contains("/")) {
            fen = "/";
        }
        if (fen == null) {
            return oneInteriorly(expression, ip);
        }
        String[] ipList = StringUtil.split(expression, fen);
        for (String aIp : ipList) {
            if (oneInteriorly(aIp, ip)) {
                return true;
            }
        }
        return false;
    }


    /**
     * IP address transfer long
     *
     * @param aIpAddress 地址转换为长整数
     * @return Number representing an ip address
     */
    public static long toLong(String aIpAddress) {
        if ("localhost".equalsIgnoreCase(aIpAddress)) {
            aIpAddress = "127.0.0.1";
        }
        if (!aIpAddress.contains(StringUtil.DOT)) {
            return 0;
        }
        if (aIpAddress.contains(":")) {
            aIpAddress = StringUtil.substringBefore(aIpAddress, ":");
        }
        long ip = 0;
        String[] t = aIpAddress.split("\\.");
        ip = Long.parseLong(t[0]) * 256 * 256 * 256;
        ip += Long.parseLong(t[1]) * 256 * 256;
        ip += Long.parseLong(t[2]) * 256;
        ip += Long.parseLong(t[3]);
        return ip;
    }


    /**
     * Long transfer IP address
     *
     * @param aIpAddress 地址转换为长整数
     * @return String representing an ip address
     */
    public static String getIPForLong(long aIpAddress) {
        return (aIpAddress >> 24 & 255) + StringUtil.DOT + (aIpAddress >> 16 & 255) + StringUtil.DOT + (aIpAddress >> 8 & 255) + StringUtil.DOT + (aIpAddress & 255);
    }

    /**
     *
     * @param address InetAddress
     * @return 得到ip和端口的标准写法
     */
    public static String getIp(InetAddress address) {
        if (address == null) {
            return StringUtil.empty;
        }
        String host = address.getHostAddress();
        if (host != null && host.contains("/")) {
            return StringUtil.substringAfter(host, "/");
        }
        return host;
    }

    public static String getIp(SocketAddress address) {
        if (address == null) {
            return StringUtil.empty;
        }
        String host = address.toString();
        if (host != null && host.contains("/")) {
            return StringUtil.substringAfter(host, "/");
        }
        return host;
    }

    public static String getOnlyIp(InetAddress address) {
        if (address == null) {
            return StringUtil.empty;
        }
        String host = address.getHostAddress();
        if (host != null && host.contains("/")) {
            host = StringUtil.substringAfter(host, "/");
        }
        if (host.contains(":"))
        {
            return StringUtil.substringBefore(host,":");
        }
        return host;
    }

    public static String getOnlyIp(SocketAddress address) {
        if (address == null) {
            return StringUtil.empty;
        }
        String host = address.toString();
        if (host != null && host.contains("/")) {
            host = StringUtil.substringAfter(host, "/");
        }
        if (host.contains(":"))
        {
            return StringUtil.substringBefore(host,":");
        }
        return host;
    }

    /**
     * @param min 范围，最小
     * @param max 范围，最大
     * @return 得到有效的端口
     */
    public static int getAvailablePort(int min, int max) {
        PortTracker portTracker = new PortTracker();
        portTracker.setMinPort(min);
        portTracker.setMaxPort(max);
        try {
            return portTracker.getPort();
        } catch (Exception e) {
            return RandomUtil.getRandomInt(min, max);
        }
    }

    static class PortTracker {

        private int minPort = 16536;

        public int getMinPort() {
            return minPort;
        }

        public void setMinPort(int minPort) {
            this.minPort = minPort;
            this.port = minPort;
        }

        public int getMaxPort() {
            return maxPort;
        }

        public void setMaxPort(int maxPort) {
            this.maxPort = maxPort;
        }

        private int maxPort = 20536;

        private int port = 16536;

        public PortTracker() {

        }

        public synchronized int getPort() throws Exception {
            if (this.port >= this.maxPort) {
                this.port = this.minPort;
            }
            int p = this.port++;
            while (SystemUtil.isUsedPort(p)) {
                p = this.port++;
            }
            return p;
        }
    }

    //-------------------------------------------------------------------------------------------------
    private static int ipc(byte b) {
        return (b >= 0) ? (int) b : ((int) b) + 256;
    }

    public static InetAddress getPublicIp() {

        try {
            InetAddress[] ia = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            if (ia.length == 0) {
                try {
                    return InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    try {
                        return InetAddress.getByName("127.0.0.0");
                    } catch (UnknownHostException ee) {
                        return InetAddress.getLocalHost();
                    }
                }
            }
            if (ia.length == 1) {
                // only one network connection available
                return ia[0];
            }
            // we have more addresses, find an address that is not local
            int b0, b1;
            for (InetAddress anIa : ia) {
                b0 = ipc(anIa.getAddress()[0]);
                b1 = ipc(anIa.getAddress()[1]);
                if ((b0 != 10) && // class A reserved
                        (b0 != 127) && // loopback
                        ((b0 != 172) || (b1 < 16) || (b1 > 31)) && // class C reserved
                        (!anIa.getHostAddress().contains(":"))
                ) {
                    return anIa;
                }
            }
            // there is only a local address, we filter out the possibly returned loopback address 127.0.0.1
            for (InetAddress anIa : ia) {
                if ((ipc(anIa.getAddress()[0]) != 127) &&
                        (!anIa.getHostAddress().contains(":"))) {
                    return anIa;
                }
            }
            // if all fails, give back whatever we have
            for (InetAddress anIa : ia) {
                if (!anIa.getHostAddress().contains(":")) {
                    return anIa;
                }
            }
            return ia[0];
        } catch (java.net.UnknownHostException e) {

            System.err.println("ERROR: (internal) " + e.getMessage());

        }
        try {
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            try {
                return InetAddress.getByName("localhost");
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取本机 IPV6  地址
     *
     * @return 获取本机
     * @throws IOException 异常
     */
    public static String getLocalIPv6Address() throws IOException {
        InetAddress inetAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                .getNetworkInterfaces();
        outer:
        while (networkInterfaces.hasMoreElements()) {
            Enumeration<InetAddress> inetAds = networkInterfaces.nextElement()
                    .getInetAddresses();
            while (inetAds.hasMoreElements()) {
                inetAddress = inetAds.nextElement();
                //Check if it's ipv6 address and reserved address
                if (inetAddress instanceof Inet6Address
                        && !isReservedAddr(inetAddress)) {
                    break outer;
                }
            }
        }

        String ipAddr = inetAddress.getHostAddress();
        // Filter network card No
        int index = ipAddr.indexOf('%');
        if (index > 0) {
            ipAddr = ipAddr.substring(0, index);
        }
        return ipAddr;
    }

    /**
     * Check if it's "local address" or "link local address" or
     * "loopbackaddress"
     * 检查是否为本地地址 或者 是本地地址链接
     *
     * @return result
     */
    private static boolean isReservedAddr(InetAddress inetAddr) {
        return inetAddr.isAnyLocalAddress() || inetAddr.isLinkLocalAddress()
                || inetAddr.isLoopbackAddress();
    }

    /**
     * 判断是否为IPV4 地址
     *
     * @param str 地址
     * @return 判断是否为IPV4
     */
    public static boolean isIpv4(String str) {
        if (str != null && str.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] s = str.split("\\.");
            if (Integer.parseInt(s[0]) <= 255) {
                if (Integer.parseInt(s[1]) <= 255) {
                    if (Integer.parseInt(s[2]) <= 255) {
                        return Integer.parseInt(s[3]) <= 255;
                    }
                }
            }
        }
        return false;

    }


    /**
     * @param ipAddr ip地址
     * @return 判断地址是否为IPV6地址
     */
    public static boolean isIpv6(String ipAddr) {
        String regex = "(^((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4}){1}|:))"
                + "|(([0-9A-Fa-f]{1,4}:){6}((:[0-9A-Fa-f]{1,4}){1}|"
                + "((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                + "(([0-9A-Fa-f]{1,4}:){5}((:[0-9A-Fa-f]{1,4}){1,2}|"
                + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                + "(([0-9A-Fa-f]{1,4}:){4}((:[0-9A-Fa-f]{1,4}){1,3}"
                + "|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){3}((:[0-9A-Fa-f]{1,4}){1,4}|"
                + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                + "(([0-9A-Fa-f]{1,4}:){2}((:[0-9A-Fa-f]{1,4}){1,5}|"
                + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))"
                + "|(([0-9A-Fa-f]{1,4}:){1}((:[0-9A-Fa-f]{1,4}){1,6}"
                + "|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                + "(:((:[0-9A-Fa-f]{1,4}){1,7}|(:[fF]{4}){0,1}:((22[0-3]|2[0-1][0-9]|"
                + "[0-1][0-9][0-9]|([0-9]){1,2})"
                + "([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:)))$)";

        if (ipAddr == null) {
            return false;
        }
        ipAddr = Normalizer.normalize(ipAddr, Normalizer.Form.NFKC);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipAddr);
        return matcher.matches();

    }

    /**
     * @param str 字符串转地址
     * @return 地址对象
     */
    public static InetSocketAddress getSocketAddress(String str) {
        String ip = StringUtil.substringBefore(str, ":");
        String portStr = StringUtil.substringAfter(str, ":");

        return new InetSocketAddress(StringUtil.trim(ip), ObjectUtil.toInt(portStr));
    }

    /**
     * @param str 字符串转地址,有范围的 192.168.0.1:1000-1010  这种将创建10个
     * @return 地址对象列表
     */
    public static List<InetSocketAddress> getSocketAddressRange(String str) {
        String ip = StringUtil.substringBefore(str, ":");
        String portStr = StringUtil.substringAfter(str, ":");
        int begin = ObjectUtil.toInt(StringUtil.substringBefore(portStr, "-"));
        int end = ObjectUtil.toInt(StringUtil.substringAfter(portStr, "-"));
        if (begin > end || begin == 0 || end == 0) {
            return new ArrayList<>(0);
        }
        List<InetSocketAddress> result = new ArrayList<>();
        for (int i = begin; i <= end; i++) {
            if (i <= 0) {
                continue;
            }
            result.add(new InetSocketAddress(StringUtil.trim(ip), i));
        }
        return result;
    }

    /**
     * 127.0.0.1:8991;127.0.0.1:8992;127.0.0.1:8993 转换为地址对象
     *
     * @param str 字符串转地址列表,
     * @return 转换为地址对象列表
     */
    public static List<InetSocketAddress> getSocketAddressList(String str) {
        List<InetSocketAddress> result = new ArrayList<>();
        String[] ips = StringUtil.split(StringUtil.replace(str, StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON);
        for (String ip : ips) {
            if (StringUtil.isNull(ip)) {
                continue;
            }
            if (ip.contains("-")) {
                result.addAll(getSocketAddressRange(str));
            } else {
                result.add(getSocketAddress(ip));
            }
        }
        return result;
    }

    /**
     * @return 用于区分硬件标识
     */
    public static String getIpEnd() {
        try {
            String ipStart = IpUtil.toLong(IpUtil.getIp(InetAddress.getLocalHost())) + StringUtil.empty;
            return ipStart.substring(ipStart.length() - 2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }


    /**
     * 测试主机Host的port端口是否被使用
     *
     * @param address InetSocketAddress地址
     * @return 是否被使用
     */
    public static boolean isPortUsing(InetSocketAddress address) {

        try {
            Socket socket = new Socket(address.getAddress(), address.getPort());  //建立一个Socket连接
            if (socket != null) {
                socket.close();
            }
            return true;
        } catch (IOException e) {
            //...
            return false;
        }
    }

}