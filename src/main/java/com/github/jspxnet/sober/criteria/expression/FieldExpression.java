package com.github.jspxnet.sober.criteria.expression;

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.enums.DatabaseEnumType;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by chenyuan on 15-5-5.
 */
public class FieldExpression implements Criterion {
    private final String field1;
    private final String compare;
    private final String field2;

    public FieldExpression(String field1, String compare, String field2) {
        this.field1 = field1;
        this.compare = compare;
        this.field2 = field2;
    }

    @Override
    public String toSqlString(TableModels soberTable, String databaseName) {
        StringBuilder sb = new StringBuilder();
        if (DatabaseEnumType.DM.equals(DatabaseEnumType.find(databaseName)))
        {
            sb.append(StringUtil.quote(field1,true)).append(compare).append(StringUtil.quote(field2,true));
        }
        else
        {
            sb.append(field1).append(compare).append(field2);
        }
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