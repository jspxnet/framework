package com.github.jspxnet.io.plist;

/**
 * 为了兼容安卓系统
 */
public class Rect {
    private int x;


    private int y;

    private int width;

    private int height;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "},{" + width + "," + height + "}";
    }
}
