package com.github.jspxnet.comm.utils;


import com.github.jspxnet.comm.table.SmsReceive;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

import java.util.*;

/**
 * Created by chenyuan on 2015/8/25.
 * 格式解析
 */
public class FormatParsing {
    static public int getMessageNumber(String response) {
        String[] lines = StringUtil.split(StringUtil.replace(response, "\r\n", "\n"), "\n");
        for (String line : lines) {
            if (line.startsWith("+CPMS")) {
                return StringUtil.toInt(StringUtil.trim(StringUtil.substringBetween(line, ":", ",")));
            }
        }
        return 0;
    }


    /*
    +CMGL: 1,"REC READ","002B0038003600310038003900380035003100370039003800380035","","15/08/24,11:39:18+32"
00320030003100355E740038670881F3003100326708FF0C6BCF67086210529F81EA62636EE1767E5143FF0C5F5367088FD86B3E91D1989D96F65934FF08514389D25206FF0963096210529F6B2165707FFB500D595652B1FF0C6B2167085E95524D595652B18FD481F38FD86B3E501F8BB05361FF1B7D2F8BA16210529F00346B21FF0C53EF

+CMGL: 2,"REC READ","002B0038003600310038003900380035003100370039003800380035","","15/08/24,11:39:22+32"
989D591683B78D600031003000305143501F8BB053615237536191D1595652B1FF0800320030003100365E74003167088FD48FD8FF09FF0C53C24E0E65B95F0F53CA6D3B52A87EC652198BE24EA4884C4FE1752853615B987F5162165FAE4FE1670D52A153F73002005B4EA4901A94F6884C4FE175285361005D

+CMGL: 3,"REC READ","00310030003600390030003000350031003700390030003100380035","","15/08/24,12:31:55+32"
30106DD85B9D3011004067DA5B507F8E8863FF0C003867080032003453F765B052BF529B54686B638FDB884C4E2DFF0C0031003200306B3E521D79CB65B054C1003562985C1D9C9CFF0C5168573A530590AEFF0C731B62335E9794FA00200030003000370037002E00740061006F00620061006F002E0063006F006D0020002390008BA256DE590D00540044
     */

