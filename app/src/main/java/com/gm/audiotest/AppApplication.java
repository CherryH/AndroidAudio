package com.gm.audiotest;

import android.app.Application;

import com.gm.common.context.GlobalContext;

/**
 * Created by HFF on 16/9/9.
 */
public class AppApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //设置全局的context
        GlobalContext.setApplication(this);
    }
}
