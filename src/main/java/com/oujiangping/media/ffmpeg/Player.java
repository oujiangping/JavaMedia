package com.oujiangping.media.ffmpeg;

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
     *
     * @author: oujiangping
     * @date: 2022/1/17 16:25
     */
    private PlayerContext playerContext;


    /**
     * 默认超时时间
     */
    private static final int TIMEOUT = 20;

    public Player(PlayerContext playerContext, Integer timeout, PlayerChannelCallBack callBack) {
        this.playerContext = playerContext;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(this.playerContext.getSourceUrl());
        FormatHelper.setPlayerTimeoutOption(grabber, this.playerContext.getSourceUrl(), timeout);
        playerContext.setGrabber(grabber);
        playerContext.setCallBack(callBack);
        playerContext.setStatus(PlayerContext.Status.INIT);
    }

    public Player(PlayerContext playerContext, PlayerChannelCallBack callBack) {
        this(playerContext, TIMEOUT, callBack);
    }

    public void start() throws FFmpegFrameGrabber.Exception {
        playerContext.getGrabber().start();
        playerContext.getCallBack().onStart();
        playerContext.setStatus(PlayerContext.Status.START);
    }

    public void play() throws FFmpegFrameGrabber.Exception {
        Frame frame;
        playerContext.setStatus(PlayerContext.Status.RUNNING);
        while ((playerContext.getStatus() == PlayerContext.Status.RUNNING) && (frame = playerContext.getGrabber().grabFrame(true, true, true, false)) != null) {
            playerContext.getCallBack().onFrame(frame);
        }
        playerContext.getGrabber().stop();
        playerContext.getCallBack().onStop();
        playerContext.getCallBack().onStop();
        playerContext.setStatus(PlayerContext.Status.STOP);
    }

    public void stop() {
        playerContext.setStatus(PlayerContext.Status.STOP);
    }

    public PlayerContext getPlayerContext() {
        return playerContext;
    }
}
