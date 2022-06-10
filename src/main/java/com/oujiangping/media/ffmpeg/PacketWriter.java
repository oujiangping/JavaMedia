package com.oujiangping.media.ffmpeg;

import java.io.IOException;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/6/10 14:46
 */
public interface PacketWriter {
    /**
     * 写数据
     * @param buffer
     * @param len
     */
    void write(byte []buffer, int len) throws IOException;
}
