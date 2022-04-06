package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.env.TXWeb;

import com.github.jspxnet.utils.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChenYuan on 2017/6/14.
 */
public class ReadEvasiveRuleConfig extends DefaultHandler {

    //命名空间,空间中的action MAP
    private final List<EvasiveRule> evasiveRuleList = new ArrayList<>();
    private String[] whiteList = null;
    private String[] blackList = null;
    private final List<QueryBlack> queryBlackRuleList = new ArrayList<>();
    private final List<ResultConfigBean> resultConfigList = new ArrayList<>();

    final private static String KEY_INSECURE_URL_KEYS = "insecureUrlKeys";
    final private static String KEY_INSECURE_QUERY_STRING_KEYS = "insecureQueryStringKeys";
    final private static String KEY_QUERY_BLACK = "queryBlack";
    final private static String KEY_VALUE = "value";
    final private static String KEY_PASSWORD = "password";



    //密码访问目录,<密码,目录>
    private Map<String,String> passwordFolderList = new HashMap<>();
    private String[] blackSuffixList = null;
    private String[] insecureUrlKeys = null;
    private String[] insecureQueryStringKeys = null;
    private final CharArrayWriter contents = new CharArrayWriter();
    private ResultConfigBean resultConfigBean = null;
    private Condition condition = null;
    private QueryBlack queryBlack = null;


    private String[] include = null;
    private EvasiveRule evasiveRule = null;
    private boolean inEvasive = false;
    private boolean inPasswordFolder = false;
    private boolean inResult = false;
    private boolean inCondition = false;
    private boolean inWhiteList = false;
    private boolean inBlackList = false;
    private boolean inBlackSuffix = false;

    private boolean inInsecureUrlKeys = false;
    private boolean inInsecureQueryStringKeys = false;
    private boolean inQueryBlack = false;
    private String password = "";

    ReadEvasiveRuleConfig() {

    }

