package com.github.jspxnet.sober.queue;

import java.io.Serializable;


public class CmdContainer implements Serializable {

    private String cmd;
    private String dataJson;
    private String className;

    public CmdContainer()
    {

    }
    public CmdContainer(String cmd, String dataJson,String className) {
        this.cmd = cmd;
        this.dataJson = dataJson;
        this.className = className;
    }

    public boolean isValid() {
        return cmd != null && dataJson != null;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
