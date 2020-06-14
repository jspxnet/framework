package com.github.jspxnet.txweb.ueditor.define;

import java.util.HashMap;
import java.util.Map;

public class FileType {

    public static final String JPG = "JPG";
    public static final String PNG = "PNG";
    public static final String GIF = "gif";

    private static final Map<String, String> types = new HashMap<String, String>() {{
        put(FileType.JPG, ".jpg");
        put(FileType.PNG, ".png");
        put(FileType.GIF, ".gif");
    }};

    public static String getSuffix(String key) {
        return FileType.types.get(key);
    }
}
