package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.env.TXWeb;

import com.github.jspxnet.utils.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenYuan on 2017/6/14.
 */
public class ReadEvasiveRuleConfig extends DefaultHandler {

    //命名空间,空间中的action MAP
    private List<EvasiveRule> evasiveRuleList = new ArrayList<EvasiveRule>();
    private String[] whiteList = null;
    private String[] blackList = null;
    private List<QueryBlack> queryBlackRuleList = new ArrayList<QueryBlack>();
    private List<ResultConfigBean> resultConfigList = new ArrayList<ResultConfigBean>();

    final private static String KEY_InsecureUrlKeys = "insecureUrlKeys";
    final private static String KEY_InsecureQueryStringKeys = "insecureQueryStringKeys";
    final private static String KEY_queryBlack = "queryBlack";


    private String[] insecureUrlKeys = null;
    private String[] insecureQueryStringKeys = null;
    private CharArrayWriter contents = new CharArrayWriter();
    private ResultConfigBean resultConfigBean = null;
    private Condition condition = null;
    private QueryBlack queryBlack = null;


    private String[] include = null;
    private EvasiveRule evasiveRule = null;
    private boolean inEvasive = false;
    private boolean inResult = false;
    private boolean inCondition = false;
    private boolean inWhiteList = false;
    private boolean inBlackList = false;
    private boolean inInsecureUrlKeys = false;
    private boolean inInsecureQueryStringKeys = false;

    private boolean inQueryBlack = false;

    ReadEvasiveRuleConfig() {

    }

    @Override
    @SuppressWarnings("unchecked")
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

        if (localName.equalsIgnoreCase(TXWeb.EVASIVE_BlackList)) {
            inBlackList = true;
        }


        if (localName.equalsIgnoreCase(KEY_InsecureUrlKeys)) {
            inInsecureUrlKeys = true;
        }

        if (localName.equalsIgnoreCase(KEY_InsecureQueryStringKeys)) {
            inInsecureQueryStringKeys = true;
        }
        if (localName.equalsIgnoreCase(KEY_queryBlack)) {
            inQueryBlack = true;
            queryBlack = new QueryBlack();
            queryBlack.setName(attr.getValue(TXWeb.CONFIG_NAME));
            queryBlack.setBlackSize(StringUtil.toInt(attr.getValue(TXWeb.EVASIVE_blackSize)));
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
        if (!inEvasive && localName.equalsIgnoreCase(TXWeb.EVASIVE_BlackList)) {
            blackList = ArrayUtil.join(blackList, StringUtil.split(StringUtil.trim(contents.toString()), StringUtil.SEMICOLON));
            inBlackList = false;
        }

        if (inInsecureUrlKeys && localName.equalsIgnoreCase(KEY_InsecureUrlKeys)) {
            insecureUrlKeys = ArrayUtil.join(insecureUrlKeys, StringUtil.split(StringUtil.replace(StringUtil.trim(contents.toString()), StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON));
            inInsecureUrlKeys = false;
        }

        if (inInsecureQueryStringKeys && localName.equalsIgnoreCase(KEY_InsecureQueryStringKeys)) {
            insecureQueryStringKeys = ArrayUtil.join(insecureQueryStringKeys, StringUtil.split(StringUtil.replace(StringUtil.trim(contents.toString()), StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON));
            inInsecureQueryStringKeys = false;
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
        if (inQueryBlack && localName.equalsIgnoreCase(KEY_queryBlack)) {
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

    List<EvasiveRule> getEvasiveRuleList() {
        return evasiveRuleList;
    }

    String[] getWhiteList() {
        return whiteList;
    }

    String[] getBlackList() {
        return blackList;
    }


    String[] getInsecureUrlKeys() {
        return insecureUrlKeys;
    }

    String[] getInsecureQueryStringKeys() {
        return insecureQueryStringKeys;
    }

    List<QueryBlack> getQueryBlackRuleList() {
        return queryBlackRuleList;
    }

    List<ResultConfigBean> getResultConfigList() {
        return resultConfigList;
    }
}
