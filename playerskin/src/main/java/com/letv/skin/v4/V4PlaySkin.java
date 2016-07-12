package com.letv.skin.v4;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.letv.controller.interfacev1.IPanoVideoChangeMode;
import com.letv.controller.interfacev1.ISplayerController;
import com.letv.skin.BaseSkin;
import com.letv.skin.BaseView;
import com.letv.skin.controller.BaseMediaController;
import com.letv.skin.interfacev1.IActionCallback;
import com.letv.skin.loading.BaseLoadingView;
import com.letv.skin.loading.MaterialLoadingView;
import com.letv.skin.popupwindow.BackLivePopWindow;
import com.letv.skin.utils.ConstantUtils;
import com.letv.skin.utils.GestureControl;
import com.letv.skin.utils.ScreenUtils;
import com.letv.skin.utils.UIPlayContext;

import java.util.Observable;
import java.util.Observer;

import de.greenrobot.event.EventBus;

public class V4PlaySkin extends BaseSkin {
    public static final String SKIN_BUILD_TYPE = "skinBuildType";
    public static final int SKIN_TYPE_NO = -1;
    public static final int SKIN_TYPE_A = 1;
    public static final int SKIN_TYPE_B = 2;
    public static final int SKIN_TYPE_C = 3;
    private static final String TAG = "V4PlaySkin";
    private int defaultWidth = 4;
    private int defaultHeight = 3;
    private int portraitHeight = 0;
    private int portraitWidth = 0;
    private int landscapeHeight = 0;
    private int landscapeWidth = 0;

    private OnClickListener downloadClickListener;
    private IActionCallback mIActionCallback;
    private IPanoVideoChangeMode panoVideoChangeModeCallback;
    private V4PanoCoverView panoCoverView;

