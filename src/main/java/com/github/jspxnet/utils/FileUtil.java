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
 * User: chenYuan
 * date: 2004-4-2
 * Time: 10:37:48
 * 文件处理单元
 */

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.file.MultiFile;
import com.github.jspxnet.io.zip.ZipFile;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.upload.multipart.RenamePolicy;
import com.sun.nio.zipfs.JarFileSystemProvider;
import com.sun.nio.zipfs.ZipFileSystemProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-1
 * Time: 11:30:53
 * To change this template use | Settings | File Templates.
 */
@Slf4j
public final class FileUtil {
    static final private String[] zipFiles = new String[]{".zip!", ".jar!", ".apk!", ".war!", ".jzb!"};

    static final private String[] zipEx = new String[]{"zip", "jar", "apk", "war", "jzb"};
    //路径通配符
    static final private String[] pathMarks = {"#", StringUtil.ASTERISK, "?"};
    //缩图文件名称
    final public static String THUMBNAIL_FILE_TYPE = "_s";

    //手机图片名称
    final public static String PHONE_FILE_TYPE = "_m";


    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';
    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';
    public static final int BUFFER_SIZE = 1024 * 4;
    private static final char SYSTEM_SEPARATOR = File.separatorChar;
    public static final char OTHER_SEPARATOR;
    public static final int M = 1048576;

    public static final String sortName = "name";
    public static final String sortDate = "date";
    public static final String[] NO_SEARCH_JAR = {"org\\apache\\","org\\codehaus","org\\sonatype","com\\aliyun","org\\bouncycastle",
            "com\\google","com\\atomikos","com\\twelvemonkeys","org\\mozilla","com\\jcraft","org\\postgresql","io\\netty\\netty"
            ,"org\\slf4j","org\\codehaus","com\\intellij","asm\\asm-commons","com\\jgoodies",".m2","jre\\lib","j2sdk\\"};

    static {
        if (isSystemWindows()) {
            OTHER_SEPARATOR = UNIX_SEPARATOR;
        } else {
            OTHER_SEPARATOR = WINDOWS_SEPARATOR;
        }
    }

    public static boolean isZipPackageFile(String file) {
        if (file == null) {
            return false;
        }
        String temp = file.toLowerCase();
        for (String fileType : zipFiles) {
            if (temp.contains(fileType)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Converts all separators transfer the Unix separator of forward slash.
     *
     * @param path the path transfer be changed, null ignored
     * @return the updated path
     */
    public static String separatorsToUnix(String path) {
        if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
    }

    /**
     * Converts all separators transfer the Windows separator of backslash.
     *
     * @param path the path transfer be changed, null ignored
     * @return the updated path
     */
    public static String separatorsToWindows(String path) {
        if (path == null || path.indexOf(UNIX_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
    }

    //-----------------------------------------------------------------------

    /**
     * Determines if Windows file system is in use.
     *
     * @return true if the system is Windows
     */
    static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }

    //-----------------------------------------------------------------------

    /**
     * Checks if the character is a separator.
     *
     * @param ch the character transfer check
     * @return true if it is a separator character
     */
    private static boolean isSeparator(char ch) {
        return (ch == UNIX_SEPARATOR) || (ch == WINDOWS_SEPARATOR);
    }

    /**
     * Converts all separators transfer the system separator.
     *
     * @param path the path transfer be changed, null ignored
     * @return the updated path
     */
    public static String separatorsToSystem(String path) {
        if (path == null) {
            return null;
        }
        if (isSystemWindows()) {
            return separatorsToWindows(path);
        } else {
            return separatorsToUnix(path);
        }
    }

    private FileUtil() {

    }


    /**
     * When the destination file is on another file system, do a "copy and delete".
     *
     * @param srcFile  the file transfer be moved
     * @param destFile the destination file
     * @param covering 是否覆盖
     * @return 一对是否成功
     */
    public static boolean moveFile(File srcFile, File destFile, boolean covering) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        if (!srcFile.exists()) {
            return false;
        }
        if (srcFile.isDirectory()) {
            return false;
        }

        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            rename = copyFile(srcFile, destFile, covering);
            if (!srcFile.delete()) {
                srcFile.deleteOnExit();
            }
        }
        return rename;
    }


    /**
     * @param file         文件
     * @param renamePolicy 重命名方式
     * @param covering     是否覆盖
     * @return 移动到类型目录
     */
    public static File moveToTypeDir(File file, RenamePolicy renamePolicy, boolean covering) {
        String type = getTypePart(file.getName());
        File newDir = new File(file.getParent(), type);
        if (!newDir.exists()) {
            makeDirectory(newDir);
        }
        File newFile = new File(newDir, file.getName());
        newFile = renamePolicy.rename(newFile);
        if (moveFile(file, newFile, covering)) {
            return newFile;
        }
        return null;
    }

    public static boolean copy(String inputFilename, String outputFilename, boolean covering) {
        return copy(new File(inputFilename), new File(outputFilename), covering);
    }


    /**
     * 拷贝文件，是否使用覆盖方式
     *
     * @param input    in file
     * @param output   out file
     * @param covering 是否使用覆盖方式
     * @return 拷贝文件
     */
    public static boolean copy(File input, File output, boolean covering) {
        if (input.isFile()) {
            return copyFile(input, output, covering);
        } else {
            MultiFile multiFile = new MultiFile();
            if (input.isDirectory()) {
                if (!FileUtil.makeDirectory(output)) {
                    return false;
                }
                return multiFile.copyDirectory(input, output, covering);
            }
        }
        return true;
    }


    /**
     * @param inputFile  进入文件
     * @param outputFile 输出文件
     * @param covering   覆盖否
     * @return 拷贝文件
     */
    public static boolean copyFile(File inputFile, File outputFile, boolean covering) {
        if (outputFile == null) {
            return false;
        }
        FileUtil.makeDirectory(outputFile.getParentFile());
        //如果问卷存在
        if (!covering && outputFile.length() != 0 && outputFile.exists()) {
            String fType = FileUtil.getTypePart(outputFile.getName());
            String fileName = FileUtil.getNamePart(outputFile.getName());
            outputFile = new File(outputFile.getParent(), fileName + "_duplicate." + fType);
        }
        try {
            return StreamUtil.copy(new BufferedInputStream(new FileInputStream(inputFile)), new BufferedOutputStream(new FileOutputStream(outputFile)), BUFFER_SIZE) && outputFile.setLastModified(inputFile.lastModified());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        }
        int len = filename.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = filename.charAt(0);
        if (ch0 == ':') {
            return -1;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2;  // return a length greater than the input
            }
            return (isSeparator(ch0) ? 1 : 0);
        } else {
            if (ch0 == '~') {
                int posUnix = filename.indexOf(UNIX_SEPARATOR, 1);
                int posWin = filename.indexOf(WINDOWS_SEPARATOR, 1);
                if (posUnix == -1 && posWin == -1) {
                    return len + 1;  // return a length greater than the input
                }
                posUnix = (posUnix == -1 ? posWin : posUnix);
                posWin = (posWin == -1 ? posUnix : posWin);
                return Math.min(posUnix, posWin) + 1;
            }
            char ch1 = filename.charAt(1);
            if (ch1 == ':') {
                ch0 = Character.toUpperCase(ch0);
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    if (len == 2 || !isSeparator(filename.charAt(2))) {
                        return 2;
                    }
                    return 3;
                }
                return -1;

            } else if (isSeparator(ch0) && isSeparator(ch1)) {
                int posUnix = filename.indexOf(UNIX_SEPARATOR, 2);
                int posWin = filename.indexOf(WINDOWS_SEPARATOR, 2);
                if ((posUnix == -1 && posWin == -1) || posUnix == 2 || posWin == 2) {
                    return -1;
                }
                posUnix = (posUnix == -1 ? posWin : posUnix);
                posWin = (posWin == -1 ? posUnix : posWin);
                return Math.min(posUnix, posWin) + 1;
            } else {
                return (isSeparator(ch0) ? 1 : 0);
            }
        }
    }

