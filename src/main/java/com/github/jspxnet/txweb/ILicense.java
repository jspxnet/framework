package com.github.jspxnet.txweb;

import com.github.jspxnet.enums.VersionEnumType;

/**
 * Created by yuan on 14-5-25.
 * 许可计算接口
 */
public interface ILicense {

    /**
     *
     * @param verifyValue 验证值，支持多个
     * @param versionType 创建的许可版本
     * @return 创建一个许可
     * @throws Exception 异常
     */
    String getLicense(String verifyValue, int versionType) throws Exception;

    /**
     *
     * @param license 许可号
     * @param verifyValue 验证值
     * @return 通过谋一个验证值，得到许可版本
     */
    VersionEnumType getLicenseVersion(String license, String verifyValue);


}
