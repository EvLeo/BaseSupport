package com.leo.support.audio.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * MP3解码器
 * Created by leo on 2017/6/5.
 */

public class NativeMP3Decoder implements Decoder {

    private static String[] mp3_formats = {"mp3"};
    private int handle = -1;
    private FloatBuffer floatBuffer;

    static {
        System.loadLibrary("mp3-codec");
    }

    public native int readSamples(int handle, FloatBuffer buffer, int numSamples);

    public native int readSamples(int handle, ShortBuffer buffer, int numSamples);

    public native int readSamples(int handle, short[] buffer, int numSamples);

    /**
     * 解码并读取16-bit PCM采样数据，返回读取到的实际样本数量
     *
     * @param samples The number of read samples.
     */
    @Override
    public int readSamples(float[] samples) {
        if (floatBuffer == null || floatBuffer.capacity() != samples.length) {
            ByteBuffer byteBuffer = ByteBuffer
                    .allocateDirect(samples.length * Float.SIZE / 8);
            byteBuffer.order(ByteOrder.nativeOrder());
            floatBuffer = byteBuffer.asFloatBuffer();
        }

        int readSample = readSamples(handle, floatBuffer, samples.length);
        if (readSample == 0) {
            closeFile(handle);
            return 0;
        }
        floatBuffer.position(0);
        floatBuffer.get(samples);
        return samples.length;
    }

    /**
     * 解码并读取16-bit PCM采样数据，返回读取到的实际样本数量
     *
     * @param samples The number of read samples.
     */
    @Override
    public int readSamples(short[] samples) {
        if (handle != -1)
            return readSamples(handle, samples, samples.length);
        return 0;
    }

    private native void closeFile(int handle);

    /**
     * 销毁解码器并释放本地相关资源
     */
    @Override
    public void release() {
        if (handle != -1) {
            closeFile(handle);
            handle = -1;
        }
    }

    /**
     * 返回解码器是否释放
     */
    @Override
    public boolean isReleased() {
        return handle == -1;
    }

    private native int downsampling(int handle, String file);

    public int downSampling(String outputWaveFile) {
        if (handle != -1)
            return downsampling(handle, outputWaveFile);
        else
            return 0;
    }

    public int getHandle() {
        return handle;
    }

    private native int openFile(String file);

    /**
     * 读取媒体文件并申请相关解码资源
     * @param file 待解码的媒体文件路径
     * @return 返回操作结果，0表示成功，负数表示失败
     */
    @Override
    public int load(String file) {
        int handleTmp = openFile(file);
        if (handleTmp < 0)
            handle = -1;
        else
            handle = handleTmp;
        return handleTmp;
    }

    private native int seekTo(int handle, int msec);

    /**
     * 定位到特定时间段，开始解码
     * @param ms 指定的时间，单位毫秒
     */
    @Override
    public void seekTo(int ms) {
        if (handle != -1)
            seekTo(handle, ms);
    }

    private native int getChannelNum(int handle);

    /**
     * 获取解码器通道数量，不一定与原媒体包含的通道相同
     * @return 返回通道数量
     */
    @Override
    public int getChannelNum() {
        if (handle != -1)
            return getChannelNum(handle);
        else
            return 0;
    }

    private native int getBitrate(int handle);

    /**
     * 获取媒体资源的码率，单位Kbps,如128Kbps
     * @return 返回资源的码率
     */
    @Override
    public int getBitrate() {
        return getBitrate(handle);
    }

    private native int getSamplerate(int handle);

    /**
     * 获取媒体资源的采样率，单位HZ, 如44100HZ
     * @return 返回资源的采样率
     */
    @Override
    public int getSampleRate() {
        return getSamplerate(handle);
    }

    private native int getDuration(int handle);

    /**
     * 获取媒体资源的播放时长，单位秒
     * @return 返回资源的播放时长
     */
    @Override
    public int getDuration() {
        if (handle != -1)
            return getDuration(handle);
        else
            return 0;
    }

    private native int getCurrentPosition(int handle);

    /**
     * 获取媒体资源当前的解码索引位置，单位毫秒
     * @return 返回资源当前的解码索引位置
     */
    @Override
    public int getCurrentPosition() {
        if (handle != -1)
            return getCurrentPosition(handle);
        else
            return 0;
    }

    private native int getSamplePerFrame(int handle);

    /**
     * 获取媒体资源每帧的采样数
     * @return 返回资源每帧的采样数
     */
    @Override
    public int getSamplePerFrame() {
        return getSamplePerFrame(handle);
    }

    private native int isReadFinished(int handle);

    /**
     * 判断是否解码结束
     * @return 解码是否成功
     */
    @Override
    public boolean isFinished() {
        if (isReleased()) {
            return true;
        }
        if (isReadFinished(handle) == 1) {
            return true;
        }
        return getCurrentPosition() / 1000 == getDuration();
    }

    /**
     * 获取该解码器所支持的媒体格式，通常指文件后缀，如"mp3"等
     * @return 返回解码器所支持的媒体格式
     */
    @Override
    public String[] getFormats() {
        return mp3_formats;
    }
}
