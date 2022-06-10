import com.oujiangping.media.ffmpeg.FFmpegMediaRecorder;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.*;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/5/27 18:17
 */
@Slf4j
public class FFmpegFrameRecorderTest {
    private static final boolean AUDIO_ENABLED = true;

    public static void main(String[] args) throws FrameGrabber.Exception, FrameRecorder.Exception {
        //String inputFile = "http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8";
        String inputFile = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
        packetRecord(inputFile, "test.flv");
        //frameRecord(inputFile, "test.flv");
    }

    public static void packetRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);

        grabber.start();

        FFmpegMediaRecorder recorder = new FFmpegMediaRecorder(null, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("flv");
        recorder.setPixelFormat(grabber.getPixelFormat());
        recorder.start(grabber.getFormatContext());

        AVPacket packet;
        long t1 = System.currentTimeMillis();
        while ((packet = grabber.grabPacket()) != null) {
            recorder.recordPacket(packet);
        }

        recorder.stop();
        grabber.stop();

    }

    public static void frameRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.setPixelFormat(0);
        grabber.start();

        FFmpegMediaRecorder recorder = new FFmpegMediaRecorder(null, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        grabber.setPixelFormat(grabber.getPixelFormat());
        recorder.start(grabber.getFormatContext());

        Frame frame;
        while ((frame = grabber.grabFrame(AUDIO_ENABLED, true, true, false)) != null) {
            recorder.record(frame);
        }
        recorder.stop();
        grabber.stop();
    }
}
