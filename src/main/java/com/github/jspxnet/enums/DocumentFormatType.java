package com.github.jspxnet.enums;


/**
 *
 *
 * @author chenYuan
 */
public enum DocumentFormatType implements EnumType {
    //html
    HTML(0, "html"),

    //markdown
    MARKDOWN(1, "markdown"),

    //link
    LINK(2, "link"),

    //base64
    BASE64(3, "base64");


    private final int value;
    private final String name;

    DocumentFormatType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public DocumentFormatType find(int value) {
        for (DocumentFormatType c : DocumentFormatType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return DocumentFormatType.HTML;
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
