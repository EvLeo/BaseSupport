package com.leo.support.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * done
 * Created by leo on 2017/5/17.
 */

public class ToastUtils {

    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int stringResId) {
        Toast.makeText(context, stringResId, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, int stringResId) {
        Toast.makeText(context, stringResId, Toast.LENGTH_SHORT).show();
    }
}
