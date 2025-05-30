package com.github.jspxnet.txweb.ueditor.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;

/**
 * 多状态集合状态
 * 其包含了多个状态的集合, 其本身自己也是一个状态
 *
 * @author hancong03@baidu.com
 */
public class MultiState implements State {

    private boolean state = false;
    private String info = null;
    private Map<String, Long> intMap = new HashMap<>();
    private Map<String, String> infoMap = new HashMap<>();
    private List<State> stateList = new ArrayList<>();

    public MultiState(boolean state) {
        this.state = state;
    }

    public MultiState(boolean state, String info) {
        this.state = state;
        this.info = info;
    }

    public MultiState(boolean state, int infoKey) {
        this.state = state;
        this.info = AppInfo.getStateInfo(infoKey);
    }

    @Override
    public boolean isSuccess() {
        return this.state;
    }

    public void addState(State state) {
        stateList.add(state);
    }

    /**
     * 该方法调用无效果
     */
    @Override
    public void putInfo(String name, String val) {
        this.infoMap.put(name, val);
    }


    @Override
    public JSONObject toJson() {

		/*
		{"state": "SUCCESS","title": "fkscdJieXian2.jpg","original": "fkscd\u63a5\u7ebf.jpg","type": "jpg","url": "upload\2015\fkscdJieXian2.jpg","size": "51811"}
		 */
        JSONObject json = new JSONObject();
        String stateVal = this.isSuccess() ? AppInfo.getStateInfo(AppInfo.SUCCESS) : this.info;
        json.put("state", stateVal);

        // 数字转换
        Iterator<String> iterator = this.intMap.keySet().iterator();
        while (iterator.hasNext()) {
            stateVal = iterator.next();
            json.put(stateVal, this.intMap.get(stateVal));
        }

        iterator = this.infoMap.keySet().iterator();
        while (iterator.hasNext()) {
            stateVal = iterator.next();
            json.put(stateVal, this.infoMap.get(stateVal));
        }

        JSONArray stateJsonArray = new JSONArray();
        for (State state : this.stateList) {
            stateJsonArray.add(state.toJson());
        }
        json.put("list", stateJsonArray);
        return json;
    }

    @Override
    public String toJsonString() {
        return toJson().toString();
    }

    @Override
    public void putInfo(String name, long val) {
        this.intMap.put(name, val);
    }

}
