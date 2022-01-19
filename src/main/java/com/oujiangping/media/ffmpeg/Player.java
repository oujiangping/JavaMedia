package com.oujiangping.media.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/17 16:08
 */
public class Player {
    /**
     * 播放设置
     * @author: oujiangping
     * @date: 2022/1/17 16:25
     */
    private PlayerContext playerContext;



    /**
     * 默认超时时间
     */
    private static final int TIMEOUT = 20;

    public Player(String url, Integer timeout, PlayerChannelCallBack callBack) {
        this.playerContext = new PlayerContext();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(url);
        FormatHelper.setPlayerTimeoutOption(grabber, url, timeout);
        playerContext.setGrabber(grabber);
        playerContext.setSourceUrl(url);
        playerContext.setCallBack(callBack);
    }

    public Player(String url, PlayerChannelCallBack callBack) {
        this(url, TIMEOUT, callBack);
    }

    public void start() throws FFmpegFrameGrabber.Exception {
        playerContext.getGrabber().start();
        playerContext.getCallBack().onStart();
    }

    public void play() throws FFmpegFrameGrabber.Exception {
        Frame frame = null;
        while((frame = playerContext.getGrabber().grabFrame(true, true, true, false)) != null) {
            playerContext.getCallBack().onFrame(frame);
        }
        playerContext.getGrabber().stop();
        playerContext.getCallBack().onStop();
    }

    public void stop() throws FFmpegFrameGrabber.Exception {
        playerContext.getGrabber().stop();
        playerContext.getCallBack().onStop();
    }

    public PlayerContext getPlayerContext() {
        return playerContext;
    }
}
