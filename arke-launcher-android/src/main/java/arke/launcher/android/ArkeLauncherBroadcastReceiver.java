package arke.launcher.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import arke.container.jpa.messenger.sms.android.AndroidIntentDeliveryHandler;
import arke.container.jpa.messenger.sms.android.AndroidIntentMessenger;
import arke.container.jpa.messenger.sms.android.AndroidSMSConstants;

public class ArkeLauncherBroadcastReceiver extends BroadcastReceiver {

    private boolean alwaysActive;

    public ArkeLauncherBroadcastReceiver() {

    }

    public ArkeLauncherBroadcastReceiver(boolean alwaysActive) {
        this.alwaysActive = alwaysActive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // check the context every time

        SharedPreferences preferences = context.getSharedPreferences(ArkeLauncherActivity.PREFERENCE_NAME, Context.MODE_WORLD_WRITEABLE);
        String serviceName = preferences.getString(ArkeLauncherActivity.PREFERENCE_KEY_ADDRESS, context.getString(R.string.service_default));
        boolean active = preferences.getBoolean(ArkeLauncherActivity.PREFERENCE_KEY_ACTIVE, false);
        if( (active || this.alwaysActive) && serviceName != null ) {
            Bundle bundle = intent.getExtras();
            if( bundle != null ) {
                Object[] pdus = (Object[])bundle.get(AndroidSMSConstants.ATTRIBUTE_PDUS);
                for( Object pdu : pdus ) {
                    byte[] pduData = (byte[])pdu;
                    SmsMessage message = SmsMessage.createFromPdu(pduData);
                    String phoneNumber = message.getOriginatingAddress();
                    String messageBody = message.getMessageBody();

                    Intent arkeIntent = AndroidIntentMessenger.toIntent(serviceName);
                    arkeIntent.putExtra(AndroidIntentDeliveryHandler.FIELD_ADDRESS, phoneNumber);
                    arkeIntent.putExtra(AndroidIntentDeliveryHandler.FIELD_MESSAGE, messageBody);
                    arkeIntent.putExtra(AndroidIntentDeliveryHandler.FIELD_TYPE, AndroidSMSConstants.DEVICE_TYPE_MOBILE);
                    // should just be delivered as per normal
                    context.startService(arkeIntent);


    //                try {
    //                    PersistentDevice persistentDevice = persistentDeviceDAO.findByNameAndType(phoneNumber, AndroidSMSConstants.DEVICE_TYPE_MOBILE);
    //                    if( persistentDevice == null ) {
    //                        persistentDevice = new PersistentDevice();
    //                        persistentDevice.setDeviceName(phoneNumber);
    //                        persistentDevice.setDeviceType(AndroidSMSConstants.DEVICE_TYPE_MOBILE);
    //                        persistentDevice.setLastUsed(new Date());
    //                        persistentDeviceDAO.create(persistentDevice);
    //                    }
    //                    ArrayList<PersistentMessagePart> parts = new ArrayList<PersistentMessagePart>(1);
    //                    PersistentMessagePart part = new PersistentMessagePart();
    //                    part.setSequenceNumber(0);
    //                    part.setContentType(ContentTypeUtils.toTextPlainMimeType(ENCODING));
    //                    part.setPayload(messageBody.getBytes(ENCODING));
    //                    parts.add(part);
    //                    container.handleInboundMessage(persistentDevice, parts);
    //                } catch( Exception ex ) {
    //                    LOG.log(Level.WARNING, "unable to handle sms from "+phoneNumber+" : "+messageBody, ex);
    //                }
                }
            }
        }

    }
}
