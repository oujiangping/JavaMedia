import com.oujiangping.media.MediaApplication;
import com.oujiangping.media.MyFFmpegFrameRecorder;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 10:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediaApplication.class)
@Slf4j
public class RecorderTest {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd__hhmmSSS");

    private static final int RECORD_LENGTH = 5000;

    private static final boolean AUDIO_ENABLED = true;

    @Test
    public void  record() throws FrameGrabber.Exception, FrameRecorder.Exception {

        String inputFile = "https://devstreaming-cdn.apple.com/videos/wwdc/2019/502gzyuhh8p2r8g8/502/0960/0960.m3u8";

        // Decodes-encodes
        String outputFile = "C:\\Users\\oujiangping\\Downloads\\test1.ts";
        frameRecord(inputFile, outputFile);

        // copies codec (no need to re-encode)
        //outputFile = "C:\\Users\\oujiangping\\Downloads\\202111181951.avi";
        //packetRecord(inputFile, outputFile);

    }

    public static void frameRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        MyFFmpegFrameRecorder recorder = new MyFFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);

        grabber.start();
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

    public static void packetRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, audioChannel);

        grabber.start();
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
}
