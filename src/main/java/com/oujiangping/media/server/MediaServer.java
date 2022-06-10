package com.oujiangping.media.server;

import com.oujiangping.media.ffmpeg.MediaRecord;
import com.oujiangping.media.ffmpeg.PacketWriter;
import com.oujiangping.media.ffmpeg.SessionPacketWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author oujiangping
 * @version 1.0
 * @date 5/25/22 9:49 PM
 */
@Component
@Slf4j
@ServerEndpoint("/media/play.flv")
public class MediaServer {
    @OnOpen
    public void onOpen(Session session) throws IOException {
        Map<String, List<String>> paramMap = session.getRequestParameterMap();
        String url = null;
        if(paramMap != null && paramMap.get("url").size() > 0) {
            url = paramMap.get("url").get(0);
        } else {
            log.error("url can't be null null");
            session.close();
            return;
        }
        log.info("onOpen");
        Thread mediaThread = new Thread(new MediaRunnable(new SessionPacketWriter(session), url));
        mediaThread.start();
    }

    public class MediaRunnable implements Runnable {
        private PacketWriter packetWriter;

        private String url;

        public MediaRunnable(PacketWriter session, String url) {
            this.packetWriter = session;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                MediaRecord.record(packetWriter, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void OnClose() {

    }

    @OnMessage
    public void OnMessage(String message, Session session) {

    }


}
