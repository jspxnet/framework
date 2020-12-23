/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.utils.ObjectUtil;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import java.util.*;
import java.io.CharArrayWriter;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-5
 * Time: 11:26:52
 * [action name="name1" alias"name2" class="fmAction"]
 * [interceptor-ref name="fmInter"/]
 * [/action]
 * <p>
 * 读取配置
 */
public class ReadConfig extends DefaultHandler {
    //命名空间,空间中的action MAP
    private Map<String, Map<String, ActionConfigBean>> allMap = null;
    //命名空间列表,<命名空间,继承的命名空间>
    private Map<String, String> extendMap = null;
    //拦截器列表,
    private Map<String, List<DefaultInterceptorBean>> defaultInterceptorMap = null;
    private Map<String, List<ResultConfigBean>> defaultResultMap = null;
    private List<ScanConfig> scanConfigList = new ArrayList<>();
    private Map<String, ActionConfigBean> groupMap;
    private CharArrayWriter contents = new CharArrayWriter();
    private ActionConfigBean actionConfigBean = null;
    private ResultConfigBean resultConfigBean = null;

    private boolean isAction = false;
    private String[] include = null;
    private String paramName;
    private boolean isDefault = false;
    private ResultConfigBean defaultResultBean = null; //默认返回
    private String namespace = StringUtil.empty;

    private final static String TAG_alias = "alias";
    private final static String TAG_pass = "pass";


