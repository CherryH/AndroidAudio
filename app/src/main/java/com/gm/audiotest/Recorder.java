package com.gm.audiotest;

import java.io.Serializable;

/**
 * Created by HFF on 16/9/5.
 */
public class Recorder implements Serializable{
    public float time;
    public String filePathString;

    public Recorder(float time, String filePathString) {
        super();
        this.time = time;
        this.filePathString = filePathString;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePathString() {
        return filePathString;
    }

    public void setFilePathString(String filePathString) {
        this.filePathString = filePathString;
    }
}
