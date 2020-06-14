/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.boot.sign;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-3-17
 * Time: 下午9:45
 */
public class ApprovalType {
    //未知状态
    static public final int UNKNOWN = 10;

    //强制关闭
    static public final int KILLED = 8;

    //结束
    static public final int FINISHED = 9;

    //已批
    static public final int OK = 4;

    //没批
    static public final int NO = 6;

    //阅读
    static public final int READ = 3;

    //待批
    static public final int WAIT = 2;

    //拟稿
    static public final int DRAFT = 0;

    private ApprovalType() {

    }

    //工作流状态图标    理稿 待批 不批 通过 未知
}