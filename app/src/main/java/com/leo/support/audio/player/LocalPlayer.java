package com.leo.support.audio.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Looper;
import android.os.Message;

import com.leo.support.audio.StatusCode;
import com.leo.support.audio.bean.Song;
import com.leo.support.log.LogUtil;

/**
 * 默认音乐播放器
 * Created by leo on 2017/6/7.
 */

public class LocalPlayer extends BasePlayer {

    private static final String TAG = "DefaultPlayer";

    public static final int MSG_LOOP = 1;

    private final MediaPlayer mMediaPlayer;

    private volatile int mCurrentPos = 0;

    public LocalPlayer(Looper looper) {
        super(looper);
        setState(StatusCode.STATUS_UN_INIT);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setState(StatusCode.STATUS_COMPLETED);
            }
        };
        mMediaPlayer.setOnCompletionListener(completionListener);
        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                setState(StatusCode.STATUS_ERROR);
                return false;
            }
        };
        mMediaPlayer.setOnErrorListener(errorListener);

        setState(StatusCode.STATUS_COMPLETED);
        getLooperHandler().sendEmptyMessage(MSG_LOOP);
    }

    @Override
    public void setDataSource(Song song) {
        reset();
        super.setDataSource(song);
        try {
            mMediaPlayer.setDataSource(song.getLocalFile().getAbsolutePath());
            mMediaPlayer.prepare();
            setState(StatusCode.STATUS_PREPARED);
        } catch (Exception e) {
            e.printStackTrace();
            setState(StatusCode.STATUS_ERROR);
        }
    }

    @Override
    public void play() {
        mMediaPlayer.start();
        setState(StatusCode.STATUS_PLAYING);
    }

    @Override
    public void pause() {
        LogUtil.v(TAG, "pause");
        if (isPlaying()) {
            try {
                mMediaPlayer.pause();
                setState(StatusCode.STATUS_PAUSE);
            } catch (IllegalStateException e) {
                LogUtil.e(TAG, e);
                setState(StatusCode.STATUS_ERROR);
            }
        }
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
        setState(StatusCode.STATUS_STOP);
    }

    @Override
    public void reset() {
        super.reset();
        mMediaPlayer.reset();
        setState(StatusCode.STATUS_HAS_INIT);
    }

    @Override
    public void release() {
        mMediaPlayer.release();
        setState(StatusCode.STATUS_RELEASE);
    }

    @Override
    public int getDuration() {
        if (isPlaying() || isPaused()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return mCurrentPos;
    }

    @Override
    public void seekTo(int position) throws Exception {
        if (null != mMediaPlayer) {
            try {
                mMediaPlayer.seekTo(position);
            } catch (IllegalStateException e) {
                LogUtil.e(TAG, e);
                throw e;
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void handleMessageImpl(Message msg) {
        super.handleMessageImpl(msg);
        if (MSG_LOOP == msg.what) {
            if (getDataSource() != null && (isPaused() || isPlaying())) {
                mCurrentPos = mMediaPlayer.getCurrentPosition();
                onPlayPositionChange(mCurrentPos, getDuration());
            }
            getLooperHandler().sendEmptyMessageDelayed(MSG_LOOP, 1000);
        }
    }
}
