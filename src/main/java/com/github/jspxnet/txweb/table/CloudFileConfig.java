package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.enums.CloudServiceEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/20 0:15
 * description: 云空间配置
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_cloud_file_config", caption = "云盘配置")
public class CloudFileConfig extends OperateTable  {


    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    //那个系统上传的
    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    //http://oss-cn-hangzhou.aliyuncs.com

    //如果为FDS方式,这里表示 http 前缀, 返回URL路径会加上这里
    @Column(caption = "endpoint", length = 250, dataType = "isLengthBetween(1,250)")
    private String endpoint = StringUtil.empty;

    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    @Column(caption = "账号Key", length = 64, dataType = "isLengthBetween(1,64)")
    private String accessKeyId = StringUtil.empty;

    @Column(caption = "KeySecret", length = 128)
    private  String accessKeySecret = StringUtil.empty;

    //DFS 文件系统中作为分组名称使用
    @Column(caption = "bucket", length = 128)
    private  String bucket = StringUtil.empty;

    @Column(caption = "regionCn", length = 128)
    private  String regionCn = StringUtil.empty;

    @Column(caption = "regionId", length = 128)
    private  String regionId = StringUtil.empty;

    @Column(caption = "roleArn", length = 128)
    private  String roleArn = StringUtil.empty;

    @Column(caption = "policyFile", length = 128)
    private  String policyFile = StringUtil.empty;

    @Column(caption = "roleSessionName", length = 128)
    private  String roleSessionName = StringUtil.empty;

    @Column(caption = "云盘类型", length = 2,enumType = CloudServiceEnumType.class)
    private  int cloudType = CloudServiceEnumType.Ali.getValue();

}
