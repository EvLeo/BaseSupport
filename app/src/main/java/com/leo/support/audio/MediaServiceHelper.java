package com.leo.support.audio;

import android.content.Intent;

import com.leo.support.audio.bean.Song;
import com.leo.support.utils.BroadcastUtils;

/**
 * Created by leo on 2017/6/16.
 */

class MediaServiceHelper {

    public void notifyPlayStatusChange(Song song, int status) {
        Intent intent = buildCommonMsgIntent(song, MediaService.MSG_REFRESH_PLAY_STATUS_CHANGE);
        intent.putExtra("status", status);
        BroadcastUtils.sendGlobalBroadcast(intent);
    }


    private Intent buildCommonMsgIntent(Song song, int type) {
        Intent intent = new Intent(MediaService.SERVICE_PLAY_EVENT_ACTION);
        intent.setExtrasClassLoader(getClass().getClassLoader());
        intent.putExtra("song", song);
        intent.putExtra("type", type);
        return intent;
    }
}