    /**
     * @param fileName 文件名
     * @param hashType MD5  SHA1  SHA-256  SHA-384 SHA-512
     * @return 返回哈希验证码
     * @throws Exception 异常 String hashType = "MD5";
     */
    public static String getHash(File fileName, String hashType) throws Exception {
        if (StringUtil.isNull(hashType) || "AUTO".equalsIgnoreCase(hashType)) {
             hashType ="MD5";
        }
        if (fileName.isFile() && fileName.exists() && fileName.canRead()) {
            InputStream fis = new FileInputStream(fileName);
            MessageDigest md5;
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                md5 = MessageDigest.getInstance(hashType);
                int numRead;
                while ((numRead = fis.read(buffer)) > 0) {
                    md5.update(buffer, 0, numRead);
                }
            } finally {
                fis.close();
            }
            return StringUtil.toHexString(md5.digest());
        }
        return StringUtil.empty;
    }

    /**
     * @param fis      留方式
     * @param hashType MD5  SHA1  SHA-256  SHA-384 SHA-512
     * @return 返回哈希验证码
     * @throws Exception 异常
     */
    public static byte[] getHash(InputStream fis, String hashType) throws Exception {
        if (StringUtil.isNull(hashType) || "AUTO".equalsIgnoreCase(hashType)) {
            hashType = "MD5";
        }
        MessageDigest md5;
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            md5 = MessageDigest.getInstance(hashType);
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
        } finally {
            fis.close();
        }
        return md5.digest();
    }

    /**
     *
     * @param fileName 文件名称
     * @param hashType 哈希类型
     * @return  文件唯一标识ID
     * @throws Exception 异常
     */
    public static String getFileGuid(File fileName, String hashType) throws Exception {
        if (fileName==null)
        {
            return null;
        }
        String head = EncryptUtil.toHex(FileUtil.readFileByte(fileName,100));
        String value = FileUtil.getHash(fileName,hashType);
        return EncryptUtil.getMd5(value + head + FileUtil.getTypePart(fileName) + fileName.length());
    }
    /**
     * 上边是标准的验证方式，但有的文件太大验证时间很慢
     * 所以提供下边这个快速的验证方法，只起一个包来比较，并不是所有的数据都比较，
     * 虽然不能保证100%的数据正确，但可以保证大部分正确，
     * 算法1M内的文件调用上边的方法，大于10的文件平均分成10份来验证就可以了
     *
     * @param fileName 文件列表
     * @param hashType 哈希类型
     * @return 得到哈希值
     * @throws Exception 异常
     */
