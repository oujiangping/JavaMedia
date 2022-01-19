package com.oujiangping.media.ffmpeg;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
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

    /**
     * 回调器
     */
    private MediaAgentCallBack mediaAgentCallBack;

    /**
     * 录像器回调
     */
    private RecorderContext recorderContext;

    /**
     * 源地址
     */
    private String sourceUrl;

    /**
     * 目的地址
     */
    private String dstUrl;


    /**
     * 播放器回调内部类
     */
    public class MyPlayerPlayerChannelCallBack implements PlayerChannelCallBack {
        @Override
        public void onFrame(Frame frame) {
            try {
                recorder.fillFrame(frame);
            } catch (CommonFFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
                mediaAgentCallBack.onPacketError(e.getMessage());
            }
        }

        @Override
        public void onStart() {
            mediaAgentCallBack.onPlayerStart();
        }

        @Override
        public void onStop() {
            mediaAgentCallBack.onPlayerStop();
        }
    }

    public class MyRecorderChannelCallBack implements RecorderChannelCallBack {

        @Override
        public void onStart() {
            mediaAgentCallBack.onRecordStart();
        }

        @Override
        public void onStop() {
            mediaAgentCallBack.onRecordStop();
        }
    }

    public MediaAgent(String sourceUrl, String dstUrl, MediaAgentCallBack callBack) {
        FFmpegLogCallback.set();
        this.mediaAgentCallBack = callBack;
        this.player = new Player(sourceUrl, new MyPlayerPlayerChannelCallBack());
        this.sourceUrl = sourceUrl;
        this.dstUrl = dstUrl;
    }

    @Override
    public void start() throws FFmpegFrameGrabber.Exception, FrameRecorder.Exception {
        player.start();
        CommonFFmpegFrameRecorder commonFFmpegFrameRecorder = new CommonFFmpegFrameRecorder(this.dstUrl, player.getPlayerContext().getGrabber().getImageWidth(), player.getPlayerContext().getGrabber().getImageHeight(), player.getPlayerContext().getGrabber().getAudioChannels());
        recorderContext = RecorderContext.builder()
                .outputPath(dstUrl)
                .width(player.getPlayerContext().getGrabber().getImageWidth())
                .height(player.getPlayerContext().getGrabber().getImageHeight())
                .recorder(commonFFmpegFrameRecorder)
                .recorderChannelCallBack(new MyRecorderChannelCallBack())
                .frameRate(player.getPlayerContext().getGrabber().getFrameRate())
                .videoBitrate(player.getPlayerContext().getGrabber().getVideoBitrate())
                .build();
        recorder = new Recorder(recorderContext);
        recorder.start();
    }

    @Override
    public void play() throws FFmpegFrameGrabber.Exception {
        player.play();
    }

    @Override
    public void stop() throws FFmpegFrameGrabber.Exception, CommonFFmpegFrameRecorder.Exception {
        player.stop();
        recorder.stop();
    }
}
