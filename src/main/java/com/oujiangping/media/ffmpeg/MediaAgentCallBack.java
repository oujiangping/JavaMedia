package com.oujiangping.media.ffmpeg;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 15:48
 */
public interface MediaAgentCallBack {
    void onPacketError(String msg);

    /**
     * 功能描述: 开始启动播放器事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onPlayerStart();

    /**
     * 功能描述: 开始播放事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onPlayerPlay();

    /**
     * 功能描述: 停止播放事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onPlayerStop();

    /**
     * 功能描述: 开始录像事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onRecordStart();

    /**
     * 功能描述: 停止录像事件
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 17:31
     */
    void onRecordStop();


}
