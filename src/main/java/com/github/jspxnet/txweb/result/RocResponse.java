package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.EnumType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.txweb.model.param.PageParam;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON 返回格式封装
 */

public class RocResponse<T> implements Serializable {

    @Column(caption = "协议")
    private final static String PROTOCOL = Environment.jspxNetRoc;

    //后边将增加多种格式的json支持
    @Column(caption = "版本")
    private final static String VERSION = Environment.jspxNetRocVersion;

    @Column(caption = "当前页数")
    @JsonIgnore(isNull = true)
    private Integer currentPage = null;

    @Column(caption = "总行数")
    @JsonIgnore(isNull = true)
    private Long totalCount  = null;

    @Column(caption = "总页数")
    @JsonIgnore(isNull = true)
    private Integer totalPage  = null;

    @Column(caption = "一页行数")
    @JsonIgnore(isNull = true)
    private Integer count  = null;

    @Column(caption = "是否成功",enumType = YesNoEnumType.class)
    private int success = 0;  //当发生错误的时候code才有明细

    @Column(caption = "业务自定义状态码")
    @JsonIgnore(isNull = true)
    private Integer code;  // 业务自定义状态码

    @Column(caption = "http状态")
    @JsonIgnore(isNull = true)
    private Integer status;  // 业务自定义状态码

    @Column(caption = "错误信息")
    @JsonIgnore(isNull = true)
    private Map<String,String> error = null;  //错误信息

    @Column(caption = "描述")
    @JsonIgnore(isNull = true)
    private String message = null; // 请求状态描述，调试用

    @Column(caption = "请求数据")
    @JsonIgnore(isNull = true)
    private T data = null;// 请求数据，对象或数组均可

    @Column(caption = "扩展数据")
    @JsonIgnore(isNull = true)
    private Map<String,Object> property = null;// 请求数据，对象或数组均可


    public RocResponse() {

    }

    public int getCurrentPage() {
        return currentPage;
    }

    public RocResponse<T>  setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public RocResponse<T> setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        calculatePage();
        return this;
    }

    public int getTotalPage() {
        return totalPage;

    }

    public RocResponse<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public int getCount() {
        return count;
    }

    public RocResponse<T> setCount(int count) {
        this.count = count;
        calculatePage();
        return this;
    }

    public int getSuccess() {
        return success;
    }

    public boolean isSuccess() {
        return success == 1;
    }

    public RocResponse<T> setSuccess(int success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public RocResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RocResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public RocResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public RocResponse<T> setProperty(String key,Object value)
    {
        if (property==null)
        {
            property = new HashMap<>();
        }
        property.put(key,value);
        return this;
    }

    public Object getProperty(String key)
    {
        if (property==null)
        {
            return null;
        }
        return property.get(key);
    }

    /**
     * 成功时候的调用
     *
     * @param data data
     * @param <T>  t
     * @return Result
     */
    public static <T> RocResponse<T> success(T data) {

        return new RocResponse<T>(data);
    }

    public static <T> RocResponse<T> success() {
        RocResponse<T> response = new RocResponse<>();
        return response.setSuccess(1);
    }

    public static <T> RocResponse<T> success(T data, String message) {
        return new RocResponse<>(data, message);
    }

    /**
     *
     * @param data 数据
     * @param currentPage 当前页
     * @param count 每页显示
     * @param totalCount 总行数
     * @param <T> 类型
     * @return 列表返回封装简化
     */
    public static <T> RocResponse<T> success(T data,int currentPage,int count,int totalCount) {
        RocResponse<T> response = new RocResponse<>(data);
        response.setCurrentPage(currentPage).setCount(count).setTotalCount(totalCount);
        return response;
    }

    /**
     *
     * @param data 数据
     * @param pageParam  默认的参数对象
     * @param totalCount 总行数
     * @param <T>  数据类型
     * @return 列表返回封装简化
     */
    public static <T> RocResponse<T> success(T data, PageParam pageParam,int totalCount) {
        RocResponse<T> response = new RocResponse<>(data);
        return response.setCurrentPage(pageParam.getCurrentPage()).setCount(pageParam.getCount()).setTotalCount(totalCount);
    }

    /**
     * 失败时候的调用
     *
     * @param code 错误code
     * @param msg  信息
     * @param <T>  数据
     * @return RocResponse
     */
    public static <T> RocResponse<T> error(int code, String msg) {
        return new RocResponse<T>(code, msg);
    }

    /**
     *
     * @param errorEnum 错误提示类型
     * @param <T>  ErrorEnumType
     * @return 返回
     */
    public static <T> RocResponse<T> error(ErrorEnumType errorEnum) {
        return new RocResponse<T>(errorEnum.getValue(),errorEnum.getName());
    }
    /**
     * 枚举方式放入
     * @param enu 枚举错误
     * @param <T> 类型
     * @return RocResponse
     */
    public static <T> RocResponse<T> error(EnumType enu) {
        return new RocResponse<T>(enu.getValue(), enu.getName());
    }

    /**
     * @param code  错误代码
     * @param error 错误信息列表
     * @param <T>   泛型
     * @return 返回对象
     */
    public static <T> RocResponse<T> error(int code, Map<String, String> error) {
        return new RocResponse<T>(code, error);
    }

    /**
     * 成功的构造函数
     *
     * @param data data
     */
    private RocResponse(T data) {
        this.data = data;
        this.success = 1;
    }

    private RocResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = 1;
    }

    private RocResponse(int code, String msg) {
        this.code = code;
        this.message = msg;
        this.success = 0;
    }

    private RocResponse(int code, Map<String, String> msg) {
        this.code = code;
        this.success = 0;
        this.error = msg;
        if (this.error != null)
        {
            this.message = this.error.values().iterator().next();
        }
    }

    private void calculatePage()
    {
        if (count==null)
        {
            count = 0;
        }
        if (totalCount==null)
        {
            totalCount = 0L;
        }
        if (count>0&&totalCount>0)
        {
            totalPage = (int)(totalCount / count);
            if (totalCount % count > 0) {
                totalPage = totalPage + 1;
            }
        } else
        {
            totalPage =  1;
        }
    }


}
