import com.oujiangping.media.MediaApplication;
import com.oujiangping.media.ffmpeg.MediaAgent;
import com.oujiangping.media.ffmpeg.MediaAgentCallBack;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.FrameRecorder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author oujiangping
 * @version 1.0
 * @date 1/18/22 8:23 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediaApplication.class)
@Slf4j
public class MediaAgentTest {
    @Test
    public void mediaAgentTest() throws FFmpegFrameGrabber.Exception, FrameRecorder.Exception, InterruptedException {
        FFmpegLogCallback.set();
        MediaAgent mediaAgent = new MediaAgent(/*"rtmp://ns8.indexforce.com/home/mystream"*/"http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8", "rtmp://127.0.0.1/live/test"/*"mediaOut/test.ts"*/, new MediaAgentCallBack() {
            @Override
            public void onPacketError(String msg) {
                System.out.println("onPacketError" + msg);
            }

            @Override
            public void onPlayerStart() {
                log.info("onPlayerStart");
            }

            @Override
            public void onPlayerPlay() {
                log.info("onPlayerPlay");
            }

            @Override
            public void onPlayerStop() {
                log.info("onPlayerStop");
            }

            @Override
            public void onRecordStart() {
                log.info("onRecordStart");
            }

            @Override
            public void onRecordStop() {
                log.info("onRecordStop");
            }
        });
        mediaAgent.start();
        Thread mediaAgentThread = new Thread(mediaAgent);
        mediaAgentThread.start();

        MediaAgent mediaAgent1 = new MediaAgent(/*"rtmp://ns8.indexforce.com/home/mystream"*/"http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8", "rtmp://127.0.0.1/live/test1"/*"mediaOut/test.ts"*/, new MediaAgentCallBack() {
            @Override
            public void onPacketError(String msg) {
                System.out.println("onPacketError" + msg);
            }

            @Override
            public void onPlayerStart() {
                log.info("onPlayerStart");
            }

            @Override
            public void onPlayerPlay() {
                log.info("onPlayerPlay");
            }

            @Override
            public void onPlayerStop() {
                log.info("onPlayerStop");
            }

            @Override
            public void onRecordStart() {
                log.info("onRecordStart");
            }

            @Override
            public void onRecordStop() {
                log.info("onRecordStop");
            }
        });
        mediaAgent1.start();
        Thread mediaAgentThread1 = new Thread(mediaAgent1);
        mediaAgentThread1.start();
        Thread.sleep(50000);
        mediaAgent.stop();
        mediaAgent1.stop();
        mediaAgentThread.join();
        mediaAgentThread1.join();
    }
}
