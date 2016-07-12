package com.example.test.test;

import android.app.Activity;
import android.os.Bundle;

import com.letv.simple.utils.PlayerSkinFactory;
import com.letv.skin.v4.V4PlaySkin;
import com.test.yiya.R;

public class SkinTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        V4PlaySkin playSkin= (V4PlaySkin) findViewById(R.id.videobody);
        PlayerSkinFactory.initPlaySkin(playSkin, V4PlaySkin.SKIN_TYPE_B);
    }

}
