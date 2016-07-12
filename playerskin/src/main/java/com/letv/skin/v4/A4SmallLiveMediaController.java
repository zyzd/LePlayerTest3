package com.letv.skin.v4;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lecloud.leutils.ReUtils;
import com.letv.skin.base.BaseLiveSeekBar;
import com.letv.skin.base.BasePlayBtn;
import com.letv.skin.controller.BaseMediaController;

/**
 * Created by 李宗源 on 2016/7/11.
 */
public class A4SmallLiveMediaController extends BaseMediaController {
//    private BaseLiveSeekBar seekbar;
//    private TextView seekTime;

    public A4SmallLiveMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public A4SmallLiveMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public A4SmallLiveMediaController(Context context) {
        super(context);
    }

    @Override
    protected void onSetLayoutId() {
        layoutId = "zyzd_skin_v4_controller_live_layout";
//        childId.add("vnew_play_btn");
        childId.add("vnew_chg_btn");
//        childId.add("vnew_seekbar");
    }

    @Override
    protected void onInitView() {

        /*seekbar = (BaseLiveSeekBar) childViews.get(2);
        seekTime = (TextView) findViewById(ReUtils.getId(context, "vnew_time_text"));
        seekbar.setOnSeekChangeListener(new BaseLiveSeekBar.OnSeekChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!seekbar.isShown()) {
                    return;
                }
                if (seekTime != null) {
                    if (seekTime.getVisibility() != VISIBLE) {
                        seekTime.setVisibility(VISIBLE);
                    }
                    LayoutParams params = (LayoutParams) seekTime.getLayoutParams();
                    int right = seekBar.getRight() - seekBar.getWidth() * progress / seekBar.getMax();
                    if (fromUser) {
                        seekTime.setText("正在播放：" + seekbar.getSeekToTime());
                    } else {
                        seekTime.setText("正在播放：" + seekbar.getCurrentTime());
                    }
                    int leftMargin = seekBar.getRight() - right - seekTime.getMeasuredWidth();
                    if (leftMargin > 0) {
                        params.rightMargin = right;
                        seekTime.setLayoutParams(params);
                    }
                }
            }
        });*/

    }

    @Override
    protected void initPlayer() {
//        BasePlayBtn playBtn = (BasePlayBtn) childViews.get(0);
//        playBtn.setPlayBtnType(BasePlayBtn.play_btn_type_vod);// 设置按钮模式
    }
}