    @Override
    //@SuppressWarnings("unchecked")
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_INCLUDE)) {

            include = ArrayUtil.add(include, attr.getValue(TXWeb.CONFIG_FILE));
        }

        if (localName.equalsIgnoreCase(TXWeb.EVASIVE_WHITELIST)) {
            inWhiteList = true;
        }

        if (localName.equalsIgnoreCase(TXWeb.EVASIVE_BLACK_LIST)) {
            inBlackList = true;
        }

        if (localName.equalsIgnoreCase(TXWeb.EVASIVE_BLACK_SUFFIX_LIST)) {
            inBlackSuffix = true;
        }

        if (localName.equalsIgnoreCase(TXWeb.EVASIVE_PASSWORD_FOLDER)) {
            inPasswordFolder = true;
        }

        if (inPasswordFolder&&localName.equalsIgnoreCase(KEY_VALUE))
        {
            password = attr.getValue(KEY_PASSWORD);
        }

        if (localName.equalsIgnoreCase(KEY_INSECURE_URL_KEYS)) {
            inInsecureUrlKeys = true;
        }

        if (localName.equalsIgnoreCase(KEY_INSECURE_QUERY_STRING_KEYS)) {
            inInsecureQueryStringKeys = true;
        }
        if (localName.equalsIgnoreCase(KEY_QUERY_BLACK)) {
            inQueryBlack = true;
            queryBlack = new QueryBlack();
            queryBlack.setName(attr.getValue(TXWeb.CONFIG_NAME));
            queryBlack.setBlackSize(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_BLACK_SIZE)));
            queryBlack.setIpField(attr.getValue(TXWeb.EVASIVE_ipField));
            queryBlack.setTimesField(attr.getValue(TXWeb.EVASIVE_timesField));
            queryBlack.setMinTimes(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_minTimes)));
            queryBlack.setImprisonSecond(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_IMPRISON_SECOND)));
            queryBlack.setResult(attr.getValue(TXWeb.CONFIG_RESULT));
        }


        if (localName.equalsIgnoreCase(TXWeb.CONFIG_EVASIVE)) {
            evasiveRule = new EvasiveRule();
            evasiveRule.setName(attr.getValue(TXWeb.CONFIG_NAME));
            evasiveRule.setInterval(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_INTERVAL), 5));
            evasiveRule.setMaxTimes(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_MAX_TIMES), 10));
            evasiveRule.setMethod(attr.getValue(TXWeb.CONFIG_METHOD));
            evasiveRule.setUrl(attr.getValue(TXWeb.EVASIVE_URL));
            evasiveRule.setImprisonSecond(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_IMPRISON_SECOND)));
            evasiveRule.setResult(attr.getValue(TXWeb.CONFIG_RESULT));
            inEvasive = true;
        }
        if (inEvasive) {
            //CONFIG_SCRIPT
            if (localName.equalsIgnoreCase(TXWeb.EVASIVE_CONDITION)) {
                condition = new Condition();
                condition.setRuleType(attr.getValue(TXWeb.CONFIG_TYPE));
                inCondition = true;
            }
        }
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_RESULT)) {
            resultConfigBean = new ResultConfigBean();
            String resultName = attr.getValue(TXWeb.CONFIG_NAME);
            if (StringUtil.isNull(resultName)) {
                resultName = StringUtil.ASTERISK;
            }
            resultConfigBean.setName(resultName.trim());
            String type = attr.getValue(TXWeb.CONFIG_TYPE);
            if (StringUtil.isNull(type)) {
                type = TXWeb.CONFIG_TEMPLATE;
            }
            resultConfigBean.setType(type.trim());
            String status = attr.getValue(TXWeb.CONFIG_STATUS);
            if (StringUtil.isNull(status)) {
                status = "200";
            }
            resultConfigBean.setStatus(StringUtil.toInt(status, 200));
            inResult = true;
        }
    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {

        if (!inEvasive && inWhiteList && localName.equalsIgnoreCase(TXWeb.EVASIVE_WHITELIST)) {
            whiteList = ArrayUtil.join(whiteList, StringUtil.split(StringUtil.trim(contents.toString()), StringUtil.SEMICOLON));
            inWhiteList = false;
        }
        if (!inEvasive && inBlackList&&localName.equalsIgnoreCase(TXWeb.EVASIVE_BLACK_LIST)) {
            blackList = ArrayUtil.join(blackList, StringUtil.split(StringUtil.trim(contents.toString()), StringUtil.SEMICOLON));
            inBlackList = false;
        }

        if (inInsecureUrlKeys && localName.equalsIgnoreCase(KEY_INSECURE_URL_KEYS)) {
            insecureUrlKeys = ArrayUtil.join(insecureUrlKeys, StringUtil.split(StringUtil.replace(StringUtil.trim(contents.toString()), StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON));
            inInsecureUrlKeys = false;
        }

        if (inInsecureQueryStringKeys && localName.equalsIgnoreCase(KEY_INSECURE_QUERY_STRING_KEYS)) {
            insecureQueryStringKeys = ArrayUtil.join(insecureQueryStringKeys, StringUtil.split(StringUtil.replace(StringUtil.trim(contents.toString()), StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON));
            inInsecureQueryStringKeys = false;
        }

        if (inBlackSuffix && localName.equalsIgnoreCase(TXWeb.EVASIVE_BLACK_SUFFIX_LIST)) {
            blackSuffixList = ArrayUtil.join(blackSuffixList, StringUtil.split(StringUtil.replace(StringUtil.trim(contents.toString()), StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON));
            inBlackSuffix = false;
        }


        if (inPasswordFolder&&localName.equalsIgnoreCase(KEY_VALUE))
        {
            String folder = StringUtil.trim(contents.toString());
            if (!StringUtil.isNull(folder)&&password!=null)
            {
                passwordFolderList.put(folder,password);
            }
        }


        if (localName.equalsIgnoreCase(TXWeb.EVASIVE_PASSWORD_FOLDER)) {
            inPasswordFolder = false;
        }



        if (inEvasive && inCondition && localName.equalsIgnoreCase(TXWeb.EVASIVE_CONDITION)) {
            condition.setScript(StringUtil.trim(contents.toString()));
            evasiveRule.addConditions(condition);
            inCondition = false;
        }
        if (inResult && localName.equalsIgnoreCase(TXWeb.CONFIG_RESULT)) {
            resultConfigBean.setValue(StringUtil.trim(contents.toString()));
            resultConfigList.add(resultConfigBean);
            inResult = false;
        }
        if (inEvasive && localName.equalsIgnoreCase(TXWeb.CONFIG_EVASIVE)) {
            inEvasive = false;
            evasiveRuleList.add(evasiveRule);
        }
        if (inQueryBlack && localName.equalsIgnoreCase(KEY_QUERY_BLACK)) {
            queryBlack.setSql(StringUtil.trim(contents.toString()));
            queryBlackRuleList.add(queryBlack);
            inQueryBlack = false;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        contents.write(ch, start, length);
    }

    public String[] getInclude() {
        return include;
    }

    public List<EvasiveRule> getEvasiveRuleList() {
        return evasiveRuleList;
    }

    public String[] getWhiteList() {
        return whiteList;
    }

    public String[] getBlackList() {
        return blackList;
    }

    public String[] getInsecureUrlKeys() {
        return insecureUrlKeys;
    }

    public String[] getInsecureQueryStringKeys() {
        return insecureQueryStringKeys;
    }

    public List<QueryBlack> getQueryBlackRuleList() {
        return queryBlackRuleList;
    }

    public String[] getBlackSuffixList() {
        return blackSuffixList;
    }

    public List<ResultConfigBean> getResultConfigList() {
        return resultConfigList;
    }

    public Map<String, String> getPasswordFolderList() {
        return passwordFolderList;
    }
}
