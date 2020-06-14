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
 * Time: 16:21:37
 */

import com.github.jspxnet.utils.StringUtil;

import java.io.*;
import java.text.*;

public class LinuxNetworkInfo extends NetworkInfo {
    public static final String IPCONFIG_COMMAND = "ifconfig";

    @Override
    public String parseMacAddress() throws Exception {
        String ipConfigResponse;
        try {
            ipConfigResponse = runConsoleCommand(IPCONFIG_COMMAND);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }
        String localHost;
        try {
            localHost = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(), 0);
        }
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(ipConfigResponse, "\n");
        String lastMacAddress = null;
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            boolean containsLocalHost = line.contains(localHost);
            // see if line contains IP address
            if (containsLocalHost && lastMacAddress != null) {
                return lastMacAddress;
            }
            // see if line contains MAC address
            int macAddressPosition = line.indexOf("HWaddr");
            if (macAddressPosition <= 0) {
                continue;
            }
            String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
            if (isMacAddress(macAddressCandidate)) {
                lastMacAddress = macAddressCandidate;
            }
        }
        ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

    @Override
    public String parseDomain(String hostname) throws Exception {
        return StringUtil.empty;
    }

    private boolean isMacAddress(String macAddressCandidate) {
        return macAddressCandidate.length() == 17;
    }
}