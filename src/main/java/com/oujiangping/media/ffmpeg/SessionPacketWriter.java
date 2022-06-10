package com.oujiangping.media.ffmpeg;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/6/10 14:48
 */
public class SessionPacketWriter implements PacketWriter {
    private Session session;

    public SessionPacketWriter(Session session) {
        this.session = session;
    }

    @Override
    public void write(byte[] buffer, int len) throws IOException {
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(buffer, 0, len));
    }
}
