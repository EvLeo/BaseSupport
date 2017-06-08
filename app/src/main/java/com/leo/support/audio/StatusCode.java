package com.leo.support.audio;

/**
 * Created by leo on 2017/6/7.
 */

public class StatusCode {

    public static final int STATUS_ERROR = -1;
    public static final int STATUS_UN_INIT = 0;
    public static final int STATUS_HAS_INIT = 1;
    public static final int STATUS_BUFFING = 2;
    public static final int STATUS_PREPARED = 3;
    public static final int STATUS_PLAYING = 4;
    public static final int STATUS_PAUSE = 5;
    public static final int STATUS_STOP = 6;
    public static final int STATUS_COMPLETED = 7;
    public static final int STATUS_RELEASE = 8;

    public static String getStatusLabel(int status) {
        switch (status) {
            case STATUS_ERROR:
                return "STATUS_ERROR";
            case STATUS_UN_INIT:
                return "STATUS_UN_INIT";
            case STATUS_HAS_INIT:
                return "STATUS_HAS_INIT";
            case STATUS_BUFFING:
                return "STATUS_BUFFING";
            case STATUS_PREPARED:
                return "STATUS_PREPARED";
            case STATUS_PLAYING:
                return "STATUS_PLAYING";
            case STATUS_PAUSE:
                return "STATUS_PAUSE";
            case STATUS_STOP:
                return "STATUS_STOP";
            case STATUS_COMPLETED:
                return "STATUS_COMPLETED";
            case STATUS_RELEASE:
                return "STATUS_RELEASE";
        }
        return "STATUS_UNKNOWN";
    }
}
