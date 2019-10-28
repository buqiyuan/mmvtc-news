package com.tab.mmvtc_news.utils;

import java.io.Serializable;
import java.util.HashMap;


public class SerializableHashMap implements Serializable{
    private HashMap<String, Object> map;

    public HashMap<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }
}
