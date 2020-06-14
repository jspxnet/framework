package com.github.jspxnet.txweb.support;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.helper.Helper;
import com.github.jspxnet.txweb.helper.PageHelper;

import java.io.Writer;

/**
 * Created by chenyuan on 15-3-30.
 * 帮助自动提取
 */
@HttpMethod(caption = "帮助")
public class HelperAction extends ActionSupport {
    public static final String JSON_formatType = "json";
    public static final String XML_formatType = "xml";
    private Helper processor = new PageHelper();
    private String type = "json";

    public HelperAction() {

    }

    public void setConfigFile(String configFile) {
        processor.setConfigFile(configFile);
        processor.setPath(Dispatcher.getRealPath());
    }

    public void setId(String id) {
        processor.setId(id);
    }

    public String getXML() throws Exception {
        return processor.getXML();
    }

    public String getJson() throws Exception {
        return processor.getJson();
    }

    public Helper getProcessor() {
        return processor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String execute() throws Exception {
        //读取文件使用配置的编码
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        processor.setEncode(environmentTemplate.getString(Environment.encode, Environment.defaultEncode));
        Writer writer = response.getWriter();

        if (JSON_formatType.equalsIgnoreCase(type)) {
            response.setContentType("text/javascript; charset=" + processor.getEncode());
            writer.write(getJson());
        } else {
            response.setContentType("text/xml; charset=" + processor.getEncode());
            writer.write("<?xml version=\"1.0\" encoding=\"" + processor.getEncode() + "\"?>\r\n");
            writer.write(getXML());
        }
        writer.flush();
        writer.close();
        return NONE;
    }
}