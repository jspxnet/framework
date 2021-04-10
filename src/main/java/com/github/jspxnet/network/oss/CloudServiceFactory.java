package com.github.jspxnet.network.oss;

import com.github.jspxnet.enums.CloudServiceEnumType;
import com.github.jspxnet.network.oss.adapter.AliYunOss;
import com.github.jspxnet.network.oss.adapter.FastDfs;
import com.github.jspxnet.network.oss.adapter.HuaWeiObs;
import com.github.jspxnet.txweb.table.CloudFileConfig;

/**
 * author chenYUan
 * 云服务工厂
 */
public  class CloudServiceFactory {
    private CloudServiceFactory()
    {

    }
    /**
     *
     * @param config 云配置
     * @return 云盘客户端
     */
    public static CloudFileClient createCloudClient(CloudFileConfig config) {
        if (CloudServiceEnumType.Ali.getValue()==config.getCloudType())
        {
            return new AliYunOss(config);
        }
        if (CloudServiceEnumType.HuaWei.getValue()==config.getCloudType())
        {
            return new HuaWeiObs(config);
        }
        if (CloudServiceEnumType.FastDfs.getValue()==config.getCloudType())
        {
            return new FastDfs(config);
        }
        return null;
    }


}
