package com.huaweisoft.videoplayer;

public class TimesUtils {
    static public String getGapTime(int time) {
        int hours = time / (1000 * 60 * 60);
        int minutes = (time - hours * (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (time - hours * (1000 * 60 * 60) - minutes * (60 * 1000)) / 1000;
        String diffTime = "";
        if (minutes < 10) {
            if (seconds < 10) {
                diffTime = hours == 0 ? "0" + minutes + ":0" + seconds : hours + ":0" + minutes + ":0" + seconds;
            } else {
                diffTime = hours == 0 ? "0" + minutes + ":" + seconds : hours + ":0" + minutes + ":" + seconds;
            }
        } else {
            if (seconds < 10) {
                diffTime = hours == 0 ? minutes + ":0" + seconds : hours + ":" + minutes + ":0" + seconds;
            } else {
                diffTime = hours == 0 ? minutes + ":" + seconds : hours + ":" + minutes + ":" + seconds;
            }
        }
        return diffTime;
    }
}
