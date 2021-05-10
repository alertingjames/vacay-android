package com.mv.vacay.logger;

/**
 * Created by a on 2016.11.22.
 */
public final class Logger {

    public static final boolean DEBUG = false;

    /**
     * verbose log print
     *
     * @param String
     * @param String
     *
     * @return int
     */
    public static int v(String tag, String msg) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.v(tag, msg);
    }

    /**
     * verbose log print
     *
     * @param String
     * @param String
     * @param Throwable
     *
     * @return int
     */
    public static int v(String tag, String msg, Throwable tr) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.v(tag, msg, tr);
    }

    /**
     * error log print
     *
     * @param String
     * @param String
     *
     * @return int
     */
    public static int e(String tag, String msg) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.e(tag, msg);
    }

    /**
     * error log print
     *
     * @param String
     * @param String
     * @param Throwable
     *
     * @return int
     */
    public static int e(String tag, String msg, Throwable tr) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.e(tag, msg, tr);
    }

    /**
     * worn log print
     *
     * @param String
     * @param String
     *
     * @return int
     */
    public static int w(String tag, String msg) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.w(tag, msg);
    }

    /**
     * worn log print
     *
     * @param String
     * @param String
     * @param Throwable
     *
     * @return int
     */
    public static int w(String tag, String msg, Throwable tr) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.w(tag, msg, tr);
    }

    /**
     * info log print
     *
     * @param String
     * @param String
     *
     * @return int
     */
    public static int i(String tag, String msg) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.i(tag, msg);
    }

    /**
     * info log print
     *
     * @param String
     * @param String
     * @param Throwable
     *
     * @return int
     */
    public static int i(String tag, String msg, Throwable tr) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.i(tag, msg, tr);
    }

    /**
     * debug log print
     *
     * @param String
     * @param String
     *
     * @return int
     */
    public static int d(String tag, String msg) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.d(tag, msg);
    }

    /**
     * debug log print
     *
     * @param String
     * @param String
     * @param Throwable
     *
     * @return int
     */
    public static int d(String tag, String msg, Throwable tr) {
        if (!DEBUG) {
            return -1;
        }
        msg = tag + " - " + msg;
        // writeLog(tag, msg);
        return android.util.Log.d(tag, msg, tr);
    }

}

