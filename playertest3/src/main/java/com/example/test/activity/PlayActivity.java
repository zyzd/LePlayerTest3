package com.example.test.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.Constant;
import com.example.test.adapter.TanmuAdapter;
import com.example.test.bean.ChatMessage;
import com.google.gson.Gson;
import com.letv.simple.utils.LetvNormalAndPanoHelper;
import com.letv.skin.bean.EventTransparency;
import com.letv.skin.v4.V4PlaySkin;
import com.test.yiya.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.danmaku.util.IOUtils;
import master.flame.danmaku.ui.widget.DanmakuView;

public class PlayActivity extends Activity implements OnClickListener {
    public final static String DATA = "data";

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private V4PlaySkin skin;
    //如果不使用全景 请将LetvNormalAndPanoHelper 换成 LetvNormalVideoHelper
    private LetvNormalAndPanoHelper playHelper;
    private Bundle bundle;
    private RelativeLayout.LayoutParams mDanmakuLayoutParams;
    private ListView mLvContent;
    private TanmuAdapter tanmuAdapter;
    private EditText mEtCommunity;
    private Button mBtnProSubmit;
    private LinearLayout mLlTanmu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initView();
        setOnListener();
        EventBus.getDefault().register(this);
        initData();
    }


    /**
     * 找到控件
     */
    private void initView() {
        skin = (V4PlaySkin) findViewById(R.id.videobody);
        mDanmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);//弹幕控件

        mLvContent = (ListView) findViewById(R.id.lv_content);
        mLlTanmu = (LinearLayout) findViewById(R.id.ll_tanmu);
        mEtCommunity = (EditText) findViewById(R.id.et_community);
        mBtnProSubmit = (Button) findViewById(R.id.btn_protraint_submit_tanmu);

        mDanmakuLayoutParams = (RelativeLayout.LayoutParams) mDanmakuView.getLayoutParams();

        mLvContent.setDivider(null);
        mLvContent.setSelector(android.R.color.transparent);
        mLvContent.setCacheColorHint(Color.TRANSPARENT);
    }

    /**
     * 设置监听
     */
    private void setOnListener() {
        mBtnProSubmit.setOnClickListener(this);
    }

    //初始化数据
    private void initData() {

        model = android.os.Build.MODEL;//获取手机型号
        loadDataFromIntent();// load data

        mLineHeight = getResources().getDisplayMetrics().density * 25;//初始化弹幕行高
        mDanmakuTrans = 0.7f;

        initPlayHelper();//创建播放器帮助类
        initDanmaku();//初始化弹幕

        if (danmaList == null) {
            danmaList = new ArrayList<ChatMessage>();
        }
        if (tanmuAdapter == null) {
            tanmuAdapter = new TanmuAdapter(danmaList, this);
            mLvContent.setAdapter(tanmuAdapter);
        } else {
            tanmuAdapter.notifyDataSetChanged();
        }
        intSocket();
    }

    private final WebSocket mConnection = new WebSocketConnection();
    final String wsuri = "ws://123.206.51.123/DanMuServer/webSocketServer/123/0";
    private ArrayList<ChatMessage> danmaList;

    private void intSocket() {
        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    //Log.e("han","连接成功");
                    //Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_LONG).show();
                    // mStatusline.setText("Status: Connected to " + wsuri);
                    // savePrefs();
                    // mSendMessage.setEnabled(true);
                    // mMessage.setEnabled(true);
                }

                @Override
                public void onTextMessage(String payload) {
                    Gson gson = new Gson();
                    ChatMessage message = gson.fromJson(payload, ChatMessage.class);

                    danmaList.add(message);
                    addDanmaku(true, message);
                    refreshListView();
                }

                @Override
                public void onClose(int code, String reason) {
                    // alert("Connection lost.");
                    //  mStatusline.setText("Status: Ready.");
                    // setButtonConnect();
                    // mSendMessage.setEnabled(false);
                    //mMessage.setEnabled(false);
                }
            });
        } catch (WebSocketException e) {

        }

    }

    public void refreshListView() {
        tanmuAdapter.notifyDataSetChanged();
        if (danmaList.size() == 0) {
            return;
        }
        mLvContent.smoothScrollToPosition(danmaList.size() - 1);
    }

    private void alert(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /**
     * 创建playHelper(播放器帮助类)
     */
    private void initPlayHelper() {
        playHelper = new LetvNormalAndPanoHelper();
        playHelper.init(this.getApplicationContext(), bundle, skin);
        //playHelper.renderCallback = new LetvNormalAndPanoHelper.PlayerRenderCallback() {
        //      @Override
        //      public void onRender() {//耗时结果回调
        // console.setText(UseTimeResult.print());
        //     }
        //  };
    }

    /**
     * 获取视频数据信息
     */
    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getBundleExtra("data");
            if (bundle == null) {
                Toast.makeText(this, "no data", Toast.LENGTH_LONG).show();
            }
        }
    }

    /********************************
     * 弹幕处理
     *************************/
    private DanmakuContext mContext;
    private DanmakuView mDanmakuView;
    private BaseDanmakuParser mParser;

    /**
     * 初始弹幕
     */
    private void initDanmaku() {
        /**
         *  设置最大显示行数
         *  BaseDanmaku.TYPE_SCROLL_RL
         */
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, mLineNums); // 滚动弹幕最大显示3行

        /**
         *  设置是否禁止重叠
         *  BaseDanmaku.TYPE_SCROLL_RL  从右往左
         *  BaseDanmaku.TYPE_FIX_TOP  位于顶部
         */
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext = DanmakuContext.create();//创建DanmakuContext

        mContext.setFBDanmakuVisibility(false)//设置是否显示底部弹幕
                .setFTDanmakuVisibility(false)//设置是否显示顶部弹幕
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 0)//(1)自动、无、阴影、描边、投影(2)经测试values会影响宽度
                .setDuplicateMergingEnabled(true)//重复合并
                .setMaximumVisibleSizeInScreen(80) //同屏最大显示数量(弹幕密度(只对滚动有效))
                .setScrollSpeedFactor(1.2f)//滚动速度，速度越快
                .setScaleTextSize(1.0f)//弹幕的文字大小
                .setDanmakuTransparency(mDanmakuTrans)//设置透明度
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter)  // 绘制背景使用BackgroundCacheStuffer,二者可以叠加
                .setMaximumLines(maxLinesPair)//最大行数
                .setR2LDanmakuVisibility(isOrientationLandScape())
                .preventOverlapping(overlappingEnablePair);//设置禁止重叠策略

        if (mDanmakuView != null) {
            mParser = createParser(this.getResources().openRawResource(R.raw.comments));//获取本地弹幕数据
            mDanmakuView.setCallback(callback);
            //mDanmakuView.setOnDanmakuClickListener(onDanmakuClickListener);
            mDanmakuView.prepare(mParser, mContext);//准备
            //mDanmakuView.showFPS(true);//显示帧数,默认不显示
            mDanmakuView.enableDanmakuDrawingCache(false);//true在模拟器上运行有问题
        }
    }

    /**
     * @return 是否横屏
     */
    public boolean isOrientationLandScape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 解析流数据，获取弹幕内容(本地raw.comments文件)
     *
     * @param stream
     * @return
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {//当输入流为null时创建一个Danmakus

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        //内容解析
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    /**
     * danmaku回调
     */
    DrawHandler.Callback callback = new DrawHandler.Callback() {
        @Override
        public void updateTimer(DanmakuTimer timer) {
        }

        @Override
        public void drawingFinished() {
        }

        @Override
        public void danmakuShown(BaseDanmaku danmaku) {
            //TODO 当弹幕文字大小或数量发生改变时，就根据弹幕所在重新修改弹幕的显示范围（上，中，下）
            if (isChangeTextSizeOrLineNums) {
                mLineHeight = danmaku.getBottom() - danmaku.getTop() + 0.5f;//获取弹幕单行高度
                isChangeTextSizeOrLineNums = false;
                changeDanmakuLocation();//修改弹幕的显示范围
            }
        }

        @Override
        public void prepared() {
            mDanmakuView.start();//开始播放
        }
    };

    /**
     * 弹幕点击事件
     */
//    IDanmakuView.OnDanmakuClickListener onDanmakuClickListener= new IDanmakuView.OnDanmakuClickListener() {
//        @Override
//        public void onDanmakuClick(BaseDanmaku latest) {
//            // Log.d("DFM", "onDanmakuClick text:" + latest.text);
//            EventBus.getDefault().post("hide");
//        }
//
//        @Override
//        public void onDanmakuClick(IDanmakus danmakus) {
//            //Log.d("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
//        }
//    };
    /**
     * 加载远程url图片，图文混排
     */
    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if (drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if (mDanmakuView != null) {
                                mDanmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private static class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            danmaku.padding = 10;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setColor(0x8125309b); //弹幕背景颜色
            //获知矩形背景
            canvas.drawRect(left + 2, top + 2, left + danmaku.paintWidth - 2, top + danmaku.paintHeight - 2, paint);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 禁用描边绘制
        }
    }

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#ffffff")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 添加自定义弹幕类型
     *
     * @param islive
     * @param message
     */
    private void addDanmaku(boolean islive, ChatMessage message) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_LR);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        // int length = (int) (danmaku.getBottom() - danmaku.getTop());
        danmaku.text = message.getMsg();
        danmaku.padding = 20;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.time = mDanmakuView.getCurrentTime() + 1200;
        danmaku.textSize = mLineHeight;
