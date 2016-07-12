package com.letv.skin.interfacev1;

import android.view.SurfaceHolder;

import com.letv.universal.iplay.ISplayer;
import com.letv.universal.iplay.OnPlayStateListener;

/**
 * 活动回调接口
 */
public interface IActionCallback {
	
	public void switchMultLive(String liveId);//开启多直播
	
	public ISplayer createPlayerCallback(SurfaceHolder holder, String path, OnPlayStateListener playStateListener);
    
}
