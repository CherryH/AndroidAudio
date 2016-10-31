package com.gm.audio.utils;

import android.content.Context;

import com.gm.common.utils.LogUtil;
import com.gm.common.utils.PathUtil;
import com.gm.common.utils.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by HFF on 16/9/9.
 * 处理音频文件
 */
public class AudioFileUtil {

    private static String mSavePath;

    public static String getAudioSavePath(Context context) {
        if (StringUtils.isEmpty(mSavePath)) {
            return PathUtil.getDiskCacheDir(context, "temp") + File.separator + "audio";
        }
        return mSavePath;
    }

    public static void setAudioSavePath(String path) {
        mSavePath = path;
    }

    /**
     * 设置输出目录
     */
    public static String initOutPath(String filePath) {

        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileNameString = generalFileName();
        File file = new File(dir, fileNameString);

        return file.getAbsolutePath();
    }

    /**
     * 随机生成文件的名称
     *
     * @return
     */
    public static String generalFileName() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        // 转换为字符串
        String formatDate = format.format(new Date());
        return formatDate + ".amr";
    }

    /**
     * 删除目标文件
     *
     * @param filePath
     */
    public static void deleteAudioFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            LogUtil.d("audio file delete failed");
            e.printStackTrace();
        }
    }
}
