package com.letv.skin.v4;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lecloud.leutils.ReUtils;
import com.letv.leskin.R;
import com.letv.skin.BaseView;
import com.letv.skin.bean.EventBrightness;
import com.letv.skin.bean.EventTransparency;
import com.letv.skin.utils.ConstantUtils;
import com.letv.skin.utils.ScreenBrightnessManager;
import com.letv.skin.utils.UIPlayContext;
import com.letv.skin.utils.VolumeUtils;
import com.letv.universal.notice.UIObserver;

import java.util.Observable;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 李宗源 on 2016/7/5.
 */
public class ARightSettingMenu extends BaseView implements UIObserver ,View.OnClickListener,SeekBar.OnSeekBarChangeListener{

    private SeekBar mSeekBarVolume;
    private SeekBar mSeekBarBrightness;
    private AudioManager mAudioManager;
    private MyVolumeReceiver mVolumeReceiver;
    private LinearLayout mSettingMenu;
    private TextView mTvDanmakuSizeSmall;
    private TextView mTvDanmakuSizeMid;
    private TextView mTvDanmakuSizeBig;
    private TextView mTvDanmakuLocalTop;
    private TextView mTvDanmakuLocalMid;
    private TextView mTvDanmakuLocalBottom;
    private SeekBar mSeekBarTrans;

    public ARightSettingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ARightSettingMenu(Context context) {
        super(context);
    }

    public ARightSettingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initPlayer() {
        player.attachObserver(this);
    }

