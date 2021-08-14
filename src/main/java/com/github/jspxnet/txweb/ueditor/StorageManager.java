package com.github.jspxnet.txweb.ueditor;

/**
 * Created by chenyuan
 * on 2015-8-6.
 */


import com.github.jspxnet.txweb.ueditor.define.AppInfo;
import com.github.jspxnet.txweb.ueditor.define.BaseState;
import com.github.jspxnet.txweb.ueditor.define.State;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class StorageManager {
    public static final int BUFFER_SIZE = 8192;

    public StorageManager() {
    }

    public static State saveBinaryFile(byte[] data, String path) {
        File file = new File(path);
        State state = valid(file);
        if (!state.isSuccess()) {
            return state;
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (IOException ioe) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        state = new BaseState(true, file.getPath());
        state.putInfo("size", data.length);
        state.putInfo("title", file.getName());
        return state;
    }

    public static State saveFileByInputStream(InputStream is, String path, long maxSize) {
        State state = null;
        File tmpFile = getTmpFile();
        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);
            int count = 0;
            while ((count = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count);
            }
            bos.flush();
            bos.close();

            if (tmpFile.length() > maxSize) {
                if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
                return new BaseState(false, AppInfo.MAX_SIZE);
            }

            state = saveTmpFile(tmpFile, path);

            if (!state.isSuccess()) {
                if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
            }
            return state;
        } catch (IOException e) {
            //...
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

    private static File getTmpFile() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        String tmpFileName = (Math.random() * 100000 + "").replace(StringUtil.DOT, StringUtil.empty);
        return new File(tmpDir, tmpFileName);
    }

    private static State saveTmpFile(File tmpFile, String path) {
        State state = null;
        File targetFile = new File(path);

        if (targetFile.canWrite()) {
            return new BaseState(false, AppInfo.PERMISSION_DENIED);
        }
        if (!FileUtil.moveFile(tmpFile, targetFile, true)) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        state = new BaseState(true);
        state.putInfo("size", targetFile.length());
        state.putInfo("title", targetFile.getName());
        return state;
    }

    private static State valid(File file) {
        File parentPath = file.getParentFile();
        if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
            return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
        }
        if (!parentPath.canWrite()) {
            return new BaseState(false, AppInfo.PERMISSION_DENIED);
        }
        return new BaseState(true);
    }
}
