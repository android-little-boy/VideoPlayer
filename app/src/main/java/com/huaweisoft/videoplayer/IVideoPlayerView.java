package com.huaweisoft.videoplayer;

import android.view.View;

public interface IVideoPlayerView {

    void setPlayProgress(int playProgress);

    void setDuration(int duration);

    void onVideoSizeChanged(int width, int height);

    void setPlayOrPause(int visibility, int idResource);

    void isShowControl(boolean isShow);

}
