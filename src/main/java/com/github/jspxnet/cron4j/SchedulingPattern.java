/*
 * cron4j - A pure Java cron-like scheduler
 *
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.jspxnet.cron4j;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * A UNIX crontab-like pattern is a string split in five space separated parts.
 * Each part is intented as:
 * </p>
 * <ol>
 * <li><strong>Minutes sub-pattern</strong>. During which minutes of the hour
 * should the task been launched? The values range is from 0 to 59.</li>
 * <li><strong>Hours sub-pattern</strong>. During which hours of the day should
 * the task been launched? The values range is from 0 to 23.</li>
 * <li><strong>Days of month sub-pattern</strong>. During which days of the
 * month should the task been launched? The values range is from 1 to 31. The
 * special value L can be used to recognize the last day of month.</li>
 * <li><strong>Months sub-pattern</strong>. During which months of the year
 * should the task been launched? The values range is from 1 (January) to 12
 * (December), otherwise this sub-pattern allows the aliases &quot;jan&quot;,
 * &quot;feb&quot;, &quot;mar&quot;, &quot;apr&quot;, &quot;may&quot;,
 * &quot;jun&quot;, &quot;jul&quot;, &quot;aug&quot;, &quot;sep&quot;,
 * &quot;oct&quot;, &quot;nov&quot; and &quot;dec&quot;.</li>
 * <li><strong>Days of week sub-pattern</strong>. During which days of the week
 * should the task been launched? The values range is from 0 (Sunday) to 6
 * (Saturday), otherwise this sub-pattern allows the aliases &quot;sun&quot;,
 * &quot;mon&quot;, &quot;tue&quot;, &quot;wed&quot;, &quot;thu&quot;,
 * &quot;fri&quot; and &quot;sat&quot;.</li>
 * </ol>
 * <p>
 * The star wildcard character is also admitted, indicating &quot;every minute
 * of the hour&quot;, &quot;every hour of the day&quot;, &quot;every day of the
 * month&quot;, &quot;every month of the year&quot; and &quot;every day of the
 * week&quot;, according to the sub-pattern in which it is used.
 * </p>
 * <p>
 * Once the scheduler is started, a task will be launched when the five parts in
 * its scheduling pattern will be true at the same time.
 * </p>
 *
 * @author Carlo Pelliccia
 * @since 2.0
 */