//        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0;
        danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            skin.setKeyBack();//返回键是否恢复竖屏状态
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playHelper != null) {
            playHelper.onResume();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playHelper != null) {
            playHelper.onPause();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playHelper != null) {
            playHelper.onDestroy();
        }
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }

        unregisterEvent();
    }

    /**
     * 注销eventbus
     */
    private void unregisterEvent() {
        EventBus.getDefault().post(Constant.EVENT_UNREGISTER_EVENT);//取消GestureControl的event订阅
        EventBus.getDefault().unregister(this);//取消订阅
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (playHelper != null) {
            playHelper.onConfigurationChanged(newConfig);
        }
        //控制横屏时才显示弹幕，resume和pause时现在自己需求才如此，实际按具体需求改变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mDanmakuView.resume();
            mContext.setR2LDanmakuVisibility(true);
            mDanmakuView.setVisibility(View.VISIBLE);
            mLvContent.setVisibility(View.GONE);
            mLlTanmu.setVisibility(View.GONE);
        } else {
            mDanmakuView.setVisibility(View.GONE);
            mDanmakuView.pause();
            mContext.setR2LDanmakuVisibility(false);
            EventBus.getDefault().post(Constant.EVENT_PANO_SETTINGMENU_GONE);
            mLvContent.setVisibility(View.VISIBLE);
            mLlTanmu.setVisibility(View.VISIBLE);
            if(dialog!= null && dialog.isShowing()){
                dialog.dismiss();//关闭横屏的弹幕输入框
            }
            if(isShow){
                hideKeyBoard();//掩藏软键盘
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void changeDanmaku(String message) {
        switch (message) {
            case Constant.EVENT_DANMAKU_SIZE_SMALL://弹幕大小
                mContext.setScaleTextSize(0.5f);
                isChangeTextSizeOrLineNums = true;
                break;
            case Constant.EVENT_DANMAKU_SIZE_MID:
                mContext.setScaleTextSize(1.0f);
                isChangeTextSizeOrLineNums = true;
                break;
            case Constant.EVENT_DANMAKU_SIZE_BIG:
                mContext.setScaleTextSize(1.5f);
                isChangeTextSizeOrLineNums = true;
                break;
            case Constant.EVENT_DANMKU_LOCAL_TOP://弹幕位置
                mDanmakuLocaton = DANMAKU_LOCATION_TOP;
                changeDanmakuLocation();
                break;
            case Constant.EVENT_DANMKU_LOCAL_MID:
                mDanmakuLocaton = DANMAKU_LOCATION_MID;
                changeDanmakuLocation();
                break;
            case Constant.EVENT_DANMKU_LOCAL_BOTTOM:
                mDanmakuLocaton = DANMAKU_LOCATION_BOTTOM;
                changeDanmakuLocation();
                break;
            case Constant.EVENT_SHOW_SENDDANMAKU_DIALOG://横屏响应显示发送弹幕输入框dialog
                showEditDialog(this);
                break;
        }
    }

    /**
     * 响应弹幕透明度设置，只设置
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void changDanmakuTrans(EventTransparency event) {
        if (event.getEvent().equals(Constant.EVENT_DANMAKU_TRANS_CHANGE)) {
            //假设范围为0.3~1.0
            mDanmakuTrans = 0.3f + (0.7f * event.getProgress()) / (100f);
            mContext.setDanmakuTransparency(mDanmakuTrans);
        }
    }

    private static final String DANMAKU_LOCATION_TOP = "top";
    private static final String DANMAKU_LOCATION_MID = "mid";
    private static final String DANMAKU_LOCATION_BOTTOM = "bottom";
    private String mDanmakuLocaton = DANMAKU_LOCATION_TOP;
    private int mDanmakuTop = 0;
    private float mLineHeight = 25;
    private int mLineNums = 5;
    private boolean isChangeTextSizeOrLineNums = true;//是否修改了弹幕文字大小或弹幕的最多行数
    private float mDanmakuTrans;//弹幕的透明度

    /**
     * 修改弹幕显示位置
     */
    public void changeDanmakuLocation() {
        switch (mDanmakuLocaton) {
            case DANMAKU_LOCATION_TOP:
                mDanmakuTop = 0;
                break;
            case DANMAKU_LOCATION_MID:
                mDanmakuTop = (int) ((getWindowHeight() - getDanMakuHeight()) / 2);
                break;
            case DANMAKU_LOCATION_BOTTOM:
                mDanmakuTop = (int) (getWindowHeight() - getDanMakuHeight());
                break;
        }
        mDanmakuLayoutParams.setMargins(0, mDanmakuTop, 0, 0);
        mDanmakuView.setLayoutParams(mDanmakuLayoutParams);
    }

    /**
     * @return 获取弹幕的文本高度
     */
    public float getDanMakuHeight() {
        return (mLineHeight + 0.5f) * mLineNums;
    }

    /**
     * @return 屏幕的高
     */
    public int getWindowHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_protraint_submit_tanmu:
                sendMessage(mEtCommunity);
                break;
        }
    }

    /**
     * 发送弹幕
     *
     * @param sendEditText(发送信息的编辑框)
     */
    private void sendMessage(EditText sendEditText) {
        if (sendEditText == null) {
            return;
        }
        if (!mConnection.isConnected()) {
            Toast.makeText(PlayActivity.this, "网络连接错误，发送失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sendEditText.getText().toString().trim())) {
            return;
        }
        mConnection.sendTextMessage(sendEditText.getText().toString());
        sendEditText.setText("");
    }


    /*====================================================*/
    private boolean isShow;
    private Dialog dialog = null;
    private EditText et_keywored;
    private TextView tv_textcount;
    private Button btn_sub;
    private String model;

    //点击别的区域的时候,设置输入法选项
    public void showEditDialog(final Context context) {
        isShow = true;
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
        }
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);

        //获取对话框布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.view_edittext, null);
        et_keywored = (EditText) view.findViewById(R.id.et_keywored);
        tv_textcount = (TextView) view.findViewById(R.id.tv_textcount);
        btn_sub = (Button) view.findViewById(R.id.btn_sub);

        //设置展示范围
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        dialog.setContentView(view);
        dialog.show();

        et_keywored.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 10) {
                    tv_textcount.setText((10 - s.length()) + "");
                } else {
                    et_keywored.setText(s.subSequence(0, 10));
                    et_keywored.setSelection(10);
                    Toast.makeText(PlayActivity.this, "字数已经超出限制", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        et_keywored.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    sendMessage(et_keywored);
                    dialog.dismiss();

                    return true;
                }

                return false;
            }
        });


        et_keywored.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                 @Override
                                                 public void onFocusChange(View v, boolean hasFocus) {
                                                     if (hasFocus) {
                                                         et_keywored.post(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 InputMethodManager imm = (InputMethodManager) PlayActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                 imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                                                                 //自动弹出
                                                                 try {
                                                                     Thread.sleep(40);
                                                                 } catch (InterruptedException e) {

                                                                 }
                                                             }
                                                         });
                                                     }
                                                 }
                                             }
        );
        btn_sub.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           if (!TextUtils.isEmpty(et_keywored.getText().toString().trim())) {
                                               //Toast.makeText(PlayActivity.this, "发射成功!", Toast.LENGTH_SHORT).show();
                                               sendMessage(et_keywored);
                                               dialog.dismiss();
                                               hideKeyBoard();
                                           } else {
                                               Toast.makeText(PlayActivity.this, "发射内容不能为空!", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                       }
                                   }

        );

        //隐藏dialog的方法
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                       @Override
                                       public void onCancel(final DialogInterface dialog) {
                                           hideKeyBoard();
                                       }
                                   }
        );

        et_keywored.setFocusable(true);
    }

    //隐藏键盘和输入框
    public void hideKeyBoard() {
        isShow = false;
        if (model.contains("MI")) {
            //小米手机
            et_keywored.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) PlayActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            });
        } else {
            //普通手机
            InputMethodManager imm = (InputMethodManager) PlayActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
