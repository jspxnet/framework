/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.download.impl;

import com.github.jspxnet.network.TransmitListener;
import com.github.jspxnet.network.download.HttpDownloadThread;
import com.github.jspxnet.boot.sign.DownStateType;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import java.util.*;




/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-4
 * Time: 21:47:32
 */
@Slf4j
public class MultiThreadImpl extends Thread implements HttpDownloadThread {
    private SiteInfoBean siteInfoBean = null; //文件信息Bean
    private long[] nStartPos; //开始位置
    private long[] nEndPos; //结束位置
    private long[] nCompleted; //结束位置
    private long downLoadFileSize = 0;
    private int stateType = DownStateType.WAITING;
    private File saveFile;
    private String downStateId = StringUtil.empty;    //是 DownState 的ID
    private int splitter = 1;
    private URL url;
    private final Date createDate = new Date();
    private final Map<String, String> valueMap = new HashMap<String, String>();
    private String namespace = StringUtil.empty;
    private int bufferSize = 1024;
    private Collection<TransmitListener> listeners = new LinkedList<TransmitListener>();

    public MultiThreadImpl() {

    }

    @Override
    public void registerListener(TransmitListener listener) {
        listeners.add(listener);
    }

    public Collection<TransmitListener> getListeners() {
        return listeners;
    }

    public void setListeners(Collection<TransmitListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void put(String k, String v) {
        valueMap.put(k, v);
    }

    @Override
    public String get(String k) {
        return valueMap.get(k);
    }

    @Override
    public String getDownStateId() {
        return downStateId;
    }

    @Override
    public void setDownStateId(String downStateId) {
        this.downStateId = downStateId;
    }

    @Override
    public int getSplitter() {
        return splitter;
    }

    @Override
    public void setSplitter(int splitter) {
        this.splitter = splitter;
    }

    @Override
    public File getSaveFile() {
        return saveFile;
    }

    @Override
    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public void setURL(URL url) {
        this.url = url;
    }

    @Override
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }


    @Override
    public void start() {
        if (saveFile == null) {
            stateType = DownStateType.ERROR;
            setQuit(true);
            return;
        }
        if (url == null) {
            stateType = DownStateType.ERROR;
            setQuit(true);
            return;
        }
        if (!FileUtil.makeDirectory(saveFile.getParent())) {
            stateType = DownStateType.ERROR;
            setQuit(true);
            return;
        }
        stateType = DownStateType.INITIALIZE;


        siteInfoBean = new SiteInfoBean();
        siteInfoBean.setUrl(url);
        siteInfoBean.setTempFilePath(saveFile.getParent());
        siteInfoBean.setSaveFile(saveFile.getName());
        siteInfoBean.setSplitter(splitter);
        tmpFile = new File(siteInfoBean.getTempFilePath() + File.separator + siteInfoBean.getSaveFile() + ".block ");
        if (tmpFile.exists()) {
            bFirst = false;
            readPos();
        } else {
            nStartPos = new long[siteInfoBean.getSplitter()];
            nEndPos = new long[siteInfoBean.getSplitter()];
            nCompleted = new long[siteInfoBean.getSplitter()];
        }
        super.start();
    }

    @Override
    public int getStateType() {
        if (nStartPos == null) {
            return stateType;
        }
        if (DownStateType.FINISH == stateType) {
            return DownStateType.FINISH;
        }
        if (DownStateType.INITIALIZE == stateType) {
            return DownStateType.INITIALIZE;
        }

        for (int i = 0; i < nStartPos.length; i++) {
            if (DownStateType.INITIALIZE == fileSplitterFetch[i].getStateType()) {
                return DownStateType.INITIALIZE;
            }
            if (DownStateType.WAITING == fileSplitterFetch[i].getStateType()) {
                return DownStateType.WAITING;
            }
            if (DownStateType.ERROR == fileSplitterFetch[i].getStateType()) {
                return DownStateType.ERROR;
            }
            if (DownStateType.DOWNLOADING == fileSplitterFetch[i].getStateType()) {
                return DownStateType.DOWNLOADING;
            }
            if (DownStateType.PAUSE == fileSplitterFetch[i].getStateType()) {
                return DownStateType.PAUSE;
            }
        }
        return stateType;
    }

    public void setStateType(int stateType) {
        for (int i = 0; i < nStartPos.length; i++) {
            fileSplitterFetch[i].setStateType(stateType);
        }
    }

    private FileSplitterFetch[] fileSplitterFetch; //子线程对象

    private boolean bFirst = true; //是否第一次取文件

    private boolean flagQuit = false; //停止标志

    private File tmpFile; //文件下载的临时信息

