package com.letv.skin.bean;

/**
 * Created by 李宗源 on 2016/7/3.
 */
public class EventBrightness {
    private String event;
    private int brightness;

    public EventBrightness(String event, int brightness){
        this.event = event;
        this.brightness = brightness;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }
}
