package arke.container.jpa.messenger.sms.android;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import arke.ContainerException;
import arke.ContentTypeUtils;
import arke.container.jpa.Messenger;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentMessagePart;
import arke.container.jpa.messenger.sms.android.data.SentMessage;
import arke.container.jpa.messenger.sms.android.data.SentMessageDAO;
import arke.container.jpa.messenger.sms.android.data.SentMessagePart;
import arke.container.jpa.messenger.sms.android.data.SentMessagePartDAO;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class AndroidSMSMessenger implements Messenger {

    private SmsManager smsManager;
    private String sourceAddress;
    private SentMessageDAO sentMessageDAO;
    private SentMessagePartDAO sentMessagePartDAO;
    private Context context;

    public AndroidSMSMessenger(
            Context context,
            String sourceAddress,
            SmsManager smsManager,
            SentMessageDAO sentMessageDAO,
            SentMessagePartDAO sentMessagePartDAO
    ) {
        this.context = context;
        this.sourceAddress = sourceAddress;
        this.smsManager = smsManager;
        this.sentMessageDAO = sentMessageDAO;
        this.sentMessagePartDAO = sentMessagePartDAO;
    }

    @Override
    public String sendMessage(PersistentDevice targetPersistentDevice, List<PersistentMessagePart> parts) throws ContainerException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for( PersistentMessagePart part : parts ) {
            String contentType = part.getContentType();
            String mimeType = ContentTypeUtils.extractMimeType(contentType);
            if( ContentTypeUtils.MIME_TYPE_TEXT_PLAIN.equals(mimeType) ) {
                // get the encoding
                String encoding = ContentTypeUtils.extractCharset(contentType);
                String message;
                if( encoding != null ) {
                    Charset charset = Charset.forName(encoding);
                    message = new String(part.getPayload(), charset);
                } else {
                    message = new String(part.getPayload());
                }
                if( !first ) {
                    stringBuilder.append('\n');
                    stringBuilder.append('\n');
                } else {
                    first = false;
                }
                stringBuilder.append(message.trim());
            }
        }

        ArrayList<String> messages = smsManager.divideMessage(stringBuilder.toString());
        String phoneNumber = targetPersistentDevice.getDeviceName();

        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(messages.size());
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>(messages.size());

        SentMessage sentMessage = new SentMessage();

        int sentMessageId = sentMessageDAO.create(sentMessage);

        for( String message : messages ) {
            SentMessagePart sentMessagePart = new SentMessagePart();
            sentMessagePart.setMessageId(sentMessageId);
            sentMessagePart.setDescription(message);
            int sentMessagePartId = sentMessagePartDAO.create(sentMessagePart);

            Intent sentIntent = new Intent(AndroidSMSConstants.SENT_SMS_INTENT_NAME);
            sentIntent.putExtra(AndroidSMSConstants.ATTRIBUTE_MESSAGE_PART_ID, sentMessagePartId);
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this.context, 0, sentIntent, 0);
            sentIntents.add(sentPendingIntent);

            Intent deliveredIntent = new Intent(AndroidSMSConstants.DELIVERED_SMS_INTENT_NAME);
            deliveredIntent.putExtra(AndroidSMSConstants.ATTRIBUTE_MESSAGE_PART_ID, sentMessagePartId);
            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this.context, 0, deliveredIntent, 0);
            deliveryIntents.add(deliveredPendingIntent);
        }

        smsManager.sendMultipartTextMessage(
                phoneNumber,
                this.sourceAddress,
                messages,
                sentIntents,
                deliveryIntents
        );

        return Integer.toString(sentMessageId);
    }

}
