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

/*
 * Copyright: Copyright (c) 2002-2003
 * Company: JavaResearch(http://www.javaresearch.org)
 * 最后更新日期:2003年1月9日
 *
 * @author Cherami, Barney, Brain
 * @version 0.8
 */

import com.github.jspxnet.utils.FileUtil;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 压缩文件工具类。
 * 此类完成一般的新建、增加、导出、删除操作以及完成对压缩文件的内容的解析。
 */
@Slf4j
public class ZipFile {


    private boolean isScaned = false;
    private boolean isChanged = false;
    private File selfFile;
    private static File workDirectory = new File(".");
    private static boolean haveSetWorkDirectory = false;
    private List<ZipFileRecord> entries = new ArrayList<>();
    private List<String> entryNames = new ArrayList<>();
    private int count = 0;
    private long totalSize = 0;

    /**
     * 缺省构造方法，一般用于创建一个新的压缩文件，但是是一个临时文件。
     */
    public ZipFile() {
        try {
            selfFile = File.createTempFile("jzj", ".tmp", workDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造方法，如果指定的文件不存在则创建一个指定名称的新文件。
     *
     * @param fileName 文件名
     */

    public ZipFile(String fileName) {
        selfFile = new File(fileName);
        createNewFile();
    }

    /**
     * 构造方法，如果指定的文件不存在则创建一个指定名称的新文件。
     *
     * @param file 文件
     */
    public ZipFile(File file) {
        selfFile = file;
        createNewFile();
    }

    /**
     * 设置系统的工作目录，设置以后就不能修改，直到JVM退出。
     * 如果指定的目录不存在则为缺省的当前目录
     *
     * @param directoryName 目录名
     */

    public static void setWorkDirectory(String directoryName) {
        if (haveSetWorkDirectory) {
            return;
        }
        File tmp = new File(directoryName);
        if (tmp.isDirectory()) {
            workDirectory = tmp;
        }
    }

    /**
     * 设置系统的工作目录，设置以后就不能修改，直到JVM退出。
     * 如果指定的目录不存在则为缺省的当前目录
     *
     * @param directory 目录
     */

    public static void setWorkDirectory(File directory) {
        if (haveSetWorkDirectory) {
            return;
        }
        if (directory != null && directory.isDirectory()) {
            workDirectory = directory;
        }
    }

    /**
     * 向自己代表的压缩包中新增文件，如果该文件已经存在则增加失败。
     *
     * @param path     新增的文件所在的目录
     * @param fileName 新增的文件的文件名
     */
    public void addFile(File path, String fileName) {
        addFileToSelf(path, fileName, false);
    }

    /**
     * 向自己代表的压缩包中新增文件。
     *
     * @param path      新增的文件所在的目录
     * @param fileName  新增的文件的文件名
     * @param overWrite 是否覆盖已有的文件
     */
    public void addFile(File path, String fileName, boolean overWrite) {
        addFileToSelf(path, fileName, overWrite);
    }

    /**
     * 向自己代表的压缩包中新增文件。
     *
     * @param path      新增的文件所在的目录
     * @param fileName  新增的文件的文件名
     * @param overWrite 是否覆盖已有的文件
     */
    private void addFileToSelf(File path, String fileName, boolean overWrite) {
        BufferedInputStream bin;
        ZipOutputStream zout;
        ZipInputStream zin;
        File tmpzip = null;
        ZipEntry addedEntry = new ZipEntry(fileName);
        try {
            tmpzip = File.createTempFile("jzj", ".tmp", workDirectory);
            zin = new ZipInputStream(new FileInputStream(selfFile));
            zout = new ZipOutputStream(new FileOutputStream(tmpzip));
            ZipEntry entry;
            int len;
            byte[] b = new byte[4096];
            while ((entry = zin.getNextEntry()) != null) {
                if (!isSameEntry(entry, addedEntry)) {
                    zout.putNextEntry(new ZipEntry(entry.getName()));
                    while ((len = zin.read(b)) != -1) {
                        zout.write(b, 0, len);
                    }
                    zout.closeEntry();
                    zin.closeEntry();
                } else if (overWrite) {

                } else {
                    zout.close();
                    zin.close();
                    tmpzip.delete();
                    tmpzip = null;
                    throw new com.github.jspxnet.io.zip.ZipException(com.github.jspxnet.io.zip.ZipException.ENTRYEXIST);
                }
            }
            bin = new BufferedInputStream(new FileInputStream(new File(path, fileName)));
            zout.putNextEntry(addedEntry);
            while ((len = bin.read(b)) != -1) {
                zout.write(b, 0, len);
            }
            zout.closeEntry();
            zout.close();
            zin.close();
            String selfFileName = selfFile.getPath();
            selfFile.delete();
            tmpzip.renameTo(new File(selfFileName));
            selfFile = new File(selfFileName);
            isChanged = true;
            count++;
        } catch (Exception e) {
            if (tmpzip != null) {
                tmpzip.delete();
            }
            e.printStackTrace();
        }
    }

    /**
     * 向自己代表的压缩包中新增多个文件，如果已经存在则不会添加。
     *
     * @param path      新增的文件所在的目录
     * @param fileNames 新增的文件的文件名
     */

    public void addFiles(File path, String[] fileNames) {
        addFilesToSelf(path, fileNames, false);
    }

    /**
     * 向自己代表的压缩包中新增多个文件。
     *
     * @param path      新增的文件所在的目录
     * @param fileNames 新增的文件的文件名
     * @param overWrite 设置在已经存在该文件时是否进行覆盖
     */
    public void addFiles(File path, String[] fileNames, boolean overWrite) {
        addFilesToSelf(path, fileNames, overWrite);
    }

    /**
     * 向自己代表的压缩包中新增多个文件。
     *
     * @param path      新增的文件所在的目录
     * @param fileNames 新增的文件的文件名
     * @param overWrite 设置在已经存在该文件时是否进行覆盖
     */
    private void addFilesToSelf(File path, String[] fileNames, boolean overWrite) {
        BufferedInputStream bin;
        ZipOutputStream zout;
        ZipInputStream zin;
        File tmpzip = null;
        ZipEntry[] addedEntries = new ZipEntry[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            addedEntries[i] = new ZipEntry(fileNames[i]);
        }

        try {
            tmpzip = File.createTempFile("jzj", ".tmp", workDirectory);
            zin = new ZipInputStream(new FileInputStream(selfFile));
            zout = new ZipOutputStream(new FileOutputStream(tmpzip));
            ZipEntry entry;
            int len = 0;
            byte[] b = new byte[4096];
            while ((entry = zin.getNextEntry()) != null) {
                if (!isContainEntry(entry, addedEntries)) {
                    zout.putNextEntry(new ZipEntry(entry.getName()));
                    while ((len = zin.read(b)) != -1) {
                        zout.write(b, 0, len);
                    }
                    zout.closeEntry();
                    zin.closeEntry();
                } else if (!overWrite) {
                    zout.close();
                    zin.close();
                    tmpzip.delete();
                    tmpzip = null;
                    throw new com.github.jspxnet.io.zip.ZipException(ZipException.ENTRYEXIST);
                }
            }
            for (int i = 0; i < addedEntries.length; i++) {
                bin = new BufferedInputStream(new FileInputStream(new File(path,
                        fileNames[i])));
                zout.putNextEntry(addedEntries[i]);
                while ((len = bin.read(b)) != -1) {
                    zout.write(b, 0, len);
                }
                zout.closeEntry();
                count++;
            }
            zout.close();
            zin.close();
            String slefFileName = selfFile.getPath();
            log.debug("rename transfer:" + slefFileName);
            selfFile.delete();
            tmpzip.renameTo(new File(slefFileName));
            selfFile = new File(slefFileName);
            isChanged = true;
        } catch (Exception e) {
            if (tmpzip != null) {
                tmpzip.delete();
            }
            e.printStackTrace();
        }

    }

    /**
     * 得到压缩包中的文件数。
     *
     * @return 压缩包中的文件数
     */
    public int size() {
        return count;
    }

    /**
     * 得到压缩包中的所有文件的大小的和。
     *
     * @return 压缩包中的所有文件的大小的和
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * 重新命名压缩包。
     *
     * @param fileName 期望的文件名
     */
    public void renameTo(String fileName) {
        if (fileName == null) {
            return;
        }
        File target = new File(fileName);
        if (target.exists()) {
            return;
        }
        selfFile.renameTo(target);
    }

    /**
     * 扫描压缩包取得全部需要的信息，如果已经扫描过而且自上次扫描后没有变化则不重新扫描。
     */
    public void scan() {
        if (selfFile == null) {
            return;
        }
        if (isChanged || !isScaned) {
            scanSelf();
        }
    }

    /**
     * 扫描压缩包取得全部需要的信息。
     */
    public void rescan() {
        if (selfFile == null) {
            return;
        }
        scanSelf();
    }

    /**
     * 判断自身是否包含指定的项目。
     *
     * @param entryName 项目名称
     * @return 存在返回true，否则返回false
     */
    public boolean isContainEntry(String entryName) {
        scan();
        return entryNames.contains(entryName);
    }

    /**
     * 扫描压缩包取得全部需要的信息。
     */
    private void scanSelf() {
        entries.clear();
        count = 0;
        ZipFileRecord record;
        try {
            java.util.zip.ZipFile file = new java.util.zip.ZipFile(selfFile);
            Enumeration<?> enu = file.entries();
            ZipEntry entry;
            while (enu.hasMoreElements()) {
                entry = (ZipEntry) enu.nextElement();
                if (entry.isDirectory() || "..\\".equals(entry.getName())) {
                    continue;
                }
                record = new ZipFileRecord(entry);
                entries.add(record);
                totalSize = totalSize + record.getSize();
                entryNames.add(record.entry.getName());
                count++;
                /*if (record.isRootEntry()) {
                  rootEntries.add(record);
                         }
                         if (record.getRootDirectory() != null &&
                    (!rootDirectories.contains(record.getRootDirectory()))) {
                  rootDirectories.add(record.getRootDirectory());
                         }*/
            }
            isScaned = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将指定的项目导出为文件，如果导出目录中已经存在该文件则不会覆盖。
     *
     * @param entryName 需要导出的项目名称
     * @param fileName  导出后的文件名
     * @throws IOException 异常
     */
    public void extractEntryToFile(String entryName, String fileName) throws IOException {
        extractEntry(entryName, fileName, false);
    }

    /**
     * 将指定的项目导出为文件。
     *
     * @param entryName 需要导出的项目名称
     * @param fileName  导出后的文件名
     * @param overWrite 文件已经存在时是否进行覆盖
     * @throws IOException 异常
     */
    public void extractEntryToFile(String entryName, String fileName,
                                   boolean overWrite) throws IOException {
        extractEntry(entryName, fileName, overWrite);
    }

    /**
     * 将指定的项目导出为文件。
     *
     * @param entryName 需要导出的项目名称
     * @param fileName  导出后的文件名
     * @param overWrite 文件已经存在时是否进行覆盖
     */
    private void extractEntry(String entryName, String fileName,
                              boolean overWrite) throws IOException {
        if (FileUtil.isFileExist(fileName) && !overWrite) {
            return;
        }
        ZipInputStream zin = new ZipInputStream(new FileInputStream(selfFile));
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(entryName)) {
                byte[] buf = new byte[4096];
                BufferedInputStream bin = new BufferedInputStream(zin);
                FileUtil.makeDirectory(fileName);
                BufferedOutputStream bout = new BufferedOutputStream(new
                        FileOutputStream(fileName));
                while (bin.read(buf, 0, 1) != -1) {
                    bout.write(buf, 0, 1);
                }
                bout.close();
                bin.close(); //by barney
            }
            zin.closeEntry();
        }
        zin.close();

    }

    /**
     * 将指定的多个项目分别导出为文件，在已经存在的情况下不进行覆盖。
     *
     * @param entryNames 需要导出的项目名称
     * @param fileNames  导出后的文件名
     */
    public void extractEntriesToFiles(String[] entryNames, String[] fileNames) {
        extractEntries(entryNames, fileNames, false);
    }

    /**
     * 将指定的多个项目分别导出为文件。
     *
     * @param entryNames 需要导出的项目名称
     * @param fileNames  导出后的文件名
     * @param overWrite  文件已经存在时是否进行覆盖
     */
    public void extractEntriesToFiles(String[] entryNames, String[] fileNames,
                                      boolean overWrite) {
        extractEntries(entryNames, fileNames, overWrite);
    }

    /**
     * 将指定的多个项目分别导出为文件。
     *
     * @param entryNames 需要导出的项目名称
     * @param fileNames  导出后的文件名
     * @param overWrite  文件已经存在时是否进行覆盖
     */
    private void extractEntries(String[] entryNames, String[] fileNames,
                                boolean overWrite) {
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(selfFile));
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                int index = getContainEntry(entry.getName(), entryNames);
                if ((index != -1) &&
                        (overWrite || !FileUtil.isFileExist(fileNames[index]))) {
                    byte[] buf = new byte[4096];
                    BufferedInputStream bin = new BufferedInputStream(zin);
                    FileUtil.makeDirectory(fileNames[index]);
                    BufferedOutputStream bout = new BufferedOutputStream(new
                            FileOutputStream(fileNames[index]));
                    while (bin.read(buf, 0, 1) != -1) {
                        bout.write(buf, 0, 1);
                    }
                    bout.close();
                    bin.close(); //by barney
                }
                zin.closeEntry();
            }
            zin.close();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * 从压缩包中删除指定的项目。
     *
     * @param entry 要删除的项目名
     */
    public void deleteEntry(String entry) {
        ZipOutputStream zout = null;
        ZipInputStream zin = null;
        File tmpzip = null;
        try {
            tmpzip = File.createTempFile("zip", ".tmp", new File("."));
            zin = new ZipInputStream(new FileInputStream(selfFile));
            zout = new ZipOutputStream(new FileOutputStream(tmpzip));
            ZipEntry ze;
            int len = 0;
            byte[] b = new byte[4096];
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals(entry)) {
                    zin.closeEntry();
                    count--;
                    continue;
                }
                zout.putNextEntry(new ZipEntry(ze.getName()));
                log.debug(ze.getName());
                while ((len = zin.read(b)) != -1) {
                    zout.write(b, 0, len);
                }
                zout.closeEntry();
                zin.closeEntry();
            }
            zout.close();
            zin.close();
            String slefFileName = selfFile.getPath();
            selfFile.delete();
            tmpzip.renameTo(new File(slefFileName));
            selfFile = new File(slefFileName);
            isChanged = true;
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * 从压缩包中删除指定的项目。
     *
     * @param entries 要删除的项目名
     */
    public void deleteEntries(String[] entries) {
        ZipOutputStream zout = null;
        ZipInputStream zin = null;
        File tmpzip = null;
        try {
            tmpzip = File.createTempFile("zip", ".tmp", new File("."));
            zin = new ZipInputStream(new FileInputStream(selfFile));
            zout = new ZipOutputStream(new FileOutputStream(tmpzip));
            ZipEntry ze;
            int len = 0;
            byte[] b = new byte[4096];
            while ((ze = zin.getNextEntry()) != null) {
                if (getContainEntry(ze.getName(), entries) != -1) {
                    zin.closeEntry();
                    count--;
                    continue;
                }
                zout.putNextEntry(new ZipEntry(ze.getName()));
                while ((len = zin.read(b)) != -1) {
                    zout.write(b, 0, len);
                }
                zout.closeEntry();
                zin.closeEntry();
            }
            zout.close();
            zin.close();
            String slefFileName = selfFile.getPath();
            selfFile.delete();
            tmpzip.renameTo(new File(slefFileName));
            selfFile = new File(slefFileName);
            isChanged = true;
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * 得到压缩包的文件名。
     *
     * @return 压缩包的文件名
     */
    public String getName() {
        return selfFile.getName();
    }

    /**
     * 得到压缩包的全文件名，即绝对路径名。
     *
     * @return 压缩包的全文件名
     */
    public String getFullName() {
        return selfFile.getPath();
    }

    /**
     * 得到压缩包所在的目录的目录名。
     *
     * @return 压缩包所在的目录的目录名
     */
    public String getParentPath() {
        return selfFile.getParent();
    }

    /**
     * 得到压缩包中的所有项目的枚举。
     *
     * @return 压缩包中的所有项目的枚举
     */
    public Iterator entries() {
        return entries.iterator();
    }

    /**
     * 得到压缩包中的所有项目的项目名的数组。
     *
     * @return 压缩包中的所有项目的项目名的数组
     */
    public String[] getEntryNames() {
        String[] names = new String[this.entries.size()];
        Iterator entries = entries();
        int i = 0;
        while (entries.hasNext()) {
            names[i++] = (String) (((ZipFileRecord) entries.next()).get(0));
        }
        return names;
    }

    /**
     * 得到压缩包中的所有项目的项目的数组。
     *
     * @return 压缩包中的所有项目的项目的数组
     */
    public ZipFileRecord[] getEntries() {
        ZipFileRecord[] records = new ZipFileRecord[count];
        for (int i = 0; i < records.length; i++) {
            records[i] =  entries.get(i);
        }
        return records;
    }

    /**
     * 得到压缩包中的所有根项目的枚举。
     * @return 压缩包中的所有根项目的枚举
     */
    /*public Iterator rootEntries() {
      return rootEntries.iterator();
       }*/
    /**
     * 得到压缩包中的所有根项目的项目名的数组。
     * @return 压缩包中的所有根项目的项目名的数组
     */
    /*public String[] getRootEntryNames() {
      String[] names = new String[rootEntries.size()];
      Iterator rootEntries = rootEntries();
      int i = 0;
      while (rootEntries.hasNext()) {
        names[i++] = (String) ( ( (ZipFileRecord) rootEntries.next()).get(
            "name"));
      }
      return names;
       }*/
    /**
     * 得到压缩包中的所有根项目目录的枚举。
     * @return 压缩包中的所有根项目目录的枚举
     */
    /*public Iterator rootDirectoryEntries() {
      return rootDirectories.iterator();
       }*/
    /**
     * 得到压缩包中的所有根项目目录名的数组。
     * @return 压缩包中的所有根项目目录名的数组
     */
    /*public String[] getRootDirectoryNames() {
      String[] names = new String[rootDirectories.size()];
      Iterator rootDirectories = rootDirectoryEntries();
      int i = 0;
      while (rootDirectories.hasNext()) {
        names[i++] = (String) rootDirectories.next();
      }
      return names;
       }*/

    /**
     * 判断两个项目是否相同。
     *
     * @param one 项目一
     * @param two 项目二
     * @return 如果两个项目的项目名相同则返回true，否则返回false
     */
    private boolean isSameEntry(ZipEntry one, ZipEntry two) {
        return one.getName().equals(two.getName());
    }

    /**
     * 判断在项目数组中是否存在指定的项目。
     *
     * @param one    指定的项目
     * @param others 项目数组
     * @return 存在返回true，否则返回false
     */
    private boolean isContainEntry(ZipEntry one, ZipEntry[] others) {
        for (ZipEntry other : others) {
            if (isSameEntry(one, other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 得到指定项目在项目数组中的索引。
     *
     * @param one    指定项目
     * @param others 项目数组
     * @return 指定项目在项目数组中的索引，不存在时返回-1
     */
    private int getContainEntry(String one, String[] others) {
        for (int i = 0; i < others.length; i++) {
            if (one.equals(others[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 如果自身代表的压缩包不存在则创建一个新文件。
     */
    private void createNewFile() {
        if (!selfFile.exists()) {
            try {
                selfFile.createNewFile();
            } catch (IOException e) {
                //...
            }
        }
    }


    /**
     * 可以直接使用在压缩下载
     * 压缩成ZIP 方法
     * @param sourceFile 原文件可以是目录,或者文件
     * @param out 输出流
     * @param keepStructure 是否保留原来的目录结构,true:保留目录结构; false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void zipOutputStream(File sourceFile, OutputStream out, boolean keepStructure) throws RuntimeException{
        try ( ZipOutputStream zos = new ZipOutputStream(out)){
            compress(sourceFile,zos,sourceFile.getName(),keepStructure);
        } catch (Exception e) {
            throw new RuntimeException("zip error from",e);
        }
    }
    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @param keepStructure  是否保留原来的目录结构,true:保留目录结构;
     *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception 异常
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepStructure) throws Exception{
        byte[] buf = new byte[1024];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            //是文件夹
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(keepStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),keepStructure);
                    } else {
                        compress(file, zos, file.getName(),keepStructure);
                    }
                }
            }
        }
    }
}