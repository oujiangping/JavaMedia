import com.oujiangping.media.MediaApplication;
import com.oujiangping.media.ffmpeg.MediaAgent;
import com.oujiangping.media.ffmpeg.MediaAgentCallBack;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
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
    public void mediaAgentTest() throws FFmpegFrameGrabber.Exception, FrameRecorder.Exception {
        MediaAgent mediaAgent = new MediaAgent("http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8", "/Users/oujiangping/Downloads/mediaOut/test.flv", new MediaAgentCallBack() {
            @Override
            public void onPacketError(String msg) {
                System.out.println("fuck onPacketError" + msg);
            }

            @Override
            public void onPlayerStart() {
                System.out.println("fuck onPlayerStart");
            }

            @Override
            public void onPlayerStop() {
                System.out.println("fuck onPlayerStop");
            }
        });
        mediaAgent.start();
        mediaAgent.play();
    }
}
