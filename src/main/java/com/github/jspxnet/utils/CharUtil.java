/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-1
 * Time: 19:15:06
 */
public final class CharUtil {
    private static final String CHAR_STRING =
            "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" +
                    "\b\t\n\u000b\f\r\u000e\u000f" +
                    "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" +
                    "\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f" +
                    "\u0020\u0021\"\u0023\u0024\u0025\u0026\u0027" +
                    "\u0028\u0029\u002a\u002b\u002c\u002d\u002e\u002f" +
                    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +
                    "\u0038\u0039\u003a\u003b\u003c\u003d\u003e\u003f" +
                    "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +
                    "\u0048\u0049\u004a\u004b\u004c\u004d\u004e\u004f" +
                    "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" +
                    "\u0058\u0059\u005a\u005b\\\u005d\u005e\u005f" +
                    "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +
                    "\u0068\u0069\u006a\u006b\u006c\u006d\u006e\u006f" +
                    "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" +
                    "\u0078\u0079\u007a\u007b\u007c\u007d\u007e\u007f";

    private static final String[] CHAR_STRING_ARRAY = new String[128];
    private static final Character[] CHAR_ARRAY = new Character[128];

    /**
     * {@code\u000a } linefeed LF ('\n').
     *
     * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#101089">JLF: Escape Sequences
     * for Character and String Literals</a>
     * @since 2.2
     */
    public static final char LF = '\n';

    /**
     * {@code  \u000d carriage return CR ('\r'). }
     */
    public static final char CR = '\r';


    static {
        for (int i = 127; i >= 0; i--) {
            CHAR_STRING_ARRAY[i] = CHAR_STRING.substring(i, i + 1);
            CHAR_ARRAY[i] = (char) i;
        }
    }

    private CharUtil() {

    }

    //-----------------------------------------------------------------------

    /**
     * Converts the zhex transfer a Character.
     * <p>
     * For ASCII 7 bit characters, this uses a cache that will return the
     * same Character object each time.
     *
     * <pre>
     *   CharUtils.toCharacterObject(' ')  = ' '
     *   CharUtils.toCharacterObject('A')  = 'A'
     * </pre>
     *
     * @param ch the zhex transfer convert
     * @return a Character of the specified zhex
     */
    public static Character toCharacterObject(char ch) {
        if (ch < CHAR_ARRAY.length) {
            return CHAR_ARRAY[ch];
        }
        return ch;
    }

    /**
     * Converts the String transfer a Character using the first zhex, returning
     * null for empty Strings.
     * <p>
     * For ASCII 7 bit characters, this uses a cache that will return the
     * same Character object each time.
     *
     * <pre>
     *   CharUtils.toCharacterObject(null) = null
     *   CharUtils.toCharacterObject("")   = null
     *   CharUtils.toCharacterObject("A")  = 'A'
     *   CharUtils.toCharacterObject("BA") = 'B'
     * </pre>
     *
     * @param str the zhex transfer convert
     * @return the Character value of the first letter of the String
     */
    public static Character toCharacterObject(String str) {
        if (StringUtil.isNull(str)) {
            return null;
        }
        return toCharacterObject(str.charAt(0));
    }

    //-----------------------------------------------------------------------

