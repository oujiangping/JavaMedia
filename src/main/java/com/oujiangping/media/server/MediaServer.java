package com.oujiangping.media.server;

import com.oujiangping.media.ffmpeg.WebsocketFFmpegFrameRecorder;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;

/**
 * @author oujiangping
 * @version 1.0
 * @date 5/25/22 9:49 PM
 */
@Component
@Slf4j
@ServerEndpoint("/media/play.flv")
public class MediaServer {
    private static final boolean AUDIO_ENABLED = true;

    @OnOpen
    public void onOpen(Session session) {
        log.info("onOpen");
        Thread mediaThread = new Thread(new MediaRunnable(session));
        mediaThread.start();
    }

    public class MediaRunnable implements Runnable {
        private Session session;

        public MediaRunnable(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            try {
                record(session);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void OnClose() {

    }

    @OnMessage
    public void OnMessage(String message, Session session) {

    }

    public void  record(Session session) throws FrameGrabber.Exception, FrameRecorder.Exception, FileNotFoundException {
        String inputFile = "http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8";
        log.info("record");
        //frameRecord(session, inputFile);
        packetRecord(session, inputFile);
    }

    public static void frameRecord(Session session, String inputFile) throws FrameGrabber.Exception, FrameRecorder.Exception, FileNotFoundException {
        avutil.av_log_set_level(avutil.AV_LOG_DEBUG);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();

        WebsocketFFmpegFrameRecorder recorder = new WebsocketFFmpegFrameRecorder(session, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("flv");
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setInterleaved(true);


        recorder.start();
        Frame frame;
        while ((frame = grabber.grabFrame(AUDIO_ENABLED, true, true, false)) != null) {
            recorder.record(frame);
        }

        recorder.stop();
        grabber.stop();
    }

    public static void packetRecord(Session session, String inputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.setPixelFormat(0);
        grabber.start();
        WebsocketFFmpegFrameRecorder recorder = new WebsocketFFmpegFrameRecorder(session, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setPixelFormat(grabber.getPixelFormat());
        recorder.setFormat("flv");
        recorder.start(grabber.getFormatContext());
        AVPacket packet;
        while ((packet = grabber.grabPacket()) != null) {
            recorder.recordPacket(packet);
        }
        recorder.stop();
        grabber.stop();

    }
}
