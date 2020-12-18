package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.AuditEnumType;
import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/26 23:46
 * description: 角色参数
 **/
@Data
public class RoleParam  implements Serializable {
    @Param(caption = "ID", max = 32)
    private String id = StringUtil.empty;

    //角色名称，也可以是部门名称
    @Param(caption = "角色名称", max = 50)
    private String name = StringUtil.empty;

    @Param(caption = "描述", max = 250)
    private String description = StringUtil.empty;

    @Param(caption = "用户类型", max = 20)
    private int userType = UserEnumType.NONE.getValue();

    @Param(caption = "图片", max = 250)
    private String images = StringUtil.empty;

    @Param(caption = "办公角色", max = 2, enumType = YesNoEnumType.class)
    private int officeType = YesNoEnumType.NO.getValue();

    //是否允许上传
    @Param(caption = "是否允许上传", max = 2, enumType = YesNoEnumType.class)
    private int useUpload = YesNoEnumType.NO.getValue();

    @Param(caption = "上传的文件大小")
    private int uploadSize = 10240;

    @Param(caption = "上传的图片大小")
    private int uploadImageSize = 1024;

    @Param(caption = "上传的视频大小")
    private int uploadVideoSize = 102400;

    //上传的文件类型
    @Param(caption = "上传的文件类型", max = 250)
    private String uploadFileTypes = StringUtil.ASTERISK;

    @Param(caption = "磁盘空间")
    private long diskSize = 102400;

    @Param(caption = "FTP共享目录", max = 250)
    private String uploadFolder = StringUtil.empty;

    @Param(caption = "冻结", max = 2)
    private int congealType = CongealEnumType.NO_CONGEAL.getValue();

    @Param(caption = "冻结时间")
    private Date congealDate = DateUtil.empty;

    @Param(caption = "审核", max = 2)
    private int auditingType = AuditEnumType.OK.getValue();

    //rwde  读 写 删 执行  ftp 情况使用 主要留给FTP空间使用
    @Param(caption = "目录权限", max = 20)
    private String permission = "rw-d-";

    @JsonIgnore
    @Param(caption = "动作列表", max = 3000,level = SafetyEnumType.NONE)
    private String operates = StringUtil.empty;
    //数据保存格式，一行一个，开始第一个参数为命名空间，然后文件名称部分为文件名称(包含通配).采用base64加密后作为id使用分割符号：后边为执行方法
    //例如： 命名空间/base64:method   注意，这里并不保存程序的class信息，采用文件名称来识别class信息
    //jcms/xxx41234:ssd

    //格式[id:name]多个使用;分开
    @Param(caption = "管理者", max = 250)
    private String manager = StringUtil.empty;

    @Param(caption = "命名空间", max = 50)
    private String namespace = StringUtil.empty;

    @Param(caption = "机构ID", max = 32)
    private String organizeId = StringUtil.empty;

}
