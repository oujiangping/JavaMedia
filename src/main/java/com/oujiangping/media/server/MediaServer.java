package com.oujiangping.media.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.nio.ByteBuffer;

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
    public void onOpen(Session session) {
        Thread mediaThread = new Thread(new MediaRunnable(session));
        mediaThread.start();
    }

    public class MediaRunnable implements Runnable {
        private Session session;

        public MediaRunnable(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            try {
                File file = new File("flv_h264.flv");
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStream fis = new BufferedInputStream(fileInputStream);
                byte[] buffer = new byte[2048];
                int len = 0;
                while ((len = fis.read(buffer)) > 0) {
                    log.info("write data {}", len);
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(buffer, 0, len));
                }
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
