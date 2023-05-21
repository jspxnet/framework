package com.github.jspxnet.sober.table;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.LinkedList;

/**
 * sqlmap配置
 * com.github.jspxnet.sober.table.SqlMapConf
 * @author chenYuan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_sql_map", caption = "sqlmap配置")
public class SqlMapConf extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "执行类型",enumType= ExecuteEnumType.class,defaultValue = "0",input = "select")
    private int executeType = ExecuteEnumType.QUERY.getValue();

    @Column(caption = "名称", length = 250, dataType = "isLengthBetween(1,200)", notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "描述", length = 250)
    private String caption = StringUtil.empty;

    @Column(caption = "返回类型", length = 250)
    private String resultType = StringUtil.empty;

    @Column(caption = "数据库类型",length = 250)
    private String databaseType = StringUtil.empty;

    @Column(caption = "SQL", length = 4000, notNull = true)
    private String context = StringUtil.empty;

    @Column(caption = "载入关联映射",enumType = YesNoEnumType.class,defaultValue = "0",input = "select")
    private int nexus = YesNoEnumType.NO.getValue();

    @Column(caption = "查询模式",enumType = QueryModelEnumType.class,defaultValue = "0",input = "select")
    private int queryModel = QueryModelEnumType.LIST.getValue();

    /**
     *  作用修改进入的参数,修复返回的类型
     */
    @Column(caption = "拦截器", length = 1000)
    private String interceptor = StringUtil.empty;

    /**
     * 当前页变量名称
     */
    @Column(caption = "分页变量", length = 50,defaultValue = "currentPage")
    private String currentPage = "currentPage";

    @Column(caption = "分页行数", length = 50,defaultValue = "count")
    private String count = "count";

    @Column(caption = "命名空间", length = 100)
    private String namespace = StringUtil.empty;
    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Column(caption = "版本号",defaultValue = "1")
    private int version = 1;

    //做初始化标识
    @JsonIgnore
    private boolean replenished = false;

    //是否引用
    @JsonIgnore
    private String quote = null;


    //索引信息
    @JsonIgnore
    private String index = null;

    @JsonIgnore
    private LinkedList<SqlMapInterceptorConf> interceptorConfList;

    public String[] getInterceptorArray() {
        return StringUtil.split(interceptor,StringUtil.SEMICOLON);
    }

    public void setInterceptor(String interceptor) {
        this.interceptor = interceptor;
    }

    public void setInterceptor(String[] interceptor) {
        this.interceptor = ArrayUtil.toString(interceptor,StringUtil.SEMICOLON);
    }

}
