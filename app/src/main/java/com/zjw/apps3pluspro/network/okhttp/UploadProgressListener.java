package com.zjw.apps3pluspro.network.okhttp;

/**
 * Created by android
 * on 2021/4/2
 */
public interface UploadProgressListener {
    void onProgress(long contentLength, int mCurrentLength);
}
