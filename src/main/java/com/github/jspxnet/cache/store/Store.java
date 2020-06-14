package com.github.jspxnet.cache.store;

import com.github.jspxnet.cache.IStore;

public abstract class Store implements IStore {
    private int maxElements = 20000;
    private int second;
    private String name = null;

    @Override
    public int getMaxElements() {
        return maxElements;
    }

    @Override
    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;
    }

    @Override
    public int getSecond() {
        return second;
    }

    @Override
    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
