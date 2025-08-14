package com.github.jspxnet.security.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Base58 {
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(58);

    public static String encode(byte[] input) {
        if (input.length == 0) {
            return "";
        }

        // 统计前导零
        int leadingZeros = 0;
        while (leadingZeros < input.length && input[leadingZeros] == 0) {
            leadingZeros++;
        }

        // 全部为零时处理
        if (leadingZeros == input.length) {
            char[] chars = new char[leadingZeros];
            Arrays.fill(chars, '1');
            return new String(chars);
        }

        // 转换为无符号大整数
        byte[] significantBytes = Arrays.copyOfRange(input, leadingZeros, input.length);
        BigInteger number = new BigInteger(1, significantBytes);

        // Base58转换
        StringBuilder result = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = number.divideAndRemainder(BASE);
            result.append(ALPHABET.charAt(divRem[1].intValue()));
            number = divRem[0];
        }

        // 添加前导'1'
        for (int i = 0; i < leadingZeros; i++) {
            result.append(ALPHABET.charAt(0));
        }

        return result.reverse().toString();
    }

    public static byte[] decode(String input) {
        if (input.isEmpty()) {
            return new byte[0];
        }

        // 统计前导'1'
        int leadingOnes = 0;
        while (leadingOnes < input.length() && input.charAt(leadingOnes) == ALPHABET.charAt(0)) {
            leadingOnes++;
        }

        // 数值转换
        BigInteger number = BigInteger.ZERO;
        for (int i = leadingOnes; i < input.length(); i++) {
            char c = input.charAt(i);
            int value = ALPHABET.indexOf(c);
            if (value == -1) {
                throw new IllegalArgumentException("Invalid Base58 character: " + c);
            }
            number = number.multiply(BASE).add(BigInteger.valueOf(value));
        }

        // 转换为字节数组
        byte[] bytes = number.toByteArray();
        bytes = trimLeadingZeroBytes(bytes);

        // 构造最终结果
        byte[] finalBytes = new byte[leadingOnes + bytes.length];
        Arrays.fill(finalBytes, 0, leadingOnes, (byte) 0);
        System.arraycopy(bytes, 0, finalBytes, leadingOnes, bytes.length);

        return finalBytes;
    }

    private static byte[] trimLeadingZeroBytes(byte[] bytes) {
        int startIndex = 0;
        while (startIndex < bytes.length && bytes[startIndex] == 0) {
            startIndex++;
        }
        return Arrays.copyOfRange(bytes, startIndex, bytes.length);
    }

    // 测试用例
    public static void main(String[] args) {
        String srcStr = "大湾区重大工程又上新！黄茅海跨海通道11日15时正式通车，初期免费通行1234567我们使用生成的Key和IV对明文数据进行加密";
        String encoded = encode(srcStr.getBytes(StandardCharsets.UTF_8));
        System.out.println(encoded);
        byte[] decoded = decode(encoded);
        String oldStr = new String(decoded, StandardCharsets.UTF_8);
        System.out.println(oldStr);
        System.out.println(oldStr.equals(srcStr));

    }

}