    @Override
    public void attachUIContext(UIPlayContext playContext) {//播放广告时掩藏自己，广告结束后显示
        super.attachUIContext(playContext);
        if (uiPlayContext != null) {
            if (uiPlayContext.isPlayingAd()) {
                setVisibility(View.GONE);
            } else {
                setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initView(final Context context) {


        findViews(context);

        setOnClickListener();

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        myRegisterReceiver();
        EventBus.getDefault().register(this);

        mSeekBarVolume.setProgress(VolumeUtils.getInstance(context).getVolume());
        mSeekBarBrightness.setProgress(ScreenBrightnessManager.getScreenBrightness(context));
        mSeekBarTrans.setProgress(70);//这儿得做修改，从sp文件获取，默认值是70。
        mSettingMenu.setVisibility(View.INVISIBLE);
    }

    private void findViews(Context context) {
        LayoutInflater.from(context).inflate(ReUtils.getLayoutId(context,"zyzd_skin_v4_setting_layout"),this);

        mSettingMenu = (LinearLayout) findViewById(R.id.settingMenu);//设置菜单

        mTvDanmakuSizeSmall = (TextView) findViewById(ReUtils.getId(context,"tv_danmaku_size_small"));//弹幕大小
        mTvDanmakuSizeMid = (TextView) findViewById(ReUtils.getId(context,"tv_danmaku_size_mid"));
        mTvDanmakuSizeBig = (TextView) findViewById(ReUtils.getId(context,"tv_danmaku_size_big"));

        mTvDanmakuLocalTop = (TextView) findViewById(ReUtils.getId(context,"tv_danmaku_local_top"));//弹幕位置
        mTvDanmakuLocalMid = (TextView) findViewById(ReUtils.getId(context,"tv_danmaku_local_mid"));
        mTvDanmakuLocalBottom = (TextView) findViewById(ReUtils.getId(context,"tv_danmuka_local_bottom"));

        mSeekBarTrans = (SeekBar) findViewById(ReUtils.getId(context,"seekbar_trans"));//弹幕透明度
        mSeekBarVolume = (SeekBar) findViewById(ReUtils.getId(context,"seekbar_volume"));//弹幕音量
        mSeekBarBrightness = (SeekBar) findViewById(ReUtils.getId(context,"seekbar_brightness"));//弹幕亮度

    }

    private void setOnClickListener() {
        mTvDanmakuSizeSmall.setOnClickListener(this);
        mTvDanmakuSizeMid.setOnClickListener(this);
        mTvDanmakuSizeBig.setOnClickListener(this);
        mTvDanmakuLocalTop.setOnClickListener(this);
        mTvDanmakuLocalMid.setOnClickListener(this);
        mTvDanmakuLocalBottom.setOnClickListener(this);

        mSeekBarTrans.setOnSeekBarChangeListener(this);
        mSeekBarBrightness.setOnSeekBarChangeListener(this);
        mSeekBarVolume.setOnSeekBarChangeListener(this);
    }


    @Override
    public void update(Observable observable, Object data) {
        Bundle bundle = (Bundle) data;

    }

    /**
     * 注册当音量发生变化时接收的广播
     */
    private void myRegisterReceiver(){
        mVolumeReceiver = new MyVolumeReceiver() ;
        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.media.VOLUME_CHANGED_ACTION") ;
        context.registerReceiver(mVolumeReceiver, filter) ;
    }

    /**
     * 改变亮度
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void changScreenBrightness(EventBrightness event) {
        if(event.getEvent().equals(ConstantUtils.EVENT_CHANGE_SCREENBRIGHTNESS_GES2PLAY)){
            mSeekBarBrightness.setProgress(event.getBrightness());
        }
    }

    @Override
    public void onClick(View v) {

        if(v==mTvDanmakuSizeSmall){//调整弹幕大小
            EventBus.getDefault().post(ConstantUtils.EVENT_DANMAKU_SIZE_SMALL);
        }else if(v==mTvDanmakuSizeMid){
            EventBus.getDefault().post(ConstantUtils.EVENT_DANMAKU_SIZE_MID);
        }else if(v==mTvDanmakuSizeBig){
            EventBus.getDefault().post(ConstantUtils.EVENT_DANMAKU_SIZE_BIG);
        }else if(v==mTvDanmakuLocalTop){//调整弹幕位置
            EventBus.getDefault().post(ConstantUtils.EVENT_DANMKU_LOCAL_TOP);
        }else if(v==mTvDanmakuLocalMid){
            EventBus.getDefault().post(ConstantUtils.EVENT_DANMKU_LOCAL_MID);
        }else if(v==mTvDanmakuLocalBottom){
            EventBus.getDefault().post(ConstantUtils.EVENT_DANMKU_LOCAL_BOTTOM);
        }
    }



    /**
     * 处理音量变化时的界面显示
     * @author long
     */
    private class MyVolumeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //如果音量发生变化则更改seekbar的位置
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                mSeekBarVolume.setProgress(VolumeUtils.getInstance(context).getVolume());
            }
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mVolumeReceiver!=null){
            context.unregisterReceiver(mVolumeReceiver);
            mVolumeReceiver= null;
        }

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setState(String message) {
        switch (message) {
            case ConstantUtils.EVENT_PANO_SETTINGMENU_VISIABLE://全屏设置菜单的显示和隐藏
                mSettingMenu.setVisibility(View.VISIBLE);
                break;
            case ConstantUtils.EVENT_PANO_SETTINGMENU_GONE:
                mSettingMenu.setVisibility(View.GONE);
                break;
            case ConstantUtils.EVENT_UNREGISTER_EVENT:
                EventBus.getDefault().unregister(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == mSeekBarTrans){
            if(fromUser){
                EventBus.getDefault().post(new EventTransparency(ConstantUtils.EVENT_DANMAKU_TRANS_CHANGE,progress));
            }
        }else if(seekBar == mSeekBarVolume){
            if(fromUser){
                VolumeUtils.getInstance(context).setVolume(progress);
            }
        }else if(seekBar == mSeekBarBrightness){
            if(fromUser){
                ScreenBrightnessManager.setScreenBrightness((Activity)context,progress);
                ScreenBrightnessManager.saveScreenBrightness((Activity)context,progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
