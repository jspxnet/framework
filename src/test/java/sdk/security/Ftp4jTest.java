package sdk.security;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/**
 * 进行隐式FTPS连接
 *
 * @author cuisuqiang
 * @version 1.0
 * @说明
 */
public class Ftp4jTest {
    public static class MyX509TrustManager implements X509TrustManager {
        private X509TrustManager tm;
        private X509Certificate[] chain;
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            this.tm.checkServerTrusted(chain, authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }
    public static void main(String[] args) {
        try {
            // key store相关信息
            KeyStore keyStore = KeyStore.getInstance("JKS");
            File certFile =  new File("D:\\website\\javapro\\jftps\\bin\\server.keystore");

            // 装载当前目录下的key store. 可用jdk中的keytool工具生成keystore
            keyStore.load(new FileInputStream(certFile), "456789".toCharArray());
            // 初始化key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "456789".toCharArray());


            // 初始化ssl context
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(),new TrustManager[] { new MyX509TrustManager() },new SecureRandom());
            SSLServerSocketFactory sslSocketFactory = context.getServerSocketFactory();
            SSLSocketFactory socketFactory = context.getSocketFactory();

            FTPClient client = new FTPClient();
            client.setServerSocketFactory(sslSocketFactory);
            client.setSocketFactory(socketFactory);
            client.connect("192.168.0.200", 990);
            boolean isLogin = client.login("admin", "admin");
            client.enterLocalPassiveMode();
            System.out.println("-------------isLogin=" +isLogin);
            client.sendCommand("PORT P");
            for (FTPFile ftpFile:client.listFiles())
            {
                System.out.println("-----------" + ftpFile.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}