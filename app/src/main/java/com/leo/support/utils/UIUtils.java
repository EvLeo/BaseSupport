package com.leo.support.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.leo.support.app.BaseApp;

/**
 * Created by leo on 2017/5/17.
 */

public class UIUtils {

    public static int getWindowWidth(Activity activity) {
        if (null == activity) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getWindowHeight(Activity activity) {
        if (null == activity) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        if (null == activity) {
            return null;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static int getSreenHeight(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        return outRect.height();
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        WindowManager wm = (WindowManager) BaseApp.getAppContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        float scale = metrics.density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        WindowManager wm = (WindowManager) BaseApp.getAppContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        float scale = metrics.density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void setScreenBrightness(Activity activity, float brightness) {
        if (null == activity) {
            return;
        }
        Window window = activity.getWindow();
        window.getAttributes().screenBrightness = brightness;
        window.setAttributes(window.getAttributes());
    }

    public static float getScreenBrightness(Activity activity) {
        if (null == activity) {
            return 1.0f;
        }
        return activity.getWindow().getAttributes().screenBrightness;
    }

    /**
     * 震动
     */
    public static void doVibrator() {
        ((Vibrator) BaseApp.getAppContext()
                .getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
    }

    /**
     * 是否运行在主线程
     *
     * @return
     */
    public static boolean isOnUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View view, int id) {
        return (T) view.findViewById(id);
    }

}