    public V4PlaySkin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4PlaySkin(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4PlaySkin(Context context) {
        super(context);
    }

    @Override
    protected void onBuild(UIPlayContext uiContext) {//在这创建子view
        // initLayoutParams();
    }

    public IActionCallback getIActionCallback() {
        return mIActionCallback;
    }

    public void setIActionCallback(IActionCallback mIActionCallback) {
        this.mIActionCallback = mIActionCallback;
    }

    /**
     * 初始化默认（4：3）播放器比例
     */
    private void initLayoutParams() {
        changeLayoutParams(defaultWidth, defaultHeight);
    }

    /**
     * 根据播放视频的宽和高设置画面布局的宽和高
     *
     * @param width
     * @param height
     */
    public void changeLayoutParams(int width, int height) {
        int screenWidth = ScreenUtils.getWight(context);
        int screenHeight = ScreenUtils.getHeight(context);
        if (screenHeight > screenWidth) {// 初始横屏状态
            screenWidth = screenHeight;
            screenHeight = ScreenUtils.getWight(context);
        }
        // 记录竖屏宽高
        portraitWidth = screenHeight;
        portraitHeight = screenHeight * height / width;
        // 记录横屏宽高
        landscapeWidth = screenWidth;
        landscapeHeight = screenHeight;
    }

    private void changeLayoutParams() {
        if (uiPlayContext.getScreenResolution(context) == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
            this.getLayoutParams().width = landscapeWidth;
            this.getLayoutParams().height = landscapeHeight;
        } else {
            this.getLayoutParams().width = portraitWidth;
            this.getLayoutParams().height = portraitHeight;
        }
        requestLayout();
    }

    public void setOnDownloadClickListener(OnClickListener l) {
        this.downloadClickListener = l;
    }

    @Override
    protected void attachComponentView() {
        changeLayoutParams();//会根据横竖屏，并记录横竖屏大小设置
        uiPlayContext.setLockFlag(false);//是否锁定
        switch (uiPlayContext.getSkinBuildType()) {
            case SKIN_TYPE_A://点播模式
                //获取屏幕的横竖屏状态
                if (uiPlayContext.getScreenResolution(context) == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
                    // largeController = new V3VodLargeMediaController(context);
                    //媒体控制器
                    final V4LargeMediaController largeController = new V4LargeMediaController(context);
                    attachBottomView(largeController);
                    attachGestureController(this, false);
                    if (panoVideoChangeModeCallback != null) {
                        largeController.registerPanoVideoChange(panoVideoChangeModeCallback);
                    }
                    if (downloadClickListener != null && uiPlayContext.isDownloadable()) {
                        BaseView rightMediaView = new V4DownloadBtn(context);
                        ((V4DownloadBtn) rightMediaView).setOnDownloadClickListener(downloadClickListener);
                        attachRightMediaView(rightMediaView);
                    }

                    final V4TopTitleView titleView = new V4TopTitleView(context);
                    attachTopFloatMediaView(titleView);
                    titleView.setOnTopTitleListener(new V4TopTitleView.ITopTitleListener() {//自己添加的
                        @Override
                        public void onSettingClick() {
                            EventBus.getDefault().post(ConstantUtils.EVENT_PANO_SETTINGMENU_VISIABLE);//需要与Constant的一致EVENT_PANO_SETTINGMENU_VISIABLE
                            hideFloatingView();
                            setShowUserFloating(true);
                        }
                    });

                    ARightSettingMenu aRightSettingMenu = new ARightSettingMenu(context);
//                    attachRightMediaView(aRightSettingMenu);
                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    aRightSettingMenu.measure(0,0);
                    int measuredWidth = aRightSettingMenu.getMeasuredWidth();
                    int wight = ScreenUtils.getWight(context);
                    int delayWight = wight - measuredWidth;
                    lp.setMargins(delayWight,0,wight,aRightSettingMenu.getMeasuredHeight());
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    attachAnyPositionView(aRightSettingMenu,lp);

                    mGestureControl.getObserver().addObserver(new Observer() {

                        @Override
                        public void update(Observable observable, Object data) {
                            Bundle bundle = (Bundle) data;
                            int state = bundle.getInt("state");
                            switch (state) {
                                case GestureControl.GESTURE_CONTROL_DOWN:
                                    break;
                                case GestureControl.GESTURE_CONTROL_SEEK:
                                    if (largeController != null && largeController.getSeekbar() != null) {
                                        largeController.getSeekbar().startTrackingTouch();
                                        largeController.getSeekbar().setProgress(bundle.getInt(GestureControl.GESTURE_CONTROL_SEEK_GAP));
                                        if (mGestureControl.mSeekToPopWindow != null) {
                                            mGestureControl.mSeekToPopWindow.setProgress(largeController.getSeekbar().getPlayerProgress(), largeController.getSeekbar().getPlayerDuration());
                                        }
                                    }
                                    break;
                                case GestureControl.GESTURE_CONTROL_UP:
                                    if (largeController != null && largeController.getSeekbar() != null) {
                                        largeController.getSeekbar().stopTrackingTouch();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                } else {
                    // smallController = new V3VodSmallMediaController(context);
                    BaseMediaController smallController = new V4SmallMediaController(context);
                    attachBottomView(smallController);
                    attachGestureController(this, true);
                    mGestureControl.setSeekable(true);
                }

                break;
            case SKIN_TYPE_B:// TODO 可能直播模式
                if (uiPlayContext.getScreenResolution(context) == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
                    // largeController = new V3LiveLargeMediaController(context);
                    V4LargeLiveMediaController largeController = new V4LargeLiveMediaController(context);
                    attachBottomView(largeController);
                    if (panoVideoChangeModeCallback != null) {
                        largeController.registerPanoVideoChange(panoVideoChangeModeCallback);
                    }
                    attachGestureController(this, false);

                    V4TopTitleView titleView = new V4TopTitleView(context);
                    attachTopFloatMediaView(titleView);

                } else {
                    // BaseMediaController smallController = new
                    // V3LiveSmallMediaController(context);
                    BaseMediaController smallController = new V4SmallLiveMediaController(context);
                    attachBottomView(smallController);
                    attachGestureController(this, true);
                    mGestureControl.setSeekable(false);
                }
                break;
            case SKIN_TYPE_C:
                BackLivePopWindow.getInstance(context).dismiss();
                if (uiPlayContext.getScreenResolution(context) == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
                    final V4LargeLiveMediaController largeController = new V4LargeLiveMediaController(context);
                    attachBottomView(largeController);
                    if (panoVideoChangeModeCallback != null) {
                        largeController.registerPanoVideoChange(panoVideoChangeModeCallback);
                    }
                    attachGestureController(this, false);
                    V4MultLiveRightView v3MultLiveLargeController = new V4MultLiveRightView(context);
//                v3MultLiveLargeController.setMark(true);
                    v3MultLiveLargeController.setIActionCallback(mIActionCallback);

                    // v3MultLiveLargeController.set
                    attachRightMediaView(v3MultLiveLargeController);
                    v3MultLiveLargeController.setActionInfoDone();

                    V4TopTitleMultLiveView titleView = new V4TopTitleMultLiveView(context);
                    attachTopFloatMediaView(titleView);
                    mGestureControl.setSeekable(true);
                    mGestureControl.getObserver().addObserver(new Observer() {

                        @Override
                        public void update(Observable observable, Object data) {
                            Bundle bundle = (Bundle) data;
                            int state = bundle.getInt("state");
                            switch (state) {
                                case GestureControl.GESTURE_CONTROL_SEEK:
                                    if (largeController != null && largeController.getSeekbar() != null) {
                                        if (largeController.getSeekbar().getVisibility() != View.VISIBLE) {
                                            mGestureControl.setSeekable(false);
                                            if (mGestureControl.mSeekToPopWindow != null) {
                                                mGestureControl.mSeekToPopWindow.dismiss();
                                            }
                                            return;
                                        }
                                        mGestureControl.setSeekable(true);
                                        largeController.getSeekbar().startTrackingTouch();
                                        largeController.getSeekbar().setProgress(bundle.getInt(GestureControl.GESTURE_CONTROL_SEEK_GAP));
                                        if (mGestureControl.mSeekToPopWindow != null) {
                                            mGestureControl.mSeekToPopWindow.setProgress(largeController.getSeekbar().getSeekToTime());
                                        }
                                    }
                                    break;
                                case GestureControl.GESTURE_CONTROL_UP:
                                    if (largeController != null && largeController.getSeekbar() != null) {
                                        largeController.getSeekbar().stopTrackingTouch();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                } else {
                    BaseMediaController smallController = new A4SmallLiveMediaController(context);
                    attachBottomView(smallController);
                    A4SmallLiveTopMediaController a4SmallLiveTopMediaController = new A4SmallLiveTopMediaController(context);
                    attachTopFloatMediaView(a4SmallLiveTopMediaController);
                    attachGestureController(this, true);
                    mGestureControl.setSeekable(false);
                }

                break;

            default:
                break;
        }
    }

    public void initPanoView() {
        uiPlayContext.isPanoVideo = true;
        uiPlayContext.panoMode = UIPlayContext.MODE_TOUCH;
        if (!V4PanoCoverView.isShowed(context)) {
            initCoverView(true);
        }
    }

    /**
     * 全景提示View
     */
    private void initCoverView(boolean needAttachPlayer) {
        if (panoCoverView == null) {
            panoCoverView = new V4PanoCoverView(context);
            attachCenterView(panoCoverView);
            if (needAttachPlayer) {
                panoCoverView.attachUIContext(uiPlayContext);
                panoCoverView.attachUIPlayControl(uiPlayContext.getPlayerController());
            }
        }
        if (uiPlayContext.panoNoticShowing) {
            panoCoverView.setVisibility(View.VISIBLE);
        } else {
            panoCoverView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachCommonView() {
        Log.e(TAG,"attachCommonView()");
        /**
         * 播放失败提示view
         */
        V4NoticeView noticeView = new V4NoticeView(context);
        noticeView.setMark(true);
        attachCenterView(noticeView);
        noticeView.setVisibility(View.GONE);

        /**
         * loading
         */
        BaseLoadingView loadingView = new MaterialLoadingView(context);
        loadingView.setMark(true);
        attachCenterView(loadingView);

        /**
         * 水印已经添加到内部，不需要在皮肤层添加
         */
//        if (uiPlayContext.getSkinBuildType() == SKIN_TYPE_C) {
//            V4WaterMarkView v3WaterMarkView = new V4WaterMarkView(context);
//            v3WaterMarkView.setMark(true);
//            attachCenterView(v3WaterMarkView);
//        }
        /**
         * 广告倒计时
         */
//        NormalTimerView timerView = new NormalTimerView(context);
//        timerView.setMark(true);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_BOTTOM);
//        attachAnyPositionView(timerView, lp);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uiPlayContext.getSkinBuildType() == SKIN_TYPE_C || uiPlayContext.getSkinBuildType() == SKIN_TYPE_B) {
            BackLivePopWindow.getInstance(context).destory();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    public void registerPanoVideoChange(IPanoVideoChangeMode panoVideoChangeModeCallback) {
        attatchPanoControll(panoVideoChangeModeCallback);
    }

    public void unRegisterPanoVideoChange() {
        attatchPanoControll(null);
    }

    private void attatchPanoControll(IPanoVideoChangeMode panoVideoChangeModeCallback) {
        this.panoVideoChangeModeCallback = panoVideoChangeModeCallback;
        for (View view : childViews) {
            if (view instanceof V4LargeMediaController) {
                V4LargeMediaController controller = (V4LargeMediaController) view;
                controller.registerPanoVideoChange(panoVideoChangeModeCallback);
            } else if (view instanceof  V4LargeLiveMediaController) {
                V4LargeLiveMediaController controller = (V4LargeLiveMediaController) view;
                controller.registerPanoVideoChange(panoVideoChangeModeCallback);
            }
        }
    }
}
