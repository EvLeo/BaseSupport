package com.leo.support.utils;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/**
 * done
 * Created by leo on 2017/5/17.
 */

public class UIThreadhandler {

    private static Object token = new Object();

    private static final int LOOP = 0x001;
    private static final int LOOP_TIMES = 0x002;

    private static Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOOP:
                    Runnable runnable = (Runnable) msg.obj;
                    run(runnable);
                    loop(runnable, msg.arg1);
                    break;
                case LOOP_TIMES:
                    LoopHandler handler = (LoopHandler) msg.obj;
                    try {
                        handler.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loop(handler, msg.arg1, --msg.arg2);
                    break;
            }
        }

        private void run(Runnable runnable) {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static boolean post(Runnable r) {
        return null != uiHandler && uiHandler.post(new ReleaseRunnable(r));
    }

    public static boolean postDelayed(Runnable r, long delayMillis) {
        return null != uiHandler && uiHandler.postDelayed(r, delayMillis);
    }

    public static boolean postAtTime(Runnable r) {
        if (null == uiHandler) {
            return false;
        }
        uiHandler.removeCallbacks(r, token);
        return uiHandler.postAtTime(r, token, SystemClock.uptimeMillis());
    }

    public static boolean postAtTimeDelayed(Runnable r, int delay) {
        if (null == uiHandler) {
            return false;
        }
        uiHandler.removeCallbacks(r, token);
        return uiHandler.postAtTime(r, token, SystemClock.uptimeMillis() + delay);
    }

    public static Handler getUIhandler() {
        return uiHandler;
    }

    public static void loop(Runnable r, int delay) {
        if (null == uiHandler) {
            return;
        }
        uiHandler.removeMessages(LOOP);
        Message msg = Message.obtain();
        msg.what = LOOP;
        msg.obj = r;
        msg.arg1 = delay;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public static void stopLoop() {
        if (uiHandler != null) {
            uiHandler.removeMessages(LOOP);
        }
    }

    public static void loop(LoopHandler handler, int delay, int times) {
        if (null == uiHandler || null == handler) {
            return;
        }
        uiHandler.removeMessages(LOOP_TIMES);
        if (times == 0) {
            handler.end();
            return;
        }
        Message msg = Message.obtain();
        msg.what = LOOP_TIMES;
        msg.obj = handler;
        msg.arg1 = delay;
        msg.arg2 = times;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public static interface LoopHandler {
        public abstract void run();

        public abstract void end();
    }


    public static class ReleaseRunnable implements Runnable {

        private Runnable mRunnable;

        public ReleaseRunnable(Runnable runnable) {
            this.mRunnable = runnable;
        }

        @Override
        public void run() {
            if (null != mRunnable) {
                try {
                    mRunnable.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
