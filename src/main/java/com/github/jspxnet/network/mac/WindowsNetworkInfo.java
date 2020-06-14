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
 * Time: 16:19:03
 */



import com.github.jspxnet.utils.ValidUtil;

import java.io.*;
import java.text.*;

public class WindowsNetworkInfo extends NetworkInfo {
    public static final String IPCONFIG_COMMAND = "ipconfig /all";

    @Override
    public String parseMacAddress() throws Exception {        // run command
        String ipConfigResponse;
        try {
            ipConfigResponse = runConsoleCommand(IPCONFIG_COMMAND);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }        // get localhost address
        String localHost = getLocalHost();
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(ipConfigResponse, "\n");
        String lastMacAddress = null;
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            // see if line contains IP address, this means stop if we've already seen a MAC address
            if (line.endsWith(localHost) && lastMacAddress != null) {
                return lastMacAddress;
            }
            // see if line might contain a MAC address
            int macAddressPosition = line.indexOf(":");
            if (macAddressPosition <= 0) {
                continue;
            }
            // trim the line and see if it matches the pattern
            String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
            if (ValidUtil.isMacAddress(macAddressCandidate)) {
                lastMacAddress = macAddressCandidate;
            }
        }
        ParseException ex = new ParseException("Cannot read MAC address from [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

}