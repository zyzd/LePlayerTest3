package com.letv.skin.v4;

import android.content.Context;
import android.util.AttributeSet;

import com.letv.skin.controller.BaseMediaController;

/**
 * Created by 李宗源 on 2016/7/11.
 */
public class A4SmallLiveTopMediaController extends BaseMediaController {

    public A4SmallLiveTopMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public A4SmallLiveTopMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public A4SmallLiveTopMediaController(Context context) {
        super(context);
    }

    @Override
    protected void onSetLayoutId() {
        layoutId = "zyzd_skin_v4_small_top_controller_live_layout";
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected void initPlayer() {
//        BasePlayBtn playBtn = (BasePlayBtn) childViews.get(0);
//        playBtn.setPlayBtnType(BasePlayBtn.play_btn_type_vod);// 设置按钮模式
    }
}
