package com.leo.support.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by leo on 2017/6/1.
 */

public class BaseFileUtils {
    public static File getDir(File parent, String dirName) {
        File file = new File(parent, dirName);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    public static boolean createEmptyFile(String path, long size) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        parent.mkdirs();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(size);
        raf.close();
        return true;
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte buffer[] = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer, 0, 1024)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    public static void copyStream2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            copyStream(is, os);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public static void copyFile(File src, File obj) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(obj);
            copyStream(is, os);
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos  = null;
        try {
            bos = new ByteArrayOutputStream();
            copyStream(is, bos);
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
        return bos.toByteArray();
    }


    public static byte[] getBytes(File file) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            copyStream(fis, bos);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bos.toByteArray();
    }

}
