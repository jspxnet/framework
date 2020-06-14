package com.github.jspxnet.network.oss.adapter;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.oss.CloudFileClient;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.table.CloudFileConfig;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.obs.services.ObsClient;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.PutObjectResult;

import java.io.*;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/9/20 0:10
 * description: jspbox
 **/
public class HuaWeiObs extends BaseCloudFile implements CloudFileClient {
    private CloudFileConfig config;

    public  HuaWeiObs(CloudFileConfig config) {
        this.config = config;
    }

    /**
     * 上传文件到阿里云
     *
     * @param cloudPath 文件
     * @param file      文件对象
     * @return 云文件路径
     */
    @Override
    public String upload(String cloudPath, File file) throws Exception {
        if (file == null || !file.isFile()) {
            return null;
        }
        if (StringUtil.isEmpty(cloudPath)) {
            cloudPath = fixCloudPath(file.getName());
        }
        try (ObsClient obsClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndpoint());
             InputStream inputStream = new FileInputStream(file)) {
            String hash = obsClient.base64Md5(new FileInputStream(file));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentEncoding(Environment.defaultEncode);
            metadata.setContentMd5(hash);
            metadata.setContentLength(file.length());
            PutObjectResult putObjectResult = obsClient.putObject(config.getBucket(), cloudPath, inputStream, metadata);
            return putObjectResult.getObjectUrl();
        }
        // 关闭OSSClient。
    }


    /**
     * @param cloudPath 云文件路径
     * @return 删除文件
     */
    @Override
    public boolean delete(String cloudPath) {
        ObsClient obsClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndpoint());
        DeleteObjectResult deleteObjectResult = obsClient.deleteObject(config.getBucket(), cloudPath);
        boolean result = deleteObjectResult.isDeleteMarker();
        try {
            obsClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public OssSts getOssSts(String roleSessionName) {
        return null;
    }


    public static void main(String[] args) throws Exception {

       byte[] content =  FileUtil.readFileByte(new File("f:\\httpd.conf"));

        String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(content));

        ObsClient obsClient = new ObsClient("11111", "2222", "2222");
        String hashObs = obsClient.base64Md5(new FileInputStream(new File("f:\\httpd.conf")));
        String hash = EncryptUtil.getBase64Encode(FileUtil.getHash(new FileInputStream(new File("f:\\httpd.conf")), "MD5"), EncryptUtil.DEFAULT);
        System.out.println("-------md5=" + md5);
        System.out.println("-------Obs=" + hashObs);
        System.out.println("-------ash=" + hash);
    }

}
