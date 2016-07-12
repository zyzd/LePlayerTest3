package com.letv.skin.interfacev1;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.letv.controller.interfacev1.ILetvPlayerController;
import com.letv.skin.utils.UIPlayContext;

public interface IPlaySkin {
	
	/**
	 * @param view
	 */
	void attachCenterView(View view);
	
	/**
	 * 加入底部view
	 * @param view
	 */
	void attachBottomView(View view);
	
	/**
	 * 加入上层浮层view
	 * @param view
	 */
	void attachTopFloatMediaView(View view);
	
	/**
	 * 加入左边浮层view
	 * @param view
	 */
	void attachLeftMediaView(View view);
	
	/**
	 * 加入右边浮层view
	 * @param view
	 */
	void attachRightMediaView(View view);

	void attachAnyPositionView(View view, LayoutParams lp);
	
	/**
	 * 加入手势控制
	 * @param view 
	 * @param cancel  是否取消当前手势控制
	 */
	void attachGestureController(View view, boolean cancel);
	
	/**
	 * 组装皮肤
	 * @param uiContext
	 */
	void build(UIPlayContext uiContext);
	
	/**
	 * 绑定一个播放器
	 * @param playerControl 绑定播放器
	 */
	void registerController(ILetvPlayerController playerControl);

	UIPlayContext getUIPlayContext();

}
