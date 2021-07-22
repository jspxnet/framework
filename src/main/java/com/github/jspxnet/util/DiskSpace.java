/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-6-6
 * Time: 3:48:18
 * http://zeroliu.javaeye.com/blog/26489
 */
@Slf4j
public class DiskSpace {
    public static final String CRLF = System.getProperty("line.separator");
    private static boolean initJini = true;

    private static String os_exec(String[] cmds) {

        Process porc = null;
        InputStream perr, pin;
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = null;
        try {

            porc = Runtime.getRuntime().exec(cmds, null, null);
            perr = porc.getErrorStream();
            pin = porc.getInputStream();
            //获取屏幕输出显示
            //while((c=pin.read())!=-1) sb.append((char) c);

            br = new BufferedReader(new InputStreamReader(pin, SystemUtil.encode));
            while ((line = br.readLine()) != null) {
                // System.out.println("exec()O: "+line);
                sb.append(line).append(CRLF);
            }
            //获取错误输出显示
            br = new BufferedReader(new InputStreamReader(perr, SystemUtil.encode));
            while ((line = br.readLine()) != null) {
                log.error("exec()E: " + line);
            }
            porc.waitFor();   //等待编译完成
            int ret = porc.exitValue(); //检查javac错误代码
            if (ret != 0) {
                log.warn("porc.exitValue() = " + ret);
            }
        } catch (Exception e) {
            log.warn("exec() " + e, e);
        } finally {
            assert porc != null;
            porc.destroy();
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return sb.toString();
    }


    private static long os_freesize(String dirName) {
        String[] cmds;
        long freeSize;
        switch (SystemUtil.OS) {
            case SystemUtil.WINDOWS:
                cmds = new String[]{"cmd.exe", "/c", "dir", dirName};
                freeSize = os_freesize_win(os_exec(cmds));
                break;
            case SystemUtil.UNIX:
                cmds = new String[]{"df", dirName};
                freeSize = os_freesize_unix(os_exec(cmds));
                break;
            default:
                cmds = new String[]{"command.exe", "/c", "dir", dirName};
                freeSize = os_freesize_win(os_exec(cmds));
                break;
        }
        return freeSize;
    }

    private static String[] os_split(String s) {
        String[] ss = s.split(" "); //空格分隔；
        List<String> ssl = new ArrayList<String>(16);
        for (int i = 0; i < ss.length; i++) {
            if (ss[i] == null) {
                continue;
            }
            ss[i] = ss[i].trim();
            if (ss[i].length() == 0) {
                continue;
            }
            ssl.add(ss[i]);
        }
        String[] ss2 = new String[ssl.size()];
        ssl.toArray(ss2);
        return ss2;
    }

    private static long os_freesize_unix(String s) {
        String lastLine = os_lastline(s); //获取最后一航；
        if (lastLine == null) {
            log.warn("(lastLine == null)");
            return -1;
        } else {
            lastLine = lastLine.trim();
        }
        //格式：/dev/sda1    101086     12485     83382  14% /boot
        //lastLine = lastLine.replace('\t', ' ');
        String[] items = os_split(lastLine);
        log.debug("os_freesize_unix() 目录:\t" + items[0]);
        log.debug("os_freesize_unix() 总共:\t" + items[1]);
        log.debug("os_freesize_unix() 已用:\t" + items[2]);
        log.debug("os_freesize_unix() 可用:\t" + items[3]);
        log.debug("os_freesize_unix() 可用%:\t" + items[4]);
        log.debug("os_freesize_unix() 挂接:\t" + items[5]);
        if (items[3] == null) {
            log.warn("(ss[3]==null)");
            return -1;
        }
        return Long.parseLong(items[3]) * 1024; //按字节算
    }

    private static long os_freesize_win(String s) {
        String lastLine = os_lastline(s); //获取最后一航；
        if (lastLine == null) {
            log.warn("(lastLine == null)");
            return -1;
        } else {
            lastLine = lastLine.trim().replaceAll(",", StringUtil.empty);
        }
        //分析
        String[] items = os_split(lastLine); //15 个目录  1,649,696,768 可用字节
        if (items.length < 4) {
            log.warn("DIR result error: " + lastLine);
            return -1;
        }
        if (items[2] == null) {
            log.warn("DIR result error: " + lastLine);
            return -1;
        }
        return Long.parseLong(items[2]);
    }

    private static String os_lastline(String s) {
        //获取多行输出的最后一行；
        BufferedReader br = new BufferedReader(new StringReader(s));
        String line, lastLine = null;
        try {
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
        } catch (Exception e) {
            log.warn("parseFreeSpace4Win() " + e);
        }
        return lastLine;
    }
// private static String os_exec_df_mock() { //模拟df返回数据
//  StringBuffer sb = new StringBuffer();
//  sb.append("Filesystem     1K-块        已用     可用 已用% 挂载点");
//  sb.append(CRLF);
//  sb.append("/dev/sda1    101086     12485     83382  14% /boot");
//  sb.append(CRLF);
//  return sb.toString();
// }

    public static long getFreeDiskSpace(String dirName) {
        //return os_freesize_unix(os_exec_df_mock()); //测试Linux
        return os_freesize(dirName);//自动识别操作系统，自动处理
    }

    public static void main(String[] args) throws IOException {
        args = new String[3];
        int x = 0;
        args[x++] = "C:";
        args[x++] = "D:";
        args[x++] = "E:";
        if (args.length == 0) {
            for (char c = 'A'; c <= 'Z'; c++) {
                String dirName = c + ":\\";  //C:\ C:
                log.info(dirName + " " +
                        getFreeDiskSpace(dirName));
            }
        } else {
            for (String arg : args) {
                log.info(arg + " 剩余空间（B）:" + getFreeDiskSpace(arg));
            }
        }
    }

    /**
     * @param dirPath 路径
     * @return 获得磁盘容量
     */
    public static long getDiskTotalSpace(String dirPath) {
        if (SystemUtil.isAndroid()) {
            initJini = false;
            try {
                File root = (File) ClassUtil.callStaticMethod(ClassUtil.loadClass("android.os.Environment"), "getRootDirectory");
                Object androidObject = ClassUtil.newInstance("android.os.StatFs", new Object[]{root.getPath()});
                //android.os.StatFs sf = new android.os.StatFs(root.getPath());
                long blockSize = ObjectUtil.toLong(BeanUtil.getProperty(androidObject, "blockSizeLong"));// sf.getBlockSizeLong();
                long blockCount = ObjectUtil.toLong(BeanUtil.getProperty(androidObject, "blockCountLong"));//sf.getBlockCountLong();
                return blockSize * blockCount;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            //File root = android.os.Environment.getRootDirectory();

        } else {
            if (initJini) {
                try {
                    File f = new File(dirPath);

/*
                    if (!com.jconfig.FileRegistry.isInited())
                        com.jconfig.FileRegistry.initialize(f, 0);
                    if (com.jconfig.FileRegistry.isInited()) {
                        com.jconfig.DiskObject diskObject =  com.jconfig.FileRegistry.createDiskObject(f, 0);
                        if (diskObject!=null)
                            return diskObject.getFile().getTotalSpace();
                    }
                    else
*/

                    initJini = false;
                } catch (Exception e) {
                    initJini = false;
                    e.printStackTrace();
                }
            }
            return DiskSpace.getFreeDiskSpace(dirPath);
        }

    }

    /**
     * 调用 println(getDiskInfo("c:"))
     * 参数为盘符 eg:"c:"
     * 获得磁盘可用空间
     *
     * @param dirPath 盘符
     * @return long  目录大小
     */
    public static long getDiskFreeSpace(String dirPath) {
        if (SystemUtil.isAndroid()) {
            initJini = false;
            try {
                File root = (File) ClassUtil.callStaticMethod(ClassUtil.loadClass("android.os.Environment"), "getRootDirectory");
                Object androidObject = ClassUtil.newInstance("android.os.StatFs", new Object[]{root.getPath()});
                //android.os.StatFs sf = new android.os.StatFs(root.getPath());
                return ObjectUtil.toLong(BeanUtil.getProperty(androidObject, "availableBlocksLong"));// sf.getBlockSizeLong();

                //  android.os.StatFs sf = new android.os.StatFs(root.getPath());
                // return sf.getAvailableBlocksLong();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //File root = android.os.Environment.getRootDirectory();

        } else {
            if (initJini) {
                try {
                    File f = new File(dirPath);
                /*    if (!com.jconfig.FileRegistry.isInited()) com.jconfig.FileRegistry.initialize(f, 0);
                    if (com.jconfig.FileRegistry.isInited())
                    {
                        com.jconfig.DiskObject diskObject =  com.jconfig.FileRegistry.createDiskObject(f, 0);
                        if (diskObject!=null)
                            return diskObject.getFile().getFreeSpace();
                    }
                    else */
                    initJini = false;
                } catch (Exception e) {
                    initJini = false;
                    e.printStackTrace();
                }
            }
            return DiskSpace.getFreeDiskSpace(dirPath);
        }
        return 0;

    }

}