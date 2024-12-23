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

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.NumberUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-28
 * Time: 12:56:15
 *
 */
public class LunarCalendar extends GregorianCalendar {
    static final int MAX_YEAR = 2100;
    static private final int[] sTermInfo = {0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758};

    final private static int[] bigLeapMonthYears = {
            6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150,
            155, 158, 185, 193};

    final private static char[] daysInGregorianMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    final private static String[] stemNames =
            {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};

    final private static String[] branchNames =
            {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    final private static String[] chineseMonthNames =
            {"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"};

    final private static String[] principleTermNames =
            {"大寒", "雨水", "春分", "谷雨", "夏满", "夏至", "大暑", "处暑", "秋分", "霜降", "小雪", "冬至"};

    final private static String[] animalNames =
            {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    final private static String[] monthNames =
            {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};

    final private static String[] chineseNumber = {"初", "十", "廿", "卅", "□"};

    final private static String[] lunarBirthday = {
            "0601 生日", //一月的最后一个星期日（月倒数第一个星期日）
            "0508 老婆"
    };

    //某月的第几个星期几
    final private static String[] fullFtvth = {
            "0150 世界麻风日", //一月的最后一个星期日（月倒数第一个星期日）
            "0520 国际母亲节",
            "0530 全国助残日",
            "0630 父亲节",
            "0730 被奴役国家周",
            "0932 国际和平日",
            "0940 国际聋人节 世界儿童日",
            "0950 世界海事日",
            "1011 国际住房日",
            "1013 国际减轻自然灾害日(减灾日)",
            "1144 感恩节"};

    final private static String[] sFtvth = {
            "0150 麻风", //一月的最后一个星期日（月倒数第一个星期日）
            "0520 母亲", //五月第二个星期日
            "0530 助残",
            "0630 父亲",
            "0730 奴国",
            "0932 平日",
            "0940 聋人",
            "0950 海事",
            "1011 住房",
            "1013 减灾",
            "1144 感恩"};

    final private static String[] chineseFtv = {
            "0101 春节",
            "0102 初二",
            "0103 初三",
            "0115 元宵",
            "0505 端午",
            "0707 七夕",
            "0715 中元",
            "0815 中秋",
            "0909 重阳",
            "1208 腊八",
            "1223 小年",
            "0100 除夕"};


    final private static String[] fullFtv = {
            "0101 元旦节",
            "0202 世界湿地日",
            "0210 国际气象节",
            "0214 情人节",
            "0301 国际海豹日",
            "0303 全国爱耳日",
            "0305 学雷锋纪念日",
            "0308 妇女节",
            "0312 植树节 孙中山逝世纪念日",
            "0314 国际警察日",
            "0315 消费者权益日",
            "0317 中国国医节 国际航海日",
            "0321 世界森林日 消除种族歧视国际日 世界儿歌日",
            "0322 世界水日",
            "0323 世界气象日",
            "0324 世界防治结核病日",
            "0325 全国中小学生安全教育日",
            "0330 巴勒斯坦国土日",
            "0401 愚人节 全国爱国卫生运动月(四月) 税收宣传月(四月)",
            "0407 世界卫生日",
            "0422 世界地球日",
            "0423 世界图书和版权日",
            "0424 亚非新闻工作者日",
            "0501 劳动节",
            "0502 劳动节假日",
            "0503 劳动节假日",
            "0504 青年节",
            "0505 碘缺乏病防治日",
            "0508 世界红十字日",
            "0512 国际护士节",
            "0515 国际家庭日",
            "0517 国际电信日",
            "0518 国际博物馆日",
            "0520 全国学生营养日",
            "0523 国际牛奶日",
            "0531 世界无烟日",
            "0601 国际儿童节",
            "0605 世界环境保护日",
            "0606 全国爱眼日",
            "0617 防治荒漠化和干旱日",
            "0623 国际奥林匹克日",
            "0625 全国土地日",
            "0626 国际禁毒日",
            "0701 香港回归纪念日 中共诞辰 世界建筑日",
            "0702 国际体育记者日",
            "0707 抗日战争纪念日",
            "0711 世界人口日",
            "0730 非洲妇女日",
            "0801 建军节",
            "0808 中国男子节(爸爸节)",
            "0815 抗日战争胜利纪念",
            "0908 国际扫盲日 国际新闻工作者日",
            "0909 毛泽东逝世纪念",
            "0910 中国教师节",
            "0914 世界清洁地球日",
            "0916 国际臭氧层保护日",
            "0918 九·一八事变纪念日",
            "0920 国际爱牙日",
            "0927 世界旅游日",
            "0928 孔子诞辰",
            "1001 国庆节 世界音乐日 国际老人节",
            "1002 国庆节假日 国际和平与民主自由斗争日",
            "1003 国庆节假日",
            "1004 世界动物日",
            "1006 老人节",
            "1008 全国高血压日 世界视觉日",
            "1009 世界邮政日 万国邮联日",
            "1010 辛亥革命纪念日 世界精神卫生日",
            "1013 世界保健日 国际教师节",
            "1014 世界标准日",
            "1015 国际盲人节(白手杖节)",
            "1016 世界粮食日",
            "1017 世界消除贫困日",
            "1022 世界传统医药日",
            "1024 联合国日",
            "1031 世界勤俭日",
            "1107 十月社会主义革命纪念日",
            "1108 中国记者日",
            "1109 全国消防安全宣传教育日",
            "1110 世界青年节",
            "1111 国际科学与和平周(本日所属的一周)",
            "1112 孙中山诞辰纪念日",
            "1114 世界糖尿病日",
            "1117 国际大学生节 世界学生节",
            "1120 彝族年",
            "1121 彝族年 世界问候日 世界电视日",
            "1122 彝族年",
            "1129 国际声援巴勒斯坦人民国际日",
            "1201 世界艾滋病日",
            "1203 世界残疾人日",
            "1205 国际经济和社会发展志愿人员日",
            "1208 国际儿童电视日",
            "1209 世界足球日",
            "1210 世界人权日",
            "1212 西安事变纪念日",
            "1213 南京大屠杀(1937年)纪念日！紧记血泪史！",
            "1220 澳门回归纪念",
            "1221 国际篮球日",
            "1224 平安夜",
            "1225 圣诞节",
            "1226 毛泽东诞辰纪念"};

    final private static String[] sFtv = {
            "0101 元旦",
            "0202 湿地",
            "0210 气象",
            "0214 情人",
            "0301 海豹",
            "0303 耳日",
            "0305 雷锋",
            "0308 妇女",
            "0312 植树",
            "0314 警察",
            "0315 权益",
            "0317 医航",
            "0321 森林",
            "0322 水日",
            "0323 气象",
            "0324 结核",
            "0325 教育",
            "0330 土日",
            "0401 愚人",
            "0407 卫生",
            "0422 地球",
            "0423 版权",
            "0424 新闻",
            "0501 劳动",
            "0502 劳动",
            "0503 劳动",
            "0504 青年",
            "0505 碘缺",
            "0508 红十",
            "0512 护士",
            "0515 家庭",
            "0517 电信",
            "0518 博物",
            "0520 营养",
            "0523 牛奶",
            "0531 无烟",
            "0601 儿童",
            "0605 环保",
            "0606 爱眼",
            "0617 防荒",
            "0623 奥林",
            "0625 土地",
            "0626 禁毒",
            "0701 回归",
            "0702 体育",
            "0707 抗日",
            "0711 人口",
            "0730 妇女",
            "0801 建军",
            "0808 父亲",
            "0815 战争",
            "0908 扫盲",
            "0909 毛纪",
            "0910 教师",
            "0914 地清",
            "0916 保氧",
            "0918 事变",
            "0920 爱牙",
            "0927 旅游",
            "0928 孔子",
            "1001 国庆",
            "1002 国庆",
            "1003 国庆",
            "1004 动物",
            "1006 老人",
            "1008 高血",
            "1009 邮政",
            "1010 辛亥",
            "1013 国教",
            "1014 标准",
            "1015 盲人",
            "1016 粮食",
            "1017 消贫",
            "1022 医药",
            "1024 联国",
            "1031 勤俭",
            "1107 革命",
            "1108 记者",
            "1109 安教",
            "1110 青年",
            "1111 科学",
            "1112 孙文",
            "1114 糖尿",
            "1117 学生",
            "1120 彝族",
            "1121 问候",
            "1122 彝族",
            "1129 援巴",
            "1201 艾滋",
            "1203 残疾",
            "1205 经济",
            "1208 儿童",
            "1209 足球",
            "1210 人权",
            "1212 西安",
            "1213 南京",
            "1220 澳归",
            "1221 篮球",
            "1224 平安",
            "1225 圣诞",
            "1226 毛诞"};

    final private static char[] chineseMonths = {
            0x00, 0x04, 0xad, 0x08, 0x5a, 0x01, 0xd5, 0x54, 0xb4, 0x09, 0x64,
            0x05, 0x59, 0x45, 0x95, 0x0a, 0xa6, 0x04, 0x55, 0x24, 0xad, 0x08,
            0x5a, 0x62, 0xda, 0x04, 0xb4, 0x05, 0xb4, 0x55, 0x52, 0x0d, 0x94,
            0x0a, 0x4a, 0x2a, 0x56, 0x02, 0x6d, 0x71, 0x6d, 0x01, 0xda, 0x02,
            0xd2, 0x52, 0xa9, 0x05, 0x49, 0x0d, 0x2a, 0x45, 0x2b, 0x09, 0x56,
            0x01, 0xb5, 0x20, 0x6d, 0x01, 0x59, 0x69, 0xd4, 0x0a, 0xa8, 0x05,
            0xa9, 0x56, 0xa5, 0x04, 0x2b, 0x09, 0x9e, 0x38, 0xb6, 0x08, 0xec,
            0x74, 0x6c, 0x05, 0xd4, 0x0a, 0xe4, 0x6a, 0x52, 0x05, 0x95, 0x0a,
            0x5a, 0x42, 0x5b, 0x04, 0xb6, 0x04, 0xb4, 0x22, 0x6a, 0x05, 0x52,
            0x75, 0xc9, 0x0a, 0x52, 0x05, 0x35, 0x55, 0x4d, 0x0a, 0x5a, 0x02,
            0x5d, 0x31, 0xb5, 0x02, 0x6a, 0x8a, 0x68, 0x05, 0xa9, 0x0a, 0x8a,
            0x6a, 0x2a, 0x05, 0x2d, 0x09, 0xaa, 0x48, 0x5a, 0x01, 0xb5, 0x09,
            0xb0, 0x39, 0x64, 0x05, 0x25, 0x75, 0x95, 0x0a, 0x96, 0x04, 0x4d,
            0x54, 0xad, 0x04, 0xda, 0x04, 0xd4, 0x44, 0xb4, 0x05, 0x54, 0x85,
            0x52, 0x0d, 0x92, 0x0a, 0x56, 0x6a, 0x56, 0x02, 0x6d, 0x02, 0x6a,
            0x41, 0xda, 0x02, 0xb2, 0xa1, 0xa9, 0x05, 0x49, 0x0d, 0x0a, 0x6d,
            0x2a, 0x09, 0x56, 0x01, 0xad, 0x50, 0x6d, 0x01, 0xd9, 0x02, 0xd1,
            0x3a, 0xa8, 0x05, 0x29, 0x85, 0xa5, 0x0c, 0x2a, 0x09, 0x96, 0x54,
            0xb6, 0x08, 0x6c, 0x09, 0x64, 0x45, 0xd4, 0x0a, 0xa4, 0x05, 0x51,
            0x25, 0x95, 0x0a, 0x2a, 0x72, 0x5b, 0x04, 0xb6, 0x04, 0xac, 0x52,
            0x6a, 0x05, 0xd2, 0x0a, 0xa2, 0x4a, 0x4a, 0x05, 0x55, 0x94, 0x2d,
            0x0a, 0x5a, 0x02, 0x75, 0x61, 0xb5, 0x02, 0x6a, 0x03, 0x61, 0x45,
            0xa9, 0x0a, 0x4a, 0x05, 0x25, 0x25, 0x2d, 0x09, 0x9a, 0x68, 0xda,
            0x08, 0xb4, 0x09, 0xa8, 0x59, 0x54, 0x03, 0xa5, 0x0a, 0x91, 0x3a,
            0x96, 0x04, 0xad, 0xb0, 0xad, 0x04, 0xda, 0x04, 0xf4, 0x62, 0xb4,
            0x05, 0x54, 0x0b, 0x44, 0x5d, 0x52, 0x0a, 0x95, 0x04, 0x55, 0x22,
            0x6d, 0x02, 0x5a, 0x71, 0xda, 0x02, 0xaa, 0x05, 0xb2, 0x55, 0x49,
            0x0b, 0x4a, 0x0a, 0x2d, 0x39, 0x36, 0x01, 0x6d, 0x80, 0x6d, 0x01,
            0xd9, 0x02, 0xe9, 0x6a, 0xa8, 0x05, 0x29, 0x0b, 0x9a, 0x4c, 0xaa,
            0x08, 0xb6, 0x08, 0xb4, 0x38, 0x6c, 0x09, 0x54, 0x75, 0xd4, 0x0a,
            0xa4, 0x05, 0x45, 0x55, 0x95, 0x0a, 0x9a, 0x04, 0x55, 0x44, 0xb5,
            0x04, 0x6a, 0x82, 0x6a, 0x05, 0xd2, 0x0a, 0x92, 0x6a, 0x4a, 0x05,
            0x55, 0x0a, 0x2a, 0x4a, 0x5a, 0x02, 0xb5, 0x02, 0xb2, 0x31, 0x69,
            0x03, 0x31, 0x73, 0xa9, 0x0a, 0x4a, 0x05, 0x2d, 0x55, 0x2d, 0x09,
            0x5a, 0x01, 0xd5, 0x48, 0xb4, 0x09, 0x68, 0x89, 0x54, 0x0b, 0xa4,
            0x0a, 0xa5, 0x6a, 0x95, 0x04, 0xad, 0x08, 0x6a, 0x44, 0xda, 0x04,
            0x74, 0x05, 0xb0, 0x25, 0x54, 0x03};


    final private static char[][] principleTermMap = {
            {21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20,
                    20, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20},
            {20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19,
                    19, 18, 18, 19, 19, 18, 18, 18, 18, 18, 18, 18},
            {21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21,
                    20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20},
            {20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20,
                    19, 20, 20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20},
            {21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21,
                    20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21},
            {22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22,
                    21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21},
            {23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23,
                    22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23},
            {23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
                    22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23},
            {23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
                    22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23},
            {24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24,
                    23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23},
            {23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23,
                    22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22},
            {22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22,
                    21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22}};


    final private static char[][] principleTermYear = {
            {13, 45, 81, 113, 149, 185, 201},
            {21, 57, 93, 125, 161, 193, 201},
            {21, 56, 88, 120, 152, 188, 200, 201},
            {21, 49, 81, 116, 144, 176, 200, 201},
            {17, 49, 77, 112, 140, 168, 200, 201},
            {28, 60, 88, 116, 148, 180, 200, 201},
            {25, 53, 84, 112, 144, 172, 200, 201},
            {29, 57, 89, 120, 148, 180, 200, 201},
            {17, 45, 73, 108, 140, 168, 200, 201},
            {28, 60, 92, 124, 160, 192, 200, 201},
            {16, 44, 80, 112, 148, 180, 200, 201},
            {17, 53, 88, 120, 156, 188, 200, 201}};

    final private static char[][] sectionalTermMap = {
            {7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5,
                    5, 5, 5, 4, 5, 5},
            {5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3,
                    3, 4, 4, 3, 3, 3},
            {6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
                    5, 5, 4, 5, 5, 5, 5},
            {5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4,
                    4, 5, 4, 4, 4, 4, 5},
            {6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
                    5, 5, 4, 5, 5, 5, 5},
            {6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5,
                    5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5},
            {7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
                    7, 7, 6, 6, 6, 7, 7},
            {8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
                    7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7},
            {8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
                    7, 7, 6, 7, 7, 7, 7},
            {9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8,
                    8, 8, 7, 7, 8, 8, 8},
            {8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7,
                    7, 7, 6, 6, 7, 7, 7},
            {7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
                    7, 7, 6, 6, 6, 7, 7}};

    final private static String[] dayNames = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    final private static String[] sectionalTermNames = {"小寒", "立春", "惊蛰", "清明", "立夏", "芒种", "小暑", "立秋", "白露", "寒露", "立冬", "大雪"};

    final private static char[][] sectionalTermYear = {
            {13, 49, 85, 117, 149, 185, 201, 250, 250},
            {13, 45, 81, 117, 149, 185, 201, 250, 250},
            {13, 48, 84, 112, 148, 184, 200, 201, 250},
            {13, 45, 76, 108, 140, 172, 200, 201, 250},
            {13, 44, 72, 104, 132, 168, 200, 201, 250},
            {5, 33, 68, 96, 124, 152, 188, 200, 201},
            {29, 57, 85, 120, 148, 176, 200, 201, 250},
            {13, 48, 76, 104, 132, 168, 196, 200, 201},
            {25, 60, 88, 120, 148, 184, 200, 201, 250},
            {16, 44, 76, 108, 144, 172, 200, 201, 250},
            {28, 60, 92, 124, 160, 192, 200, 201, 250},
            {17, 53, 85, 124, 156, 188, 200, 201, 250}};

    private final int baseChineseDate = 11;

    private final int baseChineseMonth = 11;

    private final int baseChineseYear = 4598 - 1;

    private final int baseDate = 1;

    private final int baseIndex = 0;

    private final int baseMonth = 1;

    private final int baseYear = 1901;

    @Setter
    @Getter
    private int chineseDate; // 阴历  号

    @Setter
    @Getter
    private int chineseMonth; // 阴历  月

    @Setter
    @Getter
    private int chineseYear; // 阴历  年

    @Setter
    @Getter
    private int principleTerm;

    @Setter
    @Getter
    private int sectionalTerm;




    public int daysInChineseMonth(int y, int m) {

        int index = y - baseChineseYear + baseIndex;
        int v = 0;
        int l = 0;
        int d = 30;
        if (1 <= m && m <= 8) {
            v = chineseMonths[2 * index];
            l = m - 1;
            if (((v >> l) & 0x01) == 1) {
                d = 29;
            }
        } else if (9 <= m && m <= 12) {
            v = chineseMonths[2 * index + 1];
            l = m - 9;
            if (((v >> l) & 0x01) == 1) {
                d = 29;
            }
        } else {
            v = chineseMonths[2 * index + 1];
            v = (v >> 4) & 0x0F;
            if (v != Math.abs(m)) {
                d = 0;
            } else {
                d = 29;
                for (int i = 0; i < bigLeapMonthYears.length; i++) {
                    if (bigLeapMonthYears[i] == index) {
                        d = 30;
                        break;
                    }
                }
            }
        }
        return d;
    }

    public boolean isGregorianLeapYear() {
        return isGregorianLeapYear(get(Calendar.YEAR));
    }

    public boolean isGregorianLeapYear(int year) {
        boolean isLeap = false;
        if (year % 4 == 0) {
            isLeap = true;
        }
        if (year % 100 == 0) {
            isLeap = false;
        }
        if (year % 400 == 0) {
            isLeap = true;
        }
        return isLeap;
    }

    public int daysInGregorianMonth(int y, int m) {
        int d = daysInGregorianMonth[m - 1];
        if (m == 2 && isGregorianLeapYear(y)) {
            d++;
        }
        return d;
    }

    public int nextChineseMonth(int y, int m) {
        int n = Math.abs(m) + 1;
        if (m > 0) {
            int index = y - baseChineseYear + baseIndex;
            int v = chineseMonths[2 * index + 1];
            v = (v >> 4) & 0x0F;
            if (v == m) {
                n = -m;
            }
        }
        if (n == 13) {
            n = 1;
        }
        return n;
    }

    /**
     * 法则
     *
     * @param y 年
     * @param m 月
     * @return int 法则
     */
    public int principleTerm(int y, int m) {
        if (y < 1901 || y > MAX_YEAR) {
            return 0;
        }
        int index = 0;
        int ry = y - baseYear + 1;
        while (ry >= principleTermYear[m - 1][index]) {
            index++;
        }
        int term = principleTermMap[m - 1][4 * index + ry % 4];
        if ((ry == 171) && (m == 3)) {
            term = 21;
        }
        if ((ry == 181) && (m == 5)) {
            term = 21;
        }
        return term;
    }

    /**
     * 组合条件
     *
     * @param y 年
     * @param m 月
     * @return int 组合条件
     */
    public int sectionalTerm(int y, int m) {
        if (y < 1901 || y > MAX_YEAR) {
            return 0;
        }
        int index = 0;
        int ry = y - baseYear + 1;
        while (ry >= sectionalTermYear[m - 1][index]) {
            index++;
        }
        int term = sectionalTermMap[m - 1][4 * index + ry % 4];
        if ((ry == 121) && (m == 4)) {
            term = 5;
        }
        if ((ry == 132) && (m == 4)) {
            term = 5;
        }
        if ((ry == 194) && (m == 6)) {
            term = 6;
        }
        return term;
    }


    /**
     *
     * @return 得到中文描述
     */
    public int computeChineseFields() {
        if (get(Calendar.YEAR) < 1901 || get(Calendar.YEAR) > MAX_YEAR) {
            return 1;
        }
        int startYear = baseYear;
        int startMonth = baseMonth;
        int startDate = baseDate;
        chineseYear = baseChineseYear;
        chineseMonth = baseChineseMonth;
        chineseDate = baseChineseDate;
        //  2000  1  1 眨应农 4697  11  25
        if (get(Calendar.YEAR) >= 2000) {
            startYear = baseYear + 99;
            startMonth = 1;
            startDate = 1;
            chineseYear = baseChineseYear + 99;
            chineseMonth = 11;
            chineseDate = 25;
        }
        int daysDiff = 0;
        for (int i = startYear; i < get(Calendar.YEAR); i++) {
            daysDiff += 365;
            if (isGregorianLeapYear(i)) {
                daysDiff += 1; // leap year
            }
        }
        for (int i = startMonth; i < getMonth(); i++) {
            daysDiff += daysInGregorianMonth(get(Calendar.YEAR), i);
        }

        daysDiff += get(Calendar.DATE) - startDate;

        chineseDate += daysDiff;
        int lastDate = daysInChineseMonth(chineseYear, chineseMonth);
        int nextMonth = nextChineseMonth(chineseYear, chineseMonth);
        while (chineseDate > lastDate) {
            if (Math.abs(nextMonth) < Math.abs(chineseMonth)) {
                chineseYear++;
            }
            chineseMonth = nextMonth;
            chineseDate -= lastDate;
            lastDate = daysInChineseMonth(chineseYear, chineseMonth);
            nextMonth = nextChineseMonth(chineseYear, chineseMonth);
        }
        return 0;
    }

    public int computeSolarTerms() {
        if (get(Calendar.YEAR) < 1901 || get(Calendar.YEAR) > MAX_YEAR) {
            return 1;
        }
        sectionalTerm = sectionalTerm(get(Calendar.YEAR), getMonth());
        principleTerm = principleTerm(get(Calendar.YEAR), getMonth());
        return 0;
    }

    /**
     * 设置公日历日期
     *
     * @param y 年
     * @param m 月
     * @param d 日
     * @throws Exception 异常
     */
    public void setGregorian(int y, int m, int d) throws Exception {
        setTime(StringUtil.getDate(y + "-" + m + "-" + d));
        chineseYear = 0;
        chineseMonth = 0;
        chineseDate = 0;
        sectionalTerm = 0;
        principleTerm = 0;
    }


    /**
     * @param date 设置公日历日期
     */
    public void setGregorian(String date) {
        setTime(StringUtil.getDate(date));
        chineseYear = 0;
        chineseMonth = 0;
        chineseDate = 0;
        sectionalTerm = 0;
        principleTerm = 0;
    }

    public void setGregorian(Date date) {
        setTime(date);
        chineseYear = 0;
        chineseMonth = 0;
        chineseDate = 0;
        sectionalTerm = 0;
        principleTerm = 0;
    }


    /**
     * 得到第几月的第几周
     *
     * @return int  得到第几月的第几周
     */
    public int getGregorianWeekOfMonth() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(get(Calendar.YEAR), get(Calendar.MONTH), 1, 0, 0);
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));

