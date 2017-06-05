package com.leo.support.security;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * MD5 加密
 * Created by leo on 2017/6/5.
 */

public class MD5Util {
    private static final String[] hexDigits = {
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"};

    public static String byteToHexString(byte aByte) {
        int n = aByte;
        if (n < 0)
            n = 256 + aByte;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String byteArrayToHexString(byte[] aBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aByte : aBytes) {
            stringBuffer.append(byteToHexString(aByte));
        }
        return stringBuffer.toString();
    }

    public static String encode(String string) {
        String str = null;
        if (!TextUtils.isEmpty(string)) {
            try {
                str = string;
                MessageDigest md = MessageDigest.getInstance("MD5");
                str = byteArrayToHexString(md.digest(str.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }
}
