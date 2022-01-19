package com.oujiangping.media.ffmpeg;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.util.StringUtils;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/19 11:05
 */
public class FormatHelper {
    final static String RTMP_PREFIX = "rtmp://";
    public static void setPlayerTimeoutOption(FFmpegFrameGrabber grabber, String url, Integer timeout) {
    }

    public static String extGuessFormat(String url) {
        if(!StringUtils.isEmpty(url)) {
            if(url.startsWith(RTMP_PREFIX)) {
                return "flv";
            }
        }
        return null;
    }
}