        Date begin = calendar.getTime();
        calendar.roll(Calendar.DAY_OF_WEEK, 6);
        Date end = calendar.getTime();

        if (getTime().getTime() >= begin.getTime() && getTime().getTime() <= end.getTime()) {
            return 1;
        }

        for (int i = 2; i < 6; i++) {
            calendar.add(Calendar.DATE, 1);
            begin = calendar.getTime();
            calendar.roll(Calendar.DAY_OF_WEEK, 6);
            end = calendar.getTime();
            if (getTime().getTime() >= begin.getTime() && getTime().getTime() <= end.getTime()) {
                return i;
            }
        }
        return 1;
    }


    /**
     * 是否为周节
     *
     * @param mmwwdd 日期
     * @return boolean 是否为周节
     */
    public boolean isWeekFeast(String mmwwdd) {
        int mm = StringUtil.toInt(mmwwdd.substring(0, 2));
        int ths = StringUtil.toInt(mmwwdd.substring(2, 3));
        int day = StringUtil.toInt(mmwwdd.substring(3, 4));
        if (day == 7) {
            day = 0;
        }
        if (getMonth() != mm) {
            return false;
        }
        if (getWeek() != day) {
            return false;
        }

        Calendar calendar = new GregorianCalendar();
        if (ths < 5) {
            calendar.set(get(Calendar.YEAR), get(Calendar.MONTH), 1, 0, 0);
            int times = 0;
            for (int i = 1; i <= get(Calendar.DATE); i++) {
                if (day == (calendar.get(Calendar.DAY_OF_WEEK) - 1)) {
                    times++;
                    if (times == ths && calendar.get(Calendar.DATE) == get(Calendar.DATE)) {
                        System.out.println(DateUtil.toString(calendar.getTime(), DateUtil.DAY_FORMAT));
                        System.out.println(DateUtil.toString(getTime(), DateUtil.DAY_FORMAT));
                        System.out.println("ths==" + ths);
                        System.out.println("day==" + day);
                        calendar.clear();
                        return true;
                    }
                }
                calendar.add(Calendar.DATE, 1);
            }
        } else {

            calendar.set(get(Calendar.YEAR), get(Calendar.MONTH), calendar.getMaximum(Calendar.DATE), 0, 0);
            int times = 1;
            for (int i = get(Calendar.DATE); i > 0; i--) {
                if (day == (calendar.get(Calendar.DAY_OF_WEEK) - 1)) {
                    times++;
                    if (times == (ths - 4)) {
                        calendar.clear();
                        return true;
                    }
                }
                calendar.add(Calendar.DATE, -1);
            }

        }

        return false;
    }

    public String getChineseDateText() {
        //13 号星期5
        StringBuilder buf = new StringBuilder();
        buf.append(DateUtil.toString(getTime(), "yyyy年MM月dd日")).append("\r\n"); //日期
        buf.append(dayNames[getWeek()]).append("\r\n");  //星期
        buf.append("今年第").append(get(Calendar.DAY_OF_YEAR)).append("天\r\n");  //一年的第几天
        buf.append("农历 ").append(chineseMonth).append("月").append(chineseDate).append("日\r\n"); //农历
        buf.append(getCyclicalYear()).append("年 ").append(getCyclicalMonth()).append("月 ").append(getCyclicalDate()).append("日\r\n"); //农 属象

        for (String ftv : chineseFtv) {
            String mmdd = StringUtil.substringBefore(ftv, " ");
            int mm = StringUtil.toInt(mmdd.substring(0, 2));
            int dd = StringUtil.toInt(mmdd.substring(2, 4));
            if (chineseMonth == mm && chineseDate == dd) {
                buf.append(ftv.substring(ftv.indexOf(" ") + 1)).append(" ");
                break;
            }
        }

        for (String ftv : fullFtv) {
            String mmdd = StringUtil.substringBefore(ftv, " ");
            int mm = StringUtil.toInt(mmdd.substring(0, 2));
            int dd = StringUtil.toInt(mmdd.substring(2, 4));
            if (getMonth() == mm && get(Calendar.DATE) == dd) {
                buf.append(ftv.substring(ftv.indexOf(" ") + 1)).append(" ");
                break;
            }
        }

        for (String ftv : fullFtvth) {
            String mmwwdd = StringUtil.substringBefore(ftv, " ");
            if (isWeekFeast(mmwwdd)) {
                buf.append(ftv.substring(ftv.indexOf(" ") + 1)).append(" ");
                break;
            }
        }

        for (String ftv : lunarBirthday) {
            String mmdd = StringUtil.substringBefore(ftv, " ");
            int mm = StringUtil.toInt(mmdd.substring(0, 2));
            int dd = StringUtil.toInt(mmdd.substring(2, 4));
            if (chineseMonth == mm && chineseDate == dd) {
                buf.append(ftv.substring(ftv.indexOf(" ") + 1)).append(" ");
                break;
            }
        }

        return buf.toString();
    }

    public String getChineseDateHint() {
        //13 号星期5
        if (get(Calendar.DATE) == 13 && getWeek() == 5) {
            return "黑五";
        }

        for (String ftv : sFtv) {
            String mmdd = StringUtil.substringBefore(ftv, " ");
            int mm = StringUtil.toInt(mmdd.substring(0, 2));
            int dd = StringUtil.toInt(mmdd.substring(2, 4));
            if (getMonth() == mm && get(Calendar.DATE) == dd) {
                return StringUtil.substringBefore(ftv, " ");
            }
        }

        for (String ftv : chineseFtv) {
            String mmdd = StringUtil.substringBefore(ftv, " ");
            int mm = StringUtil.toInt(mmdd.substring(0, 2));
            int dd = StringUtil.toInt(mmdd.substring(2, 4));
            if (chineseMonth == mm && chineseDate == dd) {
                return StringUtil.substringAfterLast(ftv, " ");
            }
        }

        for (String ftv : sFtvth) {
            String mmwwdd = StringUtil.substringBefore(ftv, " ");
            if (isWeekFeast(mmwwdd)) {
                return StringUtil.substringAfterLast(ftv, " ");
            }
        }

        if (get(Calendar.DATE) == sectionalTerm) {
            return sectionalTermNames[getMonth() - 1];
        } else if (get(Calendar.DATE) == principleTerm) {
            return principleTermNames[getMonth() - 1];
        }

        if (chineseDate == 1 && chineseMonth > 0) {
            return chineseMonthNames[chineseMonth - 1];
        } else if (chineseDate == 1 && chineseMonth < 0) {
            return chineseMonthNames[-chineseMonth - 1];
        }
        int idate = chineseDate % 10;
        if (idate == 0) {
            idate = 10;
        }

        if (chineseDate <= 10) {
            return chineseNumber[0] + monthNames[chineseDate - 1];
        } else if (10 >= chineseDate && chineseDate < 20) {
            return chineseNumber[1] + monthNames[idate - 1];
        } else if (20 >= chineseDate && chineseDate < 30) {
            return chineseNumber[2] + monthNames[idate - 1];
        } else if (30 >= chineseDate && chineseDate < 40) {
            return chineseNumber[3] + monthNames[idate - 1];
        }

        for (String ftv : lunarBirthday) {
            String mmdd = StringUtil.substringBefore(ftv, " ");
            int mm = StringUtil.toInt(mmdd.substring(0, 2));
            int dd = StringUtil.toInt(mmdd.substring(2, 4));
            if (chineseMonth == mm && chineseDate == dd) {
                return ftv.substring(ftv.indexOf(" ") + 1);
            }
        }
        return StringUtil.empty;
    }


    public int getYear() {
        return get(Calendar.YEAR);
    }

    public int getMonth() {
        return get(Calendar.MONTH) + 1;
    }

    public int getWeek() {
        return get(Calendar.DAY_OF_WEEK) - 1;
    }

    private String getCyclical(int number) {
        return (stemNames[number % 10] + branchNames[number % 12]);
    }


    /**
     * 得到日柱
     *
     * @return String 得到日柱
     */
    public String getCyclicalDate() {
        int dayCyclical = (int) (getTime().getTime() / 86400000 + 25567 + 10) + 1;
        return getCyclical(dayCyclical);
    }

    /**
     * @return String 得到农历年份
     */
    public String getCyclicalYear() {
        int animalIndex = (getYear() - 4) % 12;
        if (animalIndex < 0) {
            animalIndex = -animalIndex;
        }
        if (getMonth() < 2) {
            return getCyclical(getYear() - 1900 + 36 - 1) + "年-" + animalNames[animalIndex];
        }
        return getCyclical(getYear() - 1900 + 36) + "年-" + animalNames[animalIndex];
    }


    /**
     * @return String 得到月柱
     */
    public String getCyclicalMonth() {
        BigDecimal bigDecimal = NumberUtil.mul(31556925974.7, (get(Calendar.YEAR) - 1900));
        int n = get(Calendar.MONTH) * 2;
        bigDecimal = bigDecimal.add(NumberUtil.mul(sTermInfo[n], 60000));
        bigDecimal = bigDecimal.add(new BigDecimal("-2208549300000"));
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(bigDecimal.longValue()));
        long localTime = calendar.getTime().getTime() + NumberUtil.mul(-(calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000), 60000).longValue();
        calendar.setTime(new Date(localTime));
        int uDate = calendar.get(Calendar.DATE);
        if (get(Calendar.DATE) + 1 > uDate) {
            return getCyclical((get(Calendar.YEAR) - 1900) * 12 + get(Calendar.MONTH) + 13);
        }
        return getCyclical((get(Calendar.YEAR) - 1900) * 12 + get(Calendar.MONTH) + 12);
    }

    /**
     * @return Date 得到一个月的第一天
     */
    public Date getMonthStartDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(get(Calendar.YEAR), get(Calendar.MONTH), 1, 0, 0, 0);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        // 减去dayOfMonth,得到第一天的日期，因为Calendar用０代表每月的第一天，所以要减一
        return calendar.getTime();
    }

    /**
     * @return Date 得到一个月的最后一天
     */
    public Date getMonthEndDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(get(Calendar.YEAR), get(Calendar.MONTH), 1, 0, 0, 0);
        calendar.add(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
        return calendar.getTime();
    }


    /**
     * 判断是否为周末
     * @param calendar 日历
     * @return 是否
     */
    private static  boolean getIsWeekEnd(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }



    /**
     * 注意配置通过  dateMap
     * 计算向后多少个工作日
     * 碰到节假日向后推 周末如果有工作正算
     * @param dateMap 工作日列表，true为上班[班] ，false为放假[假]，如果本来就是周末的节假日则不需再设置
     * @param date 默认开始日期
     * @param day  向后多少个工作日, 不能超过 365天
     * @return 日期
     */
    public Date addWorkDay(Map<String, Boolean> dateMap,Date date,int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int times = 0;
        int i = 0;
        while (times<365) {
            times++;
            calendar.add(Calendar.DAY_OF_MONTH, 1);//整数往后推日期,负数往前推日期
            String dateStr = DateUtil.toString(calendar.getTime(), "yyyy-MM-dd");

            //已经配置了放假
            if (dateMap.containsKey(dateStr) && Boolean.FALSE.equals(dateMap.get(dateStr)))
            {
                continue;
            }
            //配置了上班,和不是周末,都是工作日
            if (dateMap.containsKey(dateStr) && Boolean.TRUE.equals(dateMap.get(dateStr)) ||!getIsWeekEnd(calendar)) {
                //配置了工作日
                i++;
            }
            if (i>=day)
            {
                break;
            }
        }
        //有放假,后推
        times = 0;
        String dateStr = DateUtil.toString(calendar.getTime(), "yyyy-MM-dd");
        while(dateMap.containsKey(dateStr) && Boolean.FALSE.equals(dateMap.get(dateStr)) || !dateMap.containsKey(dateStr) && getIsWeekEnd(calendar))
        {
            ++times;
            calendar.add(Calendar.DATE, 1);//整数往后推日期,负数往前推日期
            dateStr = DateUtil.toString(calendar.getTime(), "yyyy-MM-dd");
            if (times > 365) {
                break;
            }
        }
        return calendar.getTime();
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("年月日: " + DateUtil.toString(getTime(), DateUtil.DAY_FORMAT) + "\n");
        buf.append("年: " + getYear() + "\n");
        buf.append("月: " + getMonth() + "\n");
        buf.append("日: " + get(Calendar.DATE) + "\n");

        buf.append("  " + dayNames[getWeek()] + "\n");
        buf.append("农年: " + chineseYear + "\n");
        buf.append("农月: " + chineseMonth + "\n");
        buf.append("农日: " + chineseDate + "\n");


        buf.append("年柱: " + getCyclicalYear() + "月\n");
        buf.append("月柱: " + getCyclicalMonth() + "月\n");
        buf.append("日柱: " + getCyclicalDate() + "日\n");

        buf.append("周: " + getGregorianWeekOfMonth() + "\n");
        buf.append("润: " + isGregorianLeapYear() + "\n");
        buf.append("第: " + get(Calendar.DAY_OF_YEAR) + "天\n");

        buf.append("Heavenly Stem: " + ((chineseYear - 1) % 10) + "\n");
        buf.append("Earthly Branch: " + ((chineseYear - 1) % 12) + "\n");
        buf.append("Sectional Term: " + sectionalTerm + "\n");
        buf.append("Principle Term: " + principleTerm + "\n");
        buf.append("提示: " + getChineseDateHint() + "\n");
        return buf.toString();
    }

    public static void main(String[] arg) throws Exception {
        /*
        LunarCalendar lunarCalendar = new LunarCalendar();
        lunarCalendar.setGregorian("2007-1-18");
        lunarCalendar.computeChineseFields();
        lunarCalendar.computeSolarTerms();
        System.out.println(lunarCalendar.getChineseDateHint());
        */

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(StringUtil.getDate("2020-11-1"));
        LunarCalendar lunarCalendar = new LunarCalendar();
        for (int i = 1; i < calendar.getMaximum(Calendar.DATE); i++) {
            lunarCalendar.setGregorian(calendar.getTime());
            lunarCalendar.computeChineseFields();
            lunarCalendar.computeSolarTerms();
            System.out.println(lunarCalendar.getChineseDateHint());
            calendar.add(Calendar.DATE, 1);
            System.out.println(lunarCalendar.getChineseDateText());


        }

        /*
        Calendar c = Calendar.getInstance();
        c.setTime(StringUtil.getDate("2020-10-31",DateUtil.DAY_FORMAT));
        System.out.println(DateUtil.Format(DateUtil.DAY_FORMAT,c.getTime()));
        c.set(2020,10,31,0,0);
        System.out.println(DateUtil.Format(DateUtil.DAY_FORMAT,c.getTime()));
        //1900/1/1与 1970/1/1 相差25567日
        */


    }
}