package com.github.jspxnet.sober.table.meta;

import java.io.Serializable;

/**
 * @author chenYuan
 *
 *
 */
public interface AbstractBillPlug extends Serializable {

    void before(BillEvent event) throws Exception;

    void after(BillEvent event);

}
