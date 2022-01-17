package com.oujiangping.media;

import java.awt.*;

/**
 * 功能描述：<>
 *
 * @author oujiangping
 * @create 2022/1/17 16:09
 */
public interface PlayerChannelInterface {
    /**
     * 当帧到达的时候
     * @param: [frame]
     * @return: void
     * @author: oujiangping
     * @date: 2022/1/17 16:10
     */
    void onFrame(Frame frame);
}
