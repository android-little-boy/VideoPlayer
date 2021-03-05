package com.huaweisoft.videoplayer;

import android.view.SurfaceHolder;

public interface IVideoPlayerPresenter {
    void initData(String path);

    void initPlayer();

    void setDisplay(SurfaceHolder holder);

    void play();

    void dealWithControlUI();

    void forWard();

    void backWard();

    void setPlayerSpeed(float speed);

    void seekTo(int progress);

    void disposeMediaPlayer();
}
