package com.oujiangping.media.ffmpeg;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.io.FileNotFoundException;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

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
        FFmpegLogCallback.set();
        String inputFile = url;
        log.info("record");
        avutil.av_log_set_level(avutil.AV_LOG_DEBUG);
        MyFFmpegFrameGrabber grabber = new MyFFmpegFrameGrabber(inputFile);
        grabber.setThirdOptionsParam("rtsp_transport", "tcp");
        grabber.start();
        /**
         * 这些视频格式flv支持 不转码了
         */
        if (grabber.getVideoCodec() == AV_CODEC_ID_H264
                || grabber.getVideoCodec() == AV_CODEC_ID_FLV1
                || grabber.getVideoCodec() == AV_CODEC_ID_VP6
                || grabber.getVideoCodec() == AV_CODEC_ID_H263
                || grabber.getVideoCodec() == AV_CODEC_ID_MPEG4) {
            packetRecord(session, grabber);
            //frameRecord(session, grabber);
        } else {
            frameRecord(session, grabber);
        }
        grabber.stop();
    }

    public static void frameRecord(PacketWriter session, MyFFmpegFrameGrabber grabber) throws FrameGrabber.Exception, FrameRecorder.Exception, FileNotFoundException {
        FFmpegMediaRecorder recorder = new FFmpegMediaRecorder(session, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("flv");
        recorder.setFrameRate(grabber.getFrameRate());
        //recorder.setPixelFormat(grabber.getPixelFormat());
        recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setInterleaved(true);


        recorder.start();
        Frame frame;
        try {
            while ((frame = grabber.grabFrame(AUDIO_ENABLED, true, true, false)) != null) {
                recorder.record(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recorder.stop();
    }

    public static void packetRecord(PacketWriter session, MyFFmpegFrameGrabber grabber) throws FrameGrabber.Exception, FrameRecorder.Exception {
        FFmpegMediaRecorder recorder = new FFmpegMediaRecorder(session, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setSampleFormat(AV_SAMPLE_FMT_S16);
        recorder.setFormat("flv");
        recorder.start(grabber.getFormatContext());
        recorder.setGrabber(grabber);
        AVPacket packet;
        try {
            while ((packet = grabber.grabPacket()) != null) {
                recorder.recordPacket(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recorder.stop();
    }
}
