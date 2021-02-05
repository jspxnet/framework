package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ZipUtil;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/1/20 11:36
 * description: 签名数据
 * data并没有加密,而是压缩的数据
 */
@Data
public class SignParam implements Serializable {
    @Param(caption = "订单数据")
    private String data;

    @Param(caption = "类对象")
    private String className;

    @Param(caption = "签名")
    private String sign;

    @Param(caption = "签名方式")
    private String signType;

    public <T> T getBean()
    {
        if (StringUtil.isEmpty(data)||StringUtil.isEmpty(className))
        {
            return null;
        }
        try {
            return (T) getBean(ClassUtil.loadClass(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public <T> T getBean(Class<T> tClass)
    {
        if (StringUtil.isEmpty(data))
        {
            return null;
        }
        String desData = ZipUtil.getZipBase64Decode(data);
        JSONObject json = new JSONObject(desData);
        return json.parseObject(tClass);
    }

    @Override
    public String toString()
    {
        JSONObject json = new JSONObject();
        json.put("data",data);
        json.put("className",className);
        json.put("sign",sign);
        json.put("signType",signType);
        return json.toString();
    }


}
