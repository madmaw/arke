package arke.velocity.android;

import android.content.Context;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class AndroidVelocityUtils {
    public static final void setupEngine(Context context, VelocityEngine velocityEngine, boolean init) {
        velocityEngine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, AndroidLogChute.class.getName());
        velocityEngine.setProperty("resource.loader", "android");
        velocityEngine.setProperty("android.resource.loader.class", AndroidResourceLoader.class.getName());
        velocityEngine.setProperty("android.content.res.Resources", context.getResources());
        velocityEngine.setProperty("packageName", context.getPackageName());
        if( init ) {
            velocityEngine.init();
        }
    }
}
