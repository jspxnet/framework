package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.enums.EnumType;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/28 18:56
 * description: 图片分组
 **/
public enum ImageSysEnumType implements EnumType {

    //缩图
    THUMBNAIL(2, "缩图"),
    //手机
    MOBILE(1, "手机"),
    //原图
    NONE(0, "原图"),
    //原图
    DELETE(-1, "删除");

    private final int value;
    private final String name;

    ImageSysEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public ImageSysEnumType find(int value) {
        for (ImageSysEnumType c : ImageSysEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return ImageSysEnumType.NONE;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
