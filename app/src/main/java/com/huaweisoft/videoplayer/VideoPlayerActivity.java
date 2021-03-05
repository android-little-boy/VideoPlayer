package com.huaweisoft.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        IVideoPlayerView {
    private static final String TAG = "VideoPlayerActivity";
    private ImageView playOrPauseIv;
    private SurfaceView videoSuf;
    private SeekBar mSeekBar;
    private RelativeLayout rootViewRl;
    private LinearLayout controlLl;
    private TextView startTime, endTime;
    private ImageView forwardButton, backwardButton;
    private ImageView fullScreen;
    private boolean ISFULLSCREEN = false;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private RelativeLayout control_2;
    private RelativeLayout speed;
    private TextView speedControl;
    private TextView doubleSpeed;
    private TextView onePointFiveSpeed;
    private TextView normalSpeed;
    private TextView halfSpeed;

    private IVideoPlayerPresenter videoPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initViews();
        initData();
        initSurfaceView();
        initPlayer();
        initEvent();
    }

    private void initData() {
        Intent intent = getIntent();
        videoPlayerPresenter = new VideoPlayerPresenter(this);
        videoPlayerPresenter.initData(intent.getStringExtra("path"));
//        videoPlayerPresenter.initData(getExternalFilesDirs("")[0].getAbsolutePath() + "/你听得到.mp4");
    }

    private void initEvent() {
        playOrPauseIv.setOnClickListener(this);
        rootViewRl.setOnClickListener(this);
//        rootViewRl.setOnTouchListener(this);
        forwardButton.setOnClickListener(this);
        backwardButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        fullScreen.setOnClickListener(this);
        speedControl.setOnClickListener(this);
        doubleSpeed.setOnClickListener(this);
        onePointFiveSpeed.setOnClickListener(this);
        normalSpeed.setOnClickListener(this);
        halfSpeed.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "surfaceCreated onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        videoPlayerPresenter.disposeMediaPlayer();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ISFULLSCREEN) {   //全屏切换半屏
                ISFULLSCREEN = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
                setFitToFillAspectRatio(videoWidth, videoHeight);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initSurfaceView() {
        videoSuf = findViewById(R.id.surfaceView);
        videoSuf.setZOrderOnTop(false);
//        videoSuf.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoSuf.getHolder().addCallback(this);
    }

    private void initPlayer() {
        videoPlayerPresenter.initPlayer();
    }

    private void initViews() {
        playOrPauseIv = findViewById(R.id.playOrPause);
        startTime = findViewById(R.id.tv_start_time);
        endTime = findViewById(R.id.tv_end_time);
        mSeekBar = findViewById(R.id.tv_progess);
        rootViewRl = findViewById(R.id.root_rl);
        controlLl = findViewById(R.id.control_ll);
        forwardButton = findViewById(R.id.tv_forward);
        backwardButton = findViewById(R.id.tv_backward);
        fullScreen = findViewById(R.id.full_screen);
        control_2 = findViewById(R.id.control_2);
        speed = findViewById(R.id.speed);
        speedControl = findViewById(R.id.speed_control);
        doubleSpeed = findViewById(R.id.double_speed);
        onePointFiveSpeed = findViewById(R.id.one_point_five_speed);
        normalSpeed = findViewById(R.id.normal_speed);
        halfSpeed = findViewById(R.id.half_speed);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        videoPlayerPresenter.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void setFitToFillAspectRatio(int videoWidth, int videoHeight) {
        Rect outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int screenWidth = outRect.right - outRect.left;
        int screenHeight = outRect.bottom;
        Log.d(TAG, "setFitToFillAspectRatio:" + screenWidth + " " + screenHeight);
        android.view.ViewGroup.LayoutParams videoParams = videoSuf.getLayoutParams();
        if (videoHeight / videoWidth >= screenHeight / screenWidth) {
            videoParams.height = screenHeight;
            videoParams.width = screenHeight * videoWidth / videoHeight;
        } else {
            videoParams.width = screenWidth;
            videoParams.height = screenWidth * videoHeight / videoWidth;
        }
        videoSuf.setLayoutParams(videoParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_backward:
                videoPlayerPresenter.backWard();
                break;
            case R.id.tv_forward:
                videoPlayerPresenter.forWard();
                break;
            case R.id.playOrPause:
                videoPlayerPresenter.play();
                break;
            case R.id.root_rl:
                videoPlayerPresenter.dealWithControlUI();
                break;
            case R.id.full_screen:
                changeScreen();
                break;
            case R.id.speed_control:
                control_2.setVisibility(View.GONE);
                speed.setVisibility(View.VISIBLE);
                break;
            case R.id.double_speed:
                speedControl.setText("倍速2.0");
                control_2.setVisibility(View.VISIBLE);
                speed.setVisibility(View.GONE);
                videoPlayerPresenter.setPlayerSpeed((float) 2.0);
                break;
            case R.id.one_point_five_speed:
                speedControl.setText("倍速1.5");
                control_2.setVisibility(View.VISIBLE);
                speed.setVisibility(View.GONE);
                videoPlayerPresenter.setPlayerSpeed((float) 1.5);
                break;
            case R.id.normal_speed:
                speedControl.setText("倍速1.0");
                control_2.setVisibility(View.VISIBLE);
                speed.setVisibility(View.GONE);
                videoPlayerPresenter.setPlayerSpeed((float) 1.0);
                break;
            case R.id.half_speed:
                speedControl.setText("倍速0.5");
                control_2.setVisibility(View.VISIBLE);
                speed.setVisibility(View.GONE);
                videoPlayerPresenter.setPlayerSpeed((float) 0.5);
                break;
        }
    }

    private void changeScreen() {
        if (ISFULLSCREEN) { // 全屏转半屏

            ISFULLSCREEN = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
        } else { // 非全屏切换全屏
            ISFULLSCREEN = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 手动横屏
        }
        setFitToFillAspectRatio(videoWidth, videoHeight);
    }

    //OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (b) {
            videoPlayerPresenter.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void setPlayProgress(int playProgress) {
        startTime.setText(TimesUtils.getGapTime(playProgress));
        mSeekBar.setProgress(playProgress);
    }

    @Override
    public void setDuration(int duration) {
        endTime.setText(TimesUtils.getGapTime(duration));
        mSeekBar.setMax(duration);
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        setFitToFillAspectRatio(width, height);
        videoWidth = width;
        videoHeight = height;
    }

    @Override
    public void setPlayOrPause(int visibility, int idResource) {
        playOrPauseIv.setVisibility(visibility);
        playOrPauseIv.setImageResource(idResource);
    }

    @Override
    public void isShowControl(boolean isShow) {
        if (isShow) {
            controlLl.animate().setDuration(300).translationY(0);
        } else {
            controlLl.animate().setDuration(300).translationY(controlLl.getHeight());
        }
    }
}