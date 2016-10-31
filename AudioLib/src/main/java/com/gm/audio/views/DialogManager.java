package com.gm.audio.views;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.gm.audio.R;


public class DialogManager {

    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */

    private Dialog mDialog;

    private VoiceLineView mVoice;   //音频指示器

    private TimeText mTime; //录音时间

    private Context mContext;

    public int maxAudioLenght = 120;    //最长录制时间

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {

        mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_manager, null);
        mVoice = (VoiceLineView) view.findViewById(R.id.voice_line);
        mTime = (TimeText) view.findViewById(R.id.tv_audio_time);
        mDialog.setContentView(view);
        mDialog.show();

    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mTime.startTimer();
        }
    }


    // 时间过短
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mTime.setText("不能小于1秒!");
            mTime.stopTimer();
        }

    }

    // 隐藏dialog
    public void dismissDialog() {

        if (mDialog != null && mDialog.isShowing()) {
            mTime.stopTimer();
            mDialog.dismiss();
            mDialog = null;
        }

    }

    public void updateVoiceLevel(int level) {

        if (mDialog != null && mDialog.isShowing()) {
            mVoice.setVolume(level);
        }
    }

    /**
     * 设置最长录制时间
     *
     * @param time
     */
    public void setMaxLength(int time) {
        if (mTime != null) {
            mTime.setMaxLength(time);
        }
    }

}
