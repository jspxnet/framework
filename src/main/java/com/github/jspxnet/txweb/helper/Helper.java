package com.github.jspxnet.txweb.helper;

/**
 * Created by chenyuan on 15-3-30.
 * 帮助配置接口
 */
public interface Helper {

    String getEncode();

    void setEncode(String encode);

    String getConfigFile();

    void setConfigFile(String configFile);

    String getPath();

    void setPath(String path);

    String getId();

    void setId(String url);

    String getJson() throws Exception;

    String getXML() throws Exception;
}
