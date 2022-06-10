package com.oujiangping.media.controller;

import com.oujiangping.media.ffmpeg.MediaRecord;
import com.oujiangping.media.ffmpeg.OutputStreamPacketWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author oujiangping
 * @version 1.0
 * @date 5/25/22 9:04 PM
 */
@RequestMapping("/media")
@Slf4j
@RestController
public class MediaController {
    @RequestMapping("play.flv")
    public void flv(@RequestParam("url") String url,HttpServletResponse response) {
        try {
            response.reset();
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("video/x-flv");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Transfer-Encoding", "chunked");

            MediaRecord.record(new OutputStreamPacketWriter(outputStream), url);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
