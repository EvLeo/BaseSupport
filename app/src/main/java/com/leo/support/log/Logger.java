package com.leo.support.log;

import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * done
 * Created by LiuYu on 2017/4/24.
 */
public class Logger {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int DONOT_WRITE_LOG = 7;

    private int mLevel = DONOT_WRITE_LOG;
    private String mFileName = null;
    private final Object syncLock = new Object();

    private static Map<String, Logger> sLoggers = null;
    protected boolean isTrace = true;//将日志打印到控制台

    private Logger(String fileName, int level) {
        this.mFileName = fileName;
        this.mLevel = level;
    }

    public static Logger getLogger(String fileName) {
        return getLogger(fileName, DONOT_WRITE_LOG);
    }

    public synchronized static Logger getLogger(String fileName, int level) {
        if (fileName == null || fileName.length() == 0 || level < VERBOSE || level > DONOT_WRITE_LOG) {
            throw new IllegalArgumentException("invalid parameter fileName or level");
        }

        Logger logger = null;
        if (sLoggers == null) {
            sLoggers = new HashMap<>();
        } else {
            logger = sLoggers.get(fileName);
        }

        if (logger == null) {
            logger = new Logger(fileName, level);
            sLoggers.put(fileName, logger);
        }
        return logger;
    }

    protected void traceOn() {
        this.isTrace = true;
    }

    protected void traceOff() {
        this.isTrace = false;
    }

    public boolean isTrace() {
        return isTrace;
    }

    public void setLevel(int level) {
        this.mLevel = level;
    }

    public String getStackTrace(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    private int printToFile(int priority, String tag, String msg) {
        if (priority < mLevel) {
            return 0;
        }

        String[] prioritys = {"", "", "V", "D", "I", "W", "E", "A"};
        SimpleDateFormat sdf = new SimpleDateFormat("[MM-dd HH:mm:ss.SSS]", Locale.getDefault());
        String time = sdf.format(new Date());
        int pid = Process.myPid();
        StringBuilder sb = new StringBuilder();
        sb.append(time).append("\t").append(prioritys[priority]).append("/").append(tag).append("(")
                .append(pid).append("):").append(msg).append("\n");

        FileWriter fileWriter = null;

        synchronized (syncLock) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), mFileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileWriter = new FileWriter(file, true);
                fileWriter.write(sb.toString());
            } catch (FileNotFoundException e) {
                return -1;
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return 0;
    }

    public int v(String tag, String msg) {
        return isTrace ? Log.v(tag, msg) : printToFile(VERBOSE, tag, msg);
    }

    public int v(String tag, Throwable tr) {
        return v(tag, getStackTrace(tr));
    }

    public int d(String tag, String msg) {
        return isTrace ? Log.d(tag, msg) : printToFile(DEBUG, tag, msg);
    }

    public int d(String tag, Throwable tr) {
        return d(tag, getStackTrace(tr));
    }

    public int w(String tag, String msg) {
        return isTrace ? Log.w(tag, msg) : printToFile(WARN, tag, msg);
    }

    public int w(String tag, Throwable tr) {
        return w(tag, getStackTrace(tr));
    }

    public int i(String tag, String msg) {
        return isTrace ? Log.i(tag, msg) : printToFile(INFO, tag, msg);
    }

    public int i(String tag, Throwable tr) {
        return i(tag, getStackTrace(tr));
    }

    public int e(String tag, String msg) {
        return isTrace ? Log.e(tag, msg) : printToFile(ERROR, tag, msg);
    }

    public int e(String tag, String msg, Throwable tr) {
        return isTrace ? Log.e(tag, msg, tr) : printToFile(ERROR, tag, getStackTrace(tr));
    }

    public int e(String tag, Throwable tr) {
        return e(tag, getStackTrace(tr));
    }

}
