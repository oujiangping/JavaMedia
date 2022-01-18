package com.oujiangping.media.ffmpeg;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 14:53
 */
public interface MediaAgentInterface {
    /**
     * 开始代理
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/18 14:54
     */
    void start() throws FFmpegFrameGrabber.Exception, FrameRecorder.Exception;

    /**
     * 结束代理
     * @param: []
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/18 14:54
     */
    void stop() throws FFmpegFrameGrabber.Exception, CommonFFmpegFrameRecorder.Exception;
}
