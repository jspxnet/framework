package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.projection.Criterion;

/**
 * Created by chenyuan on 15-5-5.
 */
public class FieldExpression implements Criterion {
    private String field1;
    private String compare;
    private String field2;

    public FieldExpression(String field1, String compare, String field2) {
        this.field1 = field1;
        this.compare = compare;
        this.field2 = field2;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        sb.append(field1).append(compare).append(field2);
        return sb.toString();
    }

    @Override
    public Object[] getParameter(TableModels soberTable) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(field1).append(compare).append(field2);
        return sb.toString();
    }

    @Override
    public String[] getFields() {
        return new String[]{field1};
    }

    @Override
    public String termString() {
        return toString();
    }
}