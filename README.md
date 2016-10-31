## Android录音，项目中的一个小模块

1.音频采用amr格式输出和编码


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

2.录音按钮长按开始录音，录音结束后按钮变为发送状态，回调success事件

3.录音中有dialog提示读秒，120s倒计时，提示麦克风音量