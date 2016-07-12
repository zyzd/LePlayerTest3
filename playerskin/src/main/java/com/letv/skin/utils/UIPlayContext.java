package com.letv.skin.utils;

import android.content.Context;

import com.lecloud.entity.ActionInfo;
import com.lecloud.entity.CoverConfig;
import com.letv.controller.interfacev1.ILetvPlayerController;
import com.letv.controller.interfacev1.ISplayerController;
import com.letv.skin.model.RateTypeItem;
import com.letv.skin.v4.V4PlaySkin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UIPlayContext {
    public static final int MODE_TOUCH = 0;
    public static final int MODE_MOVE = 1;
    public boolean panoNoticShowing;
    public boolean isPanoVideo;
    public int panoMode = MODE_TOUCH;

    /**
     * 当前使用了哪种类型的皮肤,默认皮肤AC
     */
    private int skinBuildType = V4PlaySkin.SKIN_TYPE_A;
    /**
     * 当前屏幕状态，默认竖屏
     */
    private int screenOrientation = ISplayerController.SCREEN_ORIENTATION_PORTRAIT;
    /**
     * 用户按下了暂停按钮
     */
    private boolean isClickPauseByUser = false;
    /**
     * 是否保存皮肤状态(比如seekbar的位置等等)
     */
    private boolean saveInstanceState = false;
    /**
     * 播放码率
     */
    private ArrayList<RateTypeItem> rateTypeItems;
    /**
     * 标题
     */
    private String videoTitle;
    private String activityId;
    private ActionInfo actionInfo;
    private String multCurrentLiveId;//TODO 最近正在直播的ID
    /**
     * 全屏时是否需要标题栏
     */
    private boolean noWindowTitle = true;
    /**
     * 全屏时返回按键是否直接退出播放页面
     */
    private boolean isReturnback = false;
    /**
     * 当前播放码率
     */
    private int currentRateType;
    /**
     * 是否监听网络变化
     */
    private boolean useNetWorkNotice = true;
    /**
     * 提示View是否正在展示
     */
    private boolean isNotiveViewShowing = false;
    /**
     * 是否在创建播放器的时候展示mediaController
     */
    private boolean isShowMediaControllerOnStart = false;
    /**
     * 是否在播放器提示错误的时候弹出mediaController
     */
    private boolean isShowMediaControllerOnError = false;
    /**
     * 锁定转屏
     */
    private boolean lockFlag = false;
    /**
     * gpc返回是否可以下载
     */
    private boolean downloadable = true;
    /**
     * 旋转屏幕的时候，控件延迟显示，0则表示直接显示,单位毫秒
     * 例如：在变为全屏时，显示浮层
     */
    private int screenChangeShowDelay = 0;
    /**
     * 播放器控制条，标题等的 显示时间，超过该时间将自动隐藏
     */
    private int controllerHideTime = 8000;
    /**
     * 是否正在播放广告
     */
    private boolean isPlayingAd;
    /**
     * 是否进入时移模式
     */
//    private boolean isTimeShirtMode;
    private int currentTimeShirtProgress=0;
    /**
     * 是否不处于wifi条件
     */
    private boolean isNoWifiContinue;
    /**
     * 播放控制器
     */
    private ILetvPlayerController playerController;
    //多元直播状态改变回调（直译）
    private List<MultLiveStateChangeCallback> multLiveStateChangeCallbacks = new ArrayList<MultLiveStateChangeCallback>();
    //覆盖物配置（直译）
    private CoverConfig coverConfig;
    public void registerPlayerController(ILetvPlayerController playerController) {
        this.playerController = playerController;
    }

    public ILetvPlayerController getPlayerController() {
        return playerController;
    }

    public int getSkinBuildType() {
        return skinBuildType;
    }

    public void setSkinBuildType(int skinBuildType) {//设置皮肤类型
        this.skinBuildType = skinBuildType;
    }

    public boolean isClickPauseByUser() {//是否是由user点击的暂停
        return isClickPauseByUser;
    }

    public void setClickPauseByUser(boolean isClickPauseByUser) {
        this.isClickPauseByUser = isClickPauseByUser;
    }

    public int getScreenResolution(Context context) {//获取屏幕状态（竖屏还是横屏，默认是竖屏）
        int resolution = ISplayerController.SCREEN_ORIENTATION_PORTRAIT;
        switch (screenOrientation) {
            case ISplayerController.SCREEN_ORIENTATION_LANDSCAPE:
            case ISplayerController.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
            case ISplayerController.SCREEN_ORIENTATION_USER_LANDSCAPE:
                resolution = ISplayerController.SCREEN_ORIENTATION_LANDSCAPE;
            break;
            case ISplayerController.SCREEN_ORIENTATION_PORTRAIT:
            case ISplayerController.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
            case ISplayerController.SCREEN_ORIENTATION_USER_PORTRAIT:
                resolution = ISplayerController.SCREEN_ORIENTATION_PORTRAIT;
            break;
        default:
            break;
        }
        return resolution;
    }

    public void setScreenResolution(int screenResolution) {//设置屏幕状态
        this.screenOrientation = screenResolution;
    }

    public boolean isSaveInstanceState() {//是否保存皮肤状态(比如seekbar的位置等等)
        return saveInstanceState;
    }

    public void setSaveInstanceState(boolean saveInstanceState) {//设置是否保存皮肤状态(比如seekbar的位置等等)
        this.saveInstanceState = saveInstanceState;
    }

    public ArrayList<RateTypeItem> getRateTypeItems() {//获取播放码率信息
        return rateTypeItems;
    }

    public void setRateTypeItems(Map<Integer, String> ratetypes) {//设置播放码率信息，通过map数据
        if (ratetypes != null) {
            if (rateTypeItems == null) {
                rateTypeItems = new ArrayList<RateTypeItem>();
            } else {
                rateTypeItems.clear();
            }
            Iterator<Entry<Integer, String>> iterator = ratetypes.entrySet().iterator();
            while (iterator.hasNext()) {
                RateTypeItem item = new RateTypeItem();
                Entry<Integer, String> entry = iterator.next();
                item.setTypeId(entry.getKey());
                item.setName(entry.getValue());
                rateTypeItems.add(item);
            }
        }
    }

    public void setRateTypeItems(String[] ratetype, int[] typeId) {//设置播放码率信息，通过数组数据
        rateTypeItems = new ArrayList<RateTypeItem>();
        for (int i = 0; i < ratetype.length; i++) {
            RateTypeItem rateTypeItem = new RateTypeItem();
            rateTypeItem.setName(ratetype[i]);
            rateTypeItems.add(rateTypeItem);
        }
    }

    public RateTypeItem getRateTypeItemById(int id) {//通过id获取对应码率信息
        if (rateTypeItems != null) {
            for (int i = 0; i < rateTypeItems.size(); i++) {
                RateTypeItem item = rateTypeItems.get(i);
                if (id == item.getTypeId()) {
                    return item;
                }
            }
        }
        return null;
    }

    public String getVideoTitle() {//获取标题
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {//设置标题
        this.videoTitle = videoTitle;
    }

    public boolean isNoWindowTitle() {//全屏时是否需要标题栏
        return noWindowTitle;
    }

    public void setNoWindowTitle(boolean noWindowTitle) {//设置全屏时是否需要标题栏
        this.noWindowTitle = noWindowTitle;
    }

    public boolean isReturnback() {//全屏时返回按键是否直接退出播放页面
        return isReturnback;
    }

    public void setReturnback(boolean isReturnback) {//设置全屏时返回按键是否直接退出播放页面
        this.isReturnback = isReturnback;
    }

    public int getCurrentRateType() {//当前播放码率
        return currentRateType;
    }

    public void setCurrentRateType(int currentRateType) {//设置当前播放码率
        this.currentRateType = currentRateType;
    }

    public boolean isUseNetWorkNotice() {//是否监听网络变化
        return useNetWorkNotice;
    }

    public void setUseNetWorkNotice(boolean useNetWorkNotice) {//设置是否监听网络变化
        this.useNetWorkNotice = useNetWorkNotice;
    }

    public boolean isShowMediaControllerOnStart() {//是否在创建播放器的时候展示mediaController
        return isShowMediaControllerOnStart;
    }

    public void setShowMediaControllerOnStart(boolean isShowMediaControllerOnStart) {//设置是否在创建播放器的时候展示mediaController
        this.isShowMediaControllerOnStart = isShowMediaControllerOnStart;
    }

    public boolean isNotiveViewShowing() {//提示View是否正在展示
        return isNotiveViewShowing;
    }

    public void setNotiveViewShowing(boolean isNotiveViewShowing) {//设置 提示View是否正在展示
        this.isNotiveViewShowing = isNotiveViewShowing;
    }

    public int getScreenChangeShowDelay() {
        return screenChangeShowDelay;
    }

    public void setScreenChangeShowDelay(int screenChangeShowDelay) {//旋转屏幕的时候，控件延迟显示，0则表示直接显示,单位毫秒
        this.screenChangeShowDelay = screenChangeShowDelay;
    }

    public boolean isShowMediaControllerOnError() {
        return isShowMediaControllerOnError;
    }

    public void setShowMediaControllerOnError(boolean isShowMediaControllerOnError) {//是否在播放器提示错误的时候弹出mediaController
        this.isShowMediaControllerOnError = isShowMediaControllerOnError;
    }

    public boolean isLockFlag() {//锁定转屏
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {//设置锁定转屏
        this.lockFlag = lockFlag;
    }

    public boolean isDownloadable() {//gpc返回是否可以下载
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {//设置gpc返回是否可以下载
        this.downloadable = downloadable;
    }

    public int getControllerHideTime() {//播放器控制条，标题等的 显示时间，超过该时间将自动隐藏
        return controllerHideTime;
    }

    public void setControllerHideTime(int controllerHideTime) {
        this.controllerHideTime = controllerHideTime;
    }

    public boolean isPlayingAd() {//是否正在播放广告
        return isPlayingAd;
    }

    public void setIsPlayingAd(boolean isPlayingAd) {
        this.isPlayingAd = isPlayingAd;
    }
    //TODO 暂时未解析
    public ActionInfo getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(ActionInfo actionInfo) {
        this.actionInfo = actionInfo;
    }

    public String getActivityId() {//获取acitivityid
        return activityId;
    }

//    public boolean isTimeShirtMode() {
//        return isTimeShirtMode;
//    }
//
//    public void setTimeShirtMode(boolean isTimeShirtMode) {
//        this.isTimeShirtMode = isTimeShirtMode;
//    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getMultCurrentLiveId() {//TODO 最近正在直播的ID
        return multCurrentLiveId;
    }

    /**
     * 当前播放的活动直播的liveID
     * @param multCurrentLiveId
     */
    public void setMultCurrentLiveId(String multCurrentLiveId) {
        this.multCurrentLiveId = multCurrentLiveId;
        notifyMultLiveStateChange();
    }
    
    private void notifyMultLiveStateChange() {//TODO 最近直播状态改变通知
        for(MultLiveStateChangeCallback callback : multLiveStateChangeCallbacks) {
            callback.setCurrentMultLive(multCurrentLiveId);
        }
    }
    //注册监听 最近直播改变监听
    public void registerMultLiveStateChangeListener(MultLiveStateChangeCallback callback) {
        if (!multLiveStateChangeCallbacks.contains(callback)) {
            multLiveStateChangeCallbacks.add(callback);
        }
    }
    //反注册 最近直播改变监听
    public void unRegisterMultLiveChangeListener(MultLiveStateChangeCallback callback){
        if (multLiveStateChangeCallbacks.contains(callback)) {
            multLiveStateChangeCallbacks.remove(callback);
        }
    }

    public int getCurrentTimeShirtProgress() {//是否进入时移模式
        return currentTimeShirtProgress;
    }

    public void setCurrentTimeShirtProgress(int currentTimeShirtProgress) {
        this.currentTimeShirtProgress = currentTimeShirtProgress;
    }

    public CoverConfig getCoverConfig() {//覆盖物配置（直译）
		return coverConfig;
	}

	public void setCoverConfig(CoverConfig coverConfig) {
		this.coverConfig = coverConfig;
	}

	public boolean isNoWifiContinue() {//是否不处于wifi条件
		return isNoWifiContinue;
	}

	public void setNoWifiContinue(boolean isNoWifiContinue) {
		this.isNoWifiContinue = isNoWifiContinue;
	}

	public interface MultLiveStateChangeCallback {//直播状态改变
        void setCurrentMultLive(String liveId);
    }
}
