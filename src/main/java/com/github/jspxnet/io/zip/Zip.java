/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io.zip;


import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.*;
import java.util.zip.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-7-24
 * Time: 20:30:41
 */
/*
1: java.util.zip包

条目  类型 描述
Checksum 接口 被类Adler32和CRC32实现的接口
Adler32 类 使用Alder32算法来计算Checksum数目
CheckedInputStream 类 一个输入流，保存着被读取数据的Checksum
CheckedOutputStream 类 一个输出流，保存着被读取数据的Checksum
CRC32 类 使用CRC32算法来计算Checksum数目
Deflater 类 使用ZLIB压缩类，支持通常的压缩方式
DeflaterOutputStream 类 一个输出过滤流，用来压缩Deflater格式数据
GZIPInputStream 类 一个输入过滤流，读取GZIP格式压缩数据
GZIPOutputStream 类 一个输出过滤流，读取GZIP格式压缩数据
Inflater 类 使用ZLIB压缩类，支持通常的解压方式
InlfaterInputStream 类 一个输入过滤流，用来解压Inlfater格式的压缩数据
JZipEntry 类 存储ZIP条目
ZipFile 类 从ZIP文件中读取ZIP条目
ZipInputStream 类 一个输入过滤流，用来读取ZIP格式文件中的文件
ZipOutputStream 类 一个输出过滤流，用来向ZIP格式文件口写入文件
DataFormatException 异常类 抛出一个数据格式错误
ZipException 异常类 抛出一个ZIP文件
*/
public class Zip {
    static final int BUFFER = 2048;

    public Zip() {

    }

    public boolean doZipFile(String fileName, String outfile, String zipentry) {
        BufferedInputStream origin = null;
        try {
            if (!(new File(fileName)).isFile()) {
                return false;
            }
            FileOutputStream dest = new FileOutputStream(outfile);
            CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte[] data = new byte[BUFFER];
            FileInputStream fi = new FileInputStream(fileName);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(FileUtil.getFileName(zipentry));
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean doZipDir(String dirName, String type, boolean child, String outfile) {
        try {
            String fen = "@!#";
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(outfile);
            CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
            ZipOutputStream out = new ZipOutputStream(new
                    BufferedOutputStream(checksum));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte[] data = new byte[BUFFER];
            // get a list of files from current directory

            String file = FileUtil.getFileList(dirName, fen, type, child);
            if (StringUtil.isNull(file)) {
                return false;
            }
            String[] files = StringUtil.split(file, fen);
            if (files.length < 1) {
                return false;
            }
            for (String file1 : files) {
                FileInputStream fi = new FileInputStream(file1);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(file1);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String[] doUnZip(String fileName) {
        String[] result = null;
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new
                    FileInputStream(fileName);
            CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                result = ArrayUtil.add(result, entry.toString());
                int count;
                byte[] data = new byte[BUFFER];
                FileOutputStream fos = new FileOutputStream(entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}