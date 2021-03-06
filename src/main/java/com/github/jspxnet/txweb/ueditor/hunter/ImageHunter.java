package com.github.jspxnet.txweb.ueditor.hunter;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import com.github.jspxnet.txweb.ueditor.define.AppInfo;
import com.github.jspxnet.txweb.ueditor.define.BaseState;
import com.github.jspxnet.txweb.ueditor.define.MIMEType;
import com.github.jspxnet.txweb.ueditor.define.MultiState;
import com.github.jspxnet.txweb.ueditor.define.State;
import com.github.jspxnet.txweb.ueditor.StorageManager;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;

/**
 * 图片抓取器
 *
 * @author hancong03@baidu.com
 */
public class ImageHunter {

    private String filename = null;
    private String saveDirectory = null;

    private String[] allowTypes = null;
    private long maxSize = -1;

    private String[] filters = null;

    public ImageHunter(String saveDirectory, String filename, long maxSize, String[] allowFiles, String[] filter) {

        this.filename = filename;
        this.saveDirectory = saveDirectory;
        this.maxSize = maxSize;
        this.allowTypes = allowFiles;
        this.filters = filter;
    }

    public State capture(String[] list) {
        MultiState state = new MultiState(true);
        for (String source : list) {
            state.addState(captureRemoteData(source));
        }
        return state;
    }

    public State captureRemoteData(String urlStr) {

        HttpURLConnection connection = null;
        URL url = null;
        String suffix = null;

        try {
            url = new URL(urlStr);

            if (!validHost(url.getHost())) {
                return new BaseState(false, AppInfo.PREVENT_HOST);
            }

            connection = (HttpURLConnection) url.openConnection();

            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(true);

            if (!validContentState(connection.getResponseCode())) {
                return new BaseState(false, AppInfo.CONNECTION_ERROR);
            }

            suffix = MIMEType.getSuffix(connection.getContentType());

            if (!validFileType(suffix)) {
                return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
            }

            if (!validFileSize(connection.getContentLength())) {
                return new BaseState(false, AppInfo.MAX_SIZE);
            }

            File file = new File(saveDirectory, filename + "." + suffix);
            State state = StorageManager.saveFileByInputStream(connection.getInputStream(), file.getPath(), maxSize);
            if (state.isSuccess()) {
                String showUrl = FileUtil.getDecrease(saveDirectory, file.getPath());
                state.putInfo("url", showUrl);
                state.putInfo("source", urlStr);
            }
            return state;

        } catch (Exception e) {
            return new BaseState(false, AppInfo.REMOTE_FAIL);
        }

    }

    private boolean validHost(String hostname) {
        return !ArrayUtil.inArray(filters, hostname, true);
    }

    private boolean validContentState(int code) {
        return HttpURLConnection.HTTP_OK == code;
    }

    private boolean validFileType(String type) {
        return ArrayUtil.inArray(allowTypes, type, true);
    }

    private boolean validFileSize(int size) {
        return size < this.maxSize;
    }

}
