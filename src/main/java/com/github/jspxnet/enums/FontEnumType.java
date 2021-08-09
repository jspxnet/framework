package com.github.jspxnet.enums;
/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2020-1-12
 * Time: 10:20:34
 */

public enum FontEnumType implements EnumType {

    //粗斜体
    BOLD_ITALIC(3, "粗斜体"),
    //斜体
    ITALIC(2, "斜体"),
    //粗体
    BOLD(1, "粗体"),
    //默认
    DEFALUT(0, "默认");
/*
  static public int bold = 1;
    static public int italic = 2;
    static public int boldANDItalic = 3;

    //0:默认;1:粗体;2:斜体;3:粗斜体
 */
    private final int value;
    private final String name;

    FontEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public FontEnumType find(int value) {
        for (FontEnumType c : FontEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return FontEnumType.DEFALUT;
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
