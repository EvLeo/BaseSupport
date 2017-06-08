package com.leo.support.audio.player;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.leo.support.audio.StatusCode;
import com.leo.support.audio.bean.Song;


/**
 * done
 * 媒体播放器
 * Created by leo on 2017/6/7.
 */

public abstract class BasePlayer {

    private Song mSong;

    public void setDataSource(Song song) {
        this.mSong = song;
    }

    public Song getDataSource() {
        return mSong;
    }

    public abstract void play();

    public abstract void pause();

    public abstract void stop();

    public abstract void release();

    public abstract int getDuration();

    public abstract int getCurrentPosition();

    public abstract void seekTo(int position) throws Exception;

    public void reset() {
        mSong = null;
    }

    private int mState = StatusCode.STATUS_UN_INIT;

    private Handler mLooperHandler;

    public BasePlayer(Looper looper) {
        mLooperHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageImpl(msg);
            }
        };
    }

    public void handleMessageImpl(Message msg) {
    }

    public Handler getLooperHandler() {
        return mLooperHandler;
    }

    public boolean isPaused() {
        return mState == StatusCode.STATUS_PAUSE;
    }

    public boolean isPlaying() {
        return mState == StatusCode.STATUS_PLAYING;
    }

    public void setState(int state) {
        this.mState = state;
        if (null != mOnPlayStateChangeListener) {
            mOnPlayStateChangeListener.onPlayStateChange(mState);
        }
    }

    public void onPlayPositionChange(int positon, int duration) {
        if (null != mOnPlayPositionChangeListener) {
            mOnPlayPositionChangeListener.onPlayPositionChange(positon, duration);
        }
    }

    private OnPlayPositionChangeListener mOnPlayPositionChangeListener;

    public void setOnPlayPositionChangeListener(OnPlayPositionChangeListener listener) {
        mOnPlayPositionChangeListener = listener;
    }

    public static interface OnPlayPositionChangeListener {
        public abstract void onPlayPositionChange(long position, long duration);
    }

    private OnPlayStateChangeListener mOnPlayStateChangeListener;

    public void setOnPlayStateChangeListener(OnPlayStateChangeListener listener) {
        mOnPlayStateChangeListener = listener;
    }

    public static interface OnPlayStateChangeListener {
        public abstract void onPlayStateChange(int state);
    }


}
