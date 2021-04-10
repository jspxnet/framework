package com.github.jspxnet.network.oss.adapter;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.network.oss.CloudFileClient;
import com.github.jspxnet.txweb.table.CloudFileConfig;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import java.io.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/4/10 14:36
 * description:
 * FastDfs 本地分布式文件存储支持,自己要有一个内网系统的FastDfs服务器
 * 然后配置 CloudFileConfig
 * 配置中注意  Endpoint 是主机域名部分,getBucket 是 FastDfs 的文件分组名称
 *
 **/
@Slf4j
public class FastDfs extends BaseCloudFile implements CloudFileClient {
    private static  boolean isInit  = false;
    final private CloudFileConfig config;
    public  FastDfs(CloudFileConfig config) {
        this.config = config;
        if (!isInit)
        {
            try {
                ClientGlobal.initByProperties(EnvFactory.getEnvironmentTemplate().getProperties());
                isInit = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载是用 ngx_fastdfs_module 映射
     * http://xxx.xxx.xxx.xxx:8888/group1/M00/00/00/wKh5blkcHpiAAEBkAAC7L7_PW5E715.jpg
     * @param cloudPath 保存路径
     * @param file 文件
     * @return url 地址 Endpoint 是主机域名部分
     * @throws Exception 异常
     */
    @Override
    public String upload(String cloudPath, File file) throws Exception {

        //cloudPath 无用

        // 3、创建一个TrackerClient对象。
        TrackerClient trackerClient = new TrackerClient();
        // 6、获得StorageClient对象。
        StorageClient storageClient = new StorageClient(trackerClient.getTrackerServer());

        NameValuePair[] metas = new NameValuePair[2];
        metas[0] = new NameValuePair("fileName", file.getName());
        metas[1] = new NameValuePair("fileLength", file.length()+"");
        // 7、直接调用StorageClient对象方法上传文件即可。
        String[] strings = storageClient.upload_file(file.getPath(), FileUtil.getTypePart(file), metas);
        storageClient.close();
        if (strings==null&&strings.length!=2)
        {
            return null;
        }
        //getBucket 就是分组, 默认为 group1
        String groupName = null;
        if (config==null || StringUtil.isEmpty(config.getBucket()))
        {
            groupName = "group1";
        } else
        {
            groupName = config.getBucket();
        }

        if (!groupName.equals(strings[0]))
        {
            log.error("CloudFileConfig 中的Bucket :{}名称必须配置成 DFS中的 groupName:{}",groupName,strings[0]);
            return null;
        }

        return config.getEndpoint() + StringUtil.BACKSLASH + strings[0]+ StringUtil.BACKSLASH + strings[1];
    }

    @Override
    public boolean delete(String cloudPath) {
        // 3、创建一个TrackerClient对象。
        TrackerClient trackerClient = new TrackerClient();
        // 6、获得StorageClient对象。
        StorageClient storageClient = null;
        try {
            storageClient = new StorageClient(trackerClient.getTrackerServer());
            storageClient.delete_file(config.getBucket(),cloudPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                storageClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public OssSts getOssSts(String roleSessionName) {
        return null;
    }
}
