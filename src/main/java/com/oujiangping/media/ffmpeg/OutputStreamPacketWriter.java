package com.oujiangping.media.ffmpeg;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/6/10 15:01
 */
public class OutputStreamPacketWriter implements PacketWriter {
    private OutputStream outputStream;

    public OutputStreamPacketWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(byte[] buffer, int len) throws IOException {
        outputStream.write(buffer, 0, len);
        outputStream.flush();
    }
}
