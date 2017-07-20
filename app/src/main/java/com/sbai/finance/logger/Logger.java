package com.sbai.finance.logger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sbai.finance.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by houcc on 2017-07-19.
 */

public class Logger {

    private static String TAG = "Logger";

    private static final char VERBOSE = 'v';
    private static final char DEBUG = 'd';
    private static final char INFO = 'i';
    private static final char WARN = 'w';
    private static final char ERROR = 'e';

    private static String logPath;
    private static SimpleDateFormat dateFormat;
    private static ExecutorService mWorkPool;

    public static void init(Context context) {
        init(context, false);
    }

    public static void init(Context context, boolean clearLogs) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logPath = getFilePath(context) + File.separator + "Logs";
        mWorkPool = Executors.newSingleThreadExecutor();
        if (clearLogs) {
            clearLogs();
        }
    }

    public static void clearLogs() {
        if (null == logPath || null == mWorkPool) {
            Log.d(TAG, "未初始化logger");
            return;
        }
        mWorkPool.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(logPath);
                deleteFile(file);
            }
        });
    }

    public static void v(String tag, String msg) {
        v(tag, msg, false);
    }

    public static void v(String tag, String msg, boolean covered) {
        Log.v(tag, msg);
        enqueueToWrite(VERBOSE, tag, msg, covered);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, false);
    }

    public static void d(String tag, String msg, boolean covered) {
        Log.d(tag, msg);
        enqueueToWrite(DEBUG, tag, msg, covered);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, false);
    }

    public static void i(String tag, String msg, boolean covered) {
        Log.i(tag, msg);
        enqueueToWrite(INFO, tag, msg, covered);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, false);
    }

    public static void w(String tag, String msg, boolean covered) {
        Log.w(tag, msg);
        enqueueToWrite(WARN, tag, msg, covered);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, false);
    }

    public static void e(String tag, String msg, boolean covered) {
        Log.e(tag, msg);
        enqueueToWrite(ERROR, tag, msg, covered);
    }

    private static void enqueueToWrite(final char type, final String tag, final String msg, final boolean covered) {
        if (BuildConfig.IS_PROD) {
            return;
        }
        if (null == logPath || null == mWorkPool) {
            Log.e(TAG, "未初始化logger");
            return;
        }
        mWorkPool.execute(new Runnable() {
            @Override
            public void run() {
                writeToFile(type, tag, msg, covered);
            }
        });
    }

    private static void writeToFile(char type, String tag, String msg, boolean covered) {
        String fileName = logPath + File.separator + tag + ".log";
        String log = dateFormat.format(new Date()) + " " + type + "/" + tag + ": " + msg + "\n";

        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(fileName, !covered);//true为追加，false为覆盖
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(log);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    private static String getFilePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalFilesDir(null).getPath();
        } else {
            return context.getFilesDir().getPath();
        }
    }
}
