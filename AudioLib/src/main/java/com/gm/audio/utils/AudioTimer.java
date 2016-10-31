package com.gm.audio.utils;


import android.os.Handler;

/**
 * Created by HFF on 16/9/9.
 * 录音计时器
 */
public class AudioTimer {
    Handler mHandler;
    long mStartTime;
    private int maxLength = 119;  //最长时间

    private AudioTimeWatchListener listener;

    public AudioTimer() {
        mHandler = new Handler();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void startTimer() {
        if (mHandler != null) {
            mStartTime = System.currentTimeMillis();
            mHandler.postDelayed(runnable, 1000);
        }
    }

    public void stopTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }
    }

    // 定时器
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                long endTime = System.currentTimeMillis();
                int time = (int) ((endTime - mStartTime) / 1000);
                if (listener != null) {
                    listener.onTimeTick(dealTime(time));
                }
                // 限制录音时间不长于两分钟
                if (time > maxLength) {
                    listener.onTimeFinish();
                } else {
                    mHandler.postDelayed(this, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public interface AudioTimeWatchListener {
        void onTimeTick(String time);

        void onTimeFinish();
    }

    public void AddAudioTimeWatchListener(AudioTimeWatchListener listener) {
        this.listener = listener;
    }

    private String dealTime(int time) {
        return (maxLength - time+1) + "秒";
    }
}
