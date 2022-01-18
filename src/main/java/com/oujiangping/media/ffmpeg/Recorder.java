package com.oujiangping.media.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FrameRecorder;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 10:00
 */
public class Recorder {
    /**
     * 记录器context
     */
    private RecorderContext recorderContext;

    public Recorder(RecorderContext recorderContext) {
        this.recorderContext = recorderContext;
    }

    public void start() throws FrameRecorder.Exception {
        this.recorderContext.getRecorder().start();
        this.recorderContext.getRecorderChannelCallBack().onStart();
    }

    public void fillPacket(AVPacket packet) throws CommonFFmpegFrameRecorder.Exception {
        this.recorderContext.getRecorder().recordPacket(packet);
    }

    public void stop() throws CommonFFmpegFrameRecorder.Exception {
        this.recorderContext.getRecorder().stop();
        this.recorderContext.getRecorderChannelCallBack().onStop();
    }
}
