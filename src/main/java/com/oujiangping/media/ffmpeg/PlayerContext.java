package com.oujiangping.media.ffmpeg;

import lombok.Data;
import org.bytedeco.javacv.FFmpegFrameGrabber;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/17 15:58
 */
@Data
public class PlayerContext {
    /**
     * grabber对象
     */
    private FFmpegFrameGrabber grabber;

    /**
     * 源地址
     */
    private String sourceUrl;

    /**
     * 回调
     */
    private PlayerChannelCallBack callBack;
}
