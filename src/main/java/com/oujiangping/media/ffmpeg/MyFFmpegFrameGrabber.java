//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.oujiangping.media.ffmpeg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVIOContext;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avformat.Read_packet_Pointer_BytePointer_int;
import org.bytedeco.ffmpeg.avformat.Seek_Pointer_long_int;
import org.bytedeco.ffmpeg.avformat.Write_packet_Pointer_BytePointer_int;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVDictionaryEntry;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.avutil.AVRational;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avdevice;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.ffmpeg.global.swresample;
import org.bytedeco.ffmpeg.global.swscale;
import org.bytedeco.ffmpeg.swresample.SwrContext;
import org.bytedeco.ffmpeg.swscale.SwsContext;
import org.bytedeco.ffmpeg.swscale.SwsFilter;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.PointerScope;
import org.bytedeco.javacv.FFmpegLockCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Frame.Type;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.ImageMode;
import org.bytedeco.javacv.FrameGrabber.SampleMode;

public class MyFFmpegFrameGrabber extends FrameGrabber {
    private static Exception loadingException = null;
    static Map<Pointer, InputStream> inputStreams;
    static ReadCallback readCallback;
    static SeekCallback seekCallback;
    private InputStream inputStream;
    private boolean closeInputStream;
    private int maximumSize;
    private AVIOContext avio;
    private String filename;
    private AVFormatContext oc;
    private AVStream video_st;
    private AVStream audio_st;
    private AVCodecContext video_c;
    private AVCodecContext audio_c;
    private AVFrame picture;
    private AVFrame picture_rgb;
    private BytePointer[] image_ptr;
    private Buffer[] image_buf;
    private AVFrame samples_frame;
    private BytePointer[] samples_ptr;
    private Buffer[] samples_buf;
    private BytePointer[] samples_ptr_out;
    private Buffer[] samples_buf_out;
    private PointerPointer plane_ptr;
    private PointerPointer plane_ptr2;
    private AVPacket pkt;
    private int sizeof_pkt;
    private int[] got_frame;
    private SwsContext img_convert_ctx;
    private SwrContext samples_convert_ctx;
    private int samples_channels;
    private int samples_format;
    private int samples_rate;
    private boolean frameGrabbed;
    private Frame frame;
    private volatile boolean started;

