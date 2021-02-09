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

import java.text.DateFormat;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Time;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-3-31
 * Time: 18:03:15
 * 日期处理单元
 */
public class DateUtil {
    public static final int SECOND = 1000;
    public static final int MINUTE = SECOND * 60;
    public static final int HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;
    public static final long WEEK = DAY * 7;
    public static final long YEAR = DAY * 365; // or 366

    final static public String UTC_FTP_FORMAT = "yyyyMMddHHmmss";
    final static public String UTC_ST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    final static public String FULL_ST_FORMAT = "yyyy-MM-dd HH:mm:ss";
    final static public String FULL_J_FORMAT = "yyyy/MM/dd HH:mm:ss";
    final static public String CURRENCY_ST_FORMAT = "yyyy-MM-dd HH:mm";
    final static public String CURRENCY_J_FORMAT = "yyyy/MM/dd HH:mm";
    final static public String DATA_FORMAT = "yyyyMMddHHmmss";
    final static public String DAY_NUMBER_FORMAT = "yyyyMMdd";
    final static public String ST_FORMAT = "yyyy-MM-dd HH:mm";
    final static public String ST_CN_FORMAT = "yyyy年MM月dd日 HH:mm";
    final static public String CN_FORMAT = "yy年MM月dd日 HH:mm";
    final static public String DAY_FORMAT = "yyyy-MM-dd";
    final static public String SHORT_DATE_FORMAT = "yy-MM-dd";
    final static public String DATE_GUID = "yyyyMMddHHmmssSSS";

    //空日期日期 1800-01-01 01:01:01  太小有些数据库和语言不支持
    final static public Date empty = new Date(-5364687539000L);
    final static public String EMPTY_DATE_STRING = "1800-01-01";


    private DateUtil() {

    }


    public static String getLinuxTime() {
        DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return format.format(new Date());
    }
    public static boolean isEmpty(Date date) {
        return date == null || date.getTime() <= empty.getTime();
    }

