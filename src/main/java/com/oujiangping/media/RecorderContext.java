package com.oujiangping.media;

import lombok.Data;
import org.bytedeco.javacv.FFmpegFrameRecorder;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/18 10:01
 */
@Data
public class RecorderContext {
    /**
     * 输出文件地址
     */
    private String outputPath;

    /**
     * 记录器
     */
    private FFmpegFrameRecorder recorder;
}
