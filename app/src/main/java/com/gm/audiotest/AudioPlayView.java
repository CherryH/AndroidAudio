package com.gm.audiotest;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gm.audio.MediaManager;
import com.gm.common.utils.LogUtil;

/**
 * Created by HFF on 16/9/9.
 */
public class AudioPlayView extends RelativeLayout {
    TextView seconds;// 时间
    View length;// 对话框长度
    View viewanim;
    private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
    private int mMaxItemWith;


    public AudioPlayView(Context context) {
        super(context);
        init(context);

    }

    public AudioPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 获取系统宽度
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.6f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);

        View view = View.inflate(context, R.layout.item_layout, this);
        seconds = (TextView) view.findViewById(R.id.recorder_time);
        length = view.findViewById(R.id.recorder_length);
        viewanim = view.findViewById(R.id.id_recorder_anim);
    }

    public void setData(float time, final String filePath) {
        LogUtil.d("---time--- %s",String.valueOf(time));
        LogUtil.d("---filePath--- %s",filePath);
        seconds.setText(Math.round(time) + "\"");
        ViewGroup.LayoutParams lParams = length.getLayoutParams();
        lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f * time);
        length.setLayoutParams(lParams);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(filePath);
            }
        });
    }

    private void playAudio(String filePathString) {
        // 播放动画
        viewanim.setBackgroundResource(R.drawable.play);
        AnimationDrawable drawable = (AnimationDrawable) viewanim
                .getBackground();
        drawable.start();

        // 播放音频
        MediaManager.playSound(filePathString,
                new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewanim.setBackgroundResource(R.drawable.adj);

                    }
                });
    }
}
