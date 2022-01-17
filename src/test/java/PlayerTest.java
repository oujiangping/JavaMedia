import com.oujiangping.media.MediaApplication;
import com.oujiangping.media.Player;
import com.oujiangping.media.PlayerChannelCallBack;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/17 16:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediaApplication.class)
@Slf4j
public class PlayerTest {
    @Test
    public void testPlayer() throws FFmpegFrameGrabber.Exception {
        Player player = new Player("https://devstreaming-cdn.apple.com/videos/wwdc/2019/502gzyuhh8p2r8g8/502/0960/0960.m3u8", new PlayerChannelCallBack() {
            @Override
            public void onPacket(AVPacket packet) {
                System.out.println("onPacket");
            }

            @Override
            public void onStart() {
                System.out.println("onStart");
            }

            @Override
            public void onStop() {
                System.out.println("onStop");
            }
        });
        player.start();
    }
}
