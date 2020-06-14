/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io.file;

import com.github.jspxnet.utils.FileUtil;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-9-22
 * Time: 16:54:25
 */
public class MultiFile {
    public MultiFile() {

    }

    public static final int BUFFER_SIZE = 1024 * 2;

    /**
     * 删除指定目录及其中的所有内容。
     *
     * @param dirName 要删除的目录的目录名
     * @param delSelf 是否删除自己
     * @return 删除成功时返回true，否则返回false。
     */
    public boolean deleteDirectory(String dirName, boolean delSelf) {
        return deleteDirectory(new File(dirName), delSelf);
    }

    /**
     * 删除指定目录及其中的所有内容
     *
     * @param dir     要删除的目录
     * @param delSelf 是否删除自己
     * @return 删除成功时返回true，否则返回false。
     */
    public boolean deleteDirectory(File dir, boolean delSelf) {
        if ((dir == null) || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument " + dir +
                    " is not a directory. ");
        }
        File[] entries = dir.listFiles();
        for (File entry : entries) {
            if (entry.isDirectory()) {
                deleteDirectory(entry, true);
            } else {
                if (entry.canWrite()) {
                    if (!entry.delete()) {
                        entry.deleteOnExit();
                    }
                } else {
                    entry.deleteOnExit();
                }
            }
        }
        return !delSelf || dir.delete();
    }


    /**
     * 移动目录
     *
     * @param inputDir    到 目录
     * @param outputDir   移动到目录
     * @param covering    覆盖
     * @param deleteInput 删除 原目录
     * @return 是否成功
     */
    public boolean copyDirectoryMoveTo(String inputDir, String outputDir, boolean covering, boolean deleteInput) {
        return copyDirectory(inputDir, outputDir, covering) && deleteDirectory(inputDir, deleteInput);
    }


    /**
     * Copie un rpertoire dans un autre
     *
     * @param inputDir  到 目录
     * @param outputDir 移动到目录
     * @return 是否成功
     */
    public boolean copyDirectoryToOneDir(File inputDir, File outputDir) {
        if (!inputDir.isDirectory()) {
            return false;
        }
        if (!FileUtil.makeDirectory(outputDir.getAbsolutePath())) {
            return false;
        }
        File[] files = inputDir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (!copy(file, new File(outputDir.getAbsolutePath() + File.separator + file.getName()), true)) {
                    return false;
                }
            } else if (file.isDirectory()) {
                if (!copyDirectoryToOneDir(file, outputDir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean copyDirectoryToOneDir(String inputDir, String outputDir) {
        return copyDirectoryToOneDir(new File(inputDir), new File(outputDir));
    }

    /**
     * 移动到一个目录下
     *
     * @param inputDir  原目录
     * @param outputDir 移动到
     * @return 移动多少个
     */
    public int moveFolderToFolder(String inputDir, String outputDir) {
        int result = 0;
        File f = new File(inputDir);
        File[] files = f.listFiles();
        if (files != null) {
            for (File fromFile : files) {
                if (fromFile.isDirectory()) {
                    if (copyDirectoryToOneDir(fromFile.getAbsolutePath(), outputDir)) {
                        deleteDirectory(fromFile, true);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public boolean copy(String inputFilename, String outputFilename, boolean covering) {
        return copy(new File(inputFilename), new File(outputFilename), covering);
    }

    /**
     * 拷贝文件
     *
     * @param input    输入
     * @param output   输出
     * @param covering 是否覆盖
     * @return 是否成功
     */
    public boolean copy(File input, File output, boolean covering) {
        if (input.isDirectory()) {
            if (!FileUtil.makeDirectory(output)) {
                return false;
            }
            return copyDirectory(input, output, covering);
        } else {
            return FileUtil.copyFile(input, output, covering);
        }
    }


    /**
     * 拷贝目录
     *
     * @param inputDir  输入
     * @param outputDir 输出
     * @param covering  是否覆盖
     * @return 是否成功
     */
    public boolean copyDirectory(String inputDir, String outputDir, boolean covering) {
        return copyDirectory(new File(inputDir), new File(outputDir), covering);
    }

    /**
     * Copie un rpertoire dans un autre
     *
     * @param inputDir  输入
     * @param outputDir 输出
     * @param covering  是否覆盖
     * @return 是否成功
     */
    public boolean copyDirectory(File inputDir, File outputDir, boolean covering) {
        if (!FileUtil.makeDirectory(outputDir.getAbsolutePath())) {
            return false;
        }
        File[] files = inputDir.listFiles();
        for (File file : files) {
            File desFile = new File(outputDir.getAbsolutePath() + File.separator + file.getName());
            if (!desFile.exists()) {
                if (file.isDirectory()) {
                    if (!desFile.mkdir()) {
                        if (!desFile.mkdirs() || !desFile.setLastModified(file.lastModified())) {
                            return false;
                        }
                    }
                }
            }
            if (!copy(file, desFile, covering)) {
                return false;
            }
        }
        return true;
    }

}