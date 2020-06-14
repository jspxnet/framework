package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.Counters;
import com.github.jspxnet.util.MutableLong;
import com.github.jspxnet.utils.DateUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ChenYuan on 2017/6/2.
 * 用的地方太多，做一个备用
 * 这里使用单例模式设计
 */
@HttpMethod(caption = "计数器")
public class CountersView extends ActionSupport {
    @Ref
    private GenericDAO genericDAO;

    private int id = 0;

    public int getId() {
        return id;
    }

    @Param(request = false)
    public void setId(int id) {
        this.id = id;
    }

    public long add() throws Exception {

        MutableLong initValue = new MutableLong(1);
        // 利用 HashMap 的put方法弹出旧值的特性
        MutableLong oldValue = SingletonCounter.getInstance().getCounter().put(id, initValue);
        if (oldValue != null) {
            //找到数据
            initValue.set(oldValue.get() + 1);
            initValue.setLastTimeMillis(oldValue.getLastTimeMillis());
            if (initValue.get() > 3 && System.currentTimeMillis() - oldValue.getLastTimeMillis() > DateUtil.SECOND * 5) {
                update();
                initValue.setLastTimeMillis(System.currentTimeMillis());
            }
        } else {
            //没有数据
            Counters counters = genericDAO.get(Counters.class, id);
            if (counters == null) {
                //创建一个新的
                SingletonCounter.getInstance().getCounter().put(id, initValue);
                save();
            } else {
                initValue.set(counters.getNum() + 1);
                SingletonCounter.getInstance().getCounter().put(id, initValue);
            }
        }
        return initValue.get();
    }

    public long desc() throws Exception {
        MutableLong initValue = new MutableLong(1);
        // 利用 HashMap 的put方法弹出旧值的特性
        MutableLong oldValue = SingletonCounter.getInstance().getCounter().put(id, initValue);
        if (oldValue != null) {
            initValue.set(oldValue.get() - 1);
            initValue.setLastTimeMillis(oldValue.getLastTimeMillis());
            if (initValue.get() > 1 && System.currentTimeMillis() - oldValue.getLastTimeMillis() > DateUtil.SECOND * 5) {
                update();
                initValue.setLastTimeMillis(System.currentTimeMillis());
            }
        } else {
            //没有数据
            Counters counters = genericDAO.get(Counters.class, id);
            if (counters == null) {
                //创建一个新的
                SingletonCounter.getInstance().getCounter().put(id, initValue);
                save();
            } else {
                initValue.set(counters.getNum() - 1);
            }

        }
        return initValue.get();
    }

    public long getNum() throws Exception {

        MutableLong oldValue = SingletonCounter.getInstance().getCounter().get(id);
        if (oldValue != null) {
            return oldValue.get();
        }
        return 1;
    }

    private void update() throws Exception {
        Counters counters = new Counters();
        counters.setId(id);
        counters.setNum(getNum());
        counters.setPutName(Environment.SYSTEM_NAME);
        counters.setPutUid(Environment.SYSTEM_ID);
        genericDAO.update(counters);
    }

    public void update(int num) throws Exception {
        MutableLong initValue = new MutableLong(num);
        Counters counters = new Counters();
        counters.setId(id);
        counters.setNum(num);
        counters.setPutName(Environment.SYSTEM_NAME);
        counters.setPutUid(Environment.SYSTEM_ID);
        if (genericDAO.update(counters) >= 0) {
            SingletonCounter.getInstance().getCounter().put(id, initValue);
        }
    }


    private void save() throws Exception {
        Counters counters = new Counters();
        counters.setId(id);
        counters.setNum(getNum());
        counters.setPutName(Environment.SYSTEM_NAME);
        counters.setPutUid(Environment.SYSTEM_ID);
        genericDAO.save(counters);
    }
}

class SingletonCounter {
    private Map<Integer, MutableLong> counter = Collections.synchronizedMap(new HashMap<Integer, MutableLong>());
    private static SingletonCounter instance = new SingletonCounter();

    private SingletonCounter() {
    }

    public static synchronized SingletonCounter getInstance() {
        return instance;
    }

    Map<Integer, MutableLong> getCounter() {
        return counter;
    }
}