/*    public static String getFastHash(File fileName, String hashType) throws
            Exception {
        if (StringUtil.isNull(hashType) || "AUTO".equalsIgnoreCase(hashType)) {
            hashType = "MD5";
        }
        if (fileName.exists()) {
            if (fileName.length() < 1024 * 1024) {
                return getHash(fileName, hashType);
            }
            int step = (int) fileName.length() / 10;
            InputStream fis = new FileInputStream(fileName);
            MessageDigest md5;
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                md5 = MessageDigest.getInstance(hashType);
                int numRead;
                int i = 0;
                while ((numRead = fis.read(buffer)) > 0) {
                    i++;
                    int p = i * step;
                    if (p < fileName.length()) {
                        fis.skip(p);
                    } else {
                        break;
                    }
                    md5.update(buffer, 0, numRead);
                }
            } finally {
                fis.close();
            }
            return StringUtil.toHexString(md5.digest());
        }
        return StringUtil.empty;
    }*/


    /**
     * @param name 文件名称
     * @return 得到流
     * @throws IOException 读取异常
     */
    static public FileInputStream openInputStream(String name) throws IOException {
        File file = new File(name);
        if (!file.isFile()) {
            throw new IOException(name + " is not a file.");
        }
        if (!file.canRead()) {
            throw new IOException(name + " is not readable.");
        }
        return (new FileInputStream(file));
    }

    /**
     * @param name 文件名称
     * @return 输出流
     * @throws IOException 读取异常
     */
    static public FileOutputStream openOutputStream(String name) throws IOException {
        File file = new File(name);
        if (file.isDirectory()) {
            throw new IOException(name + " is a directory.");
        }
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException(name + " is not delete file.");
            }
        }
        return (new FileOutputStream(file));
    }

    /**
     * 从文件路径得到文件名。
     *
     * @param source 文件的路径，可以是相对路径也可以是绝对路径
     * @return 对应的文件名
     * @since 0.4
     */
    static public String getFileName(String source) {
        if (StringUtil.isNull(source)) {
            return StringUtil.empty;
        }
        source = mendFile(source);
        int len = source.lastIndexOf("/");
        if (len < 0) {
            return source;
        }
        return source.substring(len + 1);
    }

    /**
     * @param fileName 文件名称
     * @return long   得到最后文件的修改时间
     */
    static public long lastModified(String fileName) {
        File file = new File(fileName);
        if (!file.isFile()) {
            return 0;
        }
        return file.lastModified();
    }

    /**
     * 从文件名得到文件绝对路径。
     *
     * @param fileName 文件名
     * @return 对应的文件路径
     * @since 0.4
     */
    static public String getAbsolutePath(String fileName) {
        File file = new File(fileName);
        return file.getPath();
    }

    /**
     * 得到文件的类型。
     * 实际上就是得到文件名中最后一个“.”后面的部分。
     * jar 文件,返回jar
     *
     * @param fileName 文件名
     * @return 文件名中的类型部分
     * @since 0.5
     */
    static public String getTypePart(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return StringUtil.empty;
        }
        int point = fileName.lastIndexOf(StringUtil.DOT);
        if (point != -1) {
            String typePart = fileName.substring(point + 1);
            if (typePart.contains("!")) {
                return StringUtil.substringBefore(typePart, "!");
            }
            return typePart;
        }
        return StringUtil.empty;
    }

    /**
     * 得到文件的类型。
     * 实际上就是得到文件名中最后一个“.”后面的部分。
     *
     * @param file 文件
     * @return 文件名中的类型部分
     * @since 0.5
     */
    static public String getTypePart(File file) {
        return getTypePart(file.getName());
    }

    /**
     * @param fileName 文件名称
     * @return 只要名称部分，不要扩展名
     */
    static public String getNamePart(String fileName) {
        String result = getFileNamePart(fileName);
        int point = result.lastIndexOf(StringUtil.DOT);
        if (point != -1 && result.length() >= point) {
            return result.substring(0, point);
        }
        return result;
    }

    /**
     * 得到文件的名字部分。
     * 实际上就是路径中的最后一个路径分隔符后的部分。
     *
     * @param fileName 文件名
     * @return 文件名中的名字部分
     * @since 0.5
     */
    static public String getFileNamePart(String fileName) {

        int point = getPathLastIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return fileName;
        } else if (point == length - 1) {
            int secondPoint = getPathLastIndex(fileName, point - 1);
            if (secondPoint == -1) {
                if (length == 1) {
                    return fileName;
                } else {
                    return fileName.substring(0, point);
                }
            } else {
                return fileName.substring(secondPoint + 1, point);
            }
        } else {
            return fileName.substring(point + 1);
        }
    }

    /**
     * 得到文件名中的父路径部分。
     * 对两种路径分隔符都有效。
     * 不存在时返回""。
     * 如果文件名是以路径分隔符结尾的则不考虑该分隔符，例如"/path/"返回""。
     *
     * @param fileName 文件名
     * @return 父路径，不存在或者已经是父目录时返回""
     * @since 0.5
     */
    static public String getPathPart(String fileName) {
        if (fileName == null) {
            return StringUtil.empty;
        }
        int point = getPathLastIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return StringUtil.empty;
        } else if (point == length - 1) {
            int secondPoint = getPathLastIndex(fileName, point - 1);
            if (secondPoint == -1) {
                return StringUtil.empty;
            } else {
                return mendPath(fileName.substring(0, secondPoint));
            }
        } else {
            return mendPath(fileName.substring(0, point));
        }
    }

    public static boolean equalsFile(String file1, String file2) {
        return file1 == null && file2 == null || (file1 != null && file1.equals(file2)) || mendFile(file1).equals(mendFile(file2));
    }

    public static boolean equalsFolder(String path1, String path2) {
        return path1 == null && path2 == null || (path1 != null && path1.equals(path2)) || mendPath(path1).equals(mendPath(path2));
    }

    /**
     * 得到路径分隔符在文件路径中首次出现的位置。
     * 对于DOS或者UNIX风格的分隔符都可以。
     *
     * @param fileName 文件路径
     * @return 路径分隔符在路径中首次出现的位置，没有出现时返回-1。
     * @since 0.5
     */
    static public int getPathIndex(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return -1;
        }
        int point = fileName.indexOf('/');
        if (point == -1) {
            point = fileName.indexOf('\\');
        }
        return point;
    }

    /**
     * 得到路径分隔符在文件路径中指定位置后首次出现的位置。
     * 对于DOS或者UNIX风格的分隔符都可以。
     *
     * @param fileName  文件路径
     * @param fromIndex 开始查找的位置
     * @return 路径分隔符在路径中指定位置后首次出现的位置，没有出现时返回-1。
     * @since 0.5
     */
    static public int getPathIndex(String fileName, int fromIndex) {
        if (StringUtil.isNull(fileName)) {
            return -1;
        }
        int point = fileName.indexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.indexOf('\\', fromIndex);
        }
        return point;
    }

    /**
     * 得到路径分隔符在文件路径中最后出现的位置。
     * 对于DOS或者UNIX风格的分隔符都可以。
     *
     * @param fileName 文件路径
     * @return 路径分隔符在路径中最后出现的位置，没有出现时返回-1。
     * @since 0.5
     */
    static public int getPathLastIndex(String fileName) {
        if (!StringUtil.hasLength(fileName)) {
            return -1;
        }
        int point = fileName.lastIndexOf('/');
        if (point == -1) {
            point = fileName.lastIndexOf('\\');
        }
        return point;
    }

    /**
     * 得到路径分隔符在文件路径中指定位置前最后出现的位置。
     * 对于DOS或者UNIX风格的分隔符都可以。
     *
     * @param fileName  文件路径
     * @param fromIndex 开始查找的位置
     * @return 路径分隔符在路径中指定位置前最后出现的位置，没有出现时返回-1。
     * @since 0.5
     */
    static public int getPathLastIndex(String fileName, int fromIndex) {
        if (StringUtil.isNull(fileName)) {
            return -1;
        }
        int point = fileName.lastIndexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.lastIndexOf('\\', fromIndex);
        }
        return point;
    }

    /**
     * 将文件名中的类型部分去掉。
     *
     * @param fileName 文件名
     * @return 去掉类型部分的结果
     * @since 0.5
     */
    static public String trimType(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return StringUtil.empty;
        }
        int index = fileName.lastIndexOf(StringUtil.DOT);
        if (index != -1) {
            return fileName.substring(0, index);
        } else {
            return fileName;
        }
    }

    /**
     * 得到相对路径。
     * 文件名不是目录名的子节点时返回文件名。
     *
     * @param pathName 目录名
     * @param fileName 文件名
     * @return 得到文件名相对于目录名的相对路径，目录下不存在该文件时返回文件名
     * @since 0.5
     */
    static public String getSubPath(String pathName, String fileName) {
        if (StringUtil.isNull(fileName)) {
            return StringUtil.empty;
        }
        int index = fileName.indexOf(pathName);
        if (index != -1) {
            return fileName.substring(index + pathName.length() + 1);
        } else {
            return fileName;
        }
    }

    /**
     * 删除文件
     *
     * @param fileName file name
     * @return int
     */
    public static int delete(String fileName) {
        if (fileName == null || "".equals(fileName) || "null".equalsIgnoreCase(fileName)) {
            return 0;
        }
        int result = 0;
        File f = new File(fileName);
        if (!f.exists()) {
            return 0;
        } else if (!f.canWrite()) {
            result = -1;
        }
        if (f.isDirectory()) {
            String[] files = f.list();
            if (files != null && files.length > 0) {
                result = -1;
            }
        }
        boolean success = f.delete();
        if (!success) {
            result = -1;
        }
        return result;
    }

    /**
     * @param f 文件
     * @return 删除, 支持延时删除
     */
    public static int delete(File f) {
        if (f == null) {
            return 0;
        }
        int result = 0;
        if (!f.exists()) {
            return 0;
        }
        if (f.isDirectory()) {
            result = -1;
        }
        boolean success = f.delete();
        if (success) {
            result = 1;
        } else {
            f.deleteOnExit();
            result = 0;
        }
        return result;
    }

    /**
     * 删除指定目录及其中的所有内容。
     *
     * @param dir 要删除的目录
     * @return 删除成功时返回true，否则返回false。
     * @since 0.1
     */
    static public boolean deleteDirectory(File dir) {
        if ((dir == null) || !dir.isDirectory()) {
            return true;
        }
        File[] entries = dir.listFiles();
        if (entries == null) {
            return false;
        }
        for (File entry : entries) {
            if (entry.isDirectory()) {
                deleteDirectory(entry);
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
        return dir.delete();
    }

    /**
     * 判断指定的文件是否存在。
     *
     * @param fileName 要判断的文件的文件名
     * @return 存在时返回true，否则返回false。
     * @since 0.1
     */
    static public boolean isDirectory(String fileName) {
        return !StringUtil.isNull(fileName) && new File(fileName).isDirectory();
    }


    /**
     * @param fileName 文件名
     * @return 最后修改日期
     */
    static public long getLastModified(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return 0;
        }
        File file = new File(fileName);
        if (file.exists()) {
            return file.lastModified();
        }
        return 0;
    }

    static public boolean isFileExist(File fileName) {
        if (fileName == null) {
            return false;
        }
        return isFileExist(fileName.getPath());
    }

    /**
     * 是否为空目录
     * @param fileName 目录
     * @return 空目录
     */
    static public boolean isEmptyDirectory(File fileName) {

        if (isFileExist(fileName)) {
            return true;
        }
        return Objects.requireNonNull(fileName.listFiles()).length<1;

    }

    /**
     * 判断指定的文件是否存在。
     *
     * @param fileName 要判断的文件的文件名
     * @return 存在时返回true，否则返回false。
     * @since 0.1
     */
    static public boolean isFileExist(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return false;
        }

        String fileEx = ".jar!";
        if (fileName.contains(".jar!")) {
            fileEx = ".jar!";
        } else if (fileName.contains(".apk!")) {
            fileEx = ".apk!";
        } else if (fileName.contains(".war!")) {
            fileEx = ".war!";
        } else if (fileName.contains(".jzb!")) {
            fileEx = ".jzb!";
        } else if (fileName.contains(".gzip!")) {
            fileEx = ".gzip!";
        }


        int i = fileName.indexOf(fileEx);
        if (i == -1) {
            File file = new File(fileName);
            return file.isFile() && file.exists();
        }

        fileName = mendFile(fileName);
        i = fileName.indexOf(fileEx);
        String jarFileName = fileName.substring(0, i + 4);
        String entryName = StringUtil.substringAfterLast(fileName,fileEx);

        File file = new File(jarFileName);
        if (!file.isFile()) {
            return false;
        }

        InputStream in = null;
        try  {
            URL url = new URL("jar:file:///" +jarFileName+ "!" + entryName);
            in = url.openStream();
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (in!=null)
                {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       /*
        try {

            Path apkFile = Paths.get(jarFileName);
            log.info("1---------Paths.get(jarFileName),jarFileName:{}",jarFileName);
            ZipFileSystemProvider jarFileSystemProvider = new ZipFileSystemProvider();
            FileSystem fs = jarFileSystemProvider.newFileSystem(apkFile,null);

            //FileSystem fs = FileSystems.newFileSystem(apkFile, jarFileSystemProvider);
            log.info("2---------Paths.get(jarFileName),fs:{}",fs);
            Path dexFile = fs.getPath(entryName);
            log.info("3---------Paths.get(jarFileName),dexFile:{}",dexFile);
            return Files.exists(dexFile);
        } catch (Throwable e) {
            log.error("Paths.get(jarFileName),jarFileName:{}",jarFileName);
            e.printStackTrace();
        }*/

    }

    /**
     * 创建指定的目录。
     * 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。
     * 注意：可能会在返回false的时候创建部分父目录。
     *
     * @param file 要创建的目录
     * @return 完全创建成功时返回true，否则返回false。
     * @since 0.2
     */
    static public boolean makeDirectory(File file) {
        return file.exists() || file.mkdirs();
    }

    /**
     * 创建指定的目录。
     * 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。
     * 注意：可能会在返回false的时候创建部分父目录。
     *
     * @param fileName 要创建的目录的目录名
     * @return 完全创建成功时返回true，否则返回false。
     * @since 0.1
     */
    static public boolean makeDirectory(String fileName) {
        File file = new File(fileName);
        return makeDirectory(file);
    }

    /**
     * 清空指定目录中的文件。
     * 这个方法将尽可能删除所有的文件，但是只要有一个文件没有被删除都会返回false。
     * 另外这个方法不会迭代删除，即不会删除子目录及其内容。
     *
     * @param directory 要清空的目录
     * @return 目录下的所有文件都被成功删除时返回true，否则返回false.
     * @since 0.1
     */
    static public boolean emptyDirectory(File directory) {
        File[] entries = directory.listFiles();
        for (File entry : entries) {
            if (!entry.delete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 清空指定目录中的文件。
     * 这个方法将尽可能删除所有的文件，但是只要有一个文件没有被删除都会返回false。
     * 另外这个方法不会迭代删除，即不会删除子目录及其内容。
     *
     * @param directoryName 要清空的目录的目录名
     * @return 目录下的所有文件都被成功删除时返回true，否则返回false。
     * @since 0.1
     */
    static public boolean emptyDirectory(String directoryName) {
        File dir = new File(directoryName);
        return emptyDirectory(dir);
    }

    /**
     * 删除指定目录及其中的所有内容。
     *
     * @param dirName 要删除的目录的目录名
     * @return 删除成功时返回true，否则返回false。
     * @since 0.1
     */
    static public boolean deleteDirectory(String dirName) {
        return deleteDirectory(new File(dirName));
    }

    /**
     * 判断文件是否可写
     *
     * @param filename 要判断文件名
     * @return 返回true，否则返回false。
     * @since 0.1
     */
    static public boolean isWrite(String filename) {
        if (StringUtil.isNull(filename)) {
            return true;
        }
        File file = new File(filename);
        return !file.exists() || file.exists() && file.isFile() && file.canWrite();
    }

    /**
     * 判断文件是否可读
     *
     * @param filename 要判断文件名
     * @return 返回true，否则返回false。
     * @since 0.1
     */
    static public boolean isRead(String filename) {
        if (StringUtil.isNull(filename)) {
            return false;
        }
        File file = new File(filename);
        return file.exists() && file.isFile() && file.canRead();
    }


    /**
     * @param filename 文件名
     * @return 得到上级目录
     */
    static public String getParentPath(String filename) {
        if (filename == null) {
            return null;
        }
        File f = new File(filename);
        if (f.exists()) {
            return mendPath(f.getParent());
        } else {
            filename = mendPath(filename);
            if (filename.endsWith("/")) {
                filename = filename.substring(0, filename.length() - 1);
            }
            int x = filename.lastIndexOf("/");
            if (x != -1) {
                return filename.substring(0, x) + "/";
            }
        }
        return StringUtil.empty;
    }


    /**
     * 修复路径 让路径后边都有一个  /
     *
     * @param path 需要修复的路径
     * @return 修复后的路径
     * @since 0.2
     */
    public static String mendPath(String path) {
        if (path == null || path.length() < 1) {
            return StringUtil.empty;
        }
        String result = mendFile(path);
        if (!result.endsWith("/") && !result.endsWith("\\")) {
            result = result + "/";
        }
        return result;
    }

    /**
     * @param path 路径
     * @return 路径
     */
    public static String getURLFilePath(String path) {
        if (path == null || path.length() < 1) {
            return StringUtil.empty;
        }
        String result = StringUtil.replace(path, "/", "\\");
        if (result.startsWith("file:\\\\")) {
            return result;
        }
        return "file:\\\\" + result;

    }

    /**
     * 修复路径 是文件的/
     *
     * @param fileName 需要修复的路径
     * @return 修复后的路径
     * @since 0.2
     */
    public static String mendFile(String fileName) {
        if (fileName == null || fileName.length() < 1) {
            return StringUtil.empty;
        }
        String result = StringUtil.replace(fileName, "\\", "/");
        if (SystemUtil.OS == SystemUtil.WINDOWS) {
            if (result.startsWith("file://")) {
                result = result.substring(7);
            } else if (result.startsWith("file:/")) {
                result = result.substring(6);
            }
        } else {
            if (result.startsWith("file:")) {
                result = result.substring(5);
            }
        }
        if (result.startsWith("file:") || result.startsWith("http:") || result.startsWith("https:") || result.startsWith("ftp:") || result.startsWith("ftps:")) {
            return result;
        }
        return StringUtil.replace(result, "//", "/");
    }

    /**
     * @param filename 文件
     * @return 读取
     */
    static public byte[] readFileByte(File filename) {
        if (filename == null) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            FileChannel fileC = fis.getChannel();
            WritableByteChannel outC = Channels.newChannel(os);
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            while (true) {
                int i = fileC.read(buffer);
                if (i == 0 || i == -1) {
                    break;
                }
                buffer.flip();
                outC.write(buffer);
                buffer.clear();
            }
            fis.close();
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param filename 文件名
     * @param length   读取长度
     * @return 读取数据
     */
    static public byte[] readFileByte(File filename, int length) {
        if (filename == null||!filename.isFile()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(filename); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            FileChannel fileC = fis.getChannel();
            WritableByteChannel outC = Channels.newChannel(os);
            ByteBuffer buffer = ByteBuffer.allocateDirect(length);
            fileC.read(buffer);
            buffer.flip();
            outC.write(buffer);
            buffer.clear();
            fis.close();
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把byte数组写出到文件
     *
     * @param fileOut 文件名称
     * @param data    数据
     * @return 是否成功
     */
    static public boolean writeFile(File fileOut, byte[] data) {
        try {
            if (!fileOut.exists()) {
                if (!fileOut.createNewFile()) {
                    throw new IOException(fileOut + " is not create new file.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(fileOut, false)) {
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 合并文件
     *
     * @param outFile 输出文件
     * @param files   文件块数组
     * @return 合并文件是否成功
     */
    public static boolean mergeFiles(File outFile, File[] files) {
        try (FileChannel outChannel = new FileOutputStream(outFile).getChannel()) {
            for (File f : files) {
                if (f == null || !f.exists() || !f.isFile()) {
                    return false;
                }
                try (FileChannel fc = new FileInputStream(f).getChannel()) {
                    ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
                    while (fc.read(bb) != -1) {
                        bb.flip();
                        outChannel.write(bb);
                        bb.clear();
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param folder 目录
     * @param fen    分割
     * @return 得到文件列表，排序是时间
     */
    static public String getFileListDateSort(String folder, String fen) {
        if (StringUtil.isNull(folder)) {
            return StringUtil.empty;
        }
        List<FileInfo> fileList = new ArrayList<FileInfo>();
        File dir = new File(folder);
        if (!dir.isDirectory()) {
            return StringUtil.empty;
        }
        File[] files = dir.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isFile()) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file.getName());
                fileInfo.setIsDir(0);
                fileInfo.setSize(file.length());
                fileInfo.setDate(new Date(file.lastModified()));
                fileList.add(fileInfo);
            }
        }

        FileInfo file1, file2, file3;
        for (int i = 0; i < fileList.size() - 1; i++) {
            for (int j = i + 1; j < fileList.size(); j++) {
                file1 = fileList.get(i);
                file2 = fileList.get(j);
                if (file1.getDate().compareTo(file2.getDate()) < 0) {
                    file3 = fileList.get(j);
                    fileList.set(j, fileList.get(i));
                    fileList.set(i, file3);
                }
            }
        }
        StringBuilder result = new StringBuilder();
        for (FileInfo file : fileList) {
            result.append(file.getName()).append(fen);
        }
        fileList.clear();
        return result.toString();
    }


    /**
     * @param folder       目录
     * @param find         查询
     * @param type         类型
     * @param order        排序默认为时间，  name，szie
     * @param decreasePath 路径
     * @return 得道文件列表，并排序
     */
    static public List<FileInfo> getFileListSort(String folder, String find, String type, String order, String decreasePath) {
        if (type != null) {
            type = type.toLowerCase();
        }
        List<FileInfo> fileList = new ArrayList<FileInfo>();
        File dir = new File(folder);
        if (!dir.exists()) {
            return fileList;
        }
        File[] files = dir.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return fileList;
        }
        for (File file : files) {
            if (!StringUtil.isNull(find) && !file.getName().contains(find)) {
                continue;
            }
            FileInfo fileInfo = new FileInfo();
            if (StringUtil.isNull(type)) {

                if (file.isFile()) {
                    fileInfo.setIsDir(0);
                }
                if (file.isDirectory()) {
                    fileInfo.setIsDir(1);
                    fileInfo.setType("folder");
                } else {
                    fileInfo.setType(getTypePart(file.getName()));
                }
                if (file.isHidden()) {
                    fileInfo.setIsDir(-1);
                }
                fileInfo.setIsDir(0);
            } else if (type.contains(FileUtil.getTypePart(file.getName()))) {

                if (file.isFile()) {
                    fileInfo.setIsDir(0);
                }
                if (file.isDirectory()) {
                    fileInfo.setIsDir(1);
                    fileInfo.setType("folder");
                } else {
                    fileInfo.setType(getTypePart(file.getName()));
                }
                if (file.isHidden()) {
                    fileInfo.setIsDir(-1);
                }
                fileInfo.setName(file.getName());
                fileInfo.setIsDir(0);
            }
            fileInfo.setName(file.getName());
            fileInfo.setSize(file.length());
            fileInfo.setDate(new Date(file.lastModified()));
            fileInfo.setAbsolutePath(file.getAbsolutePath());
            if (!StringUtil.isNull(decreasePath)) {
                fileInfo.setDecreasePath(StringUtil.replace(getDecrease(folder, decreasePath), "\\", "/"));
            }
            fileList.add(fileInfo);
        }
        FileInfo file1, file2, file3;
        for (int i = 0; i < fileList.size() - 1; i++) {
            for (int j = i + 1; j < fileList.size(); j++) {
                file1 = fileList.get(i);
                file2 = fileList.get(j);
                if (order == null) {
                    if (file1.getDate().compareTo(file2.getDate()) < 0) {
                        file3 = fileList.get(j);
                        fileList.set(j, fileList.get(i));
                        fileList.set(i, file3);
                    }
                } else if ("name".equalsIgnoreCase(order)) {
                    if (file1.getName().compareTo(file2.getName()) > 0) {
                        file3 = fileList.get(j);
                        fileList.set(j, fileList.get(i));
                        fileList.set(i, file3);
                    }
                } else if ("size".equalsIgnoreCase(order)) {
                    if (file1.getSize() > file2.getSize()) {
                        file3 = fileList.get(j);
                        fileList.set(j, fileList.get(i));
                        fileList.set(i, file3);
                    }
                } else if (file1.getDate().compareTo(file2.getDate()) < 0) {
                    file3 = fileList.get(j);
                    fileList.set(j, fileList.get(i));
                    fileList.set(i, file3);
                }
            }
        }
        return fileList;
    }

    /**
     * 得到指定文件类型的列表
     *
     * @param folder 目录
     * @param fen    分割号
     * @param type   要得到的类型
     * @param chid   是否包含子目录
     * @return String 返回的目录列表
     */
    static public String getFileList(String folder, String fen, String type, boolean chid) {
        StringBuilder file = new StringBuilder(StringUtil.empty);
        File dir = new File(folder);
        if (!dir.exists()) {
            return StringUtil.empty;
        }
        File[] fileList = dir.listFiles();
        int I;
        if (fileList != null) {
            for (I = 0; I < fileList.length; I++) {
                if (fileList[I].isFile()) {
                    if (type == null || StringUtil.ASTERISK.equalsIgnoreCase(type)) {
                        file.append(fen).append(fileList[I].toString());
                    } else if (type.toLowerCase().contains(getTypePart(fileList[I].getName()).toLowerCase())) {
                        file.append(fen).append(fileList[I].toString());
                    }
                } else if (fileList[I].isDirectory()) {
                    if (chid) {
                        file.append(getFileList(fileList[I].toString(), fen, type, chid));
                    }
                }
            }
        }
        return file.toString();
    }

    static public List<File> getFileList(File dir, String[] types, boolean chid) {
        List<File> result = new ArrayList<File>();
        if (!dir.exists()) {
            return new ArrayList<File>(0);
        }
        File[] fileList = dir.listFiles();
        int I;
        if (fileList != null) {
            for (I = 0; I < fileList.length; I++) {
                if (fileList[I].isFile() && ArrayUtil.inArray(types, getTypePart(fileList[I].getName()), true)) {
                    result.add(fileList[I]);
                } else if (fileList[I].isDirectory() && chid) {
                    result.addAll(getFileList(fileList[I], types, chid));
                }
            }
        }
        return result;
    }

    /**
     * 目录是否存在
     *
     * @param fileName 文件名称
     * @return 是否存在
     */
    static public boolean hasPath(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return false;
        }
        File file = new File(fileName);
        return file.exists() || fileName.contains(":/") && fileName.indexOf(":/") <= 3 || fileName.startsWith("/");
    }


    /**
     * 得到两个路径的差
     *
     * @param path1 路径1
     * @param path2 路径2
     * @return String  得到两个路径的差
     */
    static public String getDecrease(String path1, String path2) {
        if (path1 == null) {
            return StringUtil.empty;
        }
        if (path2 == null) {
            return StringUtil.empty;
        }
        if (path1.length() == path2.length()) {
            return StringUtil.empty;
        }
        if (path2.length() > path1.length()) {
            return path2.substring(path1.length());
        } else {
            return path1.substring(path2.length());
        }
    }

    /**
     * 修复相对路径
     *
     * @param fileName 文件名称
     * @param basePath 根路径
     * @return String 修复相对路径
     */
    static public String fixPath(String fileName, String basePath) {
        String result;
        if (StringUtil.isNull(fileName)) {
            result = basePath;
        } else if (hasPath(fileName)) {
            result = fileName;
        } else {
            result = basePath + fileName;
        }

        if (SystemUtil.OS == SystemUtil.WINDOWS) {
            if (result.startsWith("/")) {
                result = result.substring(1);
            }
        }
        return result;
    }

    /**
     * 添加文件到文件数组
     *
     * @param files 文件数组
     * @param file  文件
     * @return 文件数组
     */
    static public File[] append(File[] files, File file) {
        if (files == null) {
            File[] resultfiles = new File[1];
            resultfiles[0] = file;
            return resultfiles;
        } else {
            File[] resultfiles = new File[files.length + 1];
            System.arraycopy(files, 0, resultfiles, 0, files.length);
            resultfiles[files.length] = file;
            return resultfiles;
        }
    }

    /**
     * 得到盘符
     *
     * @param fileName 文件路径
     * @return String 得到盘符
     */
    static public String getDiskVolume(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return StringUtil.empty;
        }

        String dir = mendPath(fileName);
        if (dir.contains(":/")) {
            dir = dir.substring(0, dir.indexOf(":/"));
            if (dir.startsWith("/")) {
                dir = dir.substring(1) + ":/";
            } else {
                dir = StringUtil.substringBefore(dir, ":") + ":/";
            }
            if (StringUtil.countMatches(dir, "/") > 1) {
                if (dir.startsWith("/")) {
                    dir = "/" + StringUtil.substringBefore(dir.substring(1), "/") + "/";
                }
            }
        } else {
            dir = StringUtil.substringBefore(dir.substring(1), "/") + "/";
        }

        if (StringUtil.countMatches(dir, "/") > 1) {
            dir = dir.substring(0, dir.indexOf("/")) + "/";
        }
        return dir;
    }

    /**
     * ,使用jconfig
     *
     * @param folder 得到目录占用空间
     * @return 目录占用 单位 B
     * @throws IllegalArgumentException 异常
     */
    public static long getFolderSize(File folder)
            throws IllegalArgumentException {

        if (folder == null || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid   folder");
        }
        File[] list = folder.listFiles();
        if (list == null || list.length < 1) {
            return 0;
        }
        //Get   size
        long folderSize = 0;
        for (File f : list) {
            if (f.isDirectory()) {
                folderSize += getFolderSize(f);
            } else if (f.isFile()) {
                folderSize += f.length();
            }
        }
        return folderSize;
    }


    /**
     * @param folder 目录
     * @return 返回所有保护子目录的目录列表
     * @throws IllegalArgumentException 异常
     */
    public static List<File> getFolderList(File folder) throws IllegalArgumentException {
        if (folder == null || !folder.isDirectory()) {
            return new ArrayList<>(0);
        }
        List<File> result = new LinkedList<>();
        result.add(folder);
        File[] list = folder.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    result.addAll(getFolderList(f));
                }
            }
        }
        return result;
    }

    /**
     * @param folder 目录
     * @return 返回子目录列表,只返回一级
     * @throws IllegalArgumentException 异常
     */
    public static List<File> getFirstChildFolder(File folder) throws IllegalArgumentException {
        if (folder == null || !folder.isDirectory()) {
           return new ArrayList<>(0);
        }
        List<File> result = new LinkedList<>();
        File[] list = folder.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    /**
     * @param name 文件名称
     * @return 判断是否为通配符号的文件名称
     */
    public static boolean isPatternFileName(String name) {
        return name != null && (name.contains("#") || name.contains(StringUtil.ASTERISK) || name.contains("?"));
    }

    /**
     * @param dir 目录
     * @return 扫描包路径
     */
    public static String[] scanClass(String dir) {
        URL url = ClassUtil.getResource(StringUtil.replace(dir, StringUtil.DOT, "/"));
        String path = "";
        if (url != null) {
            path = url.getPath();
        }
        if (StringUtil.isNull(path)) {
            url = ClassUtil.getResource(StringUtil.replace(dir, StringUtil.DOT, "/"));
        }
        if (url != null) {
            path = url.getPath();
        }
        if (!StringUtil.isNull(path)) {
            path = new File(path).getAbsolutePath();
        }

        String startPath = "";
        URL startUrl = ClassUtil.getResource("/");
        if (startUrl != null) {
            startPath = startUrl.getPath();
        }
        if (StringUtil.isNull(startPath)) {
            startUrl = ClassUtil.getResource("");
            if (startUrl != null) {
                startPath = startUrl.getPath();
            }
        }
        File classStartDir = new File(startPath);
        startPath = classStartDir.getAbsolutePath();

        List<File> files = getPatternFiles(path, "*.class");
        List<String> list = new ArrayList<>();
        for (File f : files) {
            String fileName = f.getAbsolutePath();
            String tempName;
            if (isZipPackageFile(fileName)) {
                tempName = StringUtil.substringBetween(fileName, "!", ".class");
                if (tempName != null && (tempName.startsWith("/") || tempName.startsWith("\\"))) {
                    tempName = tempName.substring(1);
                }
            } else {
                tempName = FileUtil.getDecrease(fileName, startPath);
                if (tempName != null && (tempName.startsWith("/") || tempName.startsWith("\\"))) {
                    tempName = tempName.substring(1);
                }
                tempName = StringUtil.substringBeforeLast(tempName, ".class");
            }
            tempName = StringUtil.replace(tempName, "\\", StringUtil.DOT);
            list.add(tempName);
        }
        return list.toArray(new String[0]);
    }

    public static boolean isPatternPath(String dir) {
        if (dir == null) {
            return false;
        }
        for (String mark : pathMarks) {
            if (dir.contains(mark)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 模糊（通配符）文件查找程序
     * 可以根据正则表达式查找
     * @param dir 文件夹名称
     * @param findFileName String 查找文件名，可带*.?进行模糊查询
     * @return 找到的文件
     */
    public static List<File> getPatternFiles(String dir, String findFileName) {
        if (findFileName==null)
        {
            return new ArrayList<>(0);
        }
        if (dir != null && dir.startsWith("file:/")) {
            dir = dir.substring(6);
        }

        //路径里边有空格
        if (dir!=null&&dir.contains("%20"))
        {
            dir = URLUtil.getUrlDecoder(dir, StandardCharsets.UTF_8.name());
        }

        //dir = FileUtil.mendPath(dir);
        //开始的文件夹
        String s = findFileName.replace(StringUtil.DOT, "#");
        s = s.replaceAll("#", "\\\\.");
        s = s.replace('*', '#');
        s = s.replaceAll("#", ".*");
        s = s.replace("?", "#");
        s = s.replaceAll("#", ".?");
        s = "^" + s + "$";
        Pattern p = Pattern.compile(s);
        if (dir==null)
        {
            List<File> result = new ArrayList<>();
            List<File> jarList = ClassUtil.getRunJarList();
            for (File searchFile:jarList) {
                //排除系统库和maven库
                if (isNoSearchJar(searchFile.getPath())) {
                    continue;
                }
                if (isPatternFileName(findFileName))
                {
                    List<File> searchList = filePattern(new File(searchFile.getPath()), null, p);
                    if (!ObjectUtil.isEmpty(searchList))
                    {
                        result.addAll(searchList);
                    }
                } else
                {
                    try {
                        Path apkFile = Paths.get(searchFile.getPath());
                        FileSystem fs = FileSystems.newFileSystem(apkFile, null);
                        Path dexFile = fs.getPath(findFileName);
                        if (Files.exists(dexFile))
                        {
                            result.add(dexFile.toFile());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return filePattern(new File(dir), dir, p);
    }

    private static boolean isNoSearchJar(String file)
    {
        for (String tag:NO_SEARCH_JAR)
        {
            if (file!=null&&file.toLowerCase().contains(tag))
            {
                return true;
            }
        }
        return false;
    }
    /**
     * @param file 起始文件夹 或者jar文件
     * @param dir  在那个路径里边查询
     * @param p    Pattern 匹配类型
     * @return 列表路径过滤
     */
    public static List<File> filePattern(File file, String dir, Pattern p) {
        if (file == null) {
            return new ArrayList<>(0);
        }

        if (file.getPath().toLowerCase().contains(".jar!")|| ArrayUtil.inArray(new String[]{"jar","war","zip"},FileUtil.getTypePart(file.getName()),true)) {

            String path = file.getPath();
            if (path.contains(".jar!"))
            {
                path = StringUtil.substringBefore(file.getPath(),".jar!") + ".jar";
            }
            try (JarInputStream zis = new JarInputStream(new FileInputStream(path))) {
                List<File> list = new ArrayList<>();
                JarEntry e;
                while ((e = zis.getNextJarEntry()) != null) {
                    if (e.isDirectory() || "..\\".equals(e.getName()) || "../".equals(e.getName())) {
                        continue;
                    }
                    Matcher fMatcher = p.matcher(FileUtil.getFileName(e.getName()));
                    if (fMatcher.matches()) {
                        list.add(new File(path + "!/" + e.getName()));
                    }
                }
                zis.closeEntry();
                return list;
            } catch (Exception e) {
                log.error("path=" + path + " file=" + file, e);
            }
        } else {
            if (file.isFile()) {
                String fileDir = FileUtil.getPathPart(file.getPath());
                String findDir = FileUtil.mendPath(StringUtil.substringBeforeLast(dir, "/"));
                findDir = FileUtil.getDecrease(fileDir, findDir);
                String checkName = findDir + FileUtil.getFileName(file.getName());
                Matcher fMatcherDir = p.matcher(checkName);
                Matcher fMatcher = p.matcher(file.getName());
                if (findDir != null && fileDir.endsWith(findDir) && fMatcher.matches() || fMatcherDir.matches()) {
                    List<File> list = new ArrayList<>();
                    list.add(file);
                    return list;
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    List<File> list = new ArrayList<>();
                    for (File file1 : files) {
                        List<File> pathList = filePattern(file1, dir, p);
                        if (!ObjectUtil.isEmpty(pathList)) {
                            list.addAll(pathList);
                        }
                    }
                    return list;
                }
            }

        }
        return new ArrayList<>(0);
    }

    /**
     * 文件目录比较
     *
     * @param folder1 目录1
     * @param folder2 目录2
     * @param type    比较方式  false:只比较长度  true：比较内容  md5比较
     * @return 返回folder1 中不相同的文件名
     */
    public static String compareFolder(File folder1, File folder2, boolean type) {
        StringBuilder sb = new StringBuilder();
        for (File f1 : Objects.requireNonNull(folder1.listFiles())) {
            if (f1.isDirectory()) {
                sb.append(compareFolder(f1, new File(folder2, f1.getName()), type));
            } else if (f1.isFile()) {
                File f2 = new File(folder2, f1.getName());
                if (!f2.isFile()) {
                    sb.append(f1.getAbsolutePath()).append(StringUtil.CRLF);
                } else {
                    if (!type && f1.length() != f2.length()) {
                        sb.append(f1.getAbsolutePath()).append(StringUtil.CRLF);
                    } else {
                        try {
                            if (!FileUtil.getHash(f1, "MD5").equalsIgnoreCase(FileUtil.getHash(f2, "MD5"))) {
                                sb.append(f1.getAbsolutePath()).append(StringUtil.CRLF);
                            }
                        } catch (Exception e) {
                            sb.append(f1.getAbsolutePath()).append(StringUtil.CRLF);
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param in 输入
     * @return 把文件读入字节数组，读取失败则返回null
     */
    static public byte[] getBytesFromInputStream(InputStream in) {
        try {
            return toByteArray(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * @param input 输入流
     * @return 字符流转化为字节数组
     * @throws IOException 异常
     */
    static private byte[] toByteArray(InputStream input) throws IOException {
        int status = 0;
        final int blockSize = 5000;
        int totalBytesRead = 0;
        int blockCount = 1;
        byte[] dynamicBuffer = new byte[blockSize * blockCount];
        final byte[] buffer = new byte[blockSize];
        boolean endOfStream = false;
        while (!endOfStream) {
            int bytesRead = 0;
            if (input.available() != 0) {
                // data is waiting so read as much as is available
                status = input.read(buffer);
                endOfStream = (status == -1);
                if (!endOfStream) {
                    bytesRead = status;
                }
            } else {
                status = input.read();
                endOfStream = (status == -1);
                buffer[0] = (byte) status;
                if (!endOfStream) {
                    bytesRead = 1;
                }
            }

            if (!endOfStream) {
                if (totalBytesRead + bytesRead > blockSize * blockCount) {
                    // expand the size of the buffer
                    blockCount++;
                    final byte[] newBuffer = new byte[blockSize * blockCount];
                    System.arraycopy(dynamicBuffer, 0,
                            newBuffer, 0, totalBytesRead);
                    dynamicBuffer = newBuffer;
                }
                System.arraycopy(buffer, 0,
                        dynamicBuffer, totalBytesRead, bytesRead);
                totalBytesRead += bytesRead;
            }
        }

        // make a copy of the array of the exact length
        final byte[] result = new byte[totalBytesRead];
        if (totalBytesRead != 0) {
            System.arraycopy(dynamicBuffer, 0, result, 0, totalBytesRead);
        }
        return result;
    }

    /**
     * @param fileName 文件
     * @param start    读取地几行开始
     * @param max      最多几行
     * @return 返回文本行
     */
    public static List<String> readLines(File fileName, int start, int max) {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            int i = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                i++;
                if (start <= i) {
                    lines.add(line);
                }
                if (max != 0 && lines.size() >= max) {
                    break;
                }
            }
            return lines;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * @param folder 目录
     * @param day    天数
     * @param types  文件类型
     * @param need   按照时间排序,保留几个文件
     * @return 删除目录下，X天前的指定类型文件
     */
    public static String deleteFile(File folder, int day, String types, int need) {
        if (StringUtil.isNull(types)) {
            return StringUtil.empty;
        }
        if (day <= 0) {
            return StringUtil.empty;
        }
        if (!folder.isDirectory()) {
            return StringUtil.empty;
        }
        File[] fileList = folder.listFiles();
        sort(fileList, sortDate, false);
        String[] fileType = StringUtil.split(types, StringUtil.SEMICOLON);
        StringBuilder sb = new StringBuilder();
        int t = need;
        for (File aFile : fileList) {
            t--;
            if (t < 0 && aFile.isFile() && aFile.canWrite()) {
                if (DateUtil.compareDay(new Date(aFile.lastModified())) > day) {
                    if (ArrayUtil.inArray(fileType, getTypePart(aFile.getName()), true)) {
                        sb.append(aFile.getPath()).append(StringUtil.CRLF);
                        if (!aFile.delete()) {
                            aFile.deleteOnExit();
                        }
                    }
                }
            }

        }
        return sb.toString().trim();
    }

    /**
     * @param src  文件
     * @param mark 标记
     * @param rule true为升序 ，false为降序
     *             按 date ,name ,size 排序
     */
    public static void sort(File[] src, String mark, boolean rule) {

        if (rule) {//升序
            for (int i = src.length; i > 0; i--) {
                for (int j = 0; j < i - 1; j++) {
                    boolean change = false;
                    if (mark.equalsIgnoreCase(sortName)) {
                        if (src[j].getName().compareTo(src[j + 1].getName()) > 0) {
                            change = true;
                        }
                    } else if (mark.equalsIgnoreCase(sortDate)) {
                        if (src[j].lastModified() > src[j + 1].lastModified()) {
                            change = true;
                        }
                    } else {
                        if (src[j].length() > src[j + 1].length()) {
                            change = true;
                        }
                    }
                    if (change) {
                        File temp = src[j];
                        src[j] = src[j + 1];
                        src[j + 1] = temp;
                    }
                }
            }
        } else {    //降序
            for (int i = src.length; i > 0; i--) {
                for (int j = 0; j < i - 1; j++) {
                    boolean change = false;
                    if (mark.equalsIgnoreCase(sortName)) {
                        if (src[j].getName().compareTo(src[j + 1].getName()) < 0) {
                            change = true;
                        }
                    } else if (mark.equalsIgnoreCase(sortDate)) {

                        if (src[j].lastModified() < src[j + 1].lastModified()) {
                            change = true;
                        }
                    } else {
                        if (src[j].length() < src[j + 1].length()) {
                            change = true;
                        }
                    }
                    if (change) {
                        File temp = src[j];
                        src[j] = src[j + 1];
                        src[j + 1] = temp;
                    }
                }
            }
        }
    }

    /**
     * @param folders  文件目录
     * @param fileName 文件名
     * @return 扫描配置中的路径, 得到匹配的文件
     */
    public static File getFile(String[] folders, String fileName) {
        if (ArrayUtil.isEmpty(folders)) {
            return null;
        }
        for (String path : folders) {
            if (StringUtil.isNull(path)) {
                continue;
            }
            File file = new File(path, fileName);
            if (file.isFile()) {
                return file;
            }
        }
        return null;
    }

    /**
     * @param file 文件名
     * @return 在同一个目录里边创建文件，同名产生序列号
     */
    public static File createFile(File file) {
        File result = file;
        while (result.exists()) {
            String name = FileUtil.getNamePart(file.getName());
            name = name.contains("_") ? StringUtil.substringAfter(name, "_") : "";
            name = name.contains(StringUtil.DOT) ? StringUtil.substringAfter(name, StringUtil.DOT) : "";
            name = FileUtil.getNamePart(file.getName()) + (StringUtil.getNumber(name) + 1);
            result = new File(file.getParent(), name + StringUtil.DOT + FileUtil.getTypePart(file.getName()).toLowerCase());
        }

        return result;
    }


    /**
     * @param list 文件列表
     * @param desc 排序方式   true, 最新的在最前,false 老的在前
     * @return 所有文件(按时间排序)
     */
    public static List<File> getFileDateSort(List<File> list, final boolean desc) {
        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File newFile) {
                    if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    }
                    if (desc) {
                        if (file.lastModified() < newFile.lastModified()) {
                            return 1;
                        } else {
                            return -1;
                        }

                    } else {
                        if (file.lastModified() > newFile.lastModified()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            });
        }
        return list;
    }


    /**
     * @param realPath 路径
     * @param files    空
     * @return 获取目录下所有文件, 包括子目录
     */
    public static List<File> getFileList(String realPath, List<File> files) {
        File realFile = new File(realPath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            assert subfiles != null;
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFileList(file.getPath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * @param name 图片名称
     * @return 得到缩图文件名称
     */
    public static String getThumbnailFileName(String name) {
        String fileName = getFileName(name);
        String namePart = getNamePart(fileName);
        if (!StringUtil.hasLength(namePart)) {
            return StringUtil.empty;
        }
        if (namePart.endsWith(THUMBNAIL_FILE_TYPE)) {
            return name;
        }
        if (namePart.endsWith(PHONE_FILE_TYPE)) {
            return name;
        }
        String newFileName = namePart + THUMBNAIL_FILE_TYPE;
        return getParentPath(name) + newFileName + StringUtil.DOT + getTypePart(name);
    }

    /**
     * 目的是为了压缩图片减少手机流量
     *
     * @param name 图片名称
     * @return 得到手机图片名称
     */
    public static String getMobileFileName(String name) {
        String fileName = getFileName(name);
        String namePart = getNamePart(fileName);
        if (!StringUtil.hasLength(namePart)) {
            return StringUtil.empty;
        }
        if (namePart.endsWith(PHONE_FILE_TYPE)) {
            return name;
        }
        if (namePart.endsWith(THUMBNAIL_FILE_TYPE)) {
            String baseNamePart = namePart.substring(0, namePart.length() - THUMBNAIL_FILE_TYPE.length());
            String newFileName = baseNamePart + PHONE_FILE_TYPE;
            return getParentPath(name) + newFileName + StringUtil.DOT + getTypePart(name);
        }
        String newFileName = namePart + PHONE_FILE_TYPE;
        return getParentPath(name) + newFileName + StringUtil.DOT + getTypePart(name);
    }

    /**
     * @param fileSize 文件大小
     * @return 格式化文件
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    final static public String KEY_classPath = "classpath:";
    final static public String KEY_classPathEx = "classpath*:";
    final static public String KEY_libraryPath = "java.library.path:";
    final static public String KEY_defaultPath = "defaultpath:";
    final static public String KEY_userPath = "user.dir";


    /**
     * 为了方便得到配置的文件信息，这里提供一个方法函数，能够自动匹配文件路径
     *
     * @param paths    路径
     * @param loadFile 需要载入的文件名称，支持通配符
     * @return 得到文件，如果为空，表示没有找到文件
     */
    static public File scanFile(String[] paths, String loadFile) {

        //找文件路径 begin
        if (loadFile.toLowerCase().startsWith(KEY_classPath)) {
            String tempPath = loadFile.substring(KEY_classPath.length());
            URL url = Thread.currentThread().getContextClassLoader().getResource(tempPath);
            if (url == null) {
                url = Thread.currentThread().getContextClassLoader().getResource(tempPath.substring(1));
            }
            if (url != null) {
                File file = new File(url.getPath());
                if (FileUtil.isFileExist(file)) {
                    return file;
                }
            }
        }
        else if (loadFile.toLowerCase().startsWith(KEY_classPathEx)) {
            String find = loadFile.substring(KEY_classPathEx.length());
            URL url = Thread.currentThread().getContextClassLoader().getResource("");
            if (url == null) {
                try {
                    url = new URL(System.getProperty(KEY_userPath));
                } catch (MalformedURLException e) {
                    //...
                }
            }
            if (url != null) {
                String findDir = new File(url.getPath()).getPath();
                List<File> files = FileUtil.getPatternFiles(findDir, find);
                if (!ObjectUtil.isEmpty(files)) {
                    return files.get(0);
                }
            }
        } else if (loadFile.toLowerCase().startsWith(KEY_libraryPath)) {
            String findFile = loadFile.substring(KEY_libraryPath.length());
            String[] findDirs = StringUtil.split(System.getProperty(KEY_libraryPath), StringUtil.SEMICOLON);
            for (String path : findDirs) {
                File file = new File(path, findFile);
                if (FileUtil.isFileExist(file)) {
                    return file;
                }

                String findDir = new File(path).getPath();
                List<File> files = FileUtil.getPatternFiles(findDir, findFile);
                if (!ObjectUtil.isEmpty(files)) {
                    return files.get(0);
                }
            }
        }

        if (loadFile.toLowerCase().startsWith(KEY_defaultPath)) {
            loadFile = loadFile.substring(KEY_defaultPath.length());
        }


        if (paths!=null && !loadFile.toLowerCase().contains(".jar!"))
        {
            for (String path : paths)
            {
                if (StringUtil.isNull(path))
                {
                    continue;
                }
                File file = new File(loadFile);
                if (file.isFile()) {
                    return file;
                }

                file = new File(path, loadFile);
                if (file.isFile()) {
                    return file;
                }

                List<File> files = FileUtil.getPatternFiles(path, loadFile);
                if (!ObjectUtil.isEmpty(files)) {
                    return files.get(0);
                }
            }
        }
        else if (FileUtil.isFileExist(loadFile))
        {
            //jar 文件里边
            return new File(loadFile);
        }

        URL url =  Environment.class.getResource("/Boot-inf/classes/" + FileUtil.getFileName(loadFile));
        if (url!=null)
        {
            return new File(url.getPath());
        }

        url =  Environment.class.getResource("/resources/" + loadFile);
        if (url!=null)
        {
            return new File(url.getPath());
        }

        url =  Environment.class.getResource("/resources/template/" + loadFile);
        if (url!=null)
        {
            return new File(url.getPath());
        }
        url =  Environment.class.getResource("/resources/reslib/" + loadFile);
        if (url!=null)
        {
            return new File(url.getPath());
        }
        url =  Environment.class.getResource(loadFile);
        if (url!=null)
        {
            return new File(url.getPath());
        }

        return null;
    }


    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        } else {
            String pathToUse = StringUtil.replace(path, "\\", "/");
            int prefixIndex = pathToUse.indexOf(":");
            String prefix = "";
            if (prefixIndex != -1) {
                prefix = pathToUse.substring(0, prefixIndex + 1);
                if (prefix.contains("/")) {
                    prefix = "";
                } else {
                    pathToUse = pathToUse.substring(prefixIndex + 1);
                }
            }

            if (pathToUse.startsWith("/")) {
                prefix = prefix + "/";
                pathToUse = pathToUse.substring(1);
            }

            String[] pathArray = delimitedListToStringArray(pathToUse, "/");
            LinkedList<String> pathElements = new LinkedList<String>();
            int tops = 0;

            int i;
            for (i = pathArray.length - 1; i >= 0; --i) {
                String element = pathArray[i];
                if (!StringUtil.DOT.equals(element)) {
                    if ("..".equals(element)) {
                        ++tops;
                    } else if (tops > 0) {
                        --tops;
                    } else {
                        pathElements.add(0, element);
                    }
                }
            }

            for (i = 0; i < tops; ++i) {
                pathElements.add(0, "..");
            }

            return prefix + collectionToDelimitedString(pathElements, "/");
        }
    }

    private static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    private static String collectionToDelimitedString(Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }


    private static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        } else if (delimiter == null) {
            return new String[]{str};
        } else {
            List<String> result = new ArrayList();
            int pos;
            if ("".equals(delimiter)) {
                for (pos = 0; pos < str.length(); ++pos) {
                    result.add(StringUtil.deleteAny(str.substring(pos, pos + 1), charsToDelete));
                }
            } else {
                int delPos;
                for (pos = 0; (delPos = str.indexOf(delimiter, pos)) != -1; pos = delPos + delimiter.length()) {
                    result.add(StringUtil.deleteAny(str.substring(pos, delPos), charsToDelete));
                }

                if (str.length() > 0 && pos <= str.length()) {
                    result.add(StringUtil.deleteAny(str.substring(pos), charsToDelete));
                }
            }
            return result.toArray(new String[result.size()]);
        }
    }

    private static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
        if (ObjectUtil.isEmpty(coll)) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator it = coll.iterator();
            while (it.hasNext()) {
                sb.append(prefix).append(it.next()).append(suffix);
                if (it.hasNext()) {
                    sb.append(delim);
                }
            }

            return sb.toString();
        }
    }

}