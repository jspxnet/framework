package com.github.jspxnet.txweb.ueditor.define;

import com.github.jspxnet.json.JSONObject;

public class BaseState implements State {

    private boolean state = false;
    private String info = null;

    private JSONObject json = new JSONObject();

    public BaseState() {
        this.state = true;
    }

    public BaseState(boolean state) {
        this.setState(state);
    }

    public BaseState(boolean state, String info) {
        this.setState(state);
        this.info = info;
        json.put("info", this.info);
    }

    public BaseState(boolean state, int infoCode) {
        this.setState(state);
        this.info = AppInfo.getStateInfo(infoCode);
        json.put("info", this.info);
    }

    @Override
    public boolean isSuccess() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setInfo(int infoCode) {
        this.info = AppInfo.getStateInfo(infoCode);
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    @Override
    public String toJsonString() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        String stateVal = this.isSuccess() ? AppInfo.getStateInfo(AppInfo.SUCCESS) : this.info;
        json.put("state", stateVal);
        return json;
    }

    @Override
    public void putInfo(String name, String val) {
        json.put(name, val);
    }

    @Override
    public void putInfo(String name, long val) {
        json.put(name, val);
    }

}
