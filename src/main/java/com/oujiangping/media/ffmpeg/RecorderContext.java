package com.oujiangping.media.ffmpeg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 10:01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecorderContext {
    /**
     * 输出文件地址
     */
    private String outputPath;

    /**
     * 记录器
     */
    private CommonFFmpegFrameRecorder recorder;

    /**
     * 视频宽度
     */
    private Integer width;

    /**
     * 视频高度
     */
    private Integer height;

    /**
     * 视频比特率
     **/
    private Integer videoBitrate;

    /**
     *视频帧率
     **/
    private Double frameRate;

    /**
     * 是否
     */
    private Integer audioChannel;

    /**
     * 记录器回调接口
     */
    private RecorderChannelCallBack recorderChannelCallBack;
}
