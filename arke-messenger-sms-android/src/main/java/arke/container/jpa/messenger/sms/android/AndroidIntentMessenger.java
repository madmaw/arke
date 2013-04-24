package arke.container.jpa.messenger.sms.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import arke.ContainerException;
import arke.MessageWrapper;
import arke.container.jpa.Messenger;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.List;
import java.util.logging.Logger;

public class AndroidIntentMessenger implements Messenger {

    private static final Logger LOG = Logger.getLogger(AndroidIntentMessenger.class.getName());

    public static final String FIELD_TO = "to";
    public static final String FIELD_MESSAGE = "message";

    public static final Intent toIntent(String address) {
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(address));
        String packageName;
        int packageIndex = address.lastIndexOf(".");
        if( packageIndex >= 0 ) {
            String className = address;
            packageName = className.substring(0, packageIndex);
            intent.setClassName(packageName, className);
        } else {
            intent.setAction(address);
        }
        return intent;
    }

    private Context context;

    public AndroidIntentMessenger(Context context) {
        this.context = context;
    }

    @Override
    public String sendMessage(PersistentDevice targetPersistentDevice, List<PersistentMessagePart> parts) throws ContainerException {
        String name = targetPersistentDevice.getDeviceName();
        String address = targetPersistentDevice.getDeviceType();
        String message = MessageWrapper.toString((List)parts);

        LOG.info("sending message "+message+" to "+name+" of type "+address);

        Intent intent = toIntent(address);
        // extract the package and class name from the address
        intent.putExtra(FIELD_TO, name);
        intent.putExtra(FIELD_MESSAGE, message);
        intent.setAction(Intent.ACTION_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //context.sendBroadcast(intent);
        // frigging thing
        context.startActivity(intent);

        // no receipts here
        return null;
    }
}
