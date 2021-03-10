package com.github.jspxnet.txweb.dto;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.EnumType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.utils.ObjectUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/3/10 23:31
 * @description: jspbox
 **/
public class SpringResponse<T> implements Serializable {

    @Column(caption = "协议")
    private final static String PROTOCOL = Environment.jspxNetRoc;

    //后边将增加多种格式的json支持
    @Column(caption = "版本")
    private final static String VERSION = Environment.jspxNetRocVersion;

    @Column(caption = "当前页数")
    private int currentPage;

    @Column(caption = "总行数")
    private int totalCount;

    @Column(caption = "总页数")
    private int totalPage;

    @Column(caption = "一页行数")
    private Integer count  = null;

    //当发生错误的时候code才有明细
    @Column(caption = "是否成功",enumType = YesNoEnumType.class)
    private int success = 0;

    @Column(caption = "业务自定义状态码")
    @JsonIgnore(isNull = true)
    private int code;

    @Column(caption = "http状态")
    @JsonIgnore(isNull = true)
    private int status;

    @Column(caption = "错误信息")
    private Map<String,?> error = new HashMap<>();

    // 请求状态描述，调试用
    @Column(caption = "描述")
    private String message = null;

    // 请求数据，对象或数组均可
    @Column(caption = "请求数据")
    private T data = null;

    @Column(caption = "扩展数据")
    private Map<String,Object> property = new HashMap<>();// 请求数据，对象或数组均可


    public SpringResponse() {

    }

    public int getCurrentPage() {
        return currentPage;
    }

    public SpringResponse<T> setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public SpringResponse<T> setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        calculatePage();
        return this;
    }

    public int getTotalPage() {
        return totalPage;

    }

    public SpringResponse<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public int getCount() {
        return count;
    }

    public SpringResponse<T> setCount(int count) {
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

    public SpringResponse<T> setSuccess(int success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public SpringResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SpringResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public SpringResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
    public SpringResponse<T> setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public SpringResponse<T> setProperty(String key, Object value)
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
    public static <T> SpringResponse<T> success(T data) {

        return new SpringResponse<T>(data);
    }

    public static <T> SpringResponse<T> success() {
        SpringResponse<T> response = new SpringResponse<>();
        return response.setSuccess(1);
    }

    public static <T> SpringResponse<T> success(T data, String message) {
        return new SpringResponse<>(data, message);
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
    public static <T> SpringResponse<T> success(T data, int currentPage, int count, int totalCount) {
        SpringResponse<T> response = new SpringResponse<>(data);
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
    public static <T> SpringResponse<T> success(T data, PageParam pageParam, int totalCount) {
        SpringResponse<T> response = new SpringResponse<>(data);
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
    public static <T> SpringResponse<T> error(int code, String msg) {
        return new SpringResponse<T>(code, msg);
    }

    /**
     *
     * @param errorEnum 错误提示类型
     * @param <T>  ErrorEnumType
     * @return 返回
     */
    public static <T> SpringResponse<T> error(ErrorEnumType errorEnum) {
        return new SpringResponse<T>(errorEnum.getValue(),errorEnum.getName());
    }
    /**
     * 枚举方式放入
     * @param enu 枚举错误
     * @param <T> 类型
     * @return RocResponse
     */
    public static <T> SpringResponse<T> error(EnumType enu) {
        return new SpringResponse<T>(enu.getValue(), enu.getName());
    }

    /**
     * @param code  错误代码
     * @param error 错误信息列表
     * @param <T>   泛型
     * @return 返回对象
     */
    public static <T> SpringResponse<T> error(int code, Map<String, ?> error) {
        return new SpringResponse<T>(code, error);
    }

    /**
     * 成功的构造函数
     *
     * @param data data
     */
    private SpringResponse(T data) {
        this.data = data;
        this.success = 1;
    }

    private SpringResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = 1;
    }

    private SpringResponse(int code, String msg) {
        this.code = code;
        this.message = msg;
        this.success = 0;
    }

    private SpringResponse(int code, Map<String, ?> msg) {
        this.code = code;
        this.success = 0;
        this.error = msg;
        if (this.error != null && !this.error.isEmpty())
        {
            this.message = ObjectUtil.toString(this.error.values().iterator().next());
        }
    }

    private void calculatePage()
    {
        if (count==null)
        {
            count = 0;
        }

        if (count>0&&totalCount>0)
        {
            totalPage = (totalCount / count);
            if (totalCount % count > 0) {
                totalPage = totalPage + 1;
            }
        } else
        {
            totalPage =  1;
        }
    }

    public Integer getStatus() {
        return status;
    }

}
