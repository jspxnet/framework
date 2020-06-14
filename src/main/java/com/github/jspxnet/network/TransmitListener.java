package com.github.jspxnet.network;

import java.io.File;

/**
 * Created by chenyuan on 14-6-12.
 * 传输监听接口
 */
public interface TransmitListener {

    void onProgressSize(long size);

    void onFinish(File file);

    void onStop(File file);

    void onError();

    void setFullSize(long size);

}
