package com.gm.audio;

import android.media.MediaRecorder;

import com.gm.audio.utils.AudioFileUtil;
import com.gm.common.utils.LogUtil;

import java.io.File;
import java.io.IOException;

public class AudioManager {

    private MediaRecorder mRecorder;
    private String mDirString;
    private String mCurrentFilePathString;

    private boolean isPrepared;// 是否准备好了


    public AudioManager(String dir) {
        mDirString = dir;
    }

    /**
     * 回调函数，准备完毕，准备好后，button才会开始显示录音框
     *
     * @author nickming
     */
    public interface AudioStageListener {
        void wellPrepared();
    }

    public AudioStageListener mListener;

    public void setOnAudioStageListener(AudioStageListener listener) {
        mListener = listener;
    }

    // 准备方法
    public void prepareAudio() {
        try {
            // 一开始应该是false的
            isPrepared = false;

            mCurrentFilePathString = AudioFileUtil.initOutPath(mDirString);
            LogUtil.d("--mCurrentFilePathString--- %s", mCurrentFilePathString);
            mRecorder = new MediaRecorder();
            initAudioRecordConfig(mCurrentFilePathString, mRecorder);
            // 严格遵守google官方api给出的mediaRecorder的状态流程图
            mRecorder.prepare();
            mRecorder.start();
            // 准备结束
            isPrepared = true;
            // 已经准备好了，可以录制了
            if (mListener != null) {
                mListener.wellPrepared();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 设置录音参数
     */
    private void initAudioRecordConfig(String outPath, MediaRecorder recorder) {
        // 设置输出文件
        recorder.setOutputFile(outPath);
        // 设置meidaRecorder的音频源是麦克风
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采样率
        recorder.setAudioSamplingRate(8000);
        //单声道
        recorder.setAudioChannels(1);
        // 设置文件音频的输出格式为amr
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 设置音频的编码格式为amr
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }


    // 获得声音的level
    public int getVoiceLevel() {
        // mRecorder.getMaxAmplitude()这个是音频的振幅范围，值域是1-32767
        if (isPrepared) {

            try {
                double ratio = (double) mRecorder.getMaxAmplitude() / 100;
                double db = 0;// 分贝
                //默认的最大音量是100,可以修改
                //同时，也可以配置灵敏度sensibility
                if (ratio > 1)
                    db = 20 * Math.log10(ratio);
                return (int) db;
            } catch (Exception e) {

            }
        }

        return 1;
    }

    // 释放资源
    public void release() {
        // 严格按照api流程进行
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception e) {
            LogUtil.e("release error");
        }

    }

    // 取消,因为prepare时产生了一个文件，所以cancel方法应该要删除这个文件，
    // 这是与release的方法的区别
    public void cancel() {
        release();
        if (mCurrentFilePathString != null) {
            File file = new File(mCurrentFilePathString);
            file.delete();
            mCurrentFilePathString = null;
        }

    }

    /**
     * 返回存储文件
     *
     * @return
     */
    public String getCurrentFilePath() {
        return mCurrentFilePathString;
    }

}
