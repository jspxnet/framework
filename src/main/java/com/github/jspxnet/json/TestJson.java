package com.github.jspxnet.json;

public class TestJson {
    public static void main(String[] args) {
        String  str = "{\"method\":{\"name\":\"noAuditing\",\"params\":[[\"2ltndr721gmtx638v7l9c6l1\"]]}}";
        JSONObject json = new JSONObject(str);
        System.out.println(json.toString(4));
    }
}
