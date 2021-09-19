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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-5
 * Time: 10:34:50
 */
public final  class FileSuffixUtil {
    private static final Map<String, String> HEAD_TYPES = new HashMap<>(40);

    static {
        HEAD_TYPES.put("jpg", "ff");
        HEAD_TYPES.put("png", "89");
        HEAD_TYPES.put("gif", "47");
        HEAD_TYPES.put("bmp", "42");
        HEAD_TYPES.put("ncb", "4d");
        HEAD_TYPES.put("suo", "d0");
        HEAD_TYPES.put("swf", "43");
        HEAD_TYPES.put("ini", "5b");
        HEAD_TYPES.put("torrent", "64");
        HEAD_TYPES.put("rar", "52");
        HEAD_TYPES.put("avi", "52");
        HEAD_TYPES.put("ico", "00");
        HEAD_TYPES.put("jar", "50");
        HEAD_TYPES.put("zip", "50");
        HEAD_TYPES.put("a", "21");
        HEAD_TYPES.put("INI", "ff");
        HEAD_TYPES.put("iso", "00");
        HEAD_TYPES.put("exe", "4d");
        HEAD_TYPES.put("com", "66");
        HEAD_TYPES.put("pyc", "b3");
        HEAD_TYPES.put("swftools", "25");
        HEAD_TYPES.put("dll", "4d");
        HEAD_TYPES.put("msi", "d0");
        HEAD_TYPES.put("pl", "23");
        HEAD_TYPES.put("lib", "21");
        HEAD_TYPES.put("doc", "d0");
        HEAD_TYPES.put("docx", "d0");
        HEAD_TYPES.put("xls", "d0");
        HEAD_TYPES.put("url", "5b");
    }

    public static final String[] IMAGE_TYPES = {"jpg", "jpeg", "gif", "png", "bmp"};
    public static final String[] ZIP_TYPES = {"rar", "zip", "7z", "gz", "bz2", "cab", "iso", "ace", "gzip", "jzb", "arj", "uue"};
    public static final String[] VIDEO_TYPES = {"flv", "swf", "mkv", "avi", "asf", "rm", "rmvb", "mpeg", "mpg", "ogg", "mp4", "wmv", "m4v", "mp3", "wav", "vob", "ram", "mid", "mod", "cpk", "3gp", "vob", "mov", "3g2", "asf", "xvid", "divx"};
    public static final String[] OFFICE_TYPES = {"doc", "docx", "xls", "xlsx", "ppt", "pptx", "mdb", "one", "pdf", "gd", "wps", "dps", "et", "ett", "rtf", "chm"};

    private FileSuffixUtil() {

    }

    static public boolean isImageSuffix(String extendName) {
        return ArrayUtil.inArray(IMAGE_TYPES, extendName, true);
    }

    static public boolean isZipSuffix(String extendName) {
        return ArrayUtil.inArray(ZIP_TYPES, extendName, true);
    }

    static public boolean isVideoSuffix(String extendName) {
        return ArrayUtil.inArray(VIDEO_TYPES, extendName, true);
    }

    static public boolean isOfficeSuffix(String extendName) {
        return ArrayUtil.inArray(OFFICE_TYPES, extendName, true);
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
        return !HEAD_TYPES.containsKey(fileType) || HEAD_TYPES.get(fileType).equals(getFileHeadHex(fileName, 1));
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
        return StringUtil.encodeHex(Objects.requireNonNull(getFileHead(filename, headLength)), " ").trim();
    }

    final private static float version = StringUtil.toFloat(System.getProperty("java.vm.specification.version"));

    /**
     * 得到文件的http  ContentType 类型
     * @param fileName 文件
     * @return  ContentType
     */
    public static String getContentType(File fileName)
    {
        if (fileName==null)
        {
            return  "application/octet-stream";
        }
        String fileType = FileUtil.getTypePart(fileName.getName());
        String contentType = null;
        if (version >= 1.7) {
            try {
                contentType = Files.probeContentType(Paths.get(fileName.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
                contentType = null;
            }
        }

        if (contentType!=null)
        {
            //为了兼容 jdk 1.6
            if ("mp4".equalsIgnoreCase(fileType)) {
                contentType = "video/mpeg4";
            } else if ("bt".equalsIgnoreCase(fileType)) {
                contentType = "application/x-bittorrent";
            } else if ("swftools".equalsIgnoreCase(fileType)) {
                contentType = "application/swftools";
            } else if ("xls".equalsIgnoreCase(fileType)) {
                contentType = "application/vnd.ms-excel";
            } else if ("doc".equalsIgnoreCase(fileType) || "docx".equalsIgnoreCase(fileType)) {
                contentType = "application/msword";
            } else if ("mdb".equalsIgnoreCase(fileType)) {
                contentType = "application/msaccess";
            } else if ("ppt".equalsIgnoreCase(fileType)) {
                contentType = "application/x-ppt";
            } else if ("xml".equalsIgnoreCase(fileType)) {
                contentType = "application/xml";
            } else if ("txt".equalsIgnoreCase(fileType) || "htm".equalsIgnoreCase(fileType) || "html".equalsIgnoreCase(fileType)) {
                contentType = "text/html";
            } else if ("zip".equalsIgnoreCase(fileType)) {
                contentType = "application/x-zip-compressed";
            } else if ("rar".equalsIgnoreCase(fileType)) {
                contentType = "application/x-rar-compressed";
            } else if (FileSuffixUtil.isImageSuffix(fileType)) {
                contentType = "image/" + fileType;
            } else if ("js".equalsIgnoreCase(fileType)) {
                contentType = "application/x-javascript";
            } else {
                contentType = "application/msword";
            }
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

}