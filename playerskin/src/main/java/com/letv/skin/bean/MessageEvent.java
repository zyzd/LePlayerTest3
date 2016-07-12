package com.letv.skin.bean;

/**
 * Created by 李宗源 on 2016/7/4.
 */
public class MessageEvent {
    private String key;
    private String value;


    public MessageEvent(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
