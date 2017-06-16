package com.leo.support.audio;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.leo.support.audio.bean.Song;
import com.leo.support.audio.player.BasePlayer;
import com.leo.support.log.LogUtil;

import java.security.PublicKey;

/**
 * 媒体播放服务
 * Created by leo on 2017/6/5.
 */

public class MediaService extends Service {

    private static final String TAG = "MediaService";

    public static final int CMD_PLAY = 0;
    public static final int CMD_RESUME = 1;
    public static final int CMD_PAUSE = 2;
    public static final int CMD_SEEK = 3;
    public static final int CMD_REQUEST_POSITION = 4;

    private static final int MSG_REFRESH_START_CODE = 100;
    public static final int MSG_REFRESH_PLAY_STATUS_CHANGE = 103;

    public static final String SERVICE_PLAY_EVENT_ACTION = "com.leo.music.player_service_event";

    private MusicPlayer mMusicPlayer;
    private Messenger mClientMessenger;
    private HandlerThread mIOHandlerThread = null;
    private MediaServiceHelper mPlayServiceHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (null == mClientMessenger) {
            mClientMessenger = new Messenger(new Handler(mIOHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    handleClientMessage(msg);
                }
            });
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIOHandlerThread = new HandlerThread("MediaPlayer");
        mIOHandlerThread.start();

        mPlayServiceHelper = new MediaServiceHelper();

        mMusicPlayer = new MusicPlayer(mIOHandlerThread.getLooper());
        mMusicPlayer.setOnPlayStateChangeListener(mPlayStateChangeListener);
    }

    private void handleClientMessage(Message msg) {
        int what = msg.what;
        LogUtil.v(TAG, "player cmd: " + what);
        switch (what) {
            case CMD_PLAY:
                msg.getData().setClassLoader(getClassLoader());
                Song song = (Song) msg.getData().getSerializable("song");
                playImpl(song);
                break;
            case CMD_RESUME:
                resumeImpl();
                break;
            case CMD_PAUSE:
                pauseImpl();
                break;
            case CMD_SEEK:
                if (null != mMusicPlayer) {
                    try {
                        mMusicPlayer.seekTo(msg.arg1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CMD_REQUEST_POSITION:
                try {
                    Message message = new Message();
                    message.arg1 = (int) mMusicPlayer.getPosition();
                    message.arg2 = (int) mMusicPlayer.getDuration();
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void pauseImpl() {
        if (null != mMusicPlayer) {
            mMusicPlayer.pause();
        }
    }

    private void resumeImpl() {
        if (null != mMusicPlayer) {
            mMusicPlayer.resume();
        }

    }

    private void playImpl(Song song) {
        if (null != mMusicPlayer) {
            mMusicPlayer.playSong(song);
        }
    }

    private BasePlayer.OnPlayStateChangeListener mPlayStateChangeListener = new BasePlayer.OnPlayStateChangeListener() {
        @Override
        public void onPlayStateChange(int state) {
            LogUtil.v(TAG, "player status: " + StatusCode.getStatusLabel(state));
            if (null != mPlayServiceHelper) {
                mPlayServiceHelper.notifyPlayStatusChange(mMusicPlayer.getCurrentSong(), state);
            }
        }
    };
}
