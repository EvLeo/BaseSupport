package com.leo.support.audio;

import android.os.Looper;

import com.leo.support.audio.bean.Song;
import com.leo.support.audio.player.BasePlayer;
import com.leo.support.audio.player.BasePlayer.OnPlayStateChangeListener;
import com.leo.support.audio.player.BasePlayer.OnPlayPositionChangeListener;
import com.leo.support.audio.player.LocalPlayer;
import com.leo.support.log.LogUtil;
import com.leo.support.network.HttpProvider;
import com.leo.support.network.HttpResult;
import com.leo.support.network.listener.CancelableListener;
import com.leo.support.utils.BaseFileUtils;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * done
 * Created by leo on 2017/6/8.
 */

public class MusicPlayer {

    private static final String TAG = "MusicPlayer";

    private static int BLOCK_SIZE = 20 * 1024 * 128;

    private BasePlayer mPlayer;

    private LocalPlayer mLocalPlayer;

    private Song mSong;

    private BufferingThread mBufferingThread;

    private int mPlayState;

    private boolean isFirst = true;

    private long mPlayPosition;
    private long mDuration;

    public MusicPlayer(Looper looper) {
        mLocalPlayer = new LocalPlayer(looper);
    }

    public void playSong(Song song) {
        this.mSong = song;
        resetPlayer();
        mPlayer = getPlayer();

        mPlayer.setOnPlayStateChangeListener(mInnerPlayStateChangeListener);
        mPlayer.setOnPlayPositionChangeListener(mInnerPlayPositionChangeListener);

        if (null != mBufferingThread) {
            mBufferingThread.cancel();
        }

        if (mSong.isOnline()) {
            mBufferingThread = new BufferingThread(song);
            mBufferingThread.start();
        } else {
            mPlayer.setDataSource(mSong);
            mPlayer.play();
        }

        mPlayState = StatusCode.STATUS_BUFFING;
        if (null != mInnerPlayStateChangeListener) {
            mInnerPlayStateChangeListener.onPlayStateChange(mPlayState);
        }

    }

    private BasePlayer getPlayer() {
        return mLocalPlayer;
    }

    public long getPosition() {
        return mPlayPosition;
    }

    public long getDuration() {
        return mDuration;
    }

    private void resetPlayer() {
        if (null != mPlayer) {
            mPlayer.setOnPlayStateChangeListener(null);
            mPlayer.setOnPlayPositionChangeListener(null);
        }
    }

    public void pause() {
        mPlayer.pause();
    }

    public void resume() {
        mPlayer.play();
    }

    public void seekTo(int position) throws Exception {
        mPlayer.seekTo(position);
    }

    public Song getCurrentSong() {
        return mSong;
    }

    private OnPlayStateChangeListener mInnerPlayStateChangeListener = new OnPlayStateChangeListener() {
        @Override
        public void onPlayStateChange(int state) {
            LogUtil.v(TAG, "onPlayStateChange state:" + StatusCode.getStatusLabel(state));
            mPlayState = state;
            if (null != mPlayStateChangeListener) {
                mPlayStateChangeListener.onPlayStateChange(state);
            }
        }
    };

    private OnPlayPositionChangeListener mInnerPlayPositionChangeListener = new OnPlayPositionChangeListener() {
        @Override
        public void onPlayPositionChange(long position, long duration) {
            mPlayPosition = position;
            mDuration = duration;

            if (null != mPlayPositionChangeListener) {
                mPlayPositionChangeListener.onPlayPositionChange(position, duration);
            }

            if (null == mBufferingThread) {
                return;
            }
            LogUtil.v(TAG, "buffered: " + mBufferingThread.getDownloadedPos() + ", total: " + mBufferingThread.getContentLength());
            LogUtil.v(TAG, "position: " + position + ", duration: " + duration);

            if (mBufferingThread.getContentLength() == mBufferingThread.getDownloadedPos()) {
                return;
            }

            long playPosition = position * mBufferingThread.getContentLength() / duration;

            if (mBufferingThread.getDownloadedPos() - playPosition < BLOCK_SIZE) {
                LogUtil.v(TAG, "onBuffering");
                mPlayer.pause();
                mPlayState = StatusCode.STATUS_BUFFING;
                if (null != mInnerPlayStateChangeListener) {
                    mInnerPlayStateChangeListener.onPlayStateChange(StatusCode.STATUS_BUFFING);
                }
            }

        }
    };

