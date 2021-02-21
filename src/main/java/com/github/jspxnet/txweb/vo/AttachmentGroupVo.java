package com.github.jspxnet.txweb.vo;

import com.github.jspxnet.txweb.result.RocResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/20 23:35
 * description: 附件结构
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AttachmentGroupVo extends RocResponse<List<AttachmentsVo>> {
    private List<String> groups;
}
