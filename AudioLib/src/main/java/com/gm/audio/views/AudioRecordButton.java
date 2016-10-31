package com.gm.audio.views;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gm.audio.AudioManager;
import com.gm.audio.R;
import com.gm.audio.utils.AudioFileUtil;
import com.gm.common.utils.ResUtil;
import com.gm.lib.utils.GMToastUtil;

public class AudioRecordButton extends Button implements AudioManager.AudioStageListener {

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_END = 3;


    private int mCurrentState = STATE_NORMAL;
    // 已经开始录音
    private boolean isRecording = false;
    //录制结束
    private boolean isEnding = false;
    //提交
    private boolean isCommit = false;

    private DialogManager mDialogManager;

    private AudioManager mAudioManager;

    private float mTime = 0;
    // 是否触发了onlongclick，准备好了
    private boolean mReady;
    private String mSavePath;

    private int maxAudioLength = 120;//最长录制时间


    /**
     * 先实现两个参数的构造方法，布局会默认引用这个构造方法， 用一个 构造参数的构造方法来引用这个方法 * @param context
     */

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new DialogManager(getContext());

        // 这里没有判断储存卡是否存在，有空要判断
        initSavePath();
        mAudioManager = new AudioManager(mSavePath);
        mAudioManager.setOnAudioStageListener(this);
        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initSavePath() {
        mSavePath = AudioFileUtil.getAudioSavePath(getContext());
    }

    /**
     * 录音完成后的回调，回调给activiy，可以获得mTime和文件的路径
     */
    public interface AudioFinishRecorderListener {
        void onFinished(float seconds, String filePath);

        void commit(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    // 获取音量大小的runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                    if (mTime >= maxAudioLength) {
                        mHandler.sendEmptyMessage(MSG_AUDIO_MAX_LENGTH);
                        isRecording = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // 准备三个常量
    private static final int MSG_AUDIO_PREPARED = 0X110;    //准备开始
    private static final int MSG_VOICE_CHANGE = 0X111;  //录制
    private static final int MSG_DIALOG_DISMISS = 0X112;    //录制时间太短
    private static final int MSG_AUDIO_MAX_LENGTH = 0X113;  //录制时间达到最大

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    isRecording = true;
                    // 显示应该是在audio end prepare之后回调
                    mDialogManager.showRecordingDialog();
                    mDialogManager.recording();
                    // 需要开启一个线程来变换音量
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel());
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                case MSG_AUDIO_MAX_LENGTH:
                    stopRecord();
                    break;
            }
        }

    };

    // 在这里面发送一个handler的消息
    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    /**
     * 直接复写这个监听函数
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(ResUtil.getColor(R.color.bg_audio_record_pressed));
                if (isEnding) {
                    isCommit = true;
                    return super.onTouchEvent(event);
                }
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_UP:
                setBackgroundColor(ResUtil.getColor(R.color.bg_audio_record_normal));

                //结束录制,修改状态
                if (isEnding && !isCommit) {
                    changeState(STATE_END);
                    return super.onTouchEvent(event);
                }
                //当前是结束录音准备发送状态
                if (isCommit && isEnding && mListener != null) {
                    mListener.commit(mTime, mAudioManager.getCurrentFilePath());
                    return super.onTouchEvent(event);
                }

                //录音状态
                // 首先判断是否有触发onlongclick事件，没有的话直接返回reset
                if (!mReady) {
                    GMToastUtil.showToast("录制时间太短!");
                    reset();
                    return super.onTouchEvent(event);
                }
                // 如果按的时间太短，还没准备好或者时间录制太短，就离开了，则显示这个dialog
                if (!isRecording || mTime < 1.0f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1000);// 持续1s
                    reset();// 恢复标志位
                } else if (mCurrentState == STATE_RECORDING) {//正常录制结束
                    //超过120s已经正常提交,防止二次提交
                    if (isEnding) {
                        return super.onTouchEvent(event);
                    }
                    changeState(STATE_END);
                    stopRecord();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 正常停止录制
     */
    private void stopRecord() {

        mDialogManager.dismissDialog();

        isRecording = false;

        mAudioManager.release();// release释放一个mediaRecorder

        if (mListener != null) {// 并且callbackActivity，保存录音

            mListener.onFinished(mTime, mAudioManager.getCurrentFilePath());
        }
        isEnding = true;
    }

    /**
     * 回复标志位以及状态
     */
    public void reset() {
        isRecording = false;
        isEnding = false;
        isCommit = false;
        changeState(STATE_NORMAL);
        mReady = false;
        mTime = 0;
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL:
                    setText(R.string.audio_record_normal);
                    break;
                case STATE_RECORDING:
                    setText(R.string.audio_record_start);
                    break;

                case STATE_END:
                    setText(R.string.audio_record_end);
                    break;
            }
        }

    }

    @Override
    public boolean onPreDraw() {
        return false;
    }

    /**
     * 设置最长录制时间
     *
     * @param time
     */
    public void setMaxLength(int time) {
        this.maxAudioLength = time;
        if (mDialogManager != null) {
            mDialogManager.setMaxLength(time);
        }
    }
}
