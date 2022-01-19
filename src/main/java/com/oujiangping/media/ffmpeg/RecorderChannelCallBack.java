package com.oujiangping.media.ffmpeg;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 10:01
 */
public interface RecorderChannelCallBack {
    /**
     * 功能描述: 开始录像事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onStart();

    /**
     * 功能描述: 停止录像事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onStop();
}