    private class BufferingThread extends Thread {
        private Song mSong;

        private long mCurrentPos = 0;

        private long mContentLen = 0;

        BufferingThread(Song song) {
            this.mSong = song;
        }

        public long getDownloadedPos() {
            return mCurrentPos;
        }

        public long getContentLength() {
            return mContentLen;
        }

        public void cancel() {
            cancelableListener.cancel();
        }

        @Override
        public void run() {
            super.run();
            mCurrentPos = 0;
            mContentLen = 0;
            HttpProvider httpProvider = new HttpProvider();
            HttpResult result = httpProvider.doGet(mSong.getUrl(), 10, mCurrentPos, cancelableListener);
            if (!result.isSuccess()) {
                mInnerPlayStateChangeListener.onPlayStateChange(StatusCode.STATUS_ERROR);
            }
        }

        private CancelableListener cancelableListener = new CancelableListener() {
            private RandomAccessFile mRandomStream;

            @Override
            public boolean onStart(long startPos, long contentLength) {
                mCurrentPos = startPos;
                mContentLen = contentLength;
                try {
                    if (mSong.getLocalFile().exists()) {
                        mSong.getLocalFile().delete();
                    }
                    BaseFileUtils.createEmptyFile(mSong.getLocalFile().getAbsolutePath(), contentLength);
                    mRandomStream = new RandomAccessFile(mSong.getLocalFile(), "rw");
                    mRandomStream.seek(startPos);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                isFirst = true;
                return true;
            }

            @Override
            public boolean onAdvance(byte[] buffer, int offset, int len) {
                if (null != mRandomStream) {
                    try {
                        mRandomStream.write(buffer, offset, len);
                        mCurrentPos += len;
                        //检查是否应该还原
                        checkBuffering();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean onComplete() {
                return true;
            }

            @Override
            public void onError(int statusCode) {
            }

            @Override
            public boolean onReady(String url) {
                return true;
            }

            @Override
            public boolean onRelease() {
                try {
                    if (null != mRandomStream) {
                        mRandomStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        };
    }

    private void checkBuffering() {
        if (mPlayState == StatusCode.STATUS_BUFFING) {
            if (mBufferingThread.getContentLength() == mBufferingThread.getDownloadedPos()) {
                if (isFirst) {
                    mPlayer.setDataSource(mSong);
                    isFirst = false;
                }
                LogUtil.v(TAG, "finish downloaded");
                mPlayer.play();
            } else {
                long playPosition = 0;
                if (mDuration > 0) {
                    playPosition = mPlayPosition * mBufferingThread.getContentLength() / mDuration;
                }
                if (mBufferingThread.getDownloadedPos() - playPosition >= BLOCK_SIZE) {
                    if (isFirst) {
                        mPlayer.setDataSource(mSong);
                        isFirst = false;
                    }
                    LogUtil.v(TAG, " first start play");
                    mPlayer.play();
                }
            }
        }
    }

    private OnPlayStateChangeListener mPlayStateChangeListener;
    private OnPlayPositionChangeListener mPlayPositionChangeListener;

    public void setOnPlayStateChangeListener(OnPlayStateChangeListener listener) {
        this.mPlayStateChangeListener = listener;
    }

    public void setOnPlayPositionChangeListener(OnPlayPositionChangeListener listener) {
        this.mPlayPositionChangeListener = listener;
    }

}
