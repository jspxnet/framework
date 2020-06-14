package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import java.io.Serializable;
import java.util.Date;

/**
 * 存储队列是分布式保存的链表,目的就是减轻数据库压力
 * 但客户端不能立刻得到存储是否成功
 */
@Table(name = "jspx_store_queue_status", caption = "存储队列记录")
public class StoreQueueStatus implements Serializable {

    @Id(auto = true, length = 32, type = IDType.uuid, dateStart = true)
    @Column(caption = "ID", length = 32, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "返回", length = 50, option = "unknown:未知;succeed:成功;fail:失败;", dataType = "isLengthBetween(2,50)", notNull = true, defaultValue = "unknown")
    private String status = StringUtil.empty;

    @Column(caption = "类名", length = 200, dataType = "isLengthBetween(2,200)")
    private String className = StringUtil.empty;

    @Column(caption = "异常信息", length = 20000)
    private String exception = StringUtil.empty;

    @Column(caption = "返回标识")
    private int result = 0;

    @Column(caption = "返回标识", length = 50)
    private String cmd = StringUtil.empty;

    //json格式保存,也可能是sql
    @Column(caption = "存储对象", length = 40000)
    private String objectData = StringUtil.empty;

    @Column(caption = "创建时间", notNull = true)
    private Date createDate = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getObjectData() {
        return objectData;
    }

    public void setObjectData(String objectData) {
        this.objectData = objectData;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