    public ReadConfig(final Map<String, Map<String, ActionConfigBean>> actionMap, final Map<String, String> extendMap,
                      final Map<String, List<DefaultInterceptorBean>> defaultInterceptorMap,
                      final Map<String, List<ResultConfigBean>> defaultResultMap) {
        this.allMap = actionMap;
        this.extendMap = extendMap;
        this.defaultResultMap = defaultResultMap;
        this.defaultInterceptorMap = defaultInterceptorMap;
    }

    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) {
        contents.reset();
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_INCLUDE)) {

            include = ArrayUtil.add(include, attr.getValue(TXWeb.CONFIG_FILE));
        }
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_PACKAGE)) {
            namespace = attr.getValue(TXWeb.CONFIG_NAMESPACE) == null ? StringUtil.empty : attr.getValue(TXWeb.CONFIG_NAMESPACE);
            if (StringUtil.isNull(namespace)) {
                namespace = TXWeb.global;
            }
            String extend = attr.getValue(TXWeb.CONFIG_EXTENDS);
            if (StringUtil.isNull(extend)) {
                extend = TXWeb.global;
            }
            if (!namespace.equals(extend)) {
                extendMap.put(namespace, extend);
            }
            groupMap = allMap.get(namespace);
            if (groupMap == null) {
                groupMap = new HashMap<String, ActionConfigBean>();
                allMap.put(namespace, groupMap);
            }
        }
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_DEFAULT)) {
            isDefault = true;
        }
        if (isDefault && localName.equalsIgnoreCase(TXWeb.CONFIG_INTERCEPTOR_REF)) {
            List<DefaultInterceptorBean> defaultInterceptors = defaultInterceptorMap.get(namespace);
            if (defaultInterceptors == null) {
                defaultInterceptors = new LinkedList<DefaultInterceptorBean>();
                defaultInterceptorMap.put(namespace, defaultInterceptors);
            }
            DefaultInterceptorBean defaultInterceptorBean = new DefaultInterceptorBean();
            defaultInterceptorBean.setName(attr.getValue(TXWeb.CONFIG_NAME));
            defaultInterceptorBean.setCaption(attr.getValue(TXWeb.CONFIG_CAPTION));
            defaultInterceptorBean.setExtend(ObjectUtil.toBoolean(attr.getValue(TXWeb.CONFIG_EXTENDS)));
            defaultInterceptors.add(defaultInterceptorBean);
        }

        if (isDefault && localName.equalsIgnoreCase(TXWeb.CONFIG_RESULT)) {

            List<ResultConfigBean> defaultResults = defaultResultMap.get(namespace);
            if (defaultResults == null) {
                defaultResults = new LinkedList<ResultConfigBean>();
                defaultResultMap.put(namespace, defaultResults);
            }
            defaultResultBean = new ResultConfigBean();

            String resultName = attr.getValue(TXWeb.CONFIG_NAME);

            if (StringUtil.isNull(resultName)) {
                resultName = StringUtil.ASTERISK;
            }
            defaultResultBean.setName(StringUtil.trim(resultName));
            String type = attr.getValue(TXWeb.CONFIG_TYPE);
            if (StringUtil.isNull(type)) {
                type = TXWeb.CONFIG_TEMPLATE;
            }
            defaultResultBean.setType(type.trim());
            defaultResults.add(defaultResultBean);
        }
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_SCAN)) {
            ScanConfig scanConfig = new ScanConfig();
            scanConfig.setPackageName(attr.getValue(TXWeb.CONFIG_PACKAGE));
            scanConfig.setNamespace(namespace);
            scanConfigList.add(scanConfig);
        }

        if (localName.equalsIgnoreCase(TXWeb.CONFIG_ACTION)) {
            actionConfigBean = new ActionConfigBean();
            String actionName = attr.getValue(TXWeb.CONFIG_NAME);
            if (actionName == null) {
                actionName = StringUtil.empty;
            }
            actionConfigBean.setActionName(actionName);
            actionConfigBean.setCaption(attr.getValue(TXWeb.CONFIG_CAPTION) == null ? StringUtil.empty : attr.getValue(TXWeb.CONFIG_CAPTION));

            actionConfigBean.setIocBean(attr.getValue(TXWeb.CONFIG_CLASS));
            try {
                actionConfigBean.setMethod(attr.getValue(TXWeb.CONFIG_METHOD));
            } catch (Exception e) {
                e.printStackTrace();
            }

            actionConfigBean.setPassInterceptor(StringUtil.split(StringUtil.replace(attr.getValue(TAG_pass), StringUtil.SEMICOLON, ","), ","));
            actionConfigBean.setSecret(StringUtil.toBoolean(attr.getValue(TXWeb.CONFIG_SECRET)));
            actionConfigBean.setMobile(StringUtil.toBoolean(attr.getValue(TXWeb.CONFIG_MOBILE)));
            actionConfigBean.setCache(StringUtil.toBoolean(attr.getValue(TXWeb.CONFIG_CACHE)));
            groupMap.put(actionName, actionConfigBean);
            ///////////别名支持begin
            String alias = attr.getValue(TAG_alias);
            if (!StringUtil.isNull(alias)) {
                String[] aliasArray = StringUtil.split(StringUtil.replace(alias, "/", StringUtil.SEMICOLON));
                for (String aa : aliasArray) {
                    if (aa == null) {
                        continue;
                    }
                    groupMap.put(aa, actionConfigBean);
                }
            }
            ///////////别名支持end
            isAction = true;
        }
        if (isAction && !isDefault) {
            if (localName.equalsIgnoreCase(TXWeb.CONFIG_INTERCEPTOR_REF)) {
                actionConfigBean.addInterceptors(attr.getValue(TXWeb.CONFIG_NAME));
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
                actionConfigBean.addResultConfig(resultConfigBean);
            }
            if (localName.equalsIgnoreCase(TXWeb.CONFIG_PARAM)) {
                paramName = attr.getValue(TXWeb.CONFIG_NAME);
            }
        }
    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (isAction && !isDefault && localName.equalsIgnoreCase(TXWeb.CONFIG_PARAM)) {
            actionConfigBean.addParam(paramName.trim(), contents.toString().trim());
            paramName = StringUtil.empty;
        }
        if (isAction && !isDefault && localName.equalsIgnoreCase(TXWeb.CONFIG_RESULT)) {
            resultConfigBean.setValue(contents.toString().trim());
        }
        if (isDefault && localName.equalsIgnoreCase(TXWeb.CONFIG_RESULT)) {
            defaultResultBean.setValue(contents.toString().trim());
        }
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_ACTION)) {
            isAction = false;
        }
        if (localName.equalsIgnoreCase(TXWeb.CONFIG_DEFAULT)) {
            isDefault = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        contents.write(ch, start, length);
    }

    public String[] getInclude() {
        return include;
    }

    public List<ScanConfig> getScanConfigList() {
        return scanConfigList;
    }

}