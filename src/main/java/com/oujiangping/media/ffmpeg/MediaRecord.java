package com.oujiangping.media.ffmpeg;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.io.FileNotFoundException;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/6/10 15:35
 */
@Slf4j
public class MediaRecord {
    private static final boolean AUDIO_ENABLED = true;

    public static void record(PacketWriter session, String url) throws FrameGrabber.Exception, FrameRecorder.Exception, FileNotFoundException {
        String inputFile = url;
        log.info("record");
        avutil.av_log_set_level(avutil.AV_LOG_DEBUG);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.setPixelFormat(AV_PIX_FMT_YUV420P);
        grabber.start();
        if(grabber.getVideoCodec() == AV_CODEC_ID_H264) {
            packetRecord(session, grabber);
        } else {
            frameRecord(session, grabber);
        }
    }

    public static void frameRecord(PacketWriter session, FFmpegFrameGrabber grabber) throws FrameGrabber.Exception, FrameRecorder.Exception, FileNotFoundException {
        WebsocketFFmpegFrameRecorder recorder = new WebsocketFFmpegFrameRecorder(session, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("flv");
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setPixelFormat(grabber.getPixelFormat());
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

    public static void packetRecord(PacketWriter session, FFmpegFrameGrabber grabber) throws FrameGrabber.Exception, FrameRecorder.Exception {
        WebsocketFFmpegFrameRecorder recorder = new WebsocketFFmpegFrameRecorder(session, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
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