    /**
     * @param date 日期
     * @param time 时间
     * @return Calendar  合并日期和时间
     */
    public static Calendar mergeDateTime(Date date, Time time) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        if (time != null) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, temp.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, temp.get(Calendar.MILLISECOND));
        }
        return cal;
    }

    /**
     * 得到标准的日期
     *
     * @return String
     */
    public static String getDateST() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DAY_FORMAT, Locale.CHINA);
        return dateFormat.format(new Date());
    }

    /**
     * 得到标准的日期 时间
     *
     * @return String
     */
    public static String getDateTimeST() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FULL_ST_FORMAT);
        return dateFormat.format(new Date());
    }

    /**
     * @return 得到当前年
     */
    public static int getYear() {
        return getYear(new Date());
    }


    /**
     * @param date 日期
     * @return 得到年
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null) {
            calendar.setTime(new Date());
        } else {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 得到月
     *
     * @return int
     */
    public static int getMonth() {
        return getMonth(new Date());
    }

    /**
     * @param date 当前日期
     * @return 得到月
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null) {
            date = new Date();
        }
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * @return int   得到号数
     */
    public static int getDate() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.DATE);
    }

    public static int getHour() {
        return StringUtil.toInt(toString(new Date(), "HH"));
    }

    public static int getMinute() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecond() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 得到今天是月中的第几天
     *
     * @return int
     */
    public static int getDAY_OF_MONTH() {
        return getDAY_OF_MONTH(new Date());
    }

    /**
     * @param trialTime 日期
     * @return 得到月中的第几天
     */
    public static int getDAY_OF_MONTH(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return int 得到今天周中的第几天
     */
    public static int getDAY_OF_WEEK() {
        return getDAY_OF_WEEK(new Date());
    }

    /**
     * @param trialTime 日期
     * @return 得到周中的第几天 7 为星期天
     */
    public static int getDAY_OF_WEEK(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * @return int  得到今天早上下午
     */
    public static int getAM_PM() {
        return getAM_PM(new Date());
    }

    /**
     * AM = 0
     * PM = 1;
     *
     * @param trialTime 日期
     * @return int 得到早上下午
     */
    public static int getAM_PM(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.AM_PM);
    }

    /**
     * @param trialTime 日期
     * @return 得到月分的最大天数
     */
    public static int getCountMonthDay(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(trialTime); //放入你的日期
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param date 日期
     * @return 取得当前日期所在周的第一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * @param date 日期
     * @return 取得当前日期所在周的最后一天
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        c.add(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * @param date 日期
     * @return 取得当前日期是多少周
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = c.get(Calendar.MONTH);
        if (month == 0 && week >= 52) {
            return 1;
        }
        return week;
    }

    /**
     * @param date 日期
     * @return 是那一年的问题
     */
    public static int getWeekInYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        if (week >= 52) {
            Date lastDate = getLastDayOfWeek(date);
            return DateUtil.getYear(lastDate);
        }
        return DateUtil.getYear(date);
    }

    /**
     * @param year 年
     * @return 得到某一年周的总数
     */
    public static int getMaxWeekNumOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long begin = calendar.getTime().getTime();
        calendar.set(Calendar.YEAR, year + 1);
        begin = calendar.getTime().getTime() - begin;
        return (int) NumberUtil.getRound(begin / WEEK, 0);
    }

    /**
     * @param trialTime 日期
     * @return 得到一个月开始的时间日期
     */
    public static Date getStartMonthDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); //放入你的日期
        calendar.set(Calendar.DATE, calendar.getMinimum(Calendar.DATE));
        return getStartDateTime(calendar.getTime());
    }

    /**
     * @param trialTime 日期
     * @return 得到一个月结束的时间日期
     */
    public static Date getEndMonthDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); //放入你的日期
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return getEndDateTime(calendar.getTime());
    }

    /**
     * @param trialTime 日期
     * @return 得到一个年开始的时间日期
     */
    public static Date getStartYearDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); //放入你的日期
        calendar.set(Calendar.MONTH, 0);
        return getStartMonthDate(calendar.getTime());
    }


    /**
     * @param trialTime 日期
     * @return 得到一年结束的时间日期
     */
    public static Date getEndYearDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); //放入你的日期
        calendar.set(Calendar.MONTH, 11);
        return getEndMonthDate(calendar.getTime());
    }

    /**
     * 中国习惯开始为星期一,结束为星期天
     *
     * @param year 年
     * @param week 第几周
     * @return 指定周开始和结束的日期时间
     */
    public static Date[] getWeekStartAndEnd(int year, int week) {
        Date[] result = new Date[2];
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        while (cal.get(Calendar.YEAR) < year) {
            cal.setTimeInMillis(cal.getTimeInMillis() + DAY);
        }
        result[0] = getStartDateTime(cal.getTime());

        //结束日期
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        while (cal.get(Calendar.YEAR) > year) {
            cal.setTimeInMillis((cal.getTimeInMillis() - DAY));
        }
        result[1] = getEndDateTime(cal.getTime());
        return result;
    }

    /**
     * @param date 当前日期
     * @return 得到当前季度开始日期和结束日期
     * @throws Exception 异常
     */
    public static Date[] getSeasonStartAndEnd(Date date) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int startMonth = 0;

        if (month <= 3) {
            startMonth = 1;
        } else if (month > 3 && month <= 6) {
            startMonth = 4;
        } else if (month > 6 && month <= 9) {
            startMonth = 7;
        } else if (month > 9) {
            startMonth = 10;
        }

        //7月-9月;
        Date[] result = new Date[2];
        int endMonth = startMonth + 2;
        Date start = DateUtil.getStartMonthDate(StringUtil.getDate((new StringBuilder().append(year).append("-").append(startMonth).append("-01")).toString()));
        Date end = DateUtil.getEndMonthDate(StringUtil.getDate((new StringBuilder().append(year).append("-").append(endMonth).append("-25")).toString()));
        result[0] = start;
        result[1] = end;
        return result;
    }


    /**
     * 得到变化年的日期
     *
     * @param move 偏移量
     * @return Date  需要偏移的日期
     */
    public static Date addYear(int move) {
        return addYear(move, new Date());
    }


    /**
     * 得到变化年的日期
     *
     * @param move 偏移量
     * @param date 需要偏移的日期
     * @return 日期
     */
    public static Date addYear(int move, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, move);
        return calendar.getTime();
    }

    /**
     * @param move 偏移量
     * @param date 需要偏移的日期
     * @return 得到变化年的月
     */
    public static Date addMonth(int move, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, move);
        return calendar.getTime();
    }

    /**
     * @param tmp 偏移量
     * @return Date 得到变化号数的日期
     */
    public static Date addDate(int tmp) {
        return addDate(tmp, new Date());
    }

    /**
     * @param tmp     添加号数
     * @param theDate 日期
     * @return 添加日期
     */
    public static Date addDate(int tmp, Date theDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(theDate);
        calendar.add(Calendar.DATE, tmp);
        return calendar.getTime();
    }

    /**
     * @param yeas  年
     * @param month 月
     * @param date  日
     * @return 日期时间，得到偏移时间
     */
    public static Date createDate(int yeas, int month, int date) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yeas);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, date);
        return calendar.getTime();
    }

    /**
     * @param date 日期
     * @return 得到这天开始的时间
     */
    public static Date getStartDateTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    /**
     * @param date 日期
     * @return 得到这天最后结束的时间
     */
    public static Date getEndDateTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartDateTime(date));
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.add(Calendar.HOUR, calendar.getActualMaximum(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        return calendar.getTime();
    }

    /**
     * @param date 日期
     * @return 得到时间，只到分，丢到秒
     */
    public static Date getDateMinute(Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 比较较两个日期,返回天数差
     *
     * @param beginDate 开始日期时间
     * @param endDate   结束日期时间
     * @return int
     */
    public static long compareDay(Date beginDate, Date endDate) {
        Calendar endDateYears = new GregorianCalendar();
        endDateYears.setTime(endDate);
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        long diffMillis = endDateYears.getTimeInMillis() - beginYears.getTimeInMillis();
        return diffMillis / (24 * 60 * 60 * 1000);
    }

    /**
     * @param date 日期
     * @return 判断是否为今天
     */
    static public boolean isToDay(Date date) {
        return toString(new Date(), DAY_NUMBER_FORMAT).equals(toString(date, DAY_NUMBER_FORMAT));
    }

    /**
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 比较实际返回秒的差距
     */
    public static long getTimeInMillis(Date beginDate, Date endDate) {
        Calendar endDateYears = new GregorianCalendar();
        endDateYears.setTime(endDate);
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        return (endDateYears.getTimeInMillis() - beginYears.getTimeInMillis());
    }

    /**
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 比较的结果为，年，月，日
     */
    public static int[] getCompareDate(Date startDate, Date endDate) {
        if (!startDate.before(endDate)) {
            return new int[]{0, 0, 0};
        }
        Calendar endDateYears = new GregorianCalendar();
        endDateYears.setTime(endDate);

        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(startDate);

        int year = endDateYears.get(Calendar.YEAR) - beginYears.get(Calendar.YEAR);
        beginYears.add(Calendar.YEAR, year);

        int month = endDateYears.get(Calendar.MONTH) - beginYears.get(Calendar.MONTH);
        beginYears.add(Calendar.MONTH, month);

        int day = endDateYears.get(Calendar.DATE) - beginYears.get(Calendar.DATE);
        beginYears.add(Calendar.DATE, day);

        if (day < 0) {

            month = month - 1;
            day = endDateYears.getMaximum(Calendar.DATE) - Math.abs(day);
            endDateYears.set(Calendar.MONTH, month);
        }

        if (month < 0) {
            year = year - 1;
            month = endDateYears.getMaximum(Calendar.MONTH) - Math.abs(month);
        }

        if (year < 0) {
            year = 0;
        }


        return new int[]{year, month, day};
    }

    /**
     * 比较较两个日期,返回天数差
     *
     * @param beginDate 开始日期时间
     * @return int
     */
    public static long compareDay(Date beginDate) {
        return compareDay(beginDate, new Date());
    }

    /**
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 返回分
     */
    public static long compareMinute(Date beginDate, Date endDate) {
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        long diffMillis = endDate.getTime() - beginYears.getTimeInMillis();
        return diffMillis / MINUTE;
    }

    /**
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 返回小时
     */
    public static long compareHour(Date beginDate, Date endDate) {
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        long diffMillis = endDate.getTime() - beginYears.getTimeInMillis();
        return diffMillis / HOUR;
    }

    /**
     * 判断是否属于这个日期范围,txweb 标题中判断是否 是新的
     *
     * @param date 创建日期
     * @param day  天数
     * @return boolean
     */
    static public boolean inDate(Date date, int day) {
        return compareDay(date) <= day;
    }


    /**
     * @param date       日期
     * @param expression 时间日期表达式   23:25-5:05;1:20-3:10
     * @return 是否在日期范围
     * @throws Exception 异常
     */
    static public boolean isInTimeExpression(Date date, String expression) throws Exception {
        String[] dateLines = StringUtil.split(expression, StringUtil.SEMICOLON);
        for (String line : dateLines) {
            if (StringUtil.isNull(line) || !line.contains("-")) {
                continue;
            }
            String startDateStr = StringUtil.trim(StringUtil.substringBefore(line, "-"));
            String endDateStr = StringUtil.trim(StringUtil.substringAfter(line, "-"));
            if (StringUtil.isNull(startDateStr) || StringUtil.isNull(endDateStr)) {
                continue;
            }
            if (!startDateStr.contains(":") || !endDateStr.contains(":")) {
                continue;
            }
            Date startDate = null;
            Date endDate = null;
            if (startDateStr.length() < 6) {
                startDate = StringUtil.getDate(DateUtil.getDateST() + " " + startDateStr);
            }
            if (endDateStr.length() < 6) {
                endDate = StringUtil.getDate(DateUtil.getDateST() + " " + endDateStr);
            }
            if (startDate == null || endDate == null) {
                continue;
            }
            if (startDate.equals(endDate)) {
                continue;
            }
            if (startDate.getTime() > endDate.getTime()) {
                endDate = StringUtil.getDate(DateUtil.toString(DateUtil.addDate(1), DateUtil.DAY_FORMAT) + " " + endDateStr);
            }
            if (isInDateRange(date, startDate, endDate)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param date      日期
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 是否在时间范围内
     */
    static public boolean isInDateRange(Date date, Date startDate, Date endDate) {
        return !(startDate == null || endDate == null) && date.after(startDate) && date.before(endDate);
    }

    static public boolean isInDateRange(Date startDate, Date endDate) {
        return isInDateRange(new Date(), startDate, endDate);
    }

    /**
     * [OPTION value="o"]保密[/OPTION]
     * [OPTION value="z1"]白羊座(3月21--4月19日)[/OPTION]
     * [OPTION value="z2"]金牛座(4月20--5月20日)[/OPTION]
     * [OPTION value="z3"]双子座(5月21--6月21日)[/OPTION]
     * [OPTION value="z4"]巨蟹座(6月22--7月22日)[/OPTION]
     * [OPTION value="z5"]狮子座(7月23--8月22日)[/OPTION]
     * [OPTION value="z6"]处女座(8月23--9月22日)[/OPTION]
     * [OPTION value="z7"]天秤座(9月23--10月23日)[/OPTION]
     * [OPTION value="z8"]天蝎座(10月24--11月21日)[/OPTION]
     * [OPTION value="z9"]射手座(11月22--12月21日)[/OPTION]
     * [OPTION value="z10"]魔羯座(12月22--1月19日)[/OPTION]
     * [OPTION value="z11"]水瓶座(1月20--2月18日)[/OPTION]
     * [OPTION value="z12"]双鱼座(2月19--3月20日)[/OPTION]
     *
     * @param date 生日日期
     * @return int 更具上边得到星座
     */
    static public int getBirthStar(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
        if (EMPTY_DATE_STRING.equals(dateFormat.format(date))) {
            return 0;
        }
        dateFormat = new SimpleDateFormat("MM");
        int mm = StringUtil.toInt(dateFormat.format(date));
        dateFormat = new SimpleDateFormat("dd");
        int dd = StringUtil.toInt(dateFormat.format(date));
        if ((mm == 3 && dd >= 21) || (mm == 4 && dd <= 19)) {
            return 1;
        }
        if ((mm == 4 && dd >= 20) || (mm == 5 && dd <= 20)) {
            return 2;
        }
        if ((mm == 5 && dd >= 21) || (mm == 6 && dd <= 21)) {
            return 3;
        }
        if ((mm == 6 && dd >= 22) || (mm == 7 && dd <= 22)) {
            return 4;
        }
        if ((mm == 7 && dd >= 23) || (mm == 8 && dd <= 22)) {
            return 5;
        }
        if ((mm == 8 && dd >= 23) || (mm == 9 && dd <= 22)) {
            return 6;
        }
        if ((mm == 9 && dd >= 23) || (mm == 10 && dd <= 23)) {
            return 7;
        }
        if ((mm == 10 && dd >= 24) || (mm == 11 && dd <= 21)) {
            return 8;
        }
        if ((mm == 11 && dd >= 22) || (mm == 12 && dd <= 21)) {
            return 9;
        }
        if ((mm == 12 && dd >= 22) || (mm == 1 && dd <= 19)) {
            return 10;
        }
        if ((mm == 1 && dd >= 20) || (mm == 2 && dd <= 18)) {
            return 11;
        }
        if ((mm == 2 && dd >= 19) || (mm == 3 && dd <= 20)) {
            return 12;
        }
        return 0;
    }

    /**
     * 转换为日期
     *
     * @param gmt "GMT+08:00"
     * @return Date
     */
    public static Date getGmtDate(String gmt) {
        if (!StringUtil.hasLength(gmt)) {
            return new Date();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.FULL_ST_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(gmt));
        try {
            String fullDate = dateFormat.format(new Date());
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.parse(fullDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static java.sql.Date toSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static Date toJavaDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

    /**
     * 返回一个String类型的他们之间的时间差 只到小时 例如:38:15:00(三十八小时15分) 如果获取的当前日期在后面 则返回0
     *
     * @param date 比较的实际
     * @return 字符串
     */
    public static String minus(Date date) {
        Date now = new Date();
        if (now.after(date)) {
            return "0";
        } else {
            long time = date.getTime() - now.getTime();
            int hour = (int) (time / (60 * 60 * 1000));
            int minute = (int) ((time % (60 * 60 * 1000)) / (60 * 1000));
            int second = (int) (((time % (60 * 60 * 1000)) % (60 * 1000)) / 1000 + 1);
            if (second == 60) {
                second = 0;
                minute += 1;
            }
            if (minute == 60) {
                minute = 0;
                hour += 1;
            }
            return "" + (hour < 10 ? ("0" + hour) : hour) + ":" + (minute < 10 ? ("0" + minute) : minute) + ":" + (second < 10 ? ("0" + second) : second);
        }
    }

    /**
     * @return 转换为字符串
     */
    @Override
    public String toString() {
        return toString(new Date(), FULL_ST_FORMAT);
    }

    /**
     * @param format 格式
     * @return String 格式化日期时间
     */
    public static String toString(String format) {
        return toString(new Date(), format);
    }

    /**
     * @param format 格式
     * @param date   日期
     * @return String  格式化日期时间
     */
    public static String toString(Date date, String format) {
        if (format == null) {
            format = FULL_ST_FORMAT;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getTimeFormatText(Date date) {
        return getTimeFormatText(date, "zh");
    }

    public static long getPhpTime(Date date) {
        return Long.parseLong(StringUtil.cut(date.getTime() + "", 10, ""));
    }

    /**
     * @param date 日期
     * @param lan  语言 目前只是中文 zh
     * @return 返回文字描述的日期
     */
    public static String getTimeFormatText(Date date, String lan) {
        if (date == null) {
            return null;
        }
        long diffMillis = compareDay(date);
        long r;
        if (diffMillis > 363) {
            r = (diffMillis / 363);
            return r + ("zh".equals(lan) ? "年前" : "years ago");
        }
        if (diffMillis > 30) {
            r = diffMillis / 30;
            return r + ("zh".equals(lan) ? "个月前" : "months ago");
        }
        if (diffMillis == 2) {
            return ("zh".equals(lan) ? "前天" : " before yesterday");
        }
        if (diffMillis == 1) {
            return ("zh".equals(lan) ? "昨天" : "yesterday");
        }
        if (diffMillis > 3) {
            return diffMillis + ("zh".equals(lan) ? "天前" : "days ago");
        }
        long minute = compareMinute(date, new Date());
        if (minute > 60) {
            r = (minute / 60);
            return r + ("zh".equals(lan) ? "小时前" : "hours ago");
        }
        if (minute >= 1) {
            return minute + ("zh".equals(lan) ? "分钟前" : "minutes ago");
        }
        return "刚刚";
    }

    /**
     * @param ms  毫秒转化时分秒毫秒
     * @param lan 语言
     * @return 转换为中文表示
     */
    public static String getTimeMillisFormat(long ms, String lan) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("zh".equalsIgnoreCase(lan) ? "天" : "day");
        }
        if (hour > 0) {
            sb.append(hour).append("zh".equalsIgnoreCase(lan) ? "小时" : "hours");
        }
        if (minute > 0) {
            sb.append(minute).append("zh".equalsIgnoreCase(lan) ? "分" : "minutes");
        }
        if (second > 0) {
            sb.append(second).append("zh".equalsIgnoreCase(lan) ? "秒" : "seconds ");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond).append("zh".equalsIgnoreCase(lan) ? "毫秒" : "millis");
        }
        return sb.toString();
    }

}