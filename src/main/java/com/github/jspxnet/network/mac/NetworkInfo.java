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

/*
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-8-31
 * Time: 16:12:22
 */

import com.github.jspxnet.utils.StringUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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

    private static String getHardwareMac() throws SocketException {
        if (localMac!=null)
        {
            return localMac;
        }
        Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
        while (el.hasMoreElements()) {
            NetworkInterface networkInterface  = el.nextElement();
            String displayName = StringUtil.toLowerCase(networkInterface.getDisplayName());
            if (displayName.contains("software"))
            {
                continue;
            }
            if (displayName.contains("virtual"))
            {
                continue;
            }
            if (displayName.contains("adapter"))
            {
                continue;
            }
            if (displayName.contains("wan"))
            {
                continue;
            }
            if (displayName.contains("vpn"))
            {
                continue;
            }
            if (displayName.contains("tap"))
            {
                continue;
            }
            byte[] mac = networkInterface.getHardwareAddress();
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
     * JDK1.6新特性获取网卡MAC地址
     */
    private static String getMac() throws SocketException {
        if (localMac!=null)
        {
            return localMac;
        }
        String hardwareMac = getHardwareMac();
        if (!StringUtil.isNullOrWhiteSpace(hardwareMac))
        {
            return hardwareMac;
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
            throw new IOException(ex.getMessage());
        }
    }

    protected String parseDomain(String hostName) throws Exception {        // get the address of the host we are looking for - verification
        java.net.InetAddress addy;
        try {
            addy = java.net.InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {

            throw new ParseException(e.getMessage(), 0);
        }
        // back out transfer the hostname - just validating
        hostName = addy.getCanonicalHostName();
        String nslookupCommand = NSLOOKUP_CMD + " " + hostName;
        // run the lookup command
        String nslookupResponse;
        try {
            nslookupResponse = runConsoleCommand(nslookupCommand);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        StringTokenizer tokeit = new StringTokenizer(nslookupResponse, "\n", false);
        while (tokeit.hasMoreTokens()) {
            String line = tokeit.nextToken();
            if (line.startsWith("Name:")) {
                line = line.substring(line.indexOf(":") + 1);
                line = line.trim();
                if (isDomain(line, hostName)) {
                    line = line.substring(hostName.length() + 1);
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

    public static String getLocalHost()  {
        try {
           InetAddress inetAddress = getLocalHostLANAddress();
           return inetAddress.getHostAddress();
        } catch (Exception e) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                return "127.0.0.1";
            }
        }
    }

    protected static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface =  ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    /**
     * 验收
     *
     * @param args 无效
     */
    public static void main(String[] args) {

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        String osArch = osBean.getArch();
        String osName = osBean.getName();

        try {
            System.out.println("Network infos");
            System.out.println("osArch:" + osArch);
            System.out.println("osName:" + osName);
            System.out.println("  Operating System: " + System.getProperty("os.name"));
            System.out.println("  IP/Localhost: " + getLocalHost());
            System.out.println("  MAC Address: " + getMacAddress());
            System.out.println("  Domain: " + getNetworkDomain());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}