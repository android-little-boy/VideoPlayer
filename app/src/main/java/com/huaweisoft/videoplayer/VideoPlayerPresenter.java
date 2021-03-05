package com.huaweisoft.videoplayer;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;

public class VideoPlayerPresenter implements IVideoPlayerPresenter, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener {

    private static final String TAG = "VideoPlayerPresenter";
    public static final int UPDATE_TIME = 0x0001;
    public static final int HIDE_CONTROL = 0x0002;
    private final IVideoPlayerView videoPlayerView;
    private String path;
    private MediaPlayer mPlayer;
    private boolean isShow = false;
    private boolean isFirstPrepare = true;

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_TIME:
                    updateTime();
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 500);
                    break;
                case HIDE_CONTROL:
                    hideControl();
                    break;
            }
            return false;
        }
    });

    public VideoPlayerPresenter(IVideoPlayerView videoPlayerView) {
        this.videoPlayerView = videoPlayerView;
    }

    public void initData(String path) {
//        path = getExternalFilesDirs("")[0].getAbsolutePath() + "/你听得到.mp4";//这里写上你的视频地址
//        this.path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        this.path = path;
        Log.d(TAG, "initData: " + path);
    }

    /**
     * 设置快进10秒方法
     */
    public void forWard() {
        if (mPlayer != null) {
            int position = mPlayer.getCurrentPosition();
            mPlayer.seekTo(position + 10000);
        }
    }

    /**
     * 设置快退10秒的方法
     */
    public void backWard() {
        if (mPlayer != null) {
            int position = mPlayer.getCurrentPosition();
            if (position > 10000) {
                position -= 10000;
            } else {
                position = 0;
            }
            mPlayer.seekTo(position);
        }
    }

    /**
     * 隐藏进度条
     */
    private void hideControl() {
        isShow = false;
        mHandler.removeMessages(UPDATE_TIME);
        videoPlayerView.isShowControl(false);
    }

    /**
     * 显示进度条
     */
    public void dealWithControlUI() {
        if (isShow) {
            play();
        }
        isShow = true;
        mHandler.removeMessages(HIDE_CONTROL);
        mHandler.sendEmptyMessage(UPDATE_TIME);
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 5000);
        videoPlayerView.isShowControl(true);
    }


    /**
     * 更新播放时间
     */
    private void updateTime() {
        Log.d(TAG, "updateTime: " + mPlayer.getCurrentPosition());
        videoPlayerView.setPlayProgress(mPlayer.getCurrentPosition());
    }


    public void setPlayerSpeed(float speed) {
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mPlayer.getPlaybackParams();
            playbackParams.setSpeed(speed);
            mPlayer.setPlaybackParams(playbackParams);
            mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 500);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 5000);
            videoPlayerView.setPlayOrPause(View.INVISIBLE, android.R.drawable.ic_media_pause);
        }
    }

    public void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnVideoSizeChangedListener(this);
        try {
            //使用手机本地视频
            mPlayer.setDataSource(path);
        } catch (Exception e) {
            Log.e(TAG, "initPlayer: ", e);
            e.printStackTrace();
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mPlayer.setDisplay(holder);
        if (isFirstPrepare) {
            isFirstPrepare = false;
            mPlayer.prepareAsync();
        }
    }

    public void play() {
        if (mPlayer == null) {
            return;
        }
        Log.i("playPath", path);
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mHandler.removeMessages(UPDATE_TIME);
            mHandler.removeMessages(HIDE_CONTROL);
            videoPlayerView.setPlayOrPause(View.VISIBLE, android.R.drawable.ic_media_play);
        } else {
            mPlayer.start();
            mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 500);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 5000);
            videoPlayerView.setPlayOrPause(View.INVISIBLE, android.R.drawable.ic_media_pause);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared getDuration: " + mp.getDuration());
        Log.d(TAG, "onPrepared: " + mp.getCurrentPosition());
        videoPlayerView.setDuration(mp.getDuration());
        videoPlayerView.setPlayProgress(mp.getCurrentPosition());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.d(TAG, "onVideoSizeChanged: " + width + " " + height);
        if (mp != null) {
            videoPlayerView.onVideoSizeChanged(width, height);
        }
    }

    @Override
    public void seekTo(int progress) {
        if (mPlayer != null) {
            mPlayer.seekTo(progress);
        }
    }

    @Override
    public void disposeMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        mHandler.removeMessages(UPDATE_TIME);
        mHandler.removeMessages(HIDE_CONTROL);
    }
}

