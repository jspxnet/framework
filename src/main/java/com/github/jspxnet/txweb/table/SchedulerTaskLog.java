package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "jspx_scheduler_task_log", caption = "任务日志")
public class SchedulerTaskLog implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "地址", length = 250, dataType = "isLengthBetween(2,250)", notNull = true)
    private String url = StringUtil.empty;

    @Column(caption = "说明", length = 250, dataType = "isLengthBetween(2,250)")
    private String title = StringUtil.empty;

    @Column(caption = "正文", length = 20000)
    private String paramContent = StringUtil.empty;

    @Column(caption = "返回", length = 20000)
    private String actionResult = StringUtil.empty;

    @Column(caption = "错误信息", length = 20000)
    private String errorInfo = StringUtil.empty;
}
