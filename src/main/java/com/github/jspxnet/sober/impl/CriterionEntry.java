package com.github.jspxnet.sober.impl;

import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.projection.Criterion;

import java.io.Serializable;

public class CriterionEntry  implements Serializable {
    private final Criterion criterion;
    private final Criteria criteria;

    public CriterionEntry(Criterion criterion, Criteria criteria) {
        this.criteria = criteria;
        this.criterion = criterion;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return criterion.toString();
    }
}

