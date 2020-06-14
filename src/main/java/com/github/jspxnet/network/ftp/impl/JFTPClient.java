package com.github.jspxnet.network.ftp.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.ftp.FTPInputStream;
import com.github.jspxnet.network.ftp.FTPOutputStream;

import com.github.jspxnet.utils.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

import com.github.jspxnet.network.ftp.IFTPClient;

/**
 * Created by yuan on 2015/4/18 0018.
 */
public class JFTPClient extends FTPClient implements IFTPClient {

    public JFTPClient(int buffer, String encoding) {
        super.setControlEncoding(encoding);
        super.setDataTimeout(DateUtil.SECOND * 30);
        super.setBufferSize(buffer);
    }

    public JFTPClient(String encoding) {
        this(1024, encoding);
    }

    /**
     * jspx.net 开发的ftp服务器扩展
     * 是否需要重新登录
     *
     * @return 判断服务器编码，协调编码
     * @throws java.io.IOException 异常
     */
    @Override
    public boolean concertEncoding() throws IOException {
        String defEncode = getControlEncoding();
        super.sendCommand("ENCODE");
        if (getReplyCode() == 215) {
            String encoding = StringUtil.substringAfter(getReplyString(), " ").trim();
            if (!defEncode.equalsIgnoreCase(encoding)) {
                setControlEncoding(encoding);
                return true;
            }
        } else {
            super.sendCommand("OPTS " + defEncode, "ON");
            return getReplyCode() != 250;
        }
        return false;
    }


    @Override
    public void setCertificate(String certFile, String keyPassword) {

    }

    /**
     * @param host     主机 有端口是用: 跟在后边
     * @param user     登录用户
     * @param password 密码
     * @return 是否登录成功
     */