    @Override
    public void run() {
        //获得文件长度
        //分割文件
        //实例FileSplitterFetch
        //启动FileSplitterFetch线程
        //等待子线程返回
        downLoadFileSize = getFileSize();

        if (downLoadFileSize < 0) {
            stateType = DownStateType.ERROR;
            setQuit(true);
            return;
        }

        if (bFirst) {
            for (int i = 0; i < nStartPos.length; i++) {
                nStartPos[i] = (i * (downLoadFileSize / nStartPos.length));
            }
            System.arraycopy(nStartPos, 1, nEndPos, 0, nEndPos.length - 1);
            nEndPos[nEndPos.length - 1] = downLoadFileSize;
        }


        File tempSave = new File(siteInfoBean.getTempFilePath(), siteInfoBean.getSaveFile() + ".tmp");
        try {

            //启动子线程
            fileSplitterFetch = new FileSplitterFetch[nStartPos.length];
            for (int i = 0; i < nStartPos.length; i++) {

                fileSplitterFetch[i] = new FileSplitterFetch(siteInfoBean.getUrl(),
                        tempSave,
                        //siteInfoBean.getTempFilePath() + File.separator + siteInfoBean.getSaveFile(),
                        nStartPos[i], nEndPos[i], i, bufferSize);

                fileSplitterFetch[i].setCompleted(nCompleted[i]);
                //log.debug("Thread " + i + " , nStartPos = " + nStartPos[i] + ", nEndPos = " + nEndPos[i]);
                fileSplitterFetch[i].start();
            }
            //等待子线程结束
            //int count = 0;
            //是否结束while循环
            long completedSize = 0;
            boolean downOver = false; //停止标志
            stateType = DownStateType.DOWNLOADING;
            while (!flagQuit && !interrupted()) {
                writePos();
                sleep(300);
                boolean breakWhile = true;
                for (int i = 0; i < nStartPos.length; i++) {
                    FileSplitterFetch fetch = fileSplitterFetch[i];

                    //统计已经传动数据begin
                    //System.out.println("Thread " + i + " , nStartPos = " + nStartPos[i] + ", nEndPos = " + nEndPos[i] + "  " + completedSize);
                    completedSize = completedSize + fetch.getCompleted();
                    if (!listeners.isEmpty()) {
                        for (TransmitListener listener : listeners) {
                            listener.onProgressSize(completedSize);
                        }
                    }
                    //统计已经传动数据 end
                    //这里不能使用 fileSplitterFetch[i].getStateType()来判断，因为在多线程下，部分块没有下载，就已经下载完了。
                    if (!fetch.isDownOver()) {
                        breakWhile = false;
                        break;
                    }
                }
                if (breakWhile) {
                    downOver = true;
                    break;
                }
            }
            sleep(100);
            if (!isInterrupted()) {
                interrupt();
            }


            for (int i = 0; i < nStartPos.length; i++) {
                FileSplitterFetch fetch = fileSplitterFetch[i];
                fetch.close();
            }

            if (downOver && getCompleted() >= downLoadFileSize && tempSave.renameTo(saveFile)) {
                stateType = DownStateType.FINISH;
                if (!listeners.isEmpty()) {
                    for (TransmitListener listener : listeners) {
                        listener.onFinish(saveFile);
                    }
                }
            } else {
                stateType = DownStateType.STOP;
                if (!listeners.isEmpty()) {
                    for (TransmitListener listener : listeners) {
                        listener.onStop(saveFile);
                    }
                }
            }
        } catch (Exception e) {
            stateType = DownStateType.ERROR;
            if (!listeners.isEmpty()) {
                for (TransmitListener listener : listeners) {
                    listener.onError();
                }
            }
            e.printStackTrace();
        } finally {
            setQuit(true);
            if (tmpFile.exists()) {
                if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
            }
        }
    }

    //获得文件长度
    private long getFileSize() {
        if (url == null) {
            return -1;
        }
        int nFileLength = -1;
        try {
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            if (SystemUtil.isAndroid()) {
                uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
            } else {
                uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)");
            }
            uc.setRequestProperty("JCache-Controlt", "no-cache");
            uc.setConnectTimeout(100000);
            if (uc instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) uc;
                httpsConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String host, SSLSession session) {
                        return true;
                    }
                });
            }
            int responseCode = uc.getResponseCode();
            if (responseCode >= 400) {
                return -2;
            }
            String sHeader;
            for (int i = 1; ; i++) {
                sHeader = uc.getHeaderFieldKey(i);
                if (sHeader != null) {
                    if ("Content-Length".equalsIgnoreCase(sHeader)) {
                        nFileLength = Integer.parseInt(uc.getHeaderField(sHeader));
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!listeners.isEmpty()) {
                for (TransmitListener listener : listeners) {
                    listener.setFullSize(nFileLength);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nFileLength;
    }

    //保存下载信息（文件指针位置）
    private void writePos() {
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(tmpFile));
            output.writeInt(nStartPos.length);
            for (int i = 0; i < nStartPos.length; i++) {
                output.writeLong(fileSplitterFetch[i].getStartPos());
                output.writeLong(fileSplitterFetch[i].getEndPos());
                output.writeLong(fileSplitterFetch[i].getCompleted());
            }
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取保存的下载信息（文件指针位置）
    private void readPos() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(tmpFile));
            int nCount = input.readInt();
            nStartPos = new long[nCount];
            nEndPos = new long[nCount];
            nCompleted = new long[nCount];
            for (int i = 0; i < nStartPos.length; i++) {
                nStartPos[i] = input.readLong();
                nEndPos[i] = input.readLong();
                nCompleted[i] = input.readLong();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getQuit() {
        return flagQuit;
    }

    //停止文件下载
    @Override
    public void setQuit(boolean flagQuit) {
        this.flagQuit = flagQuit;
        if (nStartPos == null) {
            return;
        }
        if (ArrayUtil.isEmpty(fileSplitterFetch)) {
            return;
        }
        if (flagQuit) {
            for (int i = 0; i < nStartPos.length; i++) {
                fileSplitterFetch[i].stopDownload();
                fileSplitterFetch[i].close();
            }
        }
    }

    @Override
    public long getCompleted() {
        long completed = 0;
        if (nStartPos != null && fileSplitterFetch != null) {
            for (int i = 0; i < nStartPos.length; i++) {
                completed = completed + fileSplitterFetch[i].getCompleted();
            }
        }
        return completed;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getPercent() {
        if (downLoadFileSize <= 0) {
            return "00%";
        }
        return NumberUtil.mul(NumberUtil.div((double) getCompleted(), (double) downLoadFileSize, 2).doubleValue(), 100) + "%";
    }
}