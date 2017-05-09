package nl.vu.cs.cn;


public class Log {
    private static final boolean IS_ENABLED = true;


    public static int v(final String tag, final String msg) {
        return (isLoggingEnabled() ? android.util.Log.v(tag, System.currentTimeMillis() + " | " + msg) : 0);
    }


    public static int v(final String tag, final String msg, final Throwable tr) {
        return (isLoggingEnabled() ? android.util.Log.v(tag, System.currentTimeMillis() + " | " + msg, tr) : 0);
    }


    public static int d(final String tag, final String msg) {
        return (isLoggingEnabled() ? android.util.Log.d(tag, System.currentTimeMillis() + " | " + msg) : 0);
    }


    public static int d(final String tag, final String msg, final Throwable tr) {
        return (isLoggingEnabled() ? android.util.Log.v(tag, System.currentTimeMillis() + " | " + msg, tr) : 0);
    }


    public static int i(final String tag, final String msg) {
        return (isLoggingEnabled() ? android.util.Log.i(tag, System.currentTimeMillis() + " | " + msg) : 0);
    }

    public static int i(final String tag, final String msg, final Throwable tr) {
        return (isLoggingEnabled() ? android.util.Log.i(tag, System.currentTimeMillis() + " | " + msg, tr) : 0);
    }


    public static int w(final String tag, final String msg) {
        return (isLoggingEnabled() ? android.util.Log.w(tag, System.currentTimeMillis() + " | " + msg) : 0);
    }


    public static int w(final String tag, final String msg, final Throwable tr) {
        return (isLoggingEnabled() ? android.util.Log.w(tag, System.currentTimeMillis() + " | " + msg, tr) : 0);
    }


    public static int w(final String tag, final Throwable tr) {
        return (isLoggingEnabled() ? android.util.Log.w(tag, tr) : 0);
    }


    public static int e(final String tag, final String msg) {
        return (isLoggingEnabled() ? android.util.Log.e(tag, System.currentTimeMillis() + " | " + msg) : 0);
    }


    public static int e(final String tag, final String msg, final Throwable tr) {
        return (isLoggingEnabled() ? android.util.Log.e(tag, System.currentTimeMillis() + " | " + msg, tr) : 0);
    }

    private static boolean isLoggingEnabled() {
        return IS_ENABLED;
    }

}