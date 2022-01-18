package com.oujiangping.media.ffmpeg;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 14:16
 */
public class MediaAgent implements MediaAgentInterface{
    /**
     * 播放器
     */
    private Player player;

    /**
     * 录像器
     */
    private Recorder recorder;

    public MediaAgent(Player player, Recorder recorder) {
        this.player = player;
        this.recorder = recorder;
    }

    @Override
    public void start() throws FFmpegFrameGrabber.Exception, FrameRecorder.Exception {
        player.start();
        recorder.start();
    }

    @Override
    public void stop() throws FFmpegFrameGrabber.Exception, CommonFFmpegFrameRecorder.Exception {
        player.stop();
        recorder.stop();
    }
}
