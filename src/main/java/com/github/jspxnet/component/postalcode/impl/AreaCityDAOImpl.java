/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.postalcode.impl;

import com.github.jspxnet.component.postalcode.AreaCityDAO;
import com.github.jspxnet.component.postalcode.AreaCity;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.Criteria;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-14
 * Time: 17:34:15
 * jspx.juser.dao.aop.AreaCityDAOImpl.
 */

public class AreaCityDAOImpl extends JdbcOperations implements AreaCityDAO {

    public AreaCityDAOImpl() {

    }

    @Override
    public List<AreaCity> getProvinceList() {
        return createCriteria(AreaCity.class)
                .add(Expression.or(Expression.eq("parentAreaId", null), Expression.isNull("parentAreaId")))
                .addOrder(Order.desc("postalcode"))
                .list(false);
    }

    /**
     * 得到所有的列表
     */
    @Override
    public List<AreaCity> getAreaCityList() {
        return createCriteria(AreaCity.class).list(false);
    }

    /**
     * 得到总数
     *
     * @return int
     */
    @Override
    public long getCount() {
        Criteria criteria = createCriteria(AreaCity.class);
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }

    //使用XML方式创建
    @Override
    public boolean createAreaCity(String xmlString) {

        /*
        if (StringUtil.isNull(xmlString)) return false;
        ReadAreaCity readAreaCity;
        try
        {
            readAreaCity = (ReadAreaCity) XMLUtil.xmlString(new ReadAreaCity(), xmlString);
            List<AreaCity> saveList = new ArrayList<AreaCity>();
            Map<String, AreaCity> areaCityMap = readAreaCity.getAreaCityMap();
            ////////////////////////判断是否需要创建 begin
            if (areaCityMap.size() <= getCount())
            {
                return true;
            }
            ////////////////////////判断是否需要创建 end

            for (String areaCityId : areaCityMap.keySet())
            {
                AreaCity areaCity = areaCityMap.get(areaCityId);
                AreaCity parentAreaCity = areaCity.getParentArea();
                if (parentAreaCity != null && "#".equals(parentAreaCity.getAreaName()))
                {
                    AreaCity newParentTreeItem = areaCityMap.get(parentAreaCity.getId());
                    areaCity.setParentArea(newParentTreeItem);
                }
                saveList.add(areaCity);
            }

            //////////////////////////////清空
            deleteAll(getAreaCityList());

            /////////////////////////////
            saveOrUpdateAll(saveList);

            saveList.clear();
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        */
        return true;
    }

}