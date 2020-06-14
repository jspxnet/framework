package com.github.jspxnet.txweb;

/**
 * Created by yuan on 14-5-25.
 * 许可计算接口
 */
public interface ILicense {
    String getLicense(String mac, int versionType) throws Exception;

    //String getLicenseInfo(String license) throws Exception;
    String getLicenseVersion(String license);
}
