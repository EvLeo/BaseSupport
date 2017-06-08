package com.leo.support.audio;

import android.os.Looper;

import com.leo.support.audio.bean.Song;
import com.leo.support.audio.player.BasePlayer;
import com.leo.support.audio.player.BasePlayer.OnPlayStateChangeListener;
import com.leo.support.audio.player.BasePlayer.OnPlayPositionChangeListener;
import com.leo.support.audio.player.LocalPlayer;
import com.leo.support.log.LogUtil;

/**
 * Created by leo on 2017/6/8.
 */

public class MusicPlayer {

    private static final String TAG = "MusicPlayer";

    private static int BLOCK_SIZE = 20 * 1024 * 128;

    private BasePlayer mPlayer;

    private LocalPlayer mLocalPlayer;

    private Song mSong;

    private int mPlayState;

    public MusicPlayer(Looper looper) {
        mLocalPlayer = new LocalPlayer(looper);
    }

    public void playSong(Song song) {
        this.mSong = song;
        resetPlayer();
        mPlayer = getPlayer();

        mPlayer.setOnPlayStateChangeListener(mInnerPlayStateChangeListener);
        mPlayer.setOnPlayPositionChangeListener(mInnerPlayPositionChangeListener);




    }

    private BasePlayer getPlayer() {
        return mLocalPlayer;
    }

    private void resetPlayer() {
        if (null != mPlayer) {
            mPlayer.setOnPlayStateChangeListener(null);
            mPlayer.setOnPlayPositionChangeListener(null);
        }
    }

    private OnPlayStateChangeListener mInnerPlayStateChangeListener = new OnPlayStateChangeListener() {
        @Override
        public void onPlayStateChange(int state) {
            LogUtil.v(TAG, "onPlayStateChange state:" + StatusCode.getStatusLabel(state));
            mPlayState = state;
        }
    };

    private OnPlayPositionChangeListener mInnerPlayPositionChangeListener = new OnPlayPositionChangeListener() {
        @Override
        public void onPlayPositionChange(long position, long duration) {

        }
    };

    private class BufferingThread extends Thread {
        private Song song;

        private long mCurrentPos;

        private long mConentLen = 0;
    }
}
