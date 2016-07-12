package com.letv.simple.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.letv.controller.interfacev1.IPanoVideoChangeMode;
import com.letv.pano.ISurfaceListener;
import com.letv.pano.OnPanoViewTapUpListener;
import com.letv.pano.PanoVideoControllerView;
import com.letv.pano.PanoVideoView;
import com.letv.skin.utils.UIPlayContext;
import com.letv.skin.v4.V4PlaySkin;
import com.letv.universal.iplay.ISplayer;
import com.letv.universal.widget.ReSurfaceView;

import java.util.List;

import de.greenrobot.event.EventBus;

public class LetvNormalAndPanoHelper extends LetvBaseHelper {
    private boolean isLocalPano;
    @Override
    public void init(Context mContext, Bundle mBundle, V4PlaySkin skin) {
        super.init(mContext, mBundle, skin);
        isLocalPano = mBundle.getBoolean(LetvParamsUtils.IS_LOCAL_PANO);//配置中是否是横屏
        checkSensor();//检查传感器
        createOnePlayer(null);
    }


    private boolean checkSensor() {
        SensorManager sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        // 获取全部传感器列表
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        boolean supportSensor = false;
        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        //旋转矢量传感器  判断是否支持陀螺仪
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append("	Sensor Type - " + item.getType() + "\r\n");//传感器类型
            strLog.append("	Sensor Name - " + item.getName() + "\r\n");//传感器名称
            strLog.append("	Sensor Version - " + item.getVersion() + "\r\n");//传感器版本
            strLog.append("	Sensor Vendor - " + item.getVendor() + "\r\n");//传感器的供应商
            strLog.append("	Maximum Range - " + item.getMaximumRange() + "\r\n");//传感器的最大范围
            strLog.append("	Minimum Delay - " + item.getMinDelay() + "\r\n");//获取最小的延迟
            strLog.append("	Power - " + item.getPower() + "\r\n");//功率
            strLog.append("	Resolution - " + item.getResolution() + "\r\n");//精度（？）
            strLog.append("\r\n");
            iIndex++;
            if (item.getType() == Sensor.TYPE_ROTATION_VECTOR) {//旋转矢量传感器
                supportSensor = true;
            }
        }
        Log.d("sensor", strLog.toString());
        return supportSensor;
    }

    /**
     * 初始化普通的videoview
     */
    private void initNormalVideoView() {
        if (videoView == null || !(videoView instanceof ReSurfaceView)) {
            videoView = new ReSurfaceView(mContext);
            videoView.getHolder().addCallback(surfaceCallback);
            ((ReSurfaceView)videoView).setVideoContainer(null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            skin.addVideoView(videoView, params);
            skin.unRegisterPanoVideoChange();//注销全屏播放器改变
        }
    }

    /**
     * 初始化全屏的videoview
     */
    private void initPanoVideoView() {
        if (videoView == null || !(videoView instanceof PanoVideoView)) {
            final PanoVideoControllerView panoVideoView = new PanoVideoControllerView(mContext);//保证对象唯一
            panoVideoView.registerSurfacelistener(new ISurfaceListener() {
                @Override
                public void setSurface(Surface surface) {
                    player.setDisplay(surface);
                }
            });

            // 设置手势操作层的touch事件
            // 如果手势不起作用有可能是您的layout把panovideoview的手势事件覆盖 这里也可以设置您的layout中最上层view
            panoVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return panoVideoView.onPanoTouch(v, event);
                }
            });

            // 设置video的单击事件 通知上层唤醒播控控件等
            panoVideoView.setTapUpListener(new OnPanoViewTapUpListener() {
                @Override
                public void onSingleTapUp(MotionEvent e) {
                    skin.performClick();
                }
            });
            panoVideoView.init();
            videoView = panoVideoView;
            videoView.getHolder().addCallback(surfaceCallback);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            skin.addVideoView(videoView, params);
            skin.initPanoView();
            skin.registerPanoVideoChange(new IPanoVideoChangeMode() {
                @Override
                public void switchPanoVideoMode(int mode) {
                    //先判断设备是否有旋转矢量传感器
                    PanoVideoControllerView.PanoControllMode controllMode = mode == UIPlayContext.MODE_TOUCH ? PanoVideoControllerView.PanoControllMode.GESTURE : PanoVideoControllerView.PanoControllMode.GESTURE_AND_GYRO;
                    if (controllMode == PanoVideoControllerView.PanoControllMode.GESTURE_AND_GYRO && !checkSensor()) {
                        return;
                    }
                    panoVideoView.switchControllMode(controllMode);
                }
            });
        }
    }

    @Override
    public void handlePlayState(int state, Bundle bundle) {
        super.handlePlayState(state, bundle);
        if (state == ISplayer.PLAYER_EVENT_PREPARE_VIDEO_VIEW) {
            boolean pano = bundle != null ? bundle.getBoolean("pano", false) : false;
            if (isLocalPano || pano) {
                initPanoVideoView();
            } else {
                initNormalVideoView();
            }
            playContext.setVideoContentView(videoView);
        }
    }

}
