/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.mail.util;



import com.sun.mail.util.ASCIIUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

import com.github.jspxnet.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-18
 * Time: 15:49:20
 */
public class MailUtil {
    private static final Logger log = LoggerFactory.getLogger(MailUtil.class);

    private MailUtil() {
    }

    /**
     * @param a    地址
     * @param isgb 是否为gb编码
     * @return 转换RFC822为Unicode
     */
    static public String getDisplayAddress(Address a, boolean isgb) {
        String pers;
        String addr;
        if (a instanceof InternetAddress && ((pers = ((InternetAddress) a).getPersonal()) != null)) {
            addr = pers + " " + "<" + ((InternetAddress) a).getAddress() + ">";
        } else {
            addr = a.toString();
        }

        if (isgb) {
            addr = new String(ASCIIUtility.getBytes(addr));
        }
        if (addr == null) {
            return StringUtil.empty;
        }
        return addr;
    }

    /**
     * @param value 邮件正文
     * @return 解开乱码
     */
    static public String decodeWord(String value) {
        if (StringUtil.isNull(value)) {
            int itmp = value.indexOf("=?");
            if (itmp != -1 && itmp < 4) {
                try {
                    value = MimeUtility.decodeWord(value);
                } catch (Exception e) {
                    try {
                        return decodeText(value);
                    } catch (UnsupportedEncodingException e1) {
                        log.error("ERROR decodeText:\r\n" + value, e1);
                    }
                    log.error("ERROR decodeWord:\r\n" + value, e);
                }
            }
        }
        return value;
    }

    static public String decodeText(String text) throws UnsupportedEncodingException {
        if (text == null) {
            return null;
        }
        if (text.startsWith("=?GB") || text.startsWith("=?gb")) {
            text = MimeUtility.decodeText(text);
        } else {
            text = new String(text.getBytes("ISO8859_1"));
        }
        return text;
    }


    /**
     * 邮件保存为文件
     *
     * @param message 消息
     * @param f       文件
     * @throws Exception 异常
     */
    static public void save(Message message, File f) throws Exception {
        message.writeTo(new FileOutputStream(f));
    }


    /**
     * @param flags 标识
     * @return 得到标准
     */
    static public String getFlagsString(Flags flags) {
        StringBuilder sb = new StringBuilder();
        if (flags.contains(Flags.Flag.ANSWERED)) {
            sb.append("ANSWERED/");
        }
        if (flags.contains(Flags.Flag.DELETED)) {
            sb.append("DELETED/");
        }
        if (flags.contains(Flags.Flag.DRAFT)) {
            sb.append("DRAFT/");
        }
        if (flags.contains(Flags.Flag.FLAGGED)) {
            sb.append("FLAGGED/");
        }
        if (flags.contains(Flags.Flag.RECENT)) {
            sb.append("RECENT/");
        }
        if (flags.contains(Flags.Flag.SEEN)) {
            sb.append("SEEN/");
        }
        if (flags.contains(Flags.Flag.USER)) {
            sb.append("USER/");
        }
        return sb.toString();
    }

}