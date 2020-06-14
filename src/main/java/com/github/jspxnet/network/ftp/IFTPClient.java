package com.github.jspxnet.network.ftp;

import com.github.jspxnet.network.ftp.impl.HashFile;
import com.github.jspxnet.utils.StreamEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 2015/4/18 0018.
 * FTP代理接口
 */
public interface IFTPClient {

    java.lang.String getControlEncoding();

    void setCertificate(String certFile, String keyPassword) throws Exception;

    boolean concertEncoding() throws IOException;

    boolean login(String host, String user, String password);

    boolean login(String host, String user, String password, String localAddress, int localPort) throws IOException;

    Map<String, HashFile> getHashFileList() throws IOException;

    Map<String, HashFile> getHashFileList(String hashType, String pathName) throws IOException;

    List<String> getFolderList(String pathName) throws IOException;

    boolean deleteFolder(String pathName, boolean isJspxFtps) throws IOException;

    boolean createDir(String pathName) throws IOException;

    boolean isFolder(String pathName) throws IOException;

    int downloadFolder(String pathName, String localPath, String[] filter, boolean breakPoint) throws IOException;

    boolean download(String remoteFile, File localFile, boolean breakPoint) throws IOException;

    long getFileSize(String remoteFile) throws IOException;

    boolean download(String remoteFile, File localFile, StreamEvent event, boolean breakPoint) throws IOException;

    boolean upload(String remoteFile, File localFile, boolean breakPoint) throws IOException;

    boolean upload(String remoteFile, File localFile, StreamEvent event, boolean breakPoint) throws IOException;

    boolean completePendingCommand() throws java.io.IOException;

    long getLastModified(String pathName) throws IOException;

    void setKeyManager(javax.net.ssl.KeyManager keyManager);

    boolean setRemoteLastModified(String remoteFile, long localModified) throws IOException;

    void setEnabledSessionCreation(boolean isCreation);

    boolean getEnableSessionCreation();

    void setNeedClientAuth(boolean isNeedClientAuth);

    boolean getNeedClientAuth();

    void setWantClientAuth(boolean isWantClientAuth);

    boolean getWantClientAuth();

    void setUseClientMode(boolean isClientMode);

    boolean getUseClientMode();

    void setEnabledCipherSuites(java.lang.String[] cipherSuites);

    java.lang.String[] getEnabledCipherSuites();

    void setEnabledProtocols(java.lang.String[] protocolVersions);

    java.lang.String[] getEnabledProtocols();

    void execPBSZ(long pbsz) throws java.io.IOException;

    void execPROT(java.lang.String prot) throws java.io.IOException;

    javax.net.ssl.TrustManager getTrustManager();

    void setTrustManager(javax.net.ssl.TrustManager trustManager);

    void setRemoteVerificationEnabled(boolean enable);

    boolean isRemoteVerificationEnabled();

    void addProtocolCommandListener(org.apache.commons.net.ProtocolCommandListener listener);

    void removeProtocolCommandListener(org.apache.commons.net.ProtocolCommandListener listener);

    boolean rename(java.lang.String from, java.lang.String to) throws java.io.IOException;

    boolean abort() throws java.io.IOException;

    boolean deleteFile(java.lang.String pathname) throws java.io.IOException;

    boolean removeDirectory(java.lang.String pathname) throws java.io.IOException;

    boolean makeDirectory(java.lang.String pathname) throws java.io.IOException;

    java.lang.String printWorkingDirectory() throws java.io.IOException;

    boolean sendSiteCommand(java.lang.String arguments) throws java.io.IOException;

    java.lang.String getSystemType() throws java.io.IOException;

    java.lang.String listHelp() throws java.io.IOException;

    java.lang.String listHelp(java.lang.String command) throws java.io.IOException;

    boolean sendNoOp() throws java.io.IOException;

    boolean setFileType(int fileType) throws java.io.IOException;

    boolean setFileType(int fileType, int formatOrByteSize) throws java.io.IOException;

    boolean setFileStructure(int structure) throws java.io.IOException;

    boolean setFileTransferMode(int mode) throws java.io.IOException;

    boolean remoteRetrieve(java.lang.String filename) throws java.io.IOException;

    boolean remoteStore(java.lang.String filename) throws java.io.IOException;

    boolean remoteStoreUnique(java.lang.String filename) throws java.io.IOException;

    boolean remoteStoreUnique() throws java.io.IOException;

    boolean remoteAppend(java.lang.String filename) throws java.io.IOException;

    boolean retrieveFile(java.lang.String remote, java.io.OutputStream local) throws java.io.IOException;

    java.io.InputStream retrieveFileStream(java.lang.String remote) throws java.io.IOException;

    boolean storeFile(java.lang.String remote, java.io.InputStream local) throws java.io.IOException;

    java.io.OutputStream storeFileStream(java.lang.String remote) throws java.io.IOException;

    boolean appendFile(java.lang.String remote, java.io.InputStream local) throws java.io.IOException;

    java.io.OutputStream appendFileStream(java.lang.String remote) throws java.io.IOException;

    boolean storeUniqueFile(java.lang.String remote, java.io.InputStream local) throws java.io.IOException;

    java.io.OutputStream storeUniqueFileStream(java.lang.String remote) throws java.io.IOException;

    boolean storeUniqueFile(java.io.InputStream local) throws java.io.IOException;

    java.io.OutputStream storeUniqueFileStream() throws java.io.IOException;

    boolean allocate(int bytes) throws java.io.IOException;

    boolean features() throws java.io.IOException;

    java.lang.String[] featureValues(java.lang.String feature) throws java.io.IOException;

    java.lang.String featureValue(java.lang.String feature) throws java.io.IOException;

    boolean hasFeature(java.lang.String feature) throws java.io.IOException;

    boolean hasFeature(java.lang.String feature, java.lang.String value) throws java.io.IOException;


    boolean allocate(int bytes, int recordSize) throws java.io.IOException;

    boolean doCommand(java.lang.String command, java.lang.String params) throws java.io.IOException;

    java.lang.String[] doCommandAsStrings(java.lang.String command, java.lang.String params) throws java.io.IOException;


    void disconnect() throws java.io.IOException;


    boolean isConnected();


    void enterLocalActiveMode();

    void enterLocalPassiveMode();

    boolean enterRemoteActiveMode(java.net.InetAddress host, int port) throws java.io.IOException;

    boolean enterRemotePassiveMode() throws java.io.IOException;

    java.lang.String getPassiveHost();

    int getPassivePort();

    int getDataConnectionMode();

    void setActivePortRange(int minPort, int maxPort);

    void setRestartOffset(long offset);

    long getRestartOffset();

    void setDataTimeout(int timeout);

    void setParserFactory(org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory parserFactory);

    boolean login(java.lang.String username, java.lang.String password) throws java.io.IOException;

    boolean logout() throws java.io.IOException;

    boolean changeWorkingDirectory(java.lang.String pathname) throws java.io.IOException;

    boolean changeToParentDirectory() throws java.io.IOException;

    boolean structureMount(java.lang.String pathname) throws java.io.IOException;

    boolean isUseEPSVwithIPv4();

    void setUseEPSVwithIPv4(boolean selected);

    int sendCommand(java.lang.String command, java.lang.String args) throws java.io.IOException;

    int sendCommand(int command, java.lang.String args) throws java.io.IOException;

    int sendCommand(org.apache.commons.net.ftp.FTPCmd command) throws java.io.IOException;

    int sendCommand(org.apache.commons.net.ftp.FTPCmd command, java.lang.String args) throws java.io.IOException;

    int sendCommand(java.lang.String command) throws java.io.IOException;

    int sendCommand(int command) throws java.io.IOException;

    int getReplyCode();

    int getReply() throws java.io.IOException;

    java.lang.String[] getReplyStrings();

    java.lang.String getReplyString();

    org.apache.commons.net.ftp.FTPFile[] listFiles(java.lang.String pathname) throws java.io.IOException;

    org.apache.commons.net.ftp.FTPFile[] listFiles() throws java.io.IOException;

    org.apache.commons.net.ftp.FTPFile[] listFiles(java.lang.String pathname, org.apache.commons.net.ftp.FTPFileFilter filter) throws java.io.IOException;

    org.apache.commons.net.ftp.FTPFile[] listDirectories() throws java.io.IOException;

    org.apache.commons.net.ftp.FTPFile[] listDirectories(java.lang.String parent) throws java.io.IOException;

    void bye();
}