    /**
     * 这里不能合并，因为必须保留messageId给外部删除
     *
     * @param response 请求字符串
     * @return 解析得到消息
     */
    static public List<SmsReceive> getMessageInList(String response) {
        LinkedList<SmsReceive> result = new LinkedList<SmsReceive>();
        if (response == null || !response.contains("+CMGL")) {
            return result;
        }
        String[] lines = StringUtil.split(StringUtil.replace(response, "\r\n", "\n"), "\n");

        SmsReceive smsMessageIn = null;
        for (String line : lines) {
            if (!StringUtil.hasLength(line)) {
                smsMessageIn = null;
            } else if (smsMessageIn != null && !line.startsWith("+CMGL")) {
                smsMessageIn.setContent(smsMessageIn.getContent() + StringUtil.mobileUTFToString(line));
            } else if (line.startsWith("+CMGL")) {
                String[] headLines = StringUtil.split(StringUtil.substringAfter(line, ":"), ",");
                if (headLines.length < 4) {
                    continue;
                }

                smsMessageIn = new SmsReceive();
                result.addLast(smsMessageIn);

                int messageId = StringUtil.toInt(StringUtil.trim(headLines[0]));
                smsMessageIn.setMessageId(messageId);

                String phone = StringUtil.mobileUTFToString(XMLUtil.deleteQuote(headLines[2]));
                smsMessageIn.setOriginalNo(phone);
                String dateTxt = XMLUtil.deleteQuote(headLines[4] + " " + headLines[5]);

                try {
                    Date date = StringUtil.getDate(dateTxt, "yy/MM/dd HH:mm:ss+SSS");
                    smsMessageIn.setMessageDate(date);
                    smsMessageIn.setReceiveDate(date);
                    smsMessageIn.setCreateDate(new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /*
         String txt = "AT+CMGR=1\n" +
                "+CMGR: \"REC READ\",\"002B0038003600310038003900380035003100370039003800380035\",\"\",\"15/08/24,11:39:18+32\"\n" +
                "00320030003100355E740038670881F3003100326708FF0C6BCF67086210529F81EA62636EE1767E5143FF0C5F5367088FD86B3E91D1989D96F65934FF08514389D25206FF0963096210529F6B2165707FFB500D595652B1FF0C6B2167085E95524D595652B18FD481F38FD86B3E501F8BB05361FF1B7D2F8BA16210529F00346B21FF0C53EF\n" +
                "\n" +
                "OK\n" +

+CMGR: "REC READ","002B0038003600310033003900380034003400310035003000330037","","15/08/25,22:53:53+32"
5BF976AE80A45927007300640066
     */
    static public SmsReceive getMessageIn(String response) {
        if (response == null || !response.contains("+CMGR")) {
            return null;
        }
        String[] lines = StringUtil.split(StringUtil.replace(response, "\r\n", "\n"), "\n");
        SmsReceive smsMessageIn = new SmsReceive();
        for (int i = 0; i < lines.length; i++) {
            if (!StringUtil.hasLength(lines[i])) {
                continue;
            }
            if (lines[i].startsWith("AT+CMGR=")) {
                int messageId = StringUtil.toInt(StringUtil.trim(StringUtil.substringAfter(lines[i], StringUtil.EQUAL)));
                smsMessageIn.setMessageId(messageId);
            } else if (lines[i].startsWith("+CMGR")) {
                String[] headLines = StringUtil.split(StringUtil.substringAfter(lines[i], ":"), ",");
                if (headLines.length < 3) {
                    continue;
                }
                String phone = StringUtil.mobileUTFToString(XMLUtil.deleteQuote(headLines[1]));
                smsMessageIn.setOriginalNo(phone);
                String dateTxt = XMLUtil.deleteQuote(headLines[3] + " " + headLines[4]);
                try {
                    Date date = StringUtil.getDate(dateTxt, "yy/MM/dd HH:mm:ss+SSS");
                    smsMessageIn.setMessageDate(date);
                    smsMessageIn.setReceiveDate(date);
                    smsMessageIn.setCreateDate(new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (i + 1 < lines.length) {
                    smsMessageIn.setContent(smsMessageIn.getContent() + StringUtil.mobileUTFToString(lines[i + 1]));
                }
                return smsMessageIn;
            }
        }
        return smsMessageIn;
    }

    /**
     * RING
     *
     * @param response {@code  +CLIP: "13984415037",161,"",,"",0 }
     * @return 得到来电号码
     */
    static public String getRingCode(String response) {
        String[] lines = StringUtil.split(StringUtil.replace(response, "\r\n", "\n"), "\n");
        for (String line : lines) {
            if (line.startsWith("+CLIP")) {
                return StringUtil.substringBetween(line, "\"", "\",");
            }
        }
        return null;
    }

    /**
     * @param response 应答数据
     * @return 得到新的短信id
     */
    static public int getNewMessageId(String response) {
        String[] lines = StringUtil.split(StringUtil.replace(response, "\r\n", "\n"), "\n");
        for (String line : lines) {
            if (line.startsWith("+CMTI")) {
                return StringUtil.toInt(StringUtil.trim(StringUtil.substringAfter(line, ",")));
            }
        }
        return -1;
    }

    /**
     * @param response 应答数据
     * @return 得到信号强度
     */
    static public int getSignal(String response) {
        String[] lines = StringUtil.split(StringUtil.replace(response, "\r\n", "\n"), "\n");
        for (String line : lines) {
            if (line.startsWith("+CSQ")) {
                return StringUtil.toInt(StringUtil.trim(StringUtil.substringBetween(line, ":", ",")));
            }
        }
        return 99;
    }

    /**
     * @param messageInList 短信列表
     * @return 合并规则，同一个电话号码，并且时间间隔在1分钟内，第一条短信长度大于60
     */
    static public List<SmsReceive> getJoinMessage(List<SmsReceive> messageInList) {
        //接收到的短信并不是按照顺序过来的，合并的时候，最短的一条放在尾部
        Map<String, LinkedList<SmsReceive>> phoneMessages = new HashMap<String, LinkedList<SmsReceive>>();
        for (SmsReceive messageIn : messageInList) {
            LinkedList<SmsReceive> mapList = phoneMessages.get(messageIn.getOriginalNo());
            if (mapList == null) {
                //第一条
                mapList = new LinkedList<SmsReceive>();
                mapList.add(messageIn);
                phoneMessages.put(messageIn.getOriginalNo(), mapList);
            } else {
                SmsReceive messageInOld = mapList.getLast();
                if (messageIn.getGatewayName().equalsIgnoreCase(messageInOld.getGatewayName()) &&
                        messageIn.getOriginalNo().equalsIgnoreCase(messageInOld.getOriginalNo()) &&
                        (messageInOld.getContent().length() > 60 || messageIn.getContent().length() > 60) &&
                        (Math.abs(messageIn.getMessageDate().getTime() - messageInOld.getMessageDate().getTime()) < DateUtil.SECOND * 60)) {
                    if (messageInOld.getContent().length() > 60) {
                        messageInOld.setContent(messageInOld.getContent() + messageIn.getContent());
                    } else {
                        messageInOld.setContent(messageIn.getContent() + messageInOld.getContent());
                    }
                    messageInOld.setMessageDate(messageIn.getMessageDate());
                    messageInOld.addMessageIds(messageIn.getMessageId());
                    messageInOld.setMarkType("M");
                } else {
                    mapList.addLast(messageIn);
                }
            }
        }
        List<SmsReceive> result = new ArrayList<SmsReceive>();
        for (LinkedList<SmsReceive> list : phoneMessages.values()) {
            result.addAll(list);
        }
        return result;
    }


}
