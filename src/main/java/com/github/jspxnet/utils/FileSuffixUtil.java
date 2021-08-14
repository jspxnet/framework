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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-5
 * Time: 10:34:50
 */
public final  class FileSuffixUtil {
    private static final Map<String, String> headTypes = new HashMap<String, String>();

    static {
        headTypes.put("jpg", "ff");
        headTypes.put("png", "89");
        headTypes.put("gif", "47");
        headTypes.put("bmp", "42");
        headTypes.put("ncb", "4d");
        headTypes.put("suo", "d0");
        headTypes.put("swf", "43");
        headTypes.put("ini", "5b");
        headTypes.put("torrent", "64");
        headTypes.put("rar", "52");
        headTypes.put("avi", "52");
        headTypes.put("ico", "00");
        headTypes.put("jar", "50");
        headTypes.put("zip", "50");
        headTypes.put("a", "21");
        headTypes.put("ini", "ff");
        headTypes.put("iso", "00");
        headTypes.put("exe", "4d");
        headTypes.put("com", "66");
        headTypes.put("pyc", "b3");
        headTypes.put("swftools", "25");
        headTypes.put("zip", "50");
        headTypes.put("dll", "4d");
        headTypes.put("msi", "d0");
        headTypes.put("pl", "23");
        headTypes.put("lib", "21");
        headTypes.put("doc", "d0");
        headTypes.put("docx", "d0");
        headTypes.put("xls", "d0");
        headTypes.put("url", "5b");
    }

    public static final String[] imageTypes = {"jpg", "jpeg", "gif", "png", "bmp"};
    public static final String[] zipTypes = {"rar", "zip", "7z", "gz", "bz2", "cab", "iso", "ace", "gzip", "jzb", "arj", "uue"};
    public static final String[] videoTypes = {"flv", "swf", "mkv", "avi", "asf", "rm", "rmvb", "mpeg", "mpg", "ogg", "mp4", "wmv", "m4v", "mp3", "wav", "vob", "ram", "mid", "mod", "cpk", "3gp", "vob", "mov", "3g2", "asf", "xvid", "divx"};
    public static final String[] officeTypes = {"doc", "docx", "xls", "xlsx", "ppt", "pptx", "mdb", "one", "pdf", "gd", "wps", "dps", "et", "ett", "rtf", "chm"};

    private FileSuffixUtil() {

    }

    static public boolean isImageSuffix(String extendName) {
        return ArrayUtil.inArray(imageTypes, extendName, true);
    }

    static public boolean isZipSuffix(String extendName) {
        return ArrayUtil.inArray(zipTypes, extendName, true);
    }

    static public boolean isVideoSuffix(String extendName) {
        return ArrayUtil.inArray(videoTypes, extendName, true);
    }

    static public boolean isOfficeSuffix(String extendName) {
        return ArrayUtil.inArray(officeTypes, extendName, true);
    }

    /**
     * @param fileName 文件名称
     * @return 检测文件类型和文件后缀是否匹配
     */
    public static boolean checkFileType(String fileName) {
        String fileType = FileUtil.getTypePart(fileName);
        if (fileType != null) {
            fileType = fileType.toLowerCase().trim();
        }
        return !headTypes.containsKey(fileType) || headTypes.get(fileType).equals(getFileHeadHex(fileName, 1));
    }

    /**
     * @param filename   文件名
     * @param headLength 得到长度
     * @return 头数据
     */
    static public byte[] getFileHead(String filename, int headLength) {
        File f = new File(filename);
        if (!f.isFile() || !f.canRead()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(f);
            FileChannel fileC = fis.getChannel();
            ByteArrayOutputStream aos = new ByteArrayOutputStream();
            WritableByteChannel outC = Channels.newChannel(aos);
            ByteBuffer buffer = ByteBuffer.allocateDirect(headLength);
            int i = fileC.read(buffer);
            if (i != 0 && i != -1) {
                buffer.flip();
                outC.write(buffer);
            }
            buffer.clear();
            fis.close();
            return aos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 读取文件头信息,用来判断是否是正确的文件类型
     *
     * @param filename   文件路径
     * @param headLength 一般为4
     * @return 得到文件头信息
     */
    static public String getFileHeadHex(String filename, int headLength) {
        return StringUtil.encodeHex(getFileHead(filename, headLength), " ").trim();
    }

}