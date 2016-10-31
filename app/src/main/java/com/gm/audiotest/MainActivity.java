package com.gm.audiotest;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gm.audio.MediaManager;
import com.gm.audio.views.AudioRecordButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    AudioRecordButton button;

    private ListView mlistview;
    private ArrayAdapter<Recorder> mAdapter;
    private View viewanim;
    private List<Recorder> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mlistview = (ListView) findViewById(R.id.listview);

        button = (AudioRecordButton) findViewById(R.id.recordButton);
        button.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                // TODO Auto-generated method stub
                Recorder recorder = new Recorder(seconds, filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mlistview.setSelection(mDatas.size() - 1);
            }

            @Override
            public void commit(float seconds, String filePath) {

            }
        });

        mAdapter = new RecorderAdapter(this, mDatas);
        mlistview.setAdapter(mAdapter);

        mlistview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // 播放动画
                if (viewanim != null) {//让第二个播放的时候第一个停止播放
                    viewanim.setBackgroundResource(R.drawable.adj);
                    viewanim = null;
                }
                viewanim = view.findViewById(R.id.id_recorder_anim);
                viewanim.setBackgroundResource(R.drawable.play);
                AnimationDrawable drawable = (AnimationDrawable) viewanim
                        .getBackground();
                drawable.start();

                // 播放音频
                MediaManager.playSound(mDatas.get(position).filePathString,
                        new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                viewanim.setBackgroundResource(R.drawable.adj);

                            }
                        });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

}
