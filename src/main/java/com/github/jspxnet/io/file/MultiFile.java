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

import com.github.jspxnet.upload.multipart.DefaultFileRenamePolicy;
import com.github.jspxnet.upload.multipart.RenamePolicy;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.ObjectUtil;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-9-22
 * Time: 16:54:25
 */
public final class MultiFile {
    private MultiFile() {

    }

    public static boolean deleteDirectory(String dirName, boolean delSelf)
    {
        return deleteDirectory(new File(dirName), null,delSelf);
    }
    /**
     * 删除指定目录及其中的所有内容。
     *
     * @param dirName 要删除的目录的目录名
     * @param fileTypes 是否删除自己 这个目录
     * @param delSelf 是否删除自己
     * @return 删除成功时返回true，否则返回false。
     */
    public static boolean deleteDirectory(String dirName, String[] fileTypes,boolean delSelf)
    {
        return deleteDirectory(new File(dirName), fileTypes,delSelf);
    }


    /**
     *
     * @param dir  要删除的目录
     * @param fileTypes 是否删除自己 这个目录
     * @param delSelf 删除成功时返回true，否则返回false。
     * @return 删除指定目录及其中的所有内容
     */
    public static boolean deleteDirectory(File dir,String[] fileTypes, boolean delSelf) {
        if ((dir == null) || !dir.isDirectory()) {
            throw new IllegalArgumentException("Argument " + dir +
                    " is not a directory. ");
        }
        File[] entries = dir.listFiles();
        if (entries==null)
        {
            return true;
        }
        for (File entry : entries) {
            if (entry.isDirectory()) {
                deleteDirectory(entry,fileTypes, true);
            } else {
                String fileType = FileUtil.getTypePart(entry);
                if (ObjectUtil.isEmpty(fileTypes) || ArrayUtil.inArray(fileTypes,fileType,true))
                {
                    if (entry.canWrite()) {
                        if (!entry.delete()) {
                            entry.deleteOnExit();
                        }
                    } else {
                        entry.deleteOnExit();
                    }
                }
            }
        }
        return !delSelf || dir.delete();
    }

    /**
     *
     * @param inputDir  到 目录
     * @param outputDir  移动到目录
     * @param renamePolicy 重命名规则
     * @param fileTypes 文件类型
     * @param covering 覆盖
     * @param jump 跳过
     * @param deleteInput 删除 原目录
     * @return 是否成功
     */
    public static boolean copyDirectoryMoveTo(String inputDir, String outputDir, RenamePolicy renamePolicy,String[] fileTypes,boolean covering,boolean jump, boolean deleteInput) {
        return copyDirectory(inputDir, outputDir,renamePolicy,fileTypes, covering,jump) && deleteDirectory(inputDir, deleteInput);
    }