public class SchedulingPattern implements Serializable {
    /**
     * Months aliases.
     */
    private static final String[] ALIASES = {"jan", "feb", "mar", "apr", "may",
            "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

    /**
     * The parser for the second values.
     */
    private static final ValueParser SECONDS_VALUE_PARSER = new SecondsValueParser();

    /**
     * The parser for the minute values.
     */
    private static final ValueParser MINUTE_VALUE_PARSER = new MinuteValueParser();

    /**
     * The parser for the hour values.
     */
    private static final ValueParser HOUR_VALUE_PARSER = new HourValueParser();

    /**
     * The parser for the day of month values.
     */
    private static final ValueParser DAY_OF_MONTH_VALUE_PARSER = new DayOfMonthValueParser();

    /**
     * The parser for the month values.
     */
    private static final ValueParser MONTH_VALUE_PARSER = new MonthValueParser();

    /**
     * The parser for the day of week values.
     */
    private static final ValueParser DAY_OF_WEEK_VALUE_PARSER = new DayOfWeekValueParser();

    /**
     * Validates a string as a scheduling pattern.
     *
     * @param schedulingPattern The pattern to validate.
     * @return true if the given string represents a valid scheduling pattern;
     * false otherwise.
     */
    public static boolean validate(String schedulingPattern) {
        try {
            new SchedulingPattern(schedulingPattern);
        } catch (InvalidPatternException e) {
            return false;
        }
        return true;
    }

    /**
     * The pattern as a string.
     */
    private final String asString;

    /**
     * The ValueMatcher list for the "minute" field.
     */
    protected final List<ValueMatcher> secondsMatchers = new ArrayList<>();

    /**
     * The ValueMatcher list for the "minute" field.
     */
    protected final List<ValueMatcher> minuteMatchers = new ArrayList<>();

    /**
     * The ValueMatcher list for the "hour" field.
     */
    protected final List<ValueMatcher> hourMatchers = new ArrayList<>();

    /**
     * The ValueMatcher list for the "day of month" field.
     */
    protected final List<ValueMatcher> dayOfMonthMatchers = new ArrayList<>();

    /**
     * The ValueMatcher list for the "month" field.
     */
    protected final List<ValueMatcher> monthMatchers = new ArrayList<>();

    /**
     * The ValueMatcher list for the "day of week" field.
     */
    protected final List<ValueMatcher> dayOfWeekMatchers = new ArrayList<>();

    /**
     * How many matcher groups in this pattern?
     */
    protected int matcherSize = 0;

    /**
     * Builds a SchedulingPattern parsing it from a string.
     *
     * @param pattern The pattern as a crontab-like string.
     * @throws InvalidPatternException If the supplied string is not a valid pattern.
     */
    public SchedulingPattern(String pattern) throws InvalidPatternException {
        if (StringUtil.isEmpty(pattern) || "* * * * *".equalsIgnoreCase(pattern)) {
            pattern = "0 */1 * * * *";
        }
        if (pattern.split(" ").length == 5) {
            pattern = "0 " + pattern;
        }

        if (pattern.startsWith("* ")) {
            //修复开始秒为 * 的，秒必填
            pattern = DateUtil.getSecond() + " " + StringUtil.substringAfter(pattern, "* ");
        }
        this.asString = pattern;
        StringTokenizer st1 = new StringTokenizer(pattern, "|");
        if (st1.countTokens() < 1) {
            throw new InvalidPatternException("invalid pattern: \"" + pattern + "\"");
        }
        while (st1.hasMoreTokens()) {
            String localPattern = st1.nextToken();
            StringTokenizer st2 = new StringTokenizer(localPattern, " \t");
            if (st2.countTokens() != 6) {
                throw new InvalidPatternException("invalid pattern: \"" + localPattern + "\"");
            }

            try {
                secondsMatchers.add(buildValueMatcher(st2.nextToken(), SECONDS_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing seconds field: "
                        + e.getMessage() + StringUtil.DOT);
            }

            try {

                minuteMatchers.add(buildValueMatcher(st2.nextToken(), MINUTE_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing minutes field: "
                        + e.getMessage() + StringUtil.DOT);
            }
            try {
                hourMatchers.add(buildValueMatcher(st2.nextToken(), HOUR_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing hours field: "
                        + e.getMessage() + StringUtil.DOT);
            }
            try {
                dayOfMonthMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_MONTH_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern
                        + "\". Error parsing days of month field: "
                        + e.getMessage() + StringUtil.DOT);
            }
            try {
                monthMatchers.add(buildValueMatcher(st2.nextToken(), MONTH_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing months field: "
                        + e.getMessage() + StringUtil.DOT);
            }
            try {
                dayOfWeekMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_WEEK_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern
                        + "\". Error parsing days of week field: "
                        + e.getMessage() + StringUtil.DOT);
            }
            matcherSize++;
        }
    }

    /**
     * A ValueMatcher utility builder.
     *
     * @param str    The pattern part for the ValueMatcher creation.
     * @param parser The parser used to parse the values.
     * @return The requested ValueMatcher.
     * @throws Exception If the supplied pattern part is not valid.
     */
    private static ValueMatcher buildValueMatcher(String str, ValueParser parser)
            throws Exception {

        if (str == null || str.equals(StringUtil.ASTERISK)) {
            return new AlwaysTrueValueMatcher();
        }
        List<Integer> values = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            List<Integer> local;
            try {
                local = parseListElement(element, parser);
            } catch (Exception e) {
                throw new Exception("invalid field \"" + str
                        + "\", invalid element \"" + element + "\", "
                        + e.getMessage());
            }
            for (Integer value : local) {
                if (!values.contains(value)) {
                    values.add(value);
                }
            }
        }
        if (values.isEmpty()) {
            throw new Exception("invalid field \"" + str + "\"");
        }
        if (parser == DAY_OF_MONTH_VALUE_PARSER) {
            return new DayOfMonthValueMatcher(values);
        } else {
            return new IntArrayValueMatcher(values);
        }
    }

    /**
     * Parses an element of a list of values of the pattern.
     *
     * @param str    The element string.
     * @param parser The parser used to parse the values.
     * @return A list of integers representing the allowed values.
     * @throws Exception If the supplied pattern part is not valid.
     */
    private static List<Integer> parseListElement(String str, ValueParser parser)
            throws Exception {
        StringTokenizer st = new StringTokenizer(str, "/");
        int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        List<Integer> values;
        try {
            values = parseRange(st.nextToken(), parser);
        } catch (Exception e) {
            throw new Exception("invalid range, " + e.getMessage());
        }
        if (size == 2) {
            String dStr = st.nextToken();
            int div;
            try {
                div = Integer.parseInt(dStr);
            } catch (NumberFormatException e) {
                throw new Exception("invalid divisor \"" + dStr + "\"");
            }
            if (div < 1) {
                throw new Exception("non positive divisor \"" + div + "\"");
            }
            List<Integer> values2 = new ArrayList<>();
            for (int i = 0; i < values.size(); i += div) {
                values2.add(values.get(i));
            }
            return values2;
        } else {
            return values;
        }
    }

    /**
     * Parses a range of values.
     *
     * @param str    The range string.
     * @param parser The parser used to parse the values.
     * @return A list of integers representing the allowed values.
     * @throws Exception If the supplied pattern part is not valid.
     */
    private static List<Integer> parseRange(String str, ValueParser parser)
            throws Exception {
        if (str.equals(StringUtil.ASTERISK) || str.equals(StringUtil.QUESTION)) {
            int min = parser.getMinValue();
            int max = parser.getMaxValue();
            List<Integer> values = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                values.add(i);
            }
            return values;
        }
        StringTokenizer st = new StringTokenizer(str, "-");
        int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        String v1Str = st.nextToken();
        int v1;
        try {
            v1 = parser.parse(v1Str);
        } catch (Exception e) {
            throw new Exception("invalid value \"" + v1Str + "\", "
                    + e.getMessage());
        }
        if (size == 1) {
            List<Integer> values = new ArrayList<>();
            values.add(v1);
            return values;
        } else {
            String v2Str = st.nextToken();
            int v2;
            try {
                v2 = parser.parse(v2Str);
            } catch (Exception e) {
                throw new Exception("invalid value \"" + v2Str + "\", "
                        + e.getMessage());
            }
            List<Integer> values = new ArrayList<>();
            if (v1 < v2) {
                for (int i = v1; i <= v2; i++) {
                    values.add(i);
                }
            } else if (v1 > v2) {
                int min = parser.getMinValue();
                int max = parser.getMaxValue();
                for (int i = v1; i <= max; i++) {
                    values.add(i);
                }
                for (int i = min; i <= v2; i++) {
                    values.add(i);
                }
            } else {
                // v1 == v2
                values.add(v1);
            }
            return values;
        }
    }

    /**
     * This methods returns true if the given timestamp (expressed as a UNIX-era
     * millis value) matches the pattern, according to the given time zone.
     *
     * @param timezone A time zone.
     * @param millis   The timestamp, as a UNIX-era millis value.
     * @return true if the given timestamp matches the pattern.
     */
    public boolean match(TimeZone timezone, long millis) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(millis);
        gc.setTimeZone(timezone);
        int seconds = gc.get(Calendar.SECOND);
        int minute = gc.get(Calendar.MINUTE);
        int hour = gc.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        int month = gc.get(Calendar.MONTH) + 1;
        int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK) - 1;
        int year = gc.get(Calendar.YEAR);
        for (int i = 0; i < matcherSize; i++) {
            ValueMatcher secondsMatcher = secondsMatchers.get(i);
            ValueMatcher minuteMatcher = minuteMatchers.get(i);
            ValueMatcher hourMatcher = hourMatchers.get(i);
            ValueMatcher dayOfMonthMatcher = dayOfMonthMatchers.get(i);
            ValueMatcher monthMatcher = monthMatchers.get(i);
            ValueMatcher dayOfWeekMatcher = dayOfWeekMatchers.get(i);

            boolean eval = secondsMatcher.match(seconds)
                    && minuteMatcher.match(minute)
                    && hourMatcher.match(hour)
                    && ((dayOfMonthMatcher instanceof DayOfMonthValueMatcher) ? ((DayOfMonthValueMatcher) dayOfMonthMatcher)
                    .match(dayOfMonth, month, gc.isLeapYear(year))
                    : dayOfMonthMatcher.match(dayOfMonth))
                    && monthMatcher.match(month)
                    && dayOfWeekMatcher.match(dayOfWeek);
            if (eval) {
                return true;
            }
        }
        return false;
    }

    /**
     * This methods returns true if the given timestamp (expressed as a UNIX-era
     * millis value) matches the pattern, according to the system default time
     * zone.
     *
     * @param millis The timestamp, as a UNIX-era millis value.
     * @return true if the given timestamp matches the pattern.
     */
    public boolean match(long millis) {
        return match(TimeZone.getDefault(), millis);
    }

    /**
     * Returns the pattern as a string.
     *
     * @return The pattern as a string.
     */

    @Override
    public String toString() {
        return asString;
    }

    /**
     * This utility method changes an alias to an int value.
     *
     * @param value   The value.
     * @param aliases The aliases list.
     * @param offset  The offset appplied to the aliases list indices.
     * @return The parsed value.
     * @throws Exception If the expressed values doesn't match any alias.
     */
    private static int parseAlias(String value, String[] aliases, int offset)
            throws Exception {
        for (int i = 0; i < aliases.length; i++) {
            if (aliases[i].equalsIgnoreCase(value)) {
                return offset + i;
            }
        }
        throw new Exception("invalid alias \"" + value + "\"");
    }

    /**
     * Definition for a value parser.
     */
    private interface ValueParser {

        /**
         * Attempts to parse a value.
         *
         * @param value The value.
         * @return The parsed value.
         * @throws Exception If the value can't be parsed.
         */
        int parse(String value) throws Exception;

        /**
         * Returns the minimum value accepred by the parser.
         *
         * @return The minimum value accepred by the parser.
         */
        int getMinValue();

        /**
         * Returns the maximum value accepred by the parser.
         *
         * @return The maximum value accepred by the parser.
         */
        int getMaxValue();

    }

    /**
     * A simple value parser.
     */
    private static class SimpleValueParser implements ValueParser {

        /**
         * The minimum allowed value.
         */
        protected int minValue;

        /**
         * The maximum allowed value.
         */
        protected int maxValue;

        /**
         * Builds the value parser.
         *
         * @param minValue The minimum allowed value.
         * @param maxValue The maximum allowed value.
         */
        public SimpleValueParser(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public int parse(String value) throws Exception {
            int i;
            try {
                i = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new Exception("invalid integer value");
            }
            if (i < minValue || i > maxValue) {
                throw new Exception("value out of range");
            }
            return i;
        }

        @Override
        public int getMinValue() {
            return minValue;
        }

        @Override
        public int getMaxValue() {
            return maxValue;
        }

    }

    /**
     * The minutes value parser.
     */
    private static class SecondsValueParser extends SimpleValueParser {

        /**
         * Builds the value parser.
         */
        public SecondsValueParser() {
            super(0, 59);
        }

    }

    /**
     * The minutes value parser.
     */
    private static class MinuteValueParser extends SimpleValueParser {

        /**
         * Builds the value parser.
         */
        public MinuteValueParser() {
            super(0, 59);
        }

    }

    /**
     * The hours value parser.
     */
    private static class HourValueParser extends SimpleValueParser {

        /**
         * Builds the value parser.
         */
        public HourValueParser() {
            super(0, 23);
        }

    }

    /**
     * The days of month value parser.
     */
    private static class DayOfMonthValueParser extends SimpleValueParser {

        /**
         * Builds the value parser.
         */
        public DayOfMonthValueParser() {
            super(1, 31);
        }

        /**
         * Added to support last-day-of-month.
         *
         * @param value The value to be parsed
         * @return the integer day of the month or 32 for last day of the month
         * @throws Exception if the input value is invalid
         */
        @Override
        public int parse(String value) throws Exception {
            if ("L".equalsIgnoreCase(value)) {
                return 32;
            } else {
                return super.parse(value);
            }
        }

    }

    /**
     * The value parser for the months field.
     */
    private static class MonthValueParser extends SimpleValueParser {


        /**
         * Builds the months value parser.
         */
        public MonthValueParser() {
            super(1, 12);
        }

        @Override
        public int parse(String value) throws Exception {
            try {
                // try as a simple value
                return super.parse(value);
            } catch (Exception e) {
                // try as an alias
                return parseAlias(value, ALIASES, 1);
            }
        }

    }

    /**
     * The value parser for the months field.
     */
    private static class DayOfWeekValueParser extends SimpleValueParser {

        /**
         * Days of week aliases.
         */
        private static final String[] ALIASES = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

        /**
         * Builds the months value parser.
         */
        public DayOfWeekValueParser() {
            super(0, 7);
        }

        @Override
        public int parse(String value) throws Exception {
            try {
                // try as a simple value
                return super.parse(value) % 7;
            } catch (Exception e) {
                // try as an alias
                return parseAlias(value, ALIASES, 0);
            }
        }

    }
}
