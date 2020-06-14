package sdk.security;

/**
 * Created by yuan on 2014/9/8 0008.
 */

import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;


import com.github.jspxnet.utils.StringUtil;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;

public class ConnectFTPS {
    private static FTPSClient ftpsClient;
    private static final String trust_path = "D:\\website\\javapro\\jftps\\bin\\client.p12";
    private static final String trust_pw = "456789";
    private static final String serverIP = "192.168.0.200";
    private static final int serverPort = 990;


    /**
     * 测试连接FTP With SSL，以Apache FTPServer为例
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        File certFile =  new File(trust_path);
        if (!certFile.exists())
        {
            System.err.println("证书文件没有找到,请放在 bin目录;" + trust_path);
        }

        String keyPassword = trust_pw;
        if (StringUtil.isNull(keyPassword))
        {
            System.err.println("证书密码不能为空");
        }


        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream inputStream = new FileInputStream(certFile);
        keyStore.load(inputStream, keyPassword.toCharArray());
        inputStream.close();

        ftpsClient = new FTPSClient(true);
        ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftpsClient.setKeyManager(getKeyManager(keyStore)[0]);
        //ftpsClient.setTrustManager(getTrustManager(keyStore)[0]);
        ftpsClient.setNeedClientAuth(true);
        ftpsClient.setControlEncoding("GBK");
        ftpsClient.setAuthValue("TLS");
        ftpsClient.setEnabledProtocols(new String[]{"TLSv1"});
        System.out.println("-----------isConnected=" + ftpsClient.isConnected());
        ftpsClient.connect(serverIP, serverPort);
        System.out.println("已经连接FTP");
        ftpsClient.execPBSZ(0);
        ftpsClient.execPROT("P");
        ftpsClient.login("admin", "admin");

        System.out.println("-----------ftpsClient.getLocalPort()=" + ftpsClient.getLocalPort());
        System.out.println("-----------ftpsClient.getRemotePort()=" + ftpsClient.getRemotePort());

        ftpsClient.enterLocalPassiveMode();
        ftpsClient.changeWorkingDirectory("/");


        FTPFile[] ftpFiles =  ftpsClient.listFiles();
        for (FTPFile fFile:ftpFiles)
        {
            System.out.println("---------文件名:" + fFile.getName());
        }

        System.out.println("关闭连接");
        ftpsClient.disconnect();

    }


    private static KeyManager[] getKeyManager(KeyStore keyStore) throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, trust_pw.toCharArray());
        return kmf.getKeyManagers();
    }


    private static TrustManager[] getTrustManager(KeyStore keyStore) throws Exception {
        TrustManagerFactory tf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tf.init(keyStore);
        return tf.getTrustManagers();
    }



}
