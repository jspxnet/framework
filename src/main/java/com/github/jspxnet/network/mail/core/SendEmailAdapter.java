/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.mail.core;





import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.network.mail.MailAuthenticator;
import com.github.jspxnet.boot.environment.Environment;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-17
 * Time: 16:17:15
 */
@Slf4j
public class SendEmailAdapter {
    private static final String SMTP_HOST = "mail.smtp.host";
    private static final String SMTP_AUTH = "mail.smtp.auth";
    private static final String PROTOCOL = "mail.transport.protocol";


    private MimeMessage mimeMsg; //MIME邮件对象
    private final Properties props = new Properties(); //系统属性

    @Setter
    @Getter
    private String user = StringUtil.empty; //smtp认证用户名和密码
    @Setter
    @Getter
    private String password = StringUtil.empty;
    @Setter
    @Getter
    private String from = StringUtil.empty;
    /**
     * -- SETTER --
     *
     * @param encode 设置编码
     */
    @Setter
    @Getter
    private String encode = Environment.defaultEncode;
    @Setter
    @Getter
    private String smtpHost = null;
    @Setter
    @Getter
    private int port = 25;
    /**
     * -- SETTER --
     *  设置收件人
     *
     * @param sendTo String
     */
    @Setter
    private String sendTo = StringUtil.empty;
    @Setter
    private String subject = StringUtil.empty;
    /**
     * -- SETTER --
     *  设置发件内容
     *
     * @param body 正文
     */
    @Setter
    private String body = StringUtil.empty;

    private final List<BodyPart> bodyParts = new LinkedList<BodyPart>();


//Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

    public SendEmailAdapter() {

    }


    /**
     * @param ids   id列表
     * @param files 添加多个附件
     * @return 添加是否成功
     */
    public boolean addFile(String[] ids, String[] files) {
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            if (ids.length < i) {
                if (!addFile(ids[i], files[i])) {
                    return false;
                } else if (!addFile(null, files[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 添加附件
     *
     * @param id   id
     * @param file 文件路径
     * @return 是否成功
     */
    public boolean addFile(String id, String file) {
        if (file == null) {
            return false;
        }
        try {
            if (FileUtil.isFileExist(file)) {
                MimeBodyPart bp = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(file);
                bp.setDataHandler(new DataHandler(fileDataSource));
                if (!StringUtil.isNull(id)) {
                    bp.setHeader("Content-ID", "<" + id + ">");
                }
                String fileName = MimeUtility.encodeText(fileDataSource.getName());
                bp.setFileName(fileName);
                bodyParts.add(bp);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("增加邮件附件：" + file + "发生错误！", e);
            return false;
        }
    }

    /**
     * @param copyto 拷贝到
     * @return 是否成功
     */
    public boolean setCopyTo(String copyto) {
        if (copyto == null) {
            return false;
        }
        try {
            mimeMsg.setRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(copyto));
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }


    public void clear() {

        mimeMsg = null;
    }

    public boolean sendMail() {
        return sendMail(false);
    }


    /**
     * @param replySign 判断此邮件是否需要回执，如果需要回执 "true",否则 "false"
     * @return 发送成功
     */
    public boolean sendMail(boolean replySign) {

        if (StringUtil.isNull(smtpHost) && from != null && from.contains("@")) {
            smtpHost = "smtp." + StringUtil.substringAfter(from, "@");
        }
        props.setProperty(SMTP_HOST, smtpHost); //设置SMTP主机
        props.setProperty(SMTP_AUTH, "true");
        props.setProperty(PROTOCOL, "smtp");

        Session session = Session.getInstance(props, new MailAuthenticator(user, password));
        mimeMsg = new MimeMessage(session); //创建MIME邮件对象

        final MimeMultipart multipart = new MimeMultipart();
        Transport transport = null;
        try {
            mimeMsg.setFrom(new InternetAddress(from)); //设置发信人
            mimeMsg.setSender(new InternetAddress(sendTo));
            mimeMsg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(sendTo)); //收信人
            mimeMsg.setHeader("X-Mailer", "Catseye SMTP Robot");
            mimeMsg.setSubject(subject); //主题

            if (replySign) {
//回执表示
                mimeMsg.setHeader("Disposition-Notification-To", from);
            }
//mimeMsg.saveChanges();
//mimeMsg.setHeader("Return-Receipt-To", from); //返回是否发送成功
            BodyPart bp = new MimeBodyPart();
            bp.setHeader("Content-Type", "text/html; charset=" + encode);
            bp.setContent("<meta http-equiv=Content-Type content=text/html; charset=" + encode + ">" + body, "text/html;charset=" + encode);
            multipart.addBodyPart(bp);
            if (!bodyParts.isEmpty()) {
                for (BodyPart bodyPart : bodyParts) {
                    multipart.addBodyPart(bodyPart);
                }
            }
            mimeMsg.setContent(multipart);
            transport = session.getTransport();
            if (((String) props.get("mail.smtp.host")).contains(":")) {
                String tempPort = StringUtil.substringAfter((String) props.get("mail.smtp.host"), ":");
                if (StringUtil.toInt(tempPort) > 10) {
                    port = StringUtil.toInt(tempPort);
                }
            }
            transport.connect((String) props.get("mail.smtp.host"), port, user, password);
            transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
            log.debug("send mail succeed！");
            bodyParts.clear();
        } catch (Exception e) {
            log.error("send mail error:username=" + user + "  from=" + from + " smtpHost=" + smtpHost, e);
            return false;
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (MessagingException e) {
                log.debug("force close send mail", e);
            }

        }
        return true;
    }
}