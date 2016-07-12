package com.letv.simple.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.letv.skin.v4.V4PlaySkin;
import com.letv.universal.widget.ReSurfaceView;

/**
 * 创建普通的播放器helper
 */
public class LetvNormalVideoHelper extends LetvBaseHelper {
    @Override
    public void init(Context mContext, Bundle mBundle, V4PlaySkin skin) {
        super.init(mContext, mBundle, skin);
        initVideoView();
    }

    private void initVideoView() {
        if (videoView == null || !(videoView instanceof ReSurfaceView)) {
            ReSurfaceView videoView = new ReSurfaceView(mContext);
            videoView.getHolder().addCallback(surfaceCallback);
            videoView.setVideoContainer(null);//设置播放器容器（？）
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            skin.addVideoView(videoView, params);//
            this.videoView = videoView;
        }
        playContext.setVideoContentView(videoView);
    }




}
