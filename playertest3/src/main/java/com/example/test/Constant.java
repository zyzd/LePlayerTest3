package com.example.test;

/**
 * Created by 李宗源 on 2016/7/2.
 */
public interface Constant {
    public static final String EVENT_DANMAKU_SIZE_SMALL = "danmaku_size_small";//弹幕字体_小
    public static final String EVENT_DANMAKU_SIZE_MID = "danmaku_size_mid";//弹幕字体_中
    public static final String EVENT_DANMAKU_SIZE_BIG = "danmku_size_big";//弹幕字体_大
    public static final String EVENT_DANMKU_LOCAL_TOP = "danmku_local_top";//弹幕位置_上
    public static final String EVENT_DANMKU_LOCAL_MID = "danmaku_local_mid";//弹幕位置_中
    public static final String EVENT_DANMKU_LOCAL_BOTTOM = "danmuka_local_bottom";//弹幕位置_底
    public static final String EVENT_DANMAKU_TRANS_CHANGE = "danmaku_changeTrans";//弹幕透明度
    public static final String EVENT_PANO_SETTINGMENU_GONE = "setmenu_gone";//全屏时，隐藏设置菜单
    public static final String EVENT_UNREGISTER_EVENT = "event_unregister_event";//注销gesturecontrol、V4LargeMediaController的eventbus
    public static final String EVENT_CONTROLLER_SENDMESSAGE = "sendMessage";//底层控制面板发送弹幕
    public static final String EVENT_CONTROLLER_SENDMESSAGE_FEEDBACK_SUCCESS = "sendMessage_feedback_success";//底层控制面板发送弹幕的反馈
    public static final String EVENT_SHOW_SENDDANMAKU_DIALOG = "show_senddanmaku_dialog";//显示发送弹幕的输入框
}
