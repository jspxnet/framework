package com.github.jspxnet.txweb.evasive.condition;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public class SqlDecide extends AbstractDecide {
    final static private Logger log = LoggerFactory.getLogger(SqlDecide.class);

    @Override
    public boolean execute() {
        if (StringUtil.isNull(content) || !content.toLowerCase().contains("select")) {
            return false;
        }
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        GenericDAO genericDAO = beanFactory.getBean(GenericDAO.class);
        try {
            return ObjectUtil.toBoolean(genericDAO.getUniqueResult(content));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("evasive Sql Decide config error:" + content, e);
        }
        return true;
    }
}
