package com.oujiangping.media.server;

import com.oujiangping.media.ffmpeg.CommonFFmpegFrameRecorder;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.ByteBuffer;

/**
 * @author oujiangping
 * @version 1.0
 * @date 5/25/22 9:49 PM
 */
@Component
@Slf4j
@ServerEndpoint("/media/play.flv")
public class MediaServer {
    private static final int RECORD_LENGTH = 50000;

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

        // Decodes-encodes
        String outputFile = "test_00.flv";
        log.info("record");
        frameRecord(session, inputFile, outputFile);

    }

    public static void frameRecord(Session session, String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception, FileNotFoundException {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();

        FileOutputStream outputStream = new FileOutputStream("111.flv");
        CommonFFmpegFrameRecorder recorder = new CommonFFmpegFrameRecorder(session, outputFile, outputStream, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setInterleaved(true);
        recorder.start();

        Frame frame;
        long t1 = System.currentTimeMillis();
        while ((frame = grabber.grabFrame(AUDIO_ENABLED, true, true, false)) != null) {
            recorder.record(frame);
            if ((System.currentTimeMillis() - t1) > RECORD_LENGTH) {
                break;
            }
        }
        recorder.stop();
        grabber.stop();
    }
}
