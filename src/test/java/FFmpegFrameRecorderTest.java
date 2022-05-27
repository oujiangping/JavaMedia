import com.oujiangping.media.MediaApplication;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/5/27 18:17
 */
public class FFmpegFrameRecorderTest {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd__hhmmSSS");

    private static final int RECORD_LENGTH = 5000;

    private static final boolean AUDIO_ENABLED = true;

    public static void main(String[] args) throws FrameGrabber.Exception, FrameRecorder.Exception {
        String inputFile = "http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8";
        packetRecord(inputFile, "test.ts");
        //frameRecord(inputFile, "test.flv");
    }

    public static void packetRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);

        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(), audioChannel);
        recorder.start(grabber.getFormatContext());

        AVPacket packet;
        long t1 = System.currentTimeMillis();
        while ((packet = grabber.grabPacket()) != null) {
            recorder.recordPacket(packet);
            if ((System.currentTimeMillis() - t1) > RECORD_LENGTH) {
                break;
            }
        }

        recorder.stop();
        grabber.stop();

    }

    public static void frameRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);

        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(), audioChannel);
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
