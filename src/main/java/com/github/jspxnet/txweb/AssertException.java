package com.github.jspxnet.txweb;

import com.github.jspxnet.utils.ObjectUtil;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/6 22:25
 * description: 通用的异常提示
 *
 * @author chenYuan
 * */
public abstract class AssertException {

    public static void notNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void empty(Object[] array, String message) {
        if (ObjectUtil.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isEmpty(Object array, String message) {
        if (ObjectUtil.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }
}
