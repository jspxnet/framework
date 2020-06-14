/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.utils.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-1
 * Time: 8:58:22
 */

public class RandomGenerator {
    private static final List<Integer> cacheInt = new ArrayList<Integer>(16);

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    private int low = 1;

    private int high = 100;


    /**
     * Generates a random integer in a range of integers
     *
     * @return a random integer
     */

    public int getRandomInt() {
        int r = low + (int) ((high - low + 1) * nextRandom());
        if (r > high) {
            r = high;
        }
        if (cacheInt.contains(r)) {
            return getRandomInt();

        }
        cacheInt.add(r);
        if (cacheInt.size() > 16) {
            cacheInt.clear();
        }
        return r;
    }

    public String getKeepLength(int len) {
        return NumberUtil.getKeepLength(getRandomInt(), len);
    }

    /**
     * testaio stub for the class
     * <p>
     * public static void main(String[] args)
     * {
     * RandomGenerator r1 = new RandomGenerator(1, 10);
     * RandomGenerator r2 = new RandomGenerator(0, 1);
     * int i;
     * for (i = 1; i <= 100; i++)
     * System.out.println(r1.getRandomInt() + " " + r2.getRandomInt());
     * }
     */
    private static double nextRandom() {
        int pos = (int) (java.lang.Math.random() * BUFFER_SIZE);
        if (pos == BUFFER_SIZE) {
            pos = BUFFER_SIZE - 1;
        }
        double r = buffer[pos];
        buffer[pos] = java.lang.Math.random();
        return r;
    }

    private static final int BUFFER_SIZE = 101;
    private static double[] buffer = new double[BUFFER_SIZE];

    static {
        int i;
        for (i = 0; i < BUFFER_SIZE; i++) {
            buffer[i] = java.lang.Math.random();
        }
    }

    public static void main(String[] args) {
        //1270462611852 + 5
        System.out.println(UUID.randomUUID().getLeastSignificantBits());
        System.out.println(UUID.randomUUID().getMostSignificantBits());

        RandomGenerator randomGenerator = new RandomGenerator();
        randomGenerator.setLow(1);
        randomGenerator.setHigh(999999);


        int i;
        for (i = 1; i <= 10; i++) {
            if (i > 4) {
                randomGenerator.setHigh(9);
            }
            if (i > 8) {
                randomGenerator.setHigh(99);
            }
            System.out.println(randomGenerator.getKeepLength(4));
        }
    }

}