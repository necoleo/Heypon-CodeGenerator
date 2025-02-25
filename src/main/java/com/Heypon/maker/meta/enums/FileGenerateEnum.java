package com.Heypon.maker.meta.enums;

/**
 * 文件生成方式枚举类
 */
public enum FileGenerateEnum {

    STATIC("静态生成", "static"),
    DYNAMIC("动态生成", "dynamic");

    private final String text;
    private final String value;

    FileGenerateEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
