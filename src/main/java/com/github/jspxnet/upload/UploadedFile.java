/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.upload;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AbstractWrite;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.io.WriteFile;
import com.github.jspxnet.upload.multipart.FileRenamePolicy;
import com.github.jspxnet.upload.multipart.JspxNetFileRenamePolicy;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.*;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-8-17
 * Time: 0:16:45
 */

public class UploadedFile implements Serializable {
    private final static FileRenamePolicy renamePolicy = new JspxNetFileRenamePolicy();
    private final static String crcFileName = "index.tmp";
    //变量名称
    private String name;
    //上传保存的目录
    private String dir;
    //保存的文件名称
    private String fileName;
    //原来的文件名称
    private String original;
    //流的上下文类型
    private String contentType;
    //后缀名
    private String fileType;

    //分块总数
    private int chunks = 0;

    //当前块
    private int chunk = 0;

    private long length = 0;

    private boolean chunkUpload = false;

    public UploadedFile(String name, String dir, String fileName, String original, String contentType, String fileType) {
        this.name = name;
        this.dir = dir;
        this.fileName = fileName;
        this.original = original;
        this.contentType = contentType;
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    private boolean isUpload = true;

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public File getFile() {
        if (dir == null || fileName == null) {
            return null;
        } else {
            return new File(dir, fileName);
        }
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFilePath() {

        return dir + File.separator + fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param newName 新命名文件
     * @return 从新命名文件, 不要做移动使用
     */
    public boolean renameTo(String newName) {
        File nFile = new File(dir, newName);
        nFile = renamePolicy.rename(nFile);
        File oldFile = getFile();
        if (FileUtil.moveFile(oldFile, nFile, true)) {
            dir = FileUtil.mendPath(nFile.getParent());
            //name = nFile.getName();
            fileName = nFile.getName();
            fileType = FileUtil.getTypePart(nFile);
            return true;
        }
        return false;
    }

    /**
     * @return 移动到类型目录
     */
    public boolean moveToTypeDir() {
        if (fileType == null) {
            return false;
        }
        File newDir = new File(dir, fileType.toLowerCase());
        FileUtil.makeDirectory(newDir);
        File newFile = renamePolicy.rename(new File(newDir, fileName));
        File oldFile = getFile();
        if (!oldFile.exists() || oldFile.length() <= 0) {
            return false;
        }
        if (FileUtil.copy(oldFile, newFile, true)) {
            dir = FileUtil.mendPath(newFile.getParent());
            fileName = newFile.getName();
            FileUtil.delete(oldFile);
            return true;
        }
        return false;
    }


    /**
     * @param yz 用户因之，确保目录不会重复
     * @return 只有目录
     */
    public String getChunkFolderName(String yz) {
        String body = StringUtil.getPolicyName(FileUtil.getNamePart(original), 40, FileRenamePolicy.special);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.NAME_TYPE_CHUNK).append("-").append(yz).append("-").append(body);
        return sb.toString().toLowerCase();
    }

    public File getChunkFolder(String yz) {
        return new File(dir, getChunkFolderName(yz));
    }


    /**
     * 如果是分片上传就移动到一个目录里边
     *
     * @param yz 用户因之，确保目录不会重复
     * @return 移动是否成功
     */
    public boolean moveToChunkFolder(String yz) {
        if (fileType == null) {
            return false;
        }
        File newDir = new File(dir, getChunkFolderName(yz));
        FileUtil.makeDirectory(newDir);
        String body = StringUtil.getPolicyName(FileUtil.getNamePart(original), 40, FileRenamePolicy.special);
        String typePart = FileUtil.getTypePart(original);
        File newFile = new File(newDir, body + "-[" + chunk + "]" + StringUtil.DOT + typePart);
        File oldFile = getFile();
        boolean result = FileUtil.moveFile(oldFile, newFile, true);
        if (result) {
            FileUtil.delete(oldFile);
            if (chunk == 0) {
                File crcFile = new File(newDir, crcFileName);
                AbstractWrite write = new WriteFile();
                write.setFile(crcFile.getPath());
                StringMap<String,String> valueMap = new StringMap<>();
                valueMap.setKeySplit(StringUtil.EQUAL);
                valueMap.setLineSplit(StringUtil.CRLF);
                valueMap.put("name", body);
                valueMap.put("type", typePart);
                valueMap.put("chunks", NumberUtil.toString(chunks));
                valueMap.put("currentTimeMillis", NumberUtil.toString(System.currentTimeMillis()));
                valueMap.put("chunkSize", NumberUtil.toString(length));
                write.setContent(valueMap.toString());
            }
        }
        return result;
    }


    /**
     * @param yz 文件因子
     * @return 得到已经上传的分片，数值长度为分片总长度
     */
    public boolean[] getChunkArray(String yz) {
        if (fileType == null) {
            return null;
        }
        String body = StringUtil.getPolicyName(FileUtil.getNamePart(original), 12, FileRenamePolicy.special);
        File newDir = new File(dir, getChunkFolderName(yz));
        File[] listFiles = newDir.listFiles();
        if (listFiles != null && listFiles.length <= 0) {
            return null;
        }
        if (chunks <= 0) {
            File crcFile = new File(newDir, crcFileName);
            AbstractRead read = new AutoReadTextFile();
            read.setFile(crcFile.getPath());
            StringMap<String, String> valueMap = new StringMap<>();
            valueMap.setKeySplit(StringUtil.EQUAL);
            valueMap.setLineSplit(StringUtil.CRLF);
            try {
                valueMap.setString(read.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            chunks = StringUtil.toInt(valueMap.get("chunks"));
        }

        boolean[] parts = ArrayUtil.getInitBooleanArray(chunks, false);
        if (!newDir.isDirectory()) {
            return null;
        }
        for (File file : Objects.requireNonNull(newDir.listFiles())) {
            if (file.isDirectory()) {
                continue;
            }
            if (!file.getName().startsWith(body)) {
                continue;
            }
            if (!file.getName().contains("-[")) {
                continue;
            }
            int index = StringUtil.toInt(StringUtil.substringBetween(file.getName(), "-[", "]"));
            if (index > chunks) {
                return null;
            }
            parts[index] = true;
        }
        return parts;
    }

    /**
     * @param yz 文件因子
     * @return 判断是否已经上传完成
     */
    public boolean isFolderChunkFull(String yz) {
        return !ArrayUtil.contains(getChunkArray(yz), false);
    }

    /**
     * @param yz 文件因子
     * @return 得到最后的分片
     */
    public int getLastChunk(String yz) {
        boolean[] parts = getChunkArray(yz);
        if (ArrayUtil.isEmpty(parts)) {
            return 0;
        }
        return ArrayUtil.indexOf(parts, false) - 1;
    }

    /**
     * @param yz 识别因子
     * @return 合并文件
     */
    public boolean mergeChunks(String yz) {
        if (fileType == null) {
            return false;
        }
        String body = StringUtil.getPolicyName(FileUtil.getNamePart(original), 12, FileRenamePolicy.special);
        File newDir = new File(dir, getChunkFolderName(yz));
        File[] listFiles = newDir.listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return false;
        }
        if (chunks <= 0) {
            File crcFile = new File(newDir, crcFileName);
            AbstractRead read = new AutoReadTextFile();
            read.setFile(crcFile.getPath());

            StringMap<String, String> valueMap = new StringMap<String, String>();
            valueMap.setKeySplit(StringUtil.EQUAL);
            valueMap.setLineSplit(StringUtil.CRLF);
            try {
                valueMap.setString(read.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            chunks = StringUtil.toInt(valueMap.get("chunks"));
        }
        File[] files = new File[chunks];
        for (File file : Objects.requireNonNull(newDir.listFiles())) {
            if (file.isDirectory()) {
                continue;
            }
            if (!file.getName().startsWith(body)) {
                continue;
            }
            if (!file.getName().contains("-[")) {
                continue;
            }
            int index = StringUtil.toInt(StringUtil.substringBetween(file.getName(), "-[", "]"));
            if (index > chunks) {
                return false;
            }
            files[index] = file;
        }
        File outFile = new File(dir, original);
        if (FileUtil.mergeFiles(outFile, files)) {
            this.dir = FileUtil.mendPath(outFile.getParent());
            this.fileName = outFile.getName();
            //删除分片文件
            return FileUtil.deleteDirectory(newDir);
        }
        return false;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isChunkUpload() {
        return chunkUpload;
    }

    public void setChunkUpload(boolean chunkUpload) {
        this.chunkUpload = chunkUpload;
    }
}

