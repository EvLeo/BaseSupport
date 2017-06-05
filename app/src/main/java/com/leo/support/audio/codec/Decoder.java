package com.leo.support.audio.codec;

/**
 * 解码器接口
 * Created by leo on 2017/6/5.
 */

public interface Decoder {

    int readSamples(float[] samples);

    int readSamples(short[] samples);

    void release();

    boolean isReleased();

    int load(String file);

    void seekTo(int ms);

    int getChannelNum();

    int getBitrate();

    int getSampleRate();

    int getDuration();

    int getCurrentPosition();

    int getSamplePerFrame();

    boolean isFinished();

    String[] getFormats();
}
