package com.github.jspxnet.txweb.model.dto;

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
public class AttachmentRocResponse<T> extends RocResponse<T> {
    private List<String> groups;
}
