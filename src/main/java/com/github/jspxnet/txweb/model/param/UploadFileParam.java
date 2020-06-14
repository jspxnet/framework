package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/27 9:47
 * description: 上传参数
 */
@Data
public class UploadFileParam implements Serializable {
  
    @Param(caption = "ID")
    private long id = 0;

    //用来判断是否已经存在，也是唯一ID
    @Param(caption = "Hash", max = 40)
    private String hash = StringUtil.empty;

    @Param(caption = "标题", max = 250)
    private String title = StringUtil.empty;

    @Param(caption = "关键字", max = 240)
    private String tags = StringUtil.empty;

    @Param(caption = "属性", max = 1000)
    private String attributes = StringUtil.empty;

    @Param(caption = "描述", max = 2000)
    private String content = StringUtil.empty;

    @Param(caption = "文件路径", max = 250)
    private String fileName = StringUtil.empty;

    @Param(caption = "类型", max = 10)
    private String fileType = StringUtil.empty;

    @Param(caption = "大小")
    private long fileSize = 0;

    @Param(caption = "机构ID", max = 32)
    private String organizeId = StringUtil.empty;
}
