package com.oujiangping.media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 媒体服务器应用引导
 *
 * @author oujiangping
 * @create 2022/1/17 16:22
 */
@SpringBootApplication
public class MediaApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MediaApplication.class, args);
    }
}