    /**
     *
     * @param inputDir 原目录
     * @param outputDir 移动到目录
     * @param renamePolicy  重命名规则
     * @param fileTypes 文件类型
     * @param covering 是否覆盖
     * @param jump 相同是否跳过
     * @return 是否成功
     */
    public static boolean copyDirectoryToOneDir(File inputDir, File outputDir,RenamePolicy renamePolicy,String[] fileTypes,boolean covering,boolean jump) {
        if (!inputDir.isDirectory()) {
            return false;
        }
        if (!FileUtil.makeDirectory(outputDir.getAbsolutePath())) {
            return false;
        }
        File[] files = inputDir.listFiles();
        if (files==null)
        {
            return false;
        }
        for (File file : files) {
            if (file.isFile()) {
                String fileType = FileUtil.getTypePart(file);
                if (ObjectUtil.isEmpty(fileTypes) || ArrayUtil.inArray(fileTypes,fileType,true))
                {
                    if (!copy(file, new File(outputDir.getAbsolutePath() + File.separator + file.getName()), renamePolicy, fileTypes, covering, jump)) {
                        return false;
                    }
                }
            } else if (file.isDirectory()) {
                if (!copyDirectoryToOneDir(file, outputDir,renamePolicy,fileTypes,covering,jump)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param inputDir 原目录
     * @param outputDir 移动到
     * @param renamePolicy 重命名贵州
     * @param fileTypes 文件类型
     * @param covering 是否转换
     * @param jump  存在是否跳过
     * @return 拷贝到一个目录
     */
    public static boolean copyDirectoryToOneDir(String inputDir, String outputDir,RenamePolicy renamePolicy,String[] fileTypes,boolean covering,boolean jump) {
        return copyDirectoryToOneDir(new File(inputDir), new File(outputDir),renamePolicy,fileTypes,covering,jump);
    }

    /**
     * 移动到一个目录下
     * @param inputDir 原目录
     * @param outputDir 移动到
     * @param renamePolicy 重命名规则
     * @param fileTypes 文件类型
     * @param covering  是否覆盖
     * @param jump 存在是否跳过
     * @return 移动多少个
     */
    public static int moveFolderToFolder(String inputDir, String outputDir,RenamePolicy renamePolicy,String[] fileTypes,boolean covering,boolean jump) {
        int result = 0;
        File f = new File(inputDir);
        File[] files = f.listFiles();
        if (files != null) {
            for (File fromFile : files) {
                if (fromFile.isDirectory()) {
                    if (copyDirectoryToOneDir(fromFile.getPath(), outputDir,renamePolicy,fileTypes,covering,jump)) {
                        deleteDirectory(fromFile, fileTypes,true);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param inputFilename 输入
     * @param outputFilename 输出
     * @param covering 是否覆盖
     * @return 是否成功
     */
    public static boolean copy(String inputFilename, String outputFilename,boolean covering) {
        return copy(new File(inputFilename), new File(outputFilename), new DefaultFileRenamePolicy(),null,covering,false);
    }

    /**
     *
     * @param inputFilename 输入
     * @param outputFilename 输出
     * @param renamePolicy 是否覆盖
     * @param fileTypes 文件类型，递归使用
     * @param covering 是否覆盖
     * @param jump 是否成功
     * @return 是否成功
     */
    public static boolean copy(String inputFilename, String outputFilename,RenamePolicy renamePolicy,String[] fileTypes,boolean covering,boolean jump)
    {
        return copy(new File(inputFilename), new File(outputFilename), renamePolicy,fileTypes,covering,jump);
    }

    /**
     * 拷贝文件
     *
     * @param input    输入
     * @param output   输出
     * @param renamePolicy 重命名规则
     * @param fileTypes 文件类型
     * @param covering 是否覆盖
     * @param jump 存在是否跳过
     * @return 是否成功
     */
    public static boolean copy(File input, File output, RenamePolicy renamePolicy,String[] fileTypes, boolean covering,boolean jump) {
        if (input.isDirectory()) {
            if (!FileUtil.makeDirectory(output)) {
                return false;
            }
            return copyDirectory(input, output, renamePolicy,fileTypes,covering,jump);
        } else {
            return FileUtil.copyFile(input, output, renamePolicy,covering,jump);
        }
    }

    /**
     *
     * @param inputDir 输入
     * @param outputDir 输出
     * @param covering 是否覆盖
     * @return 是否成功
     */
    public static boolean copyDirectory(String inputDir, String outputDir, boolean covering)
    {
        return copyDirectory(new File(inputDir), new File(outputDir), new DefaultFileRenamePolicy(),null,covering,false);
    }
    /**
     *
     * @param inputDir 输入
     * @param outputDir 输出
     * @param renamePolicy 重命名规则
     * @param covering 是否覆盖
     * @param jump 存在是否跳过
     * @return 拷贝目录
     */
    public static boolean copyDirectory(String inputDir, String outputDir, RenamePolicy renamePolicy, boolean covering,boolean jump)
    {
        return copyDirectory(new File(inputDir), new File(outputDir), renamePolicy,null,covering,jump);
    }

    /**
     *
     * @param inputDir  输入
     * @param outputDir  输出
     * @param renamePolicy  重命名规则
     * @param covering 是否覆盖
     * @param jump 存在是否跳过
     * @return 拷贝目录
     */
    public static boolean copyDirectory(File inputDir, File outputDir, RenamePolicy renamePolicy,boolean covering, boolean jump) {
       return copyDirectory( inputDir,  outputDir,  renamePolicy,null, covering, jump);
    }


    /**
     *
     * @param inputDir 输入
     * @param outputDir  输出
     * @param renamePolicy 重命名规则
     * @param fileTypes 文件类型
     * @param covering  是否覆盖
     * @param jump 存在是否跳过
     * @return  拷贝目录
     */
    public static boolean copyDirectory(String inputDir, String outputDir, RenamePolicy renamePolicy, String[] fileTypes,boolean covering,boolean jump)
    {
        return copyDirectory(new File(inputDir), new File(outputDir), renamePolicy,fileTypes,covering,jump);
    }



    /**
     *
     * @param inputDir  输入
     * @param outputDir  输出
     * @param renamePolicy 重命名规则
     * @param fileTypes 文件类型
     * @param covering  是否覆盖
     * @param jump 存在是否跳过
     * @return 是否成功
     */
    public static boolean copyDirectory(File inputDir, File outputDir, RenamePolicy renamePolicy,String[] fileTypes,boolean covering, boolean jump)
    {
        if (!FileUtil.makeDirectory(outputDir.getAbsolutePath())) {
            return false;
        }
        File[] files = inputDir.listFiles();
        if (files==null)
        {
            return true;
        }
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
            if (file.isDirectory())
            {
                if (!copy(file, desFile, renamePolicy,fileTypes,covering,jump)) {
                    return false;
                }
            }  else if (file.isFile())
            {
                String fileType = FileUtil.getTypePart(file);
                if (ObjectUtil.isEmpty(fileTypes) || ArrayUtil.inArray(fileTypes,fileType,true))
                {
                    if (!copy(file, desFile, renamePolicy,fileTypes,covering,jump)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}