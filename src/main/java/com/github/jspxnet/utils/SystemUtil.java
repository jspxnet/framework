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

/**
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-19
 * Time: 16:43:38
 */

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

public final class SystemUtil {
    private SystemUtil()
    {

    }
    /**
     * Operating system state flag for error.
     */
    public static final int INIT_PROBLEM = -1;
    /**
     * Operating system state flag for neither Unix nor Windows.
     */
    private static final int OTHER = 0;
    /**
     * Operating system state flag for Windows.
     */
    public static final int WINDOWS = 1;
    /**
     * Operating system state flag for Unix.
     */
    public static final int UNIX = 2;
    /**
     * Operating system state flag for Posix flavour Unix.
     */
    public static final int POSIX_UNIX = 3;

    public static final int LINUX = 5;

    public static final int ANDROID = 4;
    /**
     * Operating system state flag for Unix.
     */
    public static boolean isAndroid = false;


    /**
     * The operating system flag.
     */
    public static final int OS;


    public static String encode = System.getProperty("file.encoding");

    public static String lineSeparator = System.getProperty("line.separator");

    static {

        int os;
        try {

            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            osName = osName.toLowerCase();
            // match
            if (osName.contains("windows")) {
                os = WINDOWS;
            } else if (osName.contains("sun os") ||
                    osName.contains("sunos") ||
                    osName.contains("solaris") ||
                    osName.contains("mpe/ix") ||
                    osName.contains("freebsd") ||
                    osName.contains("irix") ||
                    osName.contains("digital unix") ||
                    osName.contains("unix") ||
                    osName.contains("mac os x")) {
                os = UNIX;

            } else if (osName.contains("hp-ux") ||
                    osName.contains("aix")) {
                os = POSIX_UNIX;
            } else if (osName.contains("linux")) {
                if ("Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"))) {
                    isAndroid = true;
                    os = ANDROID;
                } else {
                    os = LINUX;
                }

            } else {
                os = OTHER;
            }

        } catch (Exception ex) {
            os = INIT_PROBLEM;
        }
        OS = os;


    }


    public static String shell(String shell) throws IOException, InterruptedException {
        StringBuilder showInfo = new StringBuilder();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(shell);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), encode));

            String inline;
            while (null != (inline = br.readLine())) {
                showInfo.append(inline).append("\r\n");
            }
            BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream(), encode));
            while (null != (inline = er.readLine())) {
                showInfo.append(inline).append("\r\n");
            }
            int exitVal = p.exitValue();
            showInfo.append("exit value:").append(exitVal);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        } finally {
            if (p != null) {
                p.getInputStream().close();
                p.getErrorStream().close();
                p.getOutputStream().close();
            }
        }
        return showInfo.toString();
    }


    public static boolean isUsedPort(int port) throws Exception {
        if (OS == WINDOWS) {
            String str = cmd("netstat -aon|findstr \"" + port + "\"");
            return StringUtil.trim(str).contains(port + "");
        } else {
            ServerSocket client = null;
            try {
                client = new ServerSocket(port);
                return false;
            } catch (Exception e) {
                return true;
            } finally {
                if (client != null) {
                    client.close();
                }
            }
        }
    }


    public static String cmd(String shell) throws IOException, InterruptedException {
        StringBuilder showInfo = new StringBuilder();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("cmd /c " + shell);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), encode));
            String line;
            while ((line = br.readLine()) != null) {
                showInfo.append(line).append("\r\n");
            }
            p.waitFor();
        } catch (Exception e) {
            showInfo.append(e.getMessage());
        } finally {
            if (p != null) {
                p.getInputStream().close();
                p.getErrorStream().close();
                p.getOutputStream().close();
                p.destroy();
            }
        }
        return showInfo.toString();
    }

    /**
     * @param shell shell命令
     * @return 从新启动等命令
     */
    public static String nirCMD(String shell) {
        StringBuilder showInfo = new StringBuilder();
        Process process = null;
        try {
            String[] cmd = StringUtil.split("NirCMD.exe " + shell, " ");
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), encode));
            String line;
            while ((line = br.readLine()) != null) {
                showInfo.append(line).append("\r\n");
            }
            process.waitFor();
            return showInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static String saveScreensHot(String file) throws IOException, InterruptedException {
        StringBuilder showInfo = new StringBuilder();
        Process process = null;
        try {
            String[] cmd = new String[]{"NirCMD.exe", " savescreenshot", file};
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), encode));
            String line;
            while ((line = br.readLine()) != null) {
                showInfo.append(line).append("\r\n");
            }
            process.waitFor();
            return showInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static String reboot() throws IOException, InterruptedException {
        StringBuilder showInfo = new StringBuilder();
        Process process = null;
        try {
            String[] cmd = new String[]{"NirCMD.exe", " exitwin", "reboot"};
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), encode));
            String line;
            while ((line = br.readLine()) != null) {
                showInfo.append(line).append("\r\n");
            }
            process.waitFor();
            return showInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * @param file class文件
     * @return 得到class的版本好
     */
    public static int getClassVersion(File file) {
        // 主版本号和次版本号码
        byte[] data = FileUtil.readFileByte(file, 8);
        int minor_version = (((int) data[4]) << 8) + data[5];
        String hexStr = StringUtil.toHexString(data).toLowerCase();
        if (!hexStr.startsWith("cafebabe")) {
            return 0;
        }
        return StringUtil.toInt(Integer.parseInt(hexStr.substring(8, 16), 16) + StringUtil.DOT + minor_version);
    }

    /**
     * @param file class文件
     * @return 需要支持的版本
     */
    public static String getClassJDKVersion(File file) {
        // 主版本号和次版本号码
        int i = getClassVersion(file);
        switch (i) {
            case 50:
                return "1.6";
            case 49:
                return "1.5";
            case 48:
                return "1.4";
            case 47:
                return "1.3";
            case 46:
                return "1.2";
            default:
                return "1.8";
        }
    }

    public static boolean isAndroid() {
        return isAndroid;
    }

    /**
     *
     * @return 系统进程ID
     */
    public static String getPid()
    {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return StringUtil.substringBefore(name,"@");
    }


}