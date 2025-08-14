package com.github.jspxnet.txweb.apidoc;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ApiOperate implements Serializable {

    @JsonIgnore(isNull = true)
    private String url;

    @JsonIgnore(isNull = true)
    private String caption;

    //请求方法
    private String action = "GET";

    private boolean deprecated = false;

    @JsonIgnore(isNull = true)
    private ApiMethod method = null;

    /**
     * 描述说明
     */
    @JsonIgnore(isNull = true)
    private String describe;

    //外部参数，提供生成调用demo
    @JsonIgnore
    private Map<String, ApiParam> params;


    private String resultType;
    //自动得到的返回类型名称
    //private Class<?> resultType;


    @JsonIgnore(isNull = true)
    private List<ApiField> result;


   @JsonField(name = "stdMethodJson")
    public String getStdMethodJson() {
        JSONObject callDemo = new JSONObject();
        if (method != null) {
            JSONObject rocMethod = new JSONObject();

            if (method.getParams() != null && !method.getParams().isEmpty()) {
                if (method.getName().contains("/"))
                {
                    JSONObject jsonParam = new JSONObject();
                    for (ApiParam apiParam : method.getParams().values()) {
                        if ("PathVar".equals(apiParam.getFiledType())) {
                            return url;
                        }
                        if (ObjectUtil.isEmpty(apiParam.getChildren()))
                        {
                            jsonParam.put(apiParam.getName(), apiParam.getCaption());
                        } else
                        {
                            if ("JSONObject".equalsIgnoreCase(apiParam.getFiledType()))
                            {
                                jsonParam.put(apiParam.getName(), apiParam.getCaption());
                            } else
                            {
                                for (ApiParam chilParam:apiParam.getChildren())
                                {
                                    jsonParam.put(chilParam.getName(), chilParam.getCaption());
                                }
                            }

                        }
                    }
                    callDemo = jsonParam;
                } else
                {
                    JSONObject jsonParam = new JSONObject();
                    for (ApiParam apiParam : method.getParams().values()) {
                        if ("PathVar".equals(apiParam.getFiledType())) {
                            return url;
                        }
                        if (ObjectUtil.isEmpty(apiParam.getChildren()))
                        {
                            jsonParam.put(apiParam.getName(), apiParam.getCaption());
                        } else
                        {
                            if ("JSONObject".equalsIgnoreCase(apiParam.getFiledType()))
                            {
                                jsonParam.put(apiParam.getName(), apiParam.getCaption());
                            } else
                            {
                                for (ApiParam chilParam:apiParam.getChildren())
                                {
                                    jsonParam.put(chilParam.getName(), chilParam.getCaption());
                                }
                            }

                        }
                    }
                    rocMethod.put(Environment.rocParams, jsonParam);
                    callDemo.put(Environment.Protocol, Environment.jspxNetRoc);
                    callDemo.put(Environment.rocMethod, rocMethod);
                }
            }
        }

        if (params != null) {
            JSONObject paramJson = new JSONObject();
            for (ApiParam apiParam : params.values()) {
                if (ObjectUtil.isEmpty(apiParam.getChildren()))
                {
                    paramJson.put(apiParam.getName(), apiParam.getCaption());
                } else
                {
                    if ("JSONObject".equalsIgnoreCase(apiParam.getFiledType()))
                    {
                        paramJson.put(apiParam.getName(), apiParam.getCaption());
                    } else
                    {
                        for (ApiParam chilParam:apiParam.getChildren())
                        {
                            paramJson.put(chilParam.getName(), chilParam.getCaption());
                        }
                    }
                }
            }
            if (!paramJson.isEmpty()) {
                if (!method.getName().contains("/"))
                {
                    callDemo.put(Environment.Protocol, Environment.jspxNetRoc);
                    callDemo.put(Environment.rocParams, paramJson);
                }
            }
        }
        return StringUtil.trim(callDemo.toString(4));
    }

    @JsonField(name = "methodJson")
    public String getMethodJson() {
        JSONObject paramJson = new JSONObject();
        if (method != null) {

            if (method.getParams() != null && !method.getParams().isEmpty()) {

                for (ApiParam apiParam : method.getParams().values()) {
                    if ("PathVar".equals(apiParam.getFiledType())) {
                        return url;
                    }
                    if (ObjectUtil.isEmpty(apiParam.getChildren()))
                    {
                        paramJson.put(apiParam.getName(), apiParam.getCaption());
                    } else
                    {
                        if ("JSONObject".equalsIgnoreCase(apiParam.getFiledType()))
                        {
                            paramJson.put(apiParam.getName(), apiParam.getCaption());
                        } else
                        {
                            for (ApiParam chilParam:apiParam.getChildren())
                            {
                                paramJson.put(chilParam.getName(), chilParam.getCaption());
                            }
                        }
                    }
                }
            }
        }

        if (params != null) {

            for (ApiParam apiParam : params.values()) {
                if (ObjectUtil.isEmpty(apiParam.getChildren()))
                {
                    paramJson.put(apiParam.getName(), apiParam.getCaption());
                } else
                {
                    if ("JSONObject".equalsIgnoreCase(apiParam.getFiledType()))
                    {
                        paramJson.put(apiParam.getName(), apiParam.getCaption());
                    } else
                    {
                        for (ApiParam chilParam:apiParam.getChildren())
                        {
                            paramJson.put(chilParam.getName(), chilParam.getCaption());
                        }
                    }
                }
            }

        }

        return StringUtil.trim(paramJson.toString(4));
    }
}
