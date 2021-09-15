package com.github.jspxnet.network.oss.adapter;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.oss.CloudFileClient;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.table.CloudFileConfig;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/9/19 23:58
 * description: jspbox
 *
 *         CloudFileConfig cloudFileConfig = new CloudFileConfig();
 *         cloudFileConfig.setEndpoint("http://oss-cn-shanghai.aliyuncs.com");
 *         cloudFileConfig.setAccessKeyId("xxxxxxxxxxxxxx");
 *         cloudFileConfig.setAccessKeySecret("xxxxxxxxxxx");
 *         cloudFileConfig.setBucket("modoo-oss-bucket");
 *         cloudFileConfig.setRegionCn("cn-shanghai");
 *         cloudFileConfig.setRegionId("cn-shanghai");
 *         cloudFileConfig.setRoleSessionName("platform-01");
 *         cloudFileConfig.setRoleArn("acs:ram::1374578769556034:role/oss-role");
 *         CloudFileClient client = new AliYunOss(cloudFileConfig);
 *         String upload = client.upload("11-61.jpg", new File("f:\\demo\\01.jpg"));
 **/
@Slf4j
public class AliYunOss extends BaseCloudFile implements CloudFileClient {
    private static final Map<String, OssSts> ossStsPool = new HashMap<>();
    final private CloudFileConfig config;


    public AliYunOss(CloudFileConfig config) {
        this.config = config;
    }

    /**
     * 上传文件到阿里云
     *
     * @param cloudPath 云文件路径
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

        // 创建OSSClient实例。
        OSS ossClient = null;
        if (!StringUtil.isEmpty(config.getRoleSessionName())&&!StringUtil.isEmpty(config.getRoleArn()))
        {
            OssSts ossSts =  getOssSts(config.getRoleSessionName());
            log.debug(ObjectUtil.toString(ossSts));
            if ("200".equals(ossSts.getStatusCode()))
            {
                ossClient = new OSSClientBuilder().build(config.getEndpoint(), ossSts.getAccessKeyId(), ossSts.getAccessKeySecret(),ossSts.getSecurityToken());
            }
        }
        if (ossClient==null)
        {
            ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        }
        // 上传文件流。
        String contentType = FileSuffixUtil.getContentType(file);
        try (InputStream input = new FileInputStream(file)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentEncoding(Environment.defaultEncode);
            String hash = EncryptUtil.getBase64Encode(FileUtil.getHash( new FileInputStream(file), "MD5"), EncryptUtil.DEFAULT);
            metadata.setContentMD5(hash);
            ossClient.putObject(config.getBucket(), cloudPath, input);
            return getDownloadUrlByKeyName(ossClient, cloudPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return null;
    }

    /**
     * @param cloudPath 云文件路径
     * @return 删除文件
     */
    @Override
    public boolean delete(String cloudPath) {
        OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        boolean exist = ossClient.doesObjectExist(config.getBucket(), cloudPath);
        if (!exist) {
            log.error("文件不存在,filePath={}", cloudPath);
            return false;
        }
        log.info("删除文件,filePath={}", cloudPath);
        ossClient.deleteObject(config.getBucket(), cloudPath);
        ossClient.shutdown();
        return true;
    }

    /**
     * @param cloudPath 云路径
     * @return 获得url链接
     */
    private String getDownloadUrlByKeyName(OSS ossClient, String cloudPath) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(config.getBucket(), cloudPath, expiration);
        if (url != null) {
            return URLUtil.deleteQueryString(url.toString());
        }
        return "https://" + config.getBucket() + StringUtil.DOT + config.getEndpoint().replaceFirst("http://", "") + "/" + cloudPath;
    }

    @Override
    public OssSts getOssSts(String roleSessionName)
    {
        OssSts credentials = ossStsPool.get(roleSessionName);
        if (credentials == null || credentials.isExpired()) {
            credentials = createOssSts(roleSessionName);

            ossStsPool.put(roleSessionName, credentials);
        }
        return credentials;
    }


    private OssSts createOssSts(String roleSessionName)
    {

        // 当前 STS API 版本
        // String STS_API_VERSION = "2015-04-01";
        // 请首先在RAM控制台创建一个RAM用户，并为这个用户创建AccessKeys
        OssSts aliyunOssSts = new OssSts();
        try {

            IClientProfile profile = DefaultProfile.getProfile(config.getRegionCn(),config.getAccessKeyId(), config.getAccessKeySecret());
            DefaultAcsClient client = new DefaultAcsClient(profile);
            // 创建一个 AssumeRoleRequest 并设置请求参数
            final AssumeRoleRequest req = new AssumeRoleRequest();
            // req.setVersion(STS_API_VERSION);
            req.setSysProtocol(ProtocolType.HTTPS);
            req.setRoleArn(config.getRoleArn());
            req.setRoleSessionName(roleSessionName);
            req.setPolicy(null);
            // 发起请求，并得到response
            final AssumeRoleResponse resp = client.getAcsResponse(req);
            aliyunOssSts.setExpiration(resp.getCredentials().getExpiration());
            aliyunOssSts.setAccessKeyId(resp.getCredentials().getAccessKeyId());
            aliyunOssSts.setAccessKeySecret(resp.getCredentials().getAccessKeySecret());
            aliyunOssSts.setSecurityToken(resp.getCredentials().getSecurityToken());
            aliyunOssSts.setStatusCode("200");
        }catch (ClientException e){
            aliyunOssSts.setStatusCode("500");
            aliyunOssSts.setErrorCode(e.getErrCode());
            aliyunOssSts.setErrorMessage(e.getErrMsg());
            e.printStackTrace();
        }catch (Exception ee){
            ee.printStackTrace();
        }
        return aliyunOssSts;
    }

}
