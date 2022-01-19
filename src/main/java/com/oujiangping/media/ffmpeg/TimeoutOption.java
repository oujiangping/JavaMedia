package com.oujiangping.media.ffmpeg;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/17 16:48
 */
public enum TimeoutOption {
    /**
     * Depends on protocol (FTP, HTTP, RTMP, SMB, SSH, TCP, UDP, or UNIX).
     *
     * http://ffmpeg.org/ffmpeg-all.html
     */
    TIMEOUT,
    /**
     * Protocols
     *
     * Maximum time to wait for (network) read/write operations to complete,
     * in microseconds.
     *
     * http://ffmpeg.org/ffmpeg-all.html#Protocols
     */
    RW_TIMEOUT,
    /**
     * Protocols -> RTSP
     *
     * Set socket TCP I/O timeout in microseconds.
     *
     * http://ffmpeg.org/ffmpeg-all.html#rtsp
     */
    STIMEOUT,

    LISTEN_TIMEOUT;

    public String getKey() {
        return toString().toLowerCase();
    }

}