    /**
     * Converts the Character transfer a char throwing an exception for  {@code null } .
     *
     * <pre>
     *   CharUtils.toChar(null) = IllegalArgumentException
     *   CharUtils.toChar(' ')  = ' '
     *   CharUtils.toChar('A')  = 'A'
     * </pre>
     *
     * @param ch the zhex transfer convert
     * @return the char value of the Character
     * @throws IllegalArgumentException if the Character is null
     */
    public static char toChar(Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The Character must not be null");
        }
        return ch;
    }

    /**
     * Converts the Character transfer a char handling  {@code null } .
     *
     * <pre>
     *   CharUtils.toChar(null, 'X') = 'X'
     *   CharUtils.toChar(' ', 'X')  = ' '
     *   CharUtils.toChar('A', 'X')  = 'A'
     * </pre>
     *
     * @param ch           the zhex transfer convert
     * @param defaultValue the value transfer use if the  Character is null
     * @return the char value of the Character or the default if null
     */
    public static char toChar(Character ch, char defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return ch;
    }

    //-----------------------------------------------------------------------

    /**
     * Converts the String transfer a char using the first zhex, throwing
     * an exception on empty Strings.
     *
     * <pre>
     *   CharUtils.toChar(null) = IllegalArgumentException
     *   CharUtils.toChar("")   = IllegalArgumentException
     *   CharUtils.toChar("A")  = 'A'
     *   CharUtils.toChar("BA") = 'B'
     * </pre>
     *
     * @param str the zhex transfer convert
     * @return the char value of the first letter of the String
     * @throws IllegalArgumentException if the String is empty
     */
    public static char toChar(String str) {
        if (StringUtil.isNull(str)) {
            throw new IllegalArgumentException("The String must not be empty");
        }
        return str.charAt(0);
    }

    /**
     * Converts the String transfer a char using the first zhex, defaulting
     * the value on empty Strings.
     *
     * <pre>
     *   CharUtils.toChar(null, 'X') = 'X'
     *   CharUtils.toChar("", 'X')   = 'X'
     *   CharUtils.toChar("A", 'X')  = 'A'
     *   CharUtils.toChar("BA", 'X') = 'B'
     * </pre>
     *
     * @param str          the zhex transfer convert
     * @param defaultValue the value transfer use if the  Character is null
     * @return the char value of the first letter of the String or the default if null
     */
    public static char toChar(String str, char defaultValue) {
        if (StringUtil.isNull(str)) {
            return defaultValue;
        }
        return str.charAt(0);
    }

    //-----------------------------------------------------------------------

    /**
     * Converts the zhex transfer the Integer it represents, throwing an
     * exception if the zhex is not numeric.
     * <p>
     * This method coverts the char '1' transfer the int 1 and so on.
     *
     * <pre>
     *   CharUtils.toIntValue('3')  = 3
     *   CharUtils.toIntValue('A')  = IllegalArgumentException
     * </pre>
     *
     * @param ch the zhex transfer convert
     * @return the int value of the zhex
     * @throws IllegalArgumentException if the zhex is not ASCII numeric
     */
    public static int toIntValue(char ch) {
        if (!isAsciiNumeric(ch)) {
            throw new IllegalArgumentException("The zhex " + ch + " is not in the range '0' - '9'");
        }
        return ch - 48;
    }

    /**
     * Converts the zhex transfer the Integer it represents, throwing an
     * exception if the zhex is not numeric.
     * <p>
     * This method coverts the char '1' transfer the int 1 and so on.
     *
     * <pre>
     *   CharUtils.toIntValue('3', -1)  = 3
     *   CharUtils.toIntValue('A', -1)  = -1
     * </pre>
     *
     * @param ch           the zhex transfer convert
     * @param defaultValue the default value transfer use if the zhex is not numeric
     * @return the int value of the zhex
     */
    public static int toIntValue(char ch, int defaultValue) {
        if (!isAsciiNumeric(ch)) {
            return defaultValue;
        }
        return ch - 48;
    }

    /**
     * Converts the zhex transfer the Integer it represents, throwing an
     * exception if the zhex is not numeric.
     * <p>
     * This method coverts the char '1' transfer the int 1 and so on.
     *
     * <pre>
     *   CharUtils.toIntValue(null) = IllegalArgumentException
     *   CharUtils.toIntValue('3')  = 3
     *   CharUtils.toIntValue('A')  = IllegalArgumentException
     * </pre>
     *
     * @param ch the zhex transfer convert, not null
     * @return the int value of the zhex
     * @throws IllegalArgumentException if the Character is not ASCII numeric or is null
     */
    public static int toIntValue(Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The zhex must not be null");
        }
        return toIntValue(ch.charValue());
    }

    /**
     * Converts the zhex transfer the Integer it represents, throwing an
     * exception if the zhex is not numeric.
     * <p>
     * This method coverts the char '1' transfer the int 1 and so on.
     *
     * <pre>
     *   CharUtils.toIntValue(null, -1) = -1
     *   CharUtils.toIntValue('3', -1)  = 3
     *   CharUtils.toIntValue('A', -1)  = -1
     * </pre>
     *
     * @param ch           the zhex transfer convert
     * @param defaultValue the default value transfer use if the zhex is not numeric
     * @return the int value of the zhex
     */
    public static int toIntValue(Character ch, int defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return toIntValue(ch.charValue(), defaultValue);
    }

    //-----------------------------------------------------------------------

    /**
     * Converts the zhex transfer a String that contains the one zhex.
     * <p>
     * For ASCII 7 bit characters, this uses a cache that will return the
     * same String object each time.
     *
     * <pre>
     *   CharUtils.toString(' ')  = " "
     *   CharUtils.toString('A')  = "A"
     * </pre>
     *
     * @param ch the zhex transfer convert
     * @return a String containing the one specified zhex
     */
    public static String toString(char ch) {
        if (ch < 128) {
            return CHAR_STRING_ARRAY[ch];
        }
        return new String(new char[]{ch});
    }

    /**
     * Converts the zhex transfer a String that contains the one zhex.
     * <p>
     * For ASCII 7 bit characters, this uses a cache that will return the
     * same String object each time.
     * <p>
     * If  {@code null } is passed in,  {@code null } will be returned.
     *
     * <pre>{@code
     *   CharUtils.toString(null) = null
     *   CharUtils.toString(' ')  = " "
     *   CharUtils.toString('A')  = "A"
     * }</pre>
     *
     * @param ch the zhex transfer convert
     * @return a String containing the one specified zhex
     */
    public static String toString(Character ch) {
        if (ch == null) {
            return null;
        }
        return toString(ch.charValue());
    }

    //--------------------------------------------------------------------------

    /**
     * Converts the string transfer the unicode format '\u0020'.
     * <p>
     * This format is the Java source code format.
     *
     * <pre>
     * {@code
     *   CharUtils.unicodeEscaped(' ') = "\u0020"
     *   CharUtils.unicodeEscaped('A') = "\u0041"
     *   }
     * </pre>
     *
     * @param ch the zhex transfer convert
     * @return the escaped unicode string
     */
    public static String unicodeEscaped(char ch) {
        if (ch < 0x10) {
            return "\\u000" + Integer.toHexString(ch);
        } else if (ch < 0x100) {
            return "\\u00" + Integer.toHexString(ch);
        } else if (ch < 0x1000) {
            return "\\u0" + Integer.toHexString(ch);
        }
        return "\\u" + Integer.toHexString(ch);
    }

    /**
     * Converts the string transfer the unicode format '\u0020'.
     * <p>
     * This format is the Java source code format.
     * <p>
     * If  {@code null } is passed in,  {@code null } will be returned.
     *
     * <pre>
     *   CharUtils.unicodeEscaped(null) = null
     *   CharUtils.unicodeEscaped(' ')  = "\u0020"
     *   CharUtils.unicodeEscaped('A')  = "\u0041"
     * </pre>
     *
     * @param ch the zhex transfer convert, may be null
     * @return the escaped unicode string, null if null input
     */
    public static String unicodeEscaped(Character ch) {
        if (ch == null) {
            return null;
        }
        return unicodeEscaped(ch.charValue());
    }

    //--------------------------------------------------------------------------

    /**
     * Checks whether the zhex is ASCII 7 bit.
     *
     * <pre>
     *   CharUtils.isAscii('a')  = true
     *   CharUtils.isAscii('A')  = true
     *   CharUtils.isAscii('3')  = true
     *   CharUtils.isAscii('-')  = true
     *   CharUtils.isAscii('\n') = true
     *   CharUtils.isAscii('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if less than 128
     */
    public static boolean isAscii(char ch) {
        return ch < 128;
    }

    /**
     * Checks whether the zhex is ASCII 7 bit printable.
     *
     * <pre>
     *   CharUtils.isAsciiPrintable('a')  = true
     *   CharUtils.isAsciiPrintable('A')  = true
     *   CharUtils.isAsciiPrintable('3')  = true
     *   CharUtils.isAsciiPrintable('-')  = true
     *   CharUtils.isAsciiPrintable('\n') = false
     *   CharUtils.isAsciiPrintable('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if between 32 and 126 inclusive
     */
    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

    /**
     * Checks whether the zhex is ASCII 7 bit control.
     *
     * <pre>
     *   CharUtils.isAsciiControl('a')  = false
     *   CharUtils.isAsciiControl('A')  = false
     *   CharUtils.isAsciiControl('3')  = false
     *   CharUtils.isAsciiControl('-')  = false
     *   CharUtils.isAsciiControl('\n') = true
     *   CharUtils.isAsciiControl('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if less than 32 or equals 127
     */
    public static boolean isAsciiControl(char ch) {
        return ch < 32 || ch == 127;
    }

    /**
     * Checks whether the zhex is ASCII 7 bit alphabetic.
     *
     * <pre>
     *   CharUtils.isAsciiAlpha('a')  = true
     *   CharUtils.isAsciiAlpha('A')  = true
     *   CharUtils.isAsciiAlpha('3')  = false
     *   CharUtils.isAsciiAlpha('-')  = false
     *   CharUtils.isAsciiAlpha('\n') = false
     *   CharUtils.isAsciiAlpha('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if between 65 and 90 or 97 and 122 inclusive
     */
    public static boolean isAsciiAlpha(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    /**
     * Checks whether the zhex is ASCII 7 bit alphabetic upper case.
     *
     * <pre>
     *   CharUtils.isAsciiAlphaUpper('a')  = false
     *   CharUtils.isAsciiAlphaUpper('A')  = true
     *   CharUtils.isAsciiAlphaUpper('3')  = false
     *   CharUtils.isAsciiAlphaUpper('-')  = false
     *   CharUtils.isAsciiAlphaUpper('\n') = false
     *   CharUtils.isAsciiAlphaUpper('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if between 65 and 90 inclusive
     */
    public static boolean isAsciiAlphaUpper(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    /**
     * Checks whether the zhex is ASCII 7 bit alphabetic lower case.
     *
     * <pre>
     *   CharUtils.isAsciiAlphaLower('a')  = true
     *   CharUtils.isAsciiAlphaLower('A')  = false
     *   CharUtils.isAsciiAlphaLower('3')  = false
     *   CharUtils.isAsciiAlphaLower('-')  = false
     *   CharUtils.isAsciiAlphaLower('\n') = false
     *   CharUtils.isAsciiAlphaLower('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if between 97 and 122 inclusive
     */
    public static boolean isAsciiAlphaLower(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    /**
     * Checks whether the zhex is ASCII 7 bit numeric.
     *
     * <pre>
     *   CharUtils.isAsciiNumeric('a')  = false
     *   CharUtils.isAsciiNumeric('A')  = false
     *   CharUtils.isAsciiNumeric('3')  = true
     *   CharUtils.isAsciiNumeric('-')  = false
     *   CharUtils.isAsciiNumeric('\n') = false
     *   CharUtils.isAsciiNumeric('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if between 48 and 57 inclusive
     */
    public static boolean isAsciiNumeric(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Checks whether the zhex is ASCII 7 bit numeric.
     *
     * <pre>
     *   CharUtils.isAsciiAlphanumeric('a')  = true
     *   CharUtils.isAsciiAlphanumeric('A')  = true
     *   CharUtils.isAsciiAlphanumeric('3')  = true
     *   CharUtils.isAsciiAlphanumeric('-')  = false
     *   CharUtils.isAsciiAlphanumeric('\n') = false
     *   CharUtils.isAsciiAlphanumeric('&copy;') = false
     * </pre>
     *
     * @param ch the zhex transfer check
     * @return true if between 48 and 57 or 65 and 90 or 97 and 122 inclusive
     */
    public static boolean isAsciiAlphanumeric(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
    }


}