    @Override
    public boolean login(String host, String user, String password) {
        int port = 21;
        if (host.contains(":")) {
            port = StringUtil.toInt(StringUtil.substringAfterLast(host, ":"), 21);
            host = StringUtil.substringBefore(host, ":");
        }

        try {
            if (!isConnected()) {
                super.connect(host, port);
            }

            return super.login(user, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 本机多网卡的时候可以选择使用
     *
     * @param host         主机 有端口是用: 跟在后边
     * @param user         登录用户
     * @param password     密码
     * @param localAddress 本机地址
     * @param localPort    本机端口
     * @return 是否登录成功
     * @throws IOException 异常
     */
    @Override
    public boolean login(String host, String user, String password, String localAddress, int localPort) throws IOException {
        int port = 21;
        if (host.contains(":")) {
            port = StringUtil.toInt(StringUtil.substringAfterLast(host, ":"), 21);
            host = StringUtil.substringBefore(host, ":");
        }
        super.connect(host, port, InetAddress.getByName(localAddress), localPort);

        return super.login(user, password);
    }

    /**
     * @return 默认MD5 当前目录
     * @throws IOException 异常
     */
    @Override
    public Map<String, HashFile> getHashFileList() throws IOException {
        return getHashFileList(null, null);
    }


    /**
     * @param hashType 方式AUTO sha1 md5
     * @param pathName 路径
     * @return 得到FTP服务器上文件的hash列表值, 只能用在本人开发的JFTP扩展协议上 hlist
     * @throws IOException 异常
     */
    @Override
    public Map<String, HashFile> getHashFileList(String hashType, String pathName) throws IOException {
        if (StringUtil.isNull(pathName)) {
            pathName = StringUtil.empty;
        }
        if (StringUtil.isNull(hashType)) {
            hashType = "AUTO";
        }
        if (StringUtil.isNull(pathName)) {
            pathName = ".";
        }
        if (sendCommand("HLIST " + hashType, pathName) == 213) {
            String reply = getReplyString();
            String remoteHashFileName = StringUtil.substringAfter(reply, " ").trim();
            if ("0".equalsIgnoreCase(remoteHashFileName) || "one".equalsIgnoreCase(remoteHashFileName) || StringUtil.isNull(remoteHashFileName)) {
                //空目录的情况
                return new HashMap<String, HashFile>();
            }
            super.setFileType(FTP.BINARY_FILE_TYPE);
            super.enterLocalPassiveMode();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream is = retrieveFileStream(remoteHashFileName);
            if (is != null) {
                if (StreamUtil.copy(new FTPInputStream(is, this), out, getBufferSize())) {
                    Map<String, HashFile> result = new Hashtable<String, HashFile>();
                    String[] lines = StringUtil.split(StringUtil.convertCR(out.toString(getControlEncoding())), StringUtil.CR);
                    for (String line : lines) {
                        if (StringUtil.isNull(line)) {
                            continue;
                        }
                        HashFile hFile = new HashFile();
                        hFile.setHash(StringUtil.substringBefore(line, " "));
                        int x = line.indexOf(" ");
                        String str = line.substring(x + 1).trim();
                        hFile.setDateTime(StringUtil.toLong(StringUtil.substringBefore(str, " ")));
                        hFile.setName(StringUtil.substringAfter(str, " "));
                        result.put(hFile.getName(), hFile);
                    }
                    return result;
                } else {
                    throw new IOException("server not send file hash file");
                }
            }
        }
        return new HashMap<String, HashFile>();
    }

    /**
     * @param pathName 路径
     * @return 得到服务器的目录列表
     * @throws IOException 异常
     */
    @Override
    public List<String> getFolderList(String pathName) throws IOException {
        int code = 0;
        if (!StringUtil.hasLength(pathName)) {
            code = sendCommand("FLIST");
        } else {
            code = sendCommand("FLIST " + pathName);
        }
        if (code == 213) {
            String remoteHashFileName = StringUtil.trim(StringUtil.substringAfterLast(super.getReplyString(), " "));

            setFileType(FTP.BINARY_FILE_TYPE);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream is = retrieveFileStream(remoteHashFileName);
            if (is != null) {
                StreamUtil.copy(new FTPInputStream(is, this), out, getBufferSize());
            }
            List<String> result = new ArrayList<String>();

            String[] lines = StringUtil.split(StringUtil.convertCR(out.toString(getControlEncoding())), StringUtil.CR);
            for (String line : lines) {
                if (StringUtil.isNull(line)) {
                    continue;
                }
                result.add(line);
            }
            return result;
        }
        return null;
    }


    /**
     * @param pathName 本人写的服务器特有
     * @return 删除是否成功
     * @throws IOException 异常
     */
    @Override
    public boolean deleteFolder(String pathName, boolean isJspxFtps) throws IOException {
        if (StringUtil.isNull(pathName)) {
            return false;
        }
        int result = 550;
        if (isJspxFtps) {
            sendCommand("RMDIR", pathName);
            if ((result = getReplyCode()) == 250) {
                return true;
            }
        }
        if (result == 550) {
            changeWorkingDirectory(pathName);
            FTPFile[] ftpFiles = listFiles();
            for (FTPFile fptFile : ftpFiles) {
                if (fptFile.isDirectory()) {
                    deleteFolder(fptFile.getName(), false);
                }
                if (fptFile.isFile()) {
                    if (!deleteFile(fptFile.getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public long getLastModified(String pathName) throws IOException {
        sendCommand("MDTM", pathName);
        if (getReplyCode() == 213) {
            String reply = StringUtil.substringAfter(getReplyString(), " ").trim();
            if ("0".equals(reply)) {
                return 0;
            }
            String dateStr = reply;
            if (reply.toUpperCase().contains("UTC")) {
                dateStr = StringUtil.trim(StringUtil.substringBefore(reply, "UTC"));
            }

            long timesLong = 0;
            SimpleDateFormat dateFormat2 = new SimpleDateFormat(DateUtil.UTC_FTP_FORMAT);
            try {
                Date date = dateFormat2.parse(dateStr);
                timesLong = date.getTime();
                if (reply.toUpperCase().contains("UTC")) {
                    final java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTimeInMillis(timesLong);
                    cal.add(java.util.Calendar.MILLISECOND, cal.get(java.util.Calendar.ZONE_OFFSET));
                    return cal.getTimeInMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 递归创建远程服务器目录
     *
     * @param pathName 远程服务器文件绝对路径
     * @return 目录创建是否成功
     * @throws IOException 异常
     */
    @Override
    public boolean createDir(String pathName) throws IOException {
        if (!StringUtil.hasLength(pathName)) {
            return false;
        }
        if (!isFolder(pathName) && makeDirectory(pathName)) {
            return true;
        }

        String directory = pathName.substring(0, pathName.lastIndexOf("/") + 1);
        if (!"/".equalsIgnoreCase(directory) && !changeWorkingDirectory(directory)) {
            //如果远程目录不存在，则递归创建远程服务器目录
            int start;
            int end;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = pathName.substring(start, end);
                if (!changeWorkingDirectory(subDirectory)) {
                    if (makeDirectory(subDirectory) && changeWorkingDirectory(subDirectory)) {
                        //
                    } else {
                        return false;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                //检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return true;
    }


    /**
     * quote folder /investment/jspx
     *
     * @param pathName 路径
     * @return 判断是否为目录
     * @throws IOException 异常
     */
    @Override
    public boolean isFolder(String pathName) throws IOException {
        sendCommand("FOLDER", pathName);
        if (getReplyCode() == 213) {
            return StringUtil.toBoolean(StringUtil.substringAfter(getReplyString(), " ").trim());
        } else {
            String lsDir;
            String comDir;
            if (pathName.contains("/")) {
                if (pathName.endsWith("/")) {
                    lsDir = StringUtil.substringBeforeLast(pathName.substring(0, pathName.length() - 1), "/");
                    comDir = StringUtil.substringAfterLast(pathName.substring(0, pathName.length() - 1), "/");
                } else {
                    lsDir = StringUtil.substringBeforeLast(pathName, "/");
                    comDir = StringUtil.substringBeforeLast(pathName, "/");
                }
            } else {
                lsDir = pathName;
                comDir = StringUtil.replace(pathName, "/", "");
            }
            return isFolder(listFiles(lsDir), comDir);
        }
    }


    /**
     * @param ftpFiles ftp列表
     * @param pathName 路径
     * @return 是否存在目录列表
     */
    static private boolean isFolder(FTPFile[] ftpFiles, String pathName) {
        if (ftpFiles == null) {
            return false;
        }
        if (pathName == null || "".equals(pathName)) {
            return true;
        }
        for (FTPFile file : ftpFiles) {
            if (file.getName().equals(pathName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param pathName  路径
     * @param localPath 本地路径
     * @param filter    过滤不处理的文件后缀
     * @return 返回下载的文件数量
     * @throws IOException 异常
     */
    @Override
    public int downloadFolder(String pathName, String localPath, String[] filter, boolean breakPoint) throws IOException {
        pathName = FileUtil.mendPath(pathName);
        localPath = FileUtil.mendPath(localPath);
        FileUtil.makeDirectory(localPath);
        int result = 0;
        FTPFile[] ftpFiles = super.listFiles(pathName);
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isDirectory()) {
                result = result + downloadFolder(pathName + ftpFile.getName(), localPath + ftpFile.getName(), filter, breakPoint);
            }
            if (ftpFile.isFile()) {
                String fileType = FileUtil.getTypePart(ftpFile.getName());
                if (ArrayUtil.inArray(filter, fileType, true)) {
                    continue;
                }
                if (download(pathName + ftpFile.getName(), new File(localPath, ftpFile.getName()), breakPoint)) {
                    result++;
                }
            }
        }
        return result;
    }


    /**
     * @param remoteFile 远程文件
     * @param localFile  本地文件
     * @return 下载文件
     */
    @Override
    public boolean download(String remoteFile, File localFile, boolean breakPoint) throws IOException {
        return download(remoteFile, localFile, null, breakPoint);
    }

    /**
     * @param remoteFile 远程文件
     * @return 得到远程文件大小，方便后边断点续传
     * @throws IOException 异常
     */
    @Override
    public long getFileSize(String remoteFile) throws IOException {
        if (213 == sendCommand("SIZE", remoteFile)) {
            return StringUtil.toLong(StringUtil.trim(StringUtil.substringAfter(getReplyString(), " ")));
        }
        return 0;
    }

    @Override
    public boolean setRemoteLastModified(String remoteFile, long localModified) throws IOException {

        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(localModified);
        cal.add(java.util.Calendar.MILLISECOND, -cal.get(java.util.Calendar.ZONE_OFFSET));
        Date date = new Date(cal.getTimeInMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.UTC_FTP_FORMAT, Locale.ENGLISH);
        String dateStr = dateFormat.format(date);
        return (200 == sendCommand("SITE UTIME", URLUtil.getURLEncoder(remoteFile, Environment.defaultEncode) + " " + dateStr + " UTC"));
    }


    //SITE UTIME /dtmp/收款收据.docx 20130731052835 20130731052835 20130731052835 UTC

    /**
     * @param remoteFile 远程文件
     * @param localFile  本地文件
     * @param event      流量事件
     * @return 是否成功
     */
    @Override
    public boolean download(String remoteFile, File localFile, StreamEvent event, boolean breakPoint) throws IOException {

        long ftpFileSize;
        if (breakPoint && localFile.isFile()) {
            ftpFileSize = getFileSize(remoteFile);
            if (localFile.length() == ftpFileSize && localFile.length() != 0) {
                return true;
            } else if (localFile.length() > 0 && localFile.length() < ftpFileSize) {
                //开始继传
                long lastModified = getLastModified(remoteFile);
                setRestartOffset(localFile.length());
                super.setFileType(FTP.BINARY_FILE_TYPE);
                super.enterLocalPassiveMode();

                InputStream is = retrieveFileStream(remoteFile);
                return (is != null && StreamUtil.copy(new FTPInputStream(is, this), new FileOutputStream(localFile, true), getBufferSize(), event)) && (lastModified != 0 && localFile.setLastModified(lastModified));
            } else if (localFile.length() > ftpFileSize) {
                FileUtil.delete(localFile);
            }
        }
        long lastModified = getLastModified(remoteFile);
        setRestartOffset(0);
        super.setFileType(FTP.BINARY_FILE_TYPE);
        super.enterLocalPassiveMode();
        InputStream is = retrieveFileStream(remoteFile);
        return is != null && StreamUtil.copy(new FTPInputStream(is, this), new FileOutputStream(localFile), getBufferSize(), event) && (lastModified != 0 && localFile.setLastModified(lastModified));
    }


    /**
     * 上传文件
     *
     * @param remoteFile 远程文件名
     * @param localFile  本地文件
     * @return 是否成功
     */
    @Override
    public boolean upload(String remoteFile, File localFile, boolean breakPoint) throws IOException {
        return upload(remoteFile, localFile, null, breakPoint);
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件名
     * @param localFile  本地文件
     * @param event      流量事件
     * @return 是否成功
     */
    @Override
    public boolean upload(String remoteFile, File localFile, StreamEvent event, boolean breakPoint) throws IOException {
        if (!StringUtil.hasLength(remoteFile)) {
            return false;
        }
        if (localFile.isDirectory() && !isFolder(remoteFile)) {
            return createDir(remoteFile);
        }
//判断是否为目录

        long remoteSize = getFileSize(remoteFile);
        if (breakPoint && remoteSize > 0 && remoteSize < localFile.length()) {

//续传
            setFileType(FTP.BINARY_FILE_TYPE);
            InputStream is = new FileInputStream(localFile);
//断点续传
            is.skip(remoteSize);
            setRestartOffset(remoteSize);
            OutputStream out = appendFileStream(remoteFile);
            return out != null && localFile.isFile() && localFile.canRead() && StreamUtil.copy(is, new FTPOutputStream(out, this), getBufferSize(), event) && setRemoteLastModified(remoteFile, localFile.lastModified());

        } else {
            deleteFile(remoteFile);
            setFileType(FTP.BINARY_FILE_TYPE);
            OutputStream out = storeFileStream(remoteFile);
            return (localFile.isFile() && localFile.canRead() && out != null && StreamUtil.copy(new FileInputStream(localFile), new FTPOutputStream(out, this), getBufferSize(), event))
                    && setRemoteLastModified(remoteFile, localFile.lastModified());
        }
    }


    /**
     * 关闭
     */
    @Override
    public void bye() {
        if (isConnected()) {
            try {
                logout();
                disconnect();
            } catch (IOException e) {
                //..
            }

        }
    }


    @Override
    public void setKeyManager(KeyManager keyManager) {

    }

    @Override
    public void setEnabledSessionCreation(boolean isCreation) {

    }

    @Override
    public boolean getEnableSessionCreation() {
        return false;
    }

    @Override
    public void setNeedClientAuth(boolean isNeedClientAuth) {

    }

    @Override
    public boolean getNeedClientAuth() {
        return false;
    }

    @Override
    public void setWantClientAuth(boolean isWantClientAuth) {

    }

    @Override
    public boolean getWantClientAuth() {
        return false;
    }

    @Override
    public void setUseClientMode(boolean isClientMode) {

    }

    @Override
    public boolean getUseClientMode() {
        return false;
    }

    @Override
    public void setEnabledCipherSuites(String[] cipherSuites) {

    }

    @Override
    public String[] getEnabledCipherSuites() {
        return new String[0];
    }

    @Override
    public void setEnabledProtocols(String[] protocolVersions) {

    }

    @Override
    public String[] getEnabledProtocols() {
        return new String[0];
    }

    @Override
    public void execPBSZ(long pbsz) throws IOException {

    }

    @Override
    public void execPROT(String prot) {

    }

    @Override
    public TrustManager getTrustManager() {
        return null;
    }

    @Override
    public void setTrustManager(TrustManager trustManager) {

    }


}