import com.oujiangping.media.MediaApplication;
import com.oujiangping.media.ffmpeg.Player;
import com.oujiangping.media.ffmpeg.PlayerChannelCallBack;
import com.oujiangping.media.ffmpeg.PlayerContext;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
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
        PlayerContext playerContext = new PlayerContext();
        playerContext.setSourceUrl("http://39.134.66.66/PLTV/88888888/224/3221225668/index.m3u8");
        Player player = new Player(playerContext, new PlayerChannelCallBack() {
            @Override
            public void onFrame(Frame frame) {
                System.out.println("onFrame");
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
        player.play();
    }
}
