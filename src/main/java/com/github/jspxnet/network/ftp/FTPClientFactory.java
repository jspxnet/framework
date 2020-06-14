package com.github.jspxnet.network.ftp;

import com.github.jspxnet.network.ftp.impl.JFTPClient;
import com.github.jspxnet.network.ftp.impl.JFTPSClient;

import java.security.NoSuchAlgorithmException;

/**
 * Created by yuan on 2015/4/18 0018.
 * 工厂模式，分别创建SSL客户端和普通FTP客户端，不使用显示ssl
 */
public class FTPClientFactory {
    public static IFTPClient create(boolean ssl, int buffer, String encode) throws NoSuchAlgorithmException {
        if (ssl) {
            return new JFTPSClient(buffer, encode);
        } else {
            return new JFTPClient(buffer, encode);
        }
    }
}
