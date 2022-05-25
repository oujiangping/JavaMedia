package com.oujiangping.media.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @RequestMapping("flv.flv")
    public void flv(HttpServletResponse response) {
        try {
            File file = new File("flv_h264.flv");
            log.info(file.getPath());
            String filename = file.getName();
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            log.info("文件后缀名：" + ext);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("video/x-flv");
            response.setHeader("Access-Control-Allow-Origin", "*");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
