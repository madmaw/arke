package arke.velocity.android;

import android.util.Log;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class AndroidLogChute implements LogChute {

    private int MAX_TAG_LENGTH = 23;

    private String tag;

    public AndroidLogChute() {
        this(AndroidLogChute.class.getName());
    }

    public AndroidLogChute(String tag) {
        if( tag.length() > MAX_TAG_LENGTH ) {
            tag = tag.substring(0, MAX_TAG_LENGTH);
        }
        this.tag = tag;
    }

    @Override
    public void init(RuntimeServices rs) throws Exception {

    }

    @Override
    public void log(int level, String message) {
        switch(level) {
            case LogChute.DEBUG_ID:
                Log.d(this.tag, message);
                break;
            case LogChute.ERROR_ID:
                Log.e(this.tag, message);
                break;
            case LogChute.WARN_ID:
                Log.w(this.tag, message);
                break;
            case LogChute.TRACE_ID:
            case LogChute.INFO_ID:
            default:
                Log.i(this.tag, message);
                break;
        }
    }

    @Override
    public void log(int level, String message, Throwable t) {
        switch(level) {
            case LogChute.DEBUG_ID:
                Log.d(this.tag, message, t);
                break;
            case LogChute.ERROR_ID:
                Log.e(this.tag, message, t);
                break;
            case LogChute.WARN_ID:
                Log.w(this.tag, message, t);
                break;
            case LogChute.TRACE_ID:
            case LogChute.INFO_ID:
            default:
                Log.i(this.tag, message, t);
                break;
        }

    }

    @Override
    public boolean isLevelEnabled(int level) {
        return Log.isLoggable(this.tag, toAndroidLogLevel(level));
    }

    private int toAndroidLogLevel(int level) {
        int result;
        switch(level) {
            case LogChute.DEBUG_ID:
                result = Log.DEBUG;
                break;
            case LogChute.ERROR_ID:
                result = Log.ERROR;
                break;
            case LogChute.WARN_ID:
                result = Log.WARN;
                break;
            case LogChute.TRACE_ID:
            case LogChute.INFO_ID:
            default:
                result = Log.INFO;
                break;
        }
        return result;
    }
}