    private AVDictionary thirdOptions;

    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();
        throw new UnsupportedOperationException("Device enumeration not support by FFmpeg.");
    }

    public static MyFFmpegFrameGrabber createDefault(File deviceFile) throws Exception {
        return new MyFFmpegFrameGrabber(deviceFile);
    }

    public static MyFFmpegFrameGrabber createDefault(String devicePath) throws Exception {
        return new MyFFmpegFrameGrabber(devicePath);
    }

    public static MyFFmpegFrameGrabber createDefault(int deviceNumber) throws Exception {
        throw new Exception(MyFFmpegFrameGrabber.class + " does not support device numbers.");
    }

    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(avutil.class);
                Loader.load(swresample.class);
                Loader.load(avcodec.class);
                Loader.load(avformat.class);
                Loader.load(swscale.class);
                avcodec.av_jni_set_java_vm(Loader.getJavaVM(), (Pointer)null);
                avcodec.avcodec_register_all();
                avformat.av_register_all();
                avformat.avformat_network_init();
                Loader.load(avdevice.class);
                avdevice.avdevice_register_all();
            } catch (Throwable var1) {
                if (var1 instanceof Exception) {
                    throw loadingException = (Exception)var1;
                } else {
                    throw loadingException = new Exception("Failed to load " + MyFFmpegFrameGrabber.class, var1);
                }
            }
        }
    }

    public MyFFmpegFrameGrabber(File file) {
        this(file.getAbsolutePath());
    }

    public MyFFmpegFrameGrabber(String filename) {
        this.started = false;
        this.filename = filename;
        this.pixelFormat = -1;
        this.sampleFormat = -1;
        this.thirdOptions = new AVDictionary((Pointer)null);
    }

    public MyFFmpegFrameGrabber(InputStream inputStream) {
        this(inputStream, 2147483639);
    }

    public MyFFmpegFrameGrabber(InputStream inputStream, int maximumSize) {
        this.started = false;
        this.inputStream = inputStream;
        this.closeInputStream = true;
        this.pixelFormat = -1;
        this.sampleFormat = -1;
        this.maximumSize = maximumSize;
        this.thirdOptions = new AVDictionary((Pointer)null);
    }

    @Override
    public void release() throws Exception {
        Class var1 = avcodec.class;
        synchronized(avcodec.class) {
            this.releaseUnsafe();
        }
    }

    public synchronized void releaseUnsafe() throws Exception {
        this.started = false;
        if (this.plane_ptr != null && this.plane_ptr2 != null) {
            this.plane_ptr.releaseReference();
            this.plane_ptr2.releaseReference();
            this.plane_ptr = this.plane_ptr2 = null;
        }

        if (this.pkt != null) {
            if (this.pkt.stream_index() != -1) {
                avcodec.av_packet_unref(this.pkt);
            }

            this.pkt.releaseReference();
            this.pkt = null;
        }

        int i;
        if (this.image_ptr != null) {
            for(i = 0; i < this.image_ptr.length; ++i) {
                if (this.imageMode != ImageMode.RAW) {
                    avutil.av_free(this.image_ptr[i]);
                }
            }

            this.image_ptr = null;
        }

        if (this.picture_rgb != null) {
            avutil.av_frame_free(this.picture_rgb);
            this.picture_rgb = null;
        }

        if (this.picture != null) {
            avutil.av_frame_free(this.picture);
            this.picture = null;
        }

        if (this.video_c != null) {
            avcodec.avcodec_free_context(this.video_c);
            this.video_c = null;
        }

        if (this.samples_frame != null) {
            avutil.av_frame_free(this.samples_frame);
            this.samples_frame = null;
        }

        if (this.audio_c != null) {
            avcodec.avcodec_free_context(this.audio_c);
            this.audio_c = null;
        }

        if (this.inputStream == null && this.oc != null && !this.oc.isNull()) {
            avformat.avformat_close_input(this.oc);
            this.oc = null;
        }

        if (this.img_convert_ctx != null) {
            swscale.sws_freeContext(this.img_convert_ctx);
            this.img_convert_ctx = null;
        }

        if (this.samples_ptr_out != null) {
            for(i = 0; i < this.samples_ptr_out.length; ++i) {
                avutil.av_free(this.samples_ptr_out[i].position(0L));
            }

            this.samples_ptr_out = null;
            this.samples_buf_out = null;
        }

        if (this.samples_convert_ctx != null) {
            swresample.swr_free(this.samples_convert_ctx);
            this.samples_convert_ctx = null;
        }

        this.got_frame = null;
        this.frameGrabbed = false;
        this.frame = null;
        this.timestamp = 0L;
        this.frameNumber = 0;
        if (this.inputStream != null) {
            try {
                if (this.oc == null) {
                    if (this.closeInputStream) {
                        this.inputStream.close();
                    }
                } else if (this.maximumSize > 0) {
                    try {
                        this.inputStream.reset();
                    } catch (IOException var6) {
                        System.err.println("Error on InputStream.reset(): " + var6);
                    }
                }
            } catch (IOException var7) {
                throw new Exception("Error on InputStream.close(): ", var7);
            } finally {
                inputStreams.remove(this.oc);
                if (this.avio != null) {
                    if (this.avio.buffer() != null) {
                        avutil.av_free(this.avio.buffer());
                        this.avio.buffer((BytePointer)null);
                    }

                    avutil.av_free(this.avio);
                    this.avio = null;
                }

                if (this.oc != null) {
                    avformat.avformat_free_context(this.oc);
                    this.oc = null;
                }

            }
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.release();
    }

    public boolean isCloseInputStream() {
        return this.closeInputStream;
    }

    public void setCloseInputStream(boolean closeInputStream) {
        this.closeInputStream = closeInputStream;
    }

    public boolean hasVideo() {
        return this.video_st != null;
    }

    public boolean hasAudio() {
        return this.audio_st != null;
    }

    @Override
    public double getGamma() {
        return this.gamma == 0.0 ? 2.2 : this.gamma;
    }

    @Override
    public String getFormat() {
        return this.oc == null ? super.getFormat() : this.oc.iformat().name().getString();
    }

    @Override
    public int getImageWidth() {
        return this.imageWidth <= 0 && this.video_c != null ? this.video_c.width() : super.getImageWidth();
    }

    @Override
    public int getImageHeight() {
        return this.imageHeight <= 0 && this.video_c != null ? this.video_c.height() : super.getImageHeight();
    }

    @Override
    public int getAudioChannels() {
        return this.audioChannels <= 0 && this.audio_c != null ? this.audio_c.channels() : super.getAudioChannels();
    }

    public AVCodecContext getAudioC() {
        return audio_c;
    }

    public AVCodecContext getVideoC() {
        return video_c;
    }

    @Override
    public int getPixelFormat() {
        if (this.imageMode != ImageMode.COLOR && this.imageMode != ImageMode.GRAY) {
            return this.video_c != null ? this.video_c.pix_fmt() : super.getPixelFormat();
        } else if (this.pixelFormat == -1) {
            return this.imageMode == ImageMode.COLOR ? 3 : 8;
        } else {
            return this.pixelFormat;
        }
    }

    @Override
    public int getVideoCodec() {
        return this.video_c == null ? super.getVideoCodec() : this.video_c.codec_id();
    }

    @Override
    public int getVideoBitrate() {
        return this.video_c == null ? super.getVideoBitrate() : (int)this.video_c.bit_rate();
    }

    @Override
    public double getAspectRatio() {
        if (this.video_st == null) {
            return super.getAspectRatio();
        } else {
            AVRational r = avformat.av_guess_sample_aspect_ratio(this.oc, this.video_st, this.picture);
            double a = (double)r.num() / (double)r.den();
            return a == 0.0 ? 1.0 : a;
        }
    }

    @Override
    public double getFrameRate() {
        return this.getVideoFrameRate();
    }

    public double getAudioFrameRate() {
        if (this.audio_st == null) {
            return 0.0;
        } else {
            if (this.samples_frame == null || this.samples_frame.nb_samples() == 0) {
                try {
                    this.grabFrame(true, false, false, false, false);
                    this.frameGrabbed = true;
                } catch (Exception var2) {
                    return 0.0;
                }
            }

            return this.samples_frame != null && this.samples_frame.nb_samples() != 0 ? (double)this.getSampleRate() / (double)this.samples_frame.nb_samples() : 0.0;
        }
    }

    public double getVideoFrameRate() {
        if (this.video_st == null) {
            return super.getFrameRate();
        } else {
            AVRational r = this.video_st.avg_frame_rate();
            if (r.num() == 0 && r.den() == 0) {
                r = this.video_st.r_frame_rate();
            }

            return (double)r.num() / (double)r.den();
        }
    }

    @Override
    public int getAudioCodec() {
        return this.audio_c == null ? super.getAudioCodec() : this.audio_c.codec_id();
    }

    @Override
    public int getAudioBitrate() {
        return this.audio_c == null ? super.getAudioBitrate() : (int)this.audio_c.bit_rate();
    }

    @Override
    public int getSampleFormat() {
        if (this.sampleMode != SampleMode.SHORT && this.sampleMode != SampleMode.FLOAT) {
            return this.audio_c != null ? this.audio_c.sample_fmt() : super.getSampleFormat();
        } else if (this.sampleFormat == -1) {
            return this.sampleMode == SampleMode.SHORT ? 1 : 3;
        } else {
            return this.sampleFormat;
        }
    }

    @Override
    public int getSampleRate() {
        return this.sampleRate <= 0 && this.audio_c != null ? this.audio_c.sample_rate() : super.getSampleRate();
    }

    @Override
    public Map<String, String> getMetadata() {
        if (this.oc == null) {
            return super.getMetadata();
        } else {
            AVDictionaryEntry entry = null;
            Map<String, String> metadata = new HashMap();

            while((entry = avutil.av_dict_get(this.oc.metadata(), "", entry, 2)) != null) {
                metadata.put(entry.key().getString(), entry.value().getString());
            }

            return metadata;
        }
    }

    @Override
    public Map<String, String> getVideoMetadata() {
        if (this.video_st == null) {
            return super.getVideoMetadata();
        } else {
            AVDictionaryEntry entry = null;
            Map<String, String> metadata = new HashMap();

            while((entry = avutil.av_dict_get(this.video_st.metadata(), "", entry, 2)) != null) {
                metadata.put(entry.key().getString(), entry.value().getString());
            }

            return metadata;
        }
    }

    @Override
    public Map<String, String> getAudioMetadata() {
        if (this.audio_st == null) {
            return super.getAudioMetadata();
        } else {
            AVDictionaryEntry entry = null;
            Map<String, String> metadata = new HashMap();

            while((entry = avutil.av_dict_get(this.audio_st.metadata(), "", entry, 2)) != null) {
                metadata.put(entry.key().getString(), entry.value().getString());
            }

            return metadata;
        }
    }

    @Override
    public String getMetadata(String key) {
        if (this.oc == null) {
            return super.getMetadata(key);
        } else {
            AVDictionaryEntry entry = avutil.av_dict_get(this.oc.metadata(), key, (AVDictionaryEntry)null, 0);
            return entry != null && entry.value() != null ? entry.value().getString() : null;
        }
    }

    @Override
    public String getVideoMetadata(String key) {
        if (this.video_st == null) {
            return super.getVideoMetadata(key);
        } else {
            AVDictionaryEntry entry = avutil.av_dict_get(this.video_st.metadata(), key, (AVDictionaryEntry)null, 0);
            return entry != null && entry.value() != null ? entry.value().getString() : null;
        }
    }

    @Override
    public String getAudioMetadata(String key) {
        if (this.audio_st == null) {
            return super.getAudioMetadata(key);
        } else {
            AVDictionaryEntry entry = avutil.av_dict_get(this.audio_st.metadata(), key, (AVDictionaryEntry)null, 0);
            return entry != null && entry.value() != null ? entry.value().getString() : null;
        }
    }

    @Override
    public void setFrameNumber(int frameNumber) throws Exception {
        if (this.hasVideo()) {
            this.setTimestamp(Math.round((double)(1000000L * (long)frameNumber) / this.getFrameRate()));
        } else {
            super.frameNumber = frameNumber;
        }

    }

    public void setVideoFrameNumber(int frameNumber) throws Exception {
        if (this.hasVideo()) {
            this.setVideoTimestamp(Math.round((double)(1000000L * (long)frameNumber) / this.getFrameRate()));
        } else {
            super.frameNumber = frameNumber;
        }

    }

    public void setAudioFrameNumber(int frameNumber) throws Exception {
        if (this.hasAudio()) {
            this.setAudioTimestamp(Math.round((double)(1000000L * (long)frameNumber) / this.getAudioFrameRate()));
        }

    }

    @Override
    public void setTimestamp(long timestamp) throws Exception {
        this.setTimestamp(timestamp, false);
    }

    public void setTimestamp(long timestamp, boolean checkFrame) throws Exception {
        this.setTimestamp(timestamp, checkFrame ? EnumSet.of(Type.VIDEO, Type.AUDIO) : null);
    }

    public void setVideoTimestamp(long timestamp) throws Exception {
        this.setTimestamp(timestamp, EnumSet.of(Type.VIDEO));
    }

    public void setAudioTimestamp(long timestamp) throws Exception {
        this.setTimestamp(timestamp, EnumSet.of(Type.AUDIO));
    }

    private synchronized void setTimestamp(long timestamp, EnumSet<Frame.Type> frameTypesToSeek) throws Exception {
        if (this.oc == null) {
            super.timestamp = timestamp;
        } else {
            timestamp = timestamp * 1000000L / 1000000L;
            long ts0 = 0L;
            if (this.oc.start_time() != avutil.AV_NOPTS_VALUE) {
                ts0 = this.oc.start_time();
            }

            long early_ts = timestamp;
            if (frameTypesToSeek != null) {
                early_ts = timestamp - 500000L;
                if (early_ts < 0L) {
                    early_ts = 0L;
                }

                if (frameTypesToSeek.size() == 1) {
                    AVRational time_base;
                    if (frameTypesToSeek.contains(Type.VIDEO)) {
                        if (this.video_st != null && this.video_st.start_time() != avutil.AV_NOPTS_VALUE) {
                            time_base = this.video_st.time_base();
                            ts0 = 1000000L * this.video_st.start_time() * (long)time_base.num() / (long)time_base.den();
                        }
                    } else if (frameTypesToSeek.contains(Type.AUDIO) && this.audio_st != null && this.audio_st.start_time() != avutil.AV_NOPTS_VALUE) {
                        time_base = this.audio_st.time_base();
                        ts0 = 1000000L * this.audio_st.start_time() * (long)time_base.num() / (long)time_base.den();
                    }
                }
            }

            timestamp += ts0;
            early_ts += ts0;
            int ret;
            if ((ret = avformat.avformat_seek_file(this.oc, -1, Long.MIN_VALUE, early_ts, Long.MAX_VALUE, 1)) < 0) {
                throw new Exception("avformat_seek_file() error " + ret + ": Could not seek file to timestamp " + timestamp + ".");
            }

            if (this.video_c != null) {
                avcodec.avcodec_flush_buffers(this.video_c);
            }

            if (this.audio_c != null) {
                avcodec.avcodec_flush_buffers(this.audio_c);
            }

            if (this.pkt.stream_index() != -1) {
                avcodec.av_packet_unref(this.pkt);
                this.pkt.stream_index(-1);
            }

            if (frameTypesToSeek == null || !frameTypesToSeek.contains(Type.VIDEO) && !frameTypesToSeek.contains(Type.AUDIO)) {
                int count = 0;

                while(this.timestamp > timestamp + 1L && this.grabFrame(true, true, false, false) != null && count++ < 1000) {
                }

                count = 0;

                while(this.timestamp < timestamp - 1L && this.grabFrame(true, true, false, false) != null && count++ < 1000) {
                }

                this.frameGrabbed = true;
            } else {
                boolean has_video = this.hasVideo();
                boolean has_audio = this.hasAudio();
                if (has_video || has_audio) {
                    if (frameTypesToSeek.contains(Type.VIDEO) && !has_video || frameTypesToSeek.contains(Type.AUDIO) && !has_audio) {
                        frameTypesToSeek = EnumSet.of(Type.VIDEO, Type.AUDIO);
                    }

                    long initialSeekPosition = Long.MIN_VALUE;
                    long maxSeekSteps = 0L;
                    long count = 0L;
                    Frame seekFrame = null;
                    seekFrame = this.grabFrame(frameTypesToSeek.contains(Type.AUDIO), frameTypesToSeek.contains(Type.VIDEO), false, false, false);
                    if (seekFrame == null) {
                        return;
                    }

                    initialSeekPosition = seekFrame.timestamp;
                    double frameDuration = 0.0;
                    if (seekFrame.image != null && this.getFrameRate() > 0.0) {
                        frameDuration = 1000000.0 / this.getFrameRate();
                    } else if (seekFrame.samples != null && this.samples_frame != null && this.getSampleRate() > 0) {
                        frameDuration = (double)(1000000 * this.samples_frame.nb_samples()) / (double)this.getSampleRate();
                    }

                    if (frameDuration > 0.0) {
                        maxSeekSteps = (long)(10.0 * ((double)(timestamp - initialSeekPosition) - frameDuration) / frameDuration);
                        if (maxSeekSteps < 0L) {
                            maxSeekSteps = 0L;
                        }
                    } else if (initialSeekPosition < timestamp) {
                        maxSeekSteps = 1000L;
                    }

                    double delta = 0.0;
                    count = 0L;

                    while(count < maxSeekSteps) {
                        seekFrame = this.grabFrame(frameTypesToSeek.contains(Type.AUDIO), frameTypesToSeek.contains(Type.VIDEO), false, false, false);
                        if (seekFrame == null) {
                            return;
                        }

                        ++count;
                        double ts = (double)this.timestamp;
                        frameDuration = 0.0;
                        if (seekFrame.image != null && this.getFrameRate() > 0.0) {
                            frameDuration = 1000000.0 / this.getFrameRate();
                        } else if (seekFrame.samples != null && this.samples_frame != null && this.getSampleRate() > 0) {
                            frameDuration = (double)(1000000 * this.samples_frame.nb_samples()) / (double)this.getSampleRate();
                        }

                        delta = 0.0;
                        if (frameDuration > 0.0) {
                            delta = (ts - (double)ts0) / frameDuration - (double)Math.round((ts - (double)ts0) / frameDuration);
                            if (Math.abs(delta) > 0.2) {
                                delta = 0.0;
                            }
                        }

                        ts -= delta * frameDuration;
                        if (ts + frameDuration > (double)timestamp) {
                            break;
                        }
                    }

                    this.frameGrabbed = true;
                }
            }
        }

    }

    @Override
    public int getLengthInFrames() {
        return this.getLengthInVideoFrames();
    }

    @Override
    public long getLengthInTime() {
        return this.oc.duration() * 1000000L / 1000000L;
    }

    public int getLengthInVideoFrames() {
        return (int)Math.round((double)this.getLengthInTime() * this.getFrameRate() / 1000000.0);
    }

    public int getLengthInAudioFrames() {
        double afr = this.getAudioFrameRate();
        return afr > 0.0 ? (int)((double)this.getLengthInTime() * afr / 1000000.0) : 0;
    }

    public AVFormatContext getFormatContext() {
        return this.oc;
    }

    @Override
    public void start() throws Exception {
        this.start(true);
    }

    public void start(boolean findStreamInfo) throws Exception {
        Class var2 = avcodec.class;
        synchronized(avcodec.class) {
            this.startUnsafe(findStreamInfo);
        }
    }

    public void startUnsafe() throws Exception {
        this.startUnsafe(true);
    }

    public synchronized void startUnsafe(boolean findStreamInfo) throws Exception {
        PointerScope scope = new PointerScope(new Class[0]);
        Throwable var3 = null;

        try {
            if (this.oc != null && !this.oc.isNull()) {
                throw new Exception("start() has already been called: Call stop() before calling start() again.");
            }

            this.img_convert_ctx = null;
            this.oc = new AVFormatContext((Pointer)null);
            this.video_c = null;
            this.audio_c = null;
            this.plane_ptr = (PointerPointer)(new PointerPointer(8L)).retainReference();
            this.plane_ptr2 = (PointerPointer)(new PointerPointer(8L)).retainReference();
            this.pkt = (AVPacket)(new AVPacket()).retainReference();
            this.sizeof_pkt = this.pkt.sizeof();
            this.got_frame = new int[1];
            this.frameGrabbed = false;
            this.frame = new Frame();
            this.timestamp = 0L;
            this.frameNumber = 0;
            this.pkt.stream_index(-1);
            AVInputFormat f = null;
            if (this.format != null && this.format.length() > 0 && (f = avformat.av_find_input_format(this.format)) == null) {
                throw new Exception("av_find_input_format() error: Could not find input format \"" + this.format + "\".");
            }

            AVDictionary options = thirdOptions;
            if (this.frameRate > 0.0) {
                AVRational r = avutil.av_d2q(this.frameRate, 1001000);
                avutil.av_dict_set(options, "framerate", r.num() + "/" + r.den(), 0);
            }

            if (this.pixelFormat >= 0) {
                avutil.av_dict_set(options, "pixel_format", avutil.av_get_pix_fmt_name(this.pixelFormat).getString(), 0);
            } else if (this.imageMode != ImageMode.RAW) {
                avutil.av_dict_set(options, "pixel_format", this.imageMode == ImageMode.COLOR ? "bgr24" : "gray8", 0);
            }

            if (this.imageWidth > 0 && this.imageHeight > 0) {
                avutil.av_dict_set(options, "video_size", this.imageWidth + "x" + this.imageHeight, 0);
            }

            if (this.sampleRate > 0) {
                avutil.av_dict_set(options, "sample_rate", "" + this.sampleRate, 0);
            }

            if (this.audioChannels > 0) {
                avutil.av_dict_set(options, "channels", "" + this.audioChannels, 0);
            }

            Iterator var22 = this.options.entrySet().iterator();

            while(var22.hasNext()) {
                Map.Entry<String, String> e = (Map.Entry)var22.next();
                avutil.av_dict_set(options, (String)e.getKey(), (String)e.getValue(), 0);
            }

            if (this.inputStream != null) {
                if (!this.inputStream.markSupported()) {
                    this.inputStream = new BufferedInputStream(this.inputStream);
                }

                this.inputStream.mark(this.maximumSize);
                this.oc = avformat.avformat_alloc_context();
                this.avio = avformat.avio_alloc_context(new BytePointer(avutil.av_malloc(4096L)), 4096, 0, this.oc, readCallback, (Write_packet_Pointer_BytePointer_int)null, this.maximumSize > 0 ? seekCallback : null);
                this.oc.pb(this.avio);
                this.filename = this.inputStream.toString();
                inputStreams.put(this.oc, this.inputStream);
            }

            int ret;
            if (avformat.avformat_open_input(this.oc, this.filename, f, options) < 0) {
                avutil.av_dict_set(options, "pixel_format", (String)null, 0);
                if ((ret = avformat.avformat_open_input(this.oc, this.filename, f, options)) < 0) {
                    throw new Exception("avformat_open_input() error " + ret + ": Could not open input \"" + this.filename + "\". (Has setFormat() been called?)");
                }
            }

            avutil.av_dict_free(options);
            this.oc.max_delay(this.maxDelay);
            if (findStreamInfo && (ret = avformat.avformat_find_stream_info(this.oc, (PointerPointer)null)) < 0) {
                throw new Exception("avformat_find_stream_info() error " + ret + ": Could not find stream information.");
            }

            if (avutil.av_log_get_level() >= 32) {
                avformat.av_dump_format(this.oc, 0, this.filename, 0);
            }

            this.video_st = this.audio_st = null;
            AVCodecParameters video_par = null;
            AVCodecParameters audio_par = null;
            int nb_streams = this.oc.nb_streams();

            for(int i = 0; i < nb_streams; ++i) {
                AVStream st = this.oc.streams(i);
                AVCodecParameters par = st.codecpar();
                if (this.video_st != null || par.codec_type() != 0 || this.videoStream >= 0 && this.videoStream != i) {
                    if (this.audio_st == null && par.codec_type() == 1 && (this.audioStream < 0 || this.audioStream == i)) {
                        this.audio_st = st;
                        audio_par = par;
                        this.audioStream = i;
                    }
                } else {
                    this.video_st = st;
                    video_par = par;
                    this.videoStream = i;
                }
            }

            if (this.video_st == null && this.audio_st == null) {
                throw new Exception("Did not find a video or audio stream inside \"" + this.filename + "\" for videoStream == " + this.videoStream + " and audioStream == " + this.audioStream + ".");
            }

            AVCodec codec;
            Iterator var26;
            Map.Entry e;
            if (this.video_st != null) {
                codec = avcodec.avcodec_find_decoder_by_name(this.videoCodecName);
                if (codec == null) {
                    codec = avcodec.avcodec_find_decoder(video_par.codec_id());
                }

                if (codec == null) {
                    throw new Exception("avcodec_find_decoder() error: Unsupported video format or codec not found: " + video_par.codec_id() + ".");
                }

                if ((this.video_c = avcodec.avcodec_alloc_context3(codec)) == null) {
                    throw new Exception("avcodec_alloc_context3() error: Could not allocate video decoding context.");
                }

                if ((ret = avcodec.avcodec_parameters_to_context(this.video_c, this.video_st.codecpar())) < 0) {
                    this.releaseUnsafe();
                    throw new Exception("avcodec_parameters_to_context() error " + ret + ": Could not copy the video stream parameters.");
                }

                options = new AVDictionary((Pointer)null);
                var26 = this.videoOptions.entrySet().iterator();

                while(var26.hasNext()) {
                    e = (Map.Entry)var26.next();
                    avutil.av_dict_set(options, (String)e.getKey(), (String)e.getValue(), 0);
                }

                this.video_c.thread_count(0);
                if ((ret = avcodec.avcodec_open2(this.video_c, codec, options)) < 0) {
                    throw new Exception("avcodec_open2() error " + ret + ": Could not open video codec.");
                }

                avutil.av_dict_free(options);
                if (this.video_c.time_base().num() > 1000 && this.video_c.time_base().den() == 1) {
                    this.video_c.time_base().den(1000);
                }

                if ((this.picture = avutil.av_frame_alloc()) == null) {
                    throw new Exception("av_frame_alloc() error: Could not allocate raw picture frame.");
                }

                if ((this.picture_rgb = avutil.av_frame_alloc()) == null) {
                    throw new Exception("av_frame_alloc() error: Could not allocate RGB picture frame.");
                }

                this.initPictureRGB();
            }

            if (this.audio_st != null) {
                codec = avcodec.avcodec_find_decoder_by_name(this.audioCodecName);
                if (codec == null) {
                    codec = avcodec.avcodec_find_decoder(audio_par.codec_id());
                }

                if (codec == null) {
                    throw new Exception("avcodec_find_decoder() error: Unsupported audio format or codec not found: " + audio_par.codec_id() + ".");
                }

                if ((this.audio_c = avcodec.avcodec_alloc_context3(codec)) == null) {
                    throw new Exception("avcodec_alloc_context3() error: Could not allocate audio decoding context.");
                }

                if ((ret = avcodec.avcodec_parameters_to_context(this.audio_c, this.audio_st.codecpar())) < 0) {
                    this.releaseUnsafe();
                    throw new Exception("avcodec_parameters_to_context() error " + ret + ": Could not copy the audio stream parameters.");
                }

                options = new AVDictionary((Pointer)null);
                var26 = this.audioOptions.entrySet().iterator();

                while(var26.hasNext()) {
                    e = (Map.Entry)var26.next();
                    avutil.av_dict_set(options, (String)e.getKey(), (String)e.getValue(), 0);
                }

                this.audio_c.thread_count(0);
                if ((ret = avcodec.avcodec_open2(this.audio_c, codec, options)) < 0) {
                    throw new Exception("avcodec_open2() error " + ret + ": Could not open audio codec.");
                }

                avutil.av_dict_free(options);
                if ((this.samples_frame = avutil.av_frame_alloc()) == null) {
                    throw new Exception("av_frame_alloc() error: Could not allocate audio frame.");
                }

                this.samples_ptr = new BytePointer[]{null};
                this.samples_buf = new Buffer[]{null};
            }

            this.started = true;
        } catch (Throwable var20) {
            var3 = var20;
            throw var20;
        } finally {
            if (scope != null) {
                if (var3 != null) {
                    try {
                        scope.close();
                    } catch (Throwable var19) {
                        var3.addSuppressed(var19);
                    }
                } else {
                    scope.close();
                }
            }

        }

    }

    private void initPictureRGB() {
        int width = this.imageWidth > 0 ? this.imageWidth : this.video_c.width();
        int height = this.imageHeight > 0 ? this.imageHeight : this.video_c.height();
        switch (this.imageMode) {
            case COLOR:
            case GRAY:
                if (this.image_ptr != null) {
                    this.image_buf = null;
                    BytePointer[] temp = this.image_ptr;
                    this.image_ptr = null;
                    avutil.av_free(temp[0]);
                }

                int fmt = this.getPixelFormat();
                int align = 32;
                int stride = width;

                int i;
                for(i = 1; i <= align; i += i) {
                    stride = width + (i - 1) & ~(i - 1);
                    avutil.av_image_fill_linesizes(this.picture_rgb.linesize(), fmt, stride);
                    if ((this.picture_rgb.linesize(0) & align - 1) == 0) {
                        break;
                    }
                }

                i = avutil.av_image_get_buffer_size(fmt, stride, height, 1);
                this.image_ptr = new BytePointer[]{(new BytePointer(avutil.av_malloc((long)i))).capacity((long)i)};
                this.image_buf = new Buffer[]{this.image_ptr[0].asBuffer()};
                avutil.av_image_fill_arrays(new PointerPointer(this.picture_rgb), this.picture_rgb.linesize(), this.image_ptr[0], fmt, stride, height, 1);
                this.picture_rgb.format(fmt);
                this.picture_rgb.width(width);
                this.picture_rgb.height(height);
                break;
            case RAW:
                this.image_ptr = new BytePointer[]{null};
                this.image_buf = new Buffer[]{null};
                break;
            default:
                assert false;
        }

    }

    @Override
    public void stop() throws Exception {
        this.release();
    }

    @Override
    public synchronized void trigger() throws Exception {
        if (this.oc != null && !this.oc.isNull()) {
            if (this.pkt.stream_index() != -1) {
                avcodec.av_packet_unref(this.pkt);
                this.pkt.stream_index(-1);
            }

            for(int i = 0; i < this.numBuffers + 1; ++i) {
                if (avformat.av_read_frame(this.oc, this.pkt) < 0) {
                    return;
                }

                avcodec.av_packet_unref(this.pkt);
            }

        } else {
            throw new Exception("Could not trigger: No AVFormatContext. (Has start() been called?)");
        }
    }

    private void processImage() throws Exception {
        this.frame.imageWidth = this.imageWidth > 0 ? this.imageWidth : this.video_c.width();
        this.frame.imageHeight = this.imageHeight > 0 ? this.imageHeight : this.video_c.height();
        this.frame.imageDepth = 8;
        switch (this.imageMode) {
            case COLOR:
            case GRAY:
                if (this.deinterlace) {
                    throw new Exception("Cannot deinterlace: Functionality moved to FFmpegFrameFilter.");
                }

                if (this.frame.imageWidth != this.picture_rgb.width() || this.frame.imageHeight != this.picture_rgb.height()) {
                    this.initPictureRGB();
                }

                this.img_convert_ctx = swscale.sws_getCachedContext(this.img_convert_ctx, this.video_c.width(), this.video_c.height(), this.video_c.pix_fmt(), this.frame.imageWidth, this.frame.imageHeight, this.getPixelFormat(), this.imageScalingFlags != 0 ? this.imageScalingFlags : 2, (SwsFilter)null, (SwsFilter)null, (DoublePointer)null);
                if (this.img_convert_ctx == null) {
                    throw new Exception("sws_getCachedContext() error: Cannot initialize the conversion context.");
                }

                swscale.sws_scale(this.img_convert_ctx, new PointerPointer(this.picture), this.picture.linesize(), 0, this.video_c.height(), new PointerPointer(this.picture_rgb), this.picture_rgb.linesize());
                this.frame.imageStride = this.picture_rgb.linesize(0);
                this.frame.image = this.image_buf;
                this.frame.opaque = this.picture_rgb;
                break;
            case RAW:
                this.frame.imageStride = this.picture.linesize(0);
                BytePointer ptr = this.picture.data(0);
                if (ptr != null && !ptr.equals(this.image_ptr[0])) {
                    this.image_ptr[0] = ptr.capacity((long)(this.frame.imageHeight * this.frame.imageStride));
                    this.image_buf[0] = ptr.asBuffer();
                }

                this.frame.image = this.image_buf;
                this.frame.opaque = this.picture;
                break;
            default:
                assert false;
        }

        this.frame.image[0].limit(this.frame.imageHeight * this.frame.imageStride);
        this.frame.imageChannels = this.frame.imageStride / this.frame.imageWidth;
    }

    private void processSamples() throws Exception {
        int sample_format = this.samples_frame.format();
        int planes = avutil.av_sample_fmt_is_planar(sample_format) != 0 ? this.samples_frame.channels() : 1;
        int data_size = avutil.av_samples_get_buffer_size((IntPointer)null, this.audio_c.channels(), this.samples_frame.nb_samples(), this.audio_c.sample_fmt(), 1) / planes;
        if (this.samples_buf == null || this.samples_buf.length != planes) {
            this.samples_ptr = new BytePointer[planes];
            this.samples_buf = new Buffer[planes];
        }

        this.frame.sampleRate = this.audio_c.sample_rate();
        this.frame.audioChannels = this.audio_c.channels();
        this.frame.samples = this.samples_buf;
        this.frame.opaque = this.samples_frame;
        int sample_size = data_size / avutil.av_get_bytes_per_sample(sample_format);

        int sample_size_in;
        for(sample_size_in = 0; sample_size_in < planes; ++sample_size_in) {
            BytePointer p = this.samples_frame.data(sample_size_in);
            if (!p.equals(this.samples_ptr[sample_size_in]) || this.samples_ptr[sample_size_in].capacity() < (long)data_size) {
                this.samples_ptr[sample_size_in] = p.capacity((long)data_size);
                ByteBuffer b = p.asBuffer();
                switch (sample_format) {
                    case 0:
                    case 5:
                        this.samples_buf[sample_size_in] = b;
                        break;
                    case 1:
                    case 6:
                        this.samples_buf[sample_size_in] = b.asShortBuffer();
                        break;
                    case 2:
                    case 7:
                        this.samples_buf[sample_size_in] = b.asIntBuffer();
                        break;
                    case 3:
                    case 8:
                        this.samples_buf[sample_size_in] = b.asFloatBuffer();
                        break;
                    case 4:
                    case 9:
                        this.samples_buf[sample_size_in] = b.asDoubleBuffer();
                        break;
                    default:
                        assert false;
                }
            }

            this.samples_buf[sample_size_in].position(0).limit(sample_size);
        }

        if (this.audio_c.channels() != this.getAudioChannels() || this.audio_c.sample_fmt() != this.getSampleFormat() || this.audio_c.sample_rate() != this.getSampleRate()) {
            int ret;
            if (this.samples_convert_ctx == null || this.samples_channels != this.getAudioChannels() || this.samples_format != this.getSampleFormat() || this.samples_rate != this.getSampleRate()) {
                this.samples_convert_ctx = swresample.swr_alloc_set_opts(this.samples_convert_ctx, avutil.av_get_default_channel_layout(this.getAudioChannels()), this.getSampleFormat(), this.getSampleRate(), avutil.av_get_default_channel_layout(this.audio_c.channels()), this.audio_c.sample_fmt(), this.audio_c.sample_rate(), 0, (Pointer)null);
                if (this.samples_convert_ctx == null) {
                    throw new Exception("swr_alloc_set_opts() error: Cannot allocate the conversion context.");
                }

                if ((ret = swresample.swr_init(this.samples_convert_ctx)) < 0) {
                    throw new Exception("swr_init() error " + ret + ": Cannot initialize the conversion context.");
                }

                this.samples_channels = this.getAudioChannels();
                this.samples_format = this.getSampleFormat();
                this.samples_rate = this.getSampleRate();
            }

            sample_size_in = this.samples_frame.nb_samples();
            int planes_out = avutil.av_sample_fmt_is_planar(this.samples_format) != 0 ? this.samples_frame.channels() : 1;
            int sample_size_out = swresample.swr_get_out_samples(this.samples_convert_ctx, sample_size_in);
            int sample_bytes_out = avutil.av_get_bytes_per_sample(this.samples_format);
            int buffer_size_out = sample_size_out * sample_bytes_out * (planes_out > 1 ? 1 : this.samples_channels);
            int i;
            if (this.samples_buf_out == null || this.samples_buf.length != planes_out || this.samples_ptr_out[0].capacity() < (long)buffer_size_out) {
                for(i = 0; this.samples_ptr_out != null && i < this.samples_ptr_out.length; ++i) {
                    avutil.av_free(this.samples_ptr_out[i].position(0L));
                }

                this.samples_ptr_out = new BytePointer[planes_out];
                this.samples_buf_out = new Buffer[planes_out];

                for(i = 0; i < planes_out; ++i) {
                    this.samples_ptr_out[i] = (new BytePointer(avutil.av_malloc((long)buffer_size_out))).capacity((long)buffer_size_out);
                    ByteBuffer b = this.samples_ptr_out[i].asBuffer();
                    switch (this.samples_format) {
                        case 0:
                        case 5:
                            this.samples_buf_out[i] = b;
                            break;
                        case 1:
                        case 6:
                            this.samples_buf_out[i] = b.asShortBuffer();
                            break;
                        case 2:
                        case 7:
                            this.samples_buf_out[i] = b.asIntBuffer();
                            break;
                        case 3:
                        case 8:
                            this.samples_buf_out[i] = b.asFloatBuffer();
                            break;
                        case 4:
                        case 9:
                            this.samples_buf_out[i] = b.asDoubleBuffer();
                            break;
                        default:
                            assert false;
                    }
                }
            }

            this.frame.sampleRate = this.samples_rate;
            this.frame.audioChannels = this.samples_channels;
            this.frame.samples = this.samples_buf_out;
            if ((ret = swresample.swr_convert(this.samples_convert_ctx, this.plane_ptr.put(this.samples_ptr_out), sample_size_out, this.plane_ptr2.put(this.samples_ptr), sample_size_in)) < 0) {
                throw new Exception("swr_convert() error " + ret + ": Cannot convert audio samples.");
            }

            for(i = 0; i < planes_out; ++i) {
                this.samples_ptr_out[i].position(0L).limit((long)(ret * (planes_out > 1 ? 1 : this.samples_channels)));
                this.samples_buf_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : this.samples_channels));
            }
        }

    }

    public Frame grab() throws Exception {
        return this.grabFrame(true, true, true, false, true);
    }

    public Frame grabImage() throws Exception {
        return this.grabFrame(false, true, true, false, false);
    }

    public Frame grabSamples() throws Exception {
        return this.grabFrame(true, false, true, false, false);
    }

    public Frame grabKeyFrame() throws Exception {
        return this.grabFrame(false, true, true, true, false);
    }

    public Frame grabFrame(boolean doAudio, boolean doVideo, boolean doProcessing, boolean keyFrames) throws Exception {
        return this.grabFrame(doAudio, doVideo, doProcessing, keyFrames, true);
    }

    public synchronized Frame grabFrame(boolean doAudio, boolean doVideo, boolean doProcessing, boolean keyFrames, boolean doData) throws Exception {
        PointerScope scope = new PointerScope(new Class[0]);
        Throwable var7 = null;

        Frame var10;
        try {
            if (this.oc == null || this.oc.isNull()) {
                throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
            }

            if ((!doVideo || this.video_st == null) && (!doAudio || this.audio_st == null)) {
                Object var30 = null;
                return (Frame)var30;
            }

            if (!this.started) {
                throw new Exception("start() was not called successfully!");
            }

            boolean videoFrameGrabbed = this.frameGrabbed && this.frame.image != null;
            boolean audioFrameGrabbed = this.frameGrabbed && this.frame.samples != null;
            this.frameGrabbed = false;
            this.frame.keyFrame = false;
            this.frame.imageWidth = 0;
            this.frame.imageHeight = 0;
            this.frame.imageDepth = 0;
            this.frame.imageChannels = 0;
            this.frame.imageStride = 0;
            this.frame.image = null;
            this.frame.sampleRate = 0;
            this.frame.audioChannels = 0;
            this.frame.samples = null;
            this.frame.data = null;
            this.frame.opaque = null;
            if (doVideo && videoFrameGrabbed) {
                if (doProcessing) {
                    this.processImage();
                }

                this.frame.keyFrame = this.picture.key_frame() != 0;
                var10 = this.frame;
                return var10;
            }

            if (!doAudio || !audioFrameGrabbed) {
                boolean done = false;
                boolean readPacket = this.pkt.stream_index() == -1;

                while(true) {
                    while(true) {
                        label601:
                        while(!done) {
                            Object var34;
                            if (readPacket) {
                                if (this.pkt.stream_index() != -1) {
                                    avcodec.av_packet_unref(this.pkt);
                                }

                                if (avformat.av_read_frame(this.oc, this.pkt) < 0) {
                                    if (!doVideo || this.video_st == null) {
                                        this.pkt.stream_index(-1);
                                        var34 = null;
                                        return (Frame)var34;
                                    }

                                    this.pkt.stream_index(this.video_st.index());
                                    this.pkt.flags(1);
                                    this.pkt.data((BytePointer)null);
                                    this.pkt.size(0);
                                }
                            }

                            this.frame.streamIndex = this.pkt.stream_index();
                            int ret;
                            long pts;
                            AVRational time_base;
                            if (doVideo && this.video_st != null && this.pkt.stream_index() == this.video_st.index() && (!keyFrames || this.pkt.flags() == 1)) {
                                if (readPacket) {
                                    ret = avcodec.avcodec_send_packet(this.video_c, this.pkt);
                                    if (this.pkt.data() == null && this.pkt.size() == 0) {
                                        this.pkt.stream_index(-1);
                                    }

                                    if (ret != avutil.AVERROR_EAGAIN() && ret != avutil.AVERROR_EOF() && ret < 0) {
                                    }
                                }

                                this.got_frame[0] = 0;

                                while(true) {
                                    do {
                                        if (done) {
                                            continue label601;
                                        }

                                        ret = avcodec.avcodec_receive_frame(this.video_c, this.picture);
                                        if (ret == avutil.AVERROR_EAGAIN() || ret == avutil.AVERROR_EOF()) {
                                            if (this.pkt.data() == null && this.pkt.size() == 0) {
                                                var34 = null;
                                                return (Frame)var34;
                                            }

                                            readPacket = true;
                                            continue label601;
                                        }

                                        if (ret < 0) {
                                            throw new Exception("avcodec_receive_frame() error " + ret + ": Error during video decoding.");
                                        }

                                        this.got_frame[0] = 1;
                                    } while(keyFrames && this.picture.pict_type() != 1);

                                    pts = this.picture.best_effort_timestamp();
                                    time_base = this.video_st.time_base();
                                    this.timestamp = 1000000L * pts * (long)time_base.num() / (long)time_base.den();
                                    this.frameNumber = (int)Math.round((double)this.timestamp * this.getFrameRate() / 1000000.0);
                                    this.frame.image = this.image_buf;
                                    if (doProcessing) {
                                        this.processImage();
                                    }

                                    done = true;
                                    this.frame.timestamp = this.timestamp;
                                    this.frame.keyFrame = this.picture.key_frame() != 0;
                                }
                            } else if (doAudio && this.audio_st != null && this.pkt.stream_index() == this.audio_st.index()) {
                                if (readPacket) {
                                    ret = avcodec.avcodec_send_packet(this.audio_c, this.pkt);
                                    if (ret < 0) {
                                        throw new Exception("avcodec_send_packet() error " + ret + ": Error sending an audio packet for decoding.");
                                    }
                                }

                                for(this.got_frame[0] = 0; !done; this.frame.keyFrame = this.samples_frame.key_frame() != 0) {
                                    ret = avcodec.avcodec_receive_frame(this.audio_c, this.samples_frame);
                                    if (ret == avutil.AVERROR_EAGAIN() || ret == avutil.AVERROR_EOF()) {
                                        readPacket = true;
                                        break;
                                    }

                                    if (ret < 0) {
                                        throw new Exception("avcodec_receive_frame() error " + ret + ": Error during audio decoding.");
                                    }

                                    this.got_frame[0] = 1;
                                    pts = this.samples_frame.best_effort_timestamp();
                                    time_base = this.audio_st.time_base();
                                    this.timestamp = 1000000L * pts * (long)time_base.num() / (long)time_base.den();
                                    this.frame.samples = this.samples_buf;
                                    if (doProcessing) {
                                        this.processSamples();
                                    }

                                    done = true;
                                    this.frame.timestamp = this.timestamp;
                                }
                            } else if (doData) {
                                if (!readPacket) {
                                    readPacket = true;
                                } else {
                                    this.frame.data = this.pkt.data().position(0L).capacity((long)this.pkt.size()).asByteBuffer();
                                    done = true;
                                }
                            }
                        }

                        Frame var33 = this.frame;
                        return var33;
                    }
                }
            }

            if (doProcessing) {
                this.processSamples();
            }

            this.frame.keyFrame = this.samples_frame.key_frame() != 0;
            var10 = this.frame;
        } catch (Throwable var28) {
            var7 = var28;
            throw var28;
        } finally {
            if (scope != null) {
                if (var7 != null) {
                    try {
                        scope.close();
                    } catch (Throwable var27) {
                        var7.addSuppressed(var27);
                    }
                } else {
                    scope.close();
                }
            }

        }

        return var10;
    }

    public synchronized AVPacket grabPacket() throws Exception {
        if (this.oc != null && !this.oc.isNull()) {
            if (!this.started) {
                throw new Exception("start() was not called successfully!");
            } else {
                return avformat.av_read_frame(this.oc, this.pkt) < 0 ? null : this.pkt;
            }
        } else {
            throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
        }
    }

    static {
        try {
            tryLoad();
            FFmpegLockCallback.init();
        } catch (Exception var1) {
        }

        inputStreams = Collections.synchronizedMap(new HashMap());
        readCallback = (ReadCallback)(new ReadCallback()).retainReference();
        seekCallback = (SeekCallback)(new SeekCallback()).retainReference();
    }

    static class SeekCallback extends Seek_Pointer_long_int {
        SeekCallback() {
        }

        @Override
        public long call(Pointer opaque, long offset, int whence) {
            try {
                InputStream is;
                long size;
                long remaining;
                long n;
                is = (InputStream) MyFFmpegFrameGrabber.inputStreams.get(opaque);
                size = 0L;
                label64:
                switch (whence) {
                    case 0:
                        is.reset();
                    case 1:
                        break;
                    case 2:
                        is.reset();

                        while(true) {
                            remaining = is.skip(Long.MAX_VALUE);
                            if (remaining == 0L) {
                                offset += size;
                                is.reset();
                                break label64;
                            }

                            size += remaining;
                        }
                    case 65536:
                        remaining = 0L;

                        while(true) {
                            n = is.skip(Long.MAX_VALUE);
                            if (n == 0L) {
                                is.reset();

                                while(true) {
                                    n = is.skip(Long.MAX_VALUE);
                                    if (n == 0L) {
                                        offset = size - remaining;
                                        is.reset();
                                        break label64;
                                    }

                                    size += n;
                                }
                            }

                            remaining += n;
                        }
                    default:
                        return -1L;
                }

                for(remaining = offset; remaining > 0L; remaining -= n) {
                    n = is.skip(remaining);
                    if (n == 0L) {
                        break;
                    }
                }

                return whence == 65536 ? size : 0L;
            } catch (Throwable var12) {
                System.err.println("Error on InputStream.reset() or skip(): " + var12);
                return -1L;
            }
        }
    }

    static class ReadCallback extends Read_packet_Pointer_BytePointer_int {
        ReadCallback() {
        }

        @Override
        public int call(Pointer opaque, BytePointer buf, int buf_size) {
            try {
                byte[] b = new byte[buf_size];
                InputStream is = (InputStream) MyFFmpegFrameGrabber.inputStreams.get(opaque);
                int size = is.read(b, 0, buf_size);
                if (size < 0) {
                    return 0;
                } else {
                    buf.put(b, 0, size);
                    return size;
                }
            } catch (Throwable var7) {
                System.err.println("Error on InputStream.read(): " + var7);
                return -1;
            }
        }
    }

    public void setThirdOptionsParam(String key, String value) {
        avutil.av_dict_set(thirdOptions, key, value, 0);
    }

    public static class Exception extends FrameGrabber.Exception {
        public Exception(String message) {
            super(message + " (For more details, make sure FFmpegLogCallback.set() has been called.)");
        }

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
