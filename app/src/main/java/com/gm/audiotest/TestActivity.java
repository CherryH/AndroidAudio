package com.gm.audiotest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.gm.audio.utils.AudioFileUtil;
import com.gm.audio.views.AudioRecordButton;
import com.gm.common.utils.PathUtil;
import com.gm.common.utils.ViewUtil;

import java.io.File;

/**
 * Created by HFF on 16/9/9.
 */
public class TestActivity extends Activity {
    FrameLayout fl_audio;
    AudioRecordButton bt_record;
    Button bt_clear;
    String audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AudioFileUtil.setAudioSavePath(PathUtil.getDiskCacheDir(this, "yyc") + File.separator + "audio");
        setContentView(R.layout.test_activity);
        fl_audio = ViewUtil.find(this, R.id.fl_audio);
        bt_record = ViewUtil.find(this, R.id.bt_record);
        bt_clear = ViewUtil.find(this, R.id.bt_clear);
//        bt_record.setMaxLength(10);
        bt_record.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                Log.d("---TestActivity---", filePath);
                AudioPlayView playView = new AudioPlayView(TestActivity.this);
                playView.setData(seconds, filePath);
                fl_audio.addView(playView);
                audioFile = filePath;
            }

            @Override
            public void commit(float seconds, String filePath) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bt_record.reset();
                    }
                }, 2000);
            }
        });
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fl_audio.removeAllViews();
                AudioFileUtil.deleteAudioFile(audioFile);
                bt_record.reset();
            }
        });
    }
}
