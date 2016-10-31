package com.gm.audio.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.gm.audio.utils.AudioTimer;


/**
 * Created by i on 2015/9/10.
 * 计时器
 */
public class TimeText extends TextView implements AudioTimer.AudioTimeWatchListener {
    private AudioTimer timer;//计时

    public TimeText(Context context) {
        super(context);
        init();
    }

    public TimeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        timer = new AudioTimer();
        timer.AddAudioTimeWatchListener(this);
    }

    public void startTimer() {
        if (timer != null) {
            timer.startTimer();
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stopTimer();
        }
    }

    @Override
    public void onTimeTick(String time) {
        Log.d("--isRecording---", time);
        setText(time);
    }

    @Override
    public void onTimeFinish() {
        setText("录制结束!");
    }

    /**
     * 设置最长录制时间
     *
     * @param time
     */
    public void setMaxLength(int time) {
        if (timer != null) {
            timer.setMaxLength(time);
        }
    }
}

