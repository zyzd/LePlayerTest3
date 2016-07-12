package com.letv.skin;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.letv.controller.interfacev1.ILetvPlayerController;
import com.letv.controller.interfacev1.IPlayerRequestController;
import com.letv.controller.interfacev1.ISplayerController;
import com.letv.skin.utils.UIPlayContext;

/**
 * 皮肤基础类
 * 
 * @author pys
 *
 */
public abstract class BaseView extends RelativeLayout {
    protected Context context;
    public ISplayerController player;//播放控制器接口
    protected IPlayerRequestController requestController;//请求播放控制器
    protected UIPlayContext uiPlayContext;//ui上下文
    private boolean isMark;

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public BaseView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    /**
     * view初始化完毕之后，需要获取到ui的一些上下文，父类初始化完毕之后，会调用此方法
     * 
     * @param playContext
     */
    public void attachUIContext(UIPlayContext playContext) {
        this.uiPlayContext = playContext;
    }

    public void attachUIPlayControl(ILetvPlayerController playerControl) {
        this.player = playerControl.getIsPlayerController();
        this.requestController = playerControl.getRequestController();
        initPlayer();
    }

    /**
     * 播放器已经初始化完毕了，在子类可以对播放器做一些处理
     */
    protected abstract void initPlayer();

    /**
     * 初始化view
     * 
     * @param context
     */
    protected abstract void initView(Context context);

    /**
     * 将view恢复初始化状态
     */
    protected void reset() {

    }


    @Override
    protected void onDetachedFromWindow() {//销毁View时调用，可以进行取消广播注册
        super.onDetachedFromWindow();
    }

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean isMark) {
        this.isMark = isMark;
    }

    public void setKeyBack(){//自己添加的
        if (uiPlayContext != null) {
            /**
             * 返回键是否恢复竖屏状态
             */
            if (uiPlayContext.isReturnback()) {
                if (player != null) {
                    // 返回竖屏状态
                    player.setScreenResolution(ISplayerController.SCREEN_ORIENTATION_USER_PORTRAIT);
                }
            } else {
                ((Activity) context).finish();
            }
        }
    }
}
