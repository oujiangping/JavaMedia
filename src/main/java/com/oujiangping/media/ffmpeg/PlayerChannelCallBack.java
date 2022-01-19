package com.oujiangping.media.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.Frame;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/17 16:09
 */
public interface PlayerChannelCallBack {
    /**
     * 当帧到达的时候
     * @param: [frame]
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 16:10
     */
    void onFrame(Frame frame);

    /**
     * 功能描述: 开始播放事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onStart();

    /**
     * 功能描述: 停止播放事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onStop();
}