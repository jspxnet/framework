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

/*
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-19
 * Time: 16:43:38
 */

import com.github.jspxnet.network.mac.NetworkInfo;
import com.github.jspxnet.security.utils.EncryptUtil;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ServerSocket;
import java.nio.charset.Charset;

public final class SystemUtil {
    private SystemUtil()
    {

    }

    final public static float jdkVersion = StringUtil.toFloat(System.getProperty("java.vm.specification.version"));

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


    public static String encode = Charset.defaultCharset().displayName();

    public static final String SYSTEM_GUID;

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

        SYSTEM_GUID = getSystemGuid();
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
            return e.getLocalizedMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static String saveScreensHot(String file)  {
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
            return e.getLocalizedMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static String reboot()  {
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


   public static String getCPUSerial() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (StringUtil.isNullOrWhiteSpace(result.toString().trim())) {
            //没有读取到
            result = new StringBuilder("00000000");
        }
        return result.toString().trim();
    }



    /**
     * 绑定网卡，ip和操作系统
     * @return 根据当前系统环境生成一个GUID，主要用于许可
     */
   public static String getSystemGuid()
   {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        String osArch = osBean.getArch();
        String osName = osBean.getName();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(getCPUSerial()).append("/").append(NetworkInfo.getMacAddress()).append(osName).append(osArch);
            return EncryptUtil.getMd5(sb.toString()).toUpperCase();
        } catch (IOException e) {
            return EncryptUtil.getMd5(sb.toString()).toUpperCase();
        }
   }

/*

   public static void main(String[] args) {
        System.out.println(getCPUSerial());
        System.out.println(getSystemGuid());
        System.out.println(SYSTEM_GUID );
   }
*/


}