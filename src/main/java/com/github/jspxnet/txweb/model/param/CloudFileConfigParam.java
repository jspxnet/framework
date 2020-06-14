package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.CloudServiceEnumType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class CloudFileConfigParam implements Serializable  {

    @Param(caption = "ID",min = 1,max = 32)
    private long id;

    //那个系统上传的
    @Param(caption = "命名空间", max = 50,required = true, message = "不能为空")
    private String namespace = StringUtil.empty;

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    //http://oss-cn-hangzhou.aliyuncs.com
    @Param(caption = "endpoint", max = 250)
    private String endpoint = StringUtil.empty;

    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    @Param(caption = "账号Key", max = 64, required = true, message = "不能为空")
    private String accessKeyId = StringUtil.empty;

    @Param(caption = "KeySecret", max = 128, required = true,  message = "不能为空")
    private  String accessKeySecret = StringUtil.empty;

    @Param(caption = "bucket", max = 128,required = true,  message = "不能为空" )
    private  String bucket = StringUtil.empty;

    @Param(caption = "云盘类型", max = 2,required = true, enumType = CloudServiceEnumType.class)
    private  int cloudType = CloudServiceEnumType.Ali.getValue();


}
