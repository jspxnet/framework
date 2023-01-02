package com.github.jspxnet.sober.impl;

import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.Order;
import java.io.Serializable;

/**
 * @author chenyuan
 */
public class OrderEntry implements Serializable {
    private final Order order;
    private final Criteria criteria;

    public OrderEntry(Order order, Criteria criteria) {
        this.criteria = criteria;
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return order.toString();
    }
}