package com.letv.skin.bean;

/**
 * Created by 李宗源 on 2016/7/3.
 */
public class EventTransparency {
    private String event;
    private int progress;

    public EventTransparency(String event, int progress) {
        this.event = event;
        this.progress = progress;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
