package arke.container.jpa.messenger.sms.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import arke.ContentTypeUtils;
import arke.container.jpa.JPAContainer;
import arke.ContainerDataException;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentDeviceDAO;
import arke.container.jpa.data.PersistentMessagePart;
import arke.container.jpa.messenger.sms.android.data.SentMessagePart;
import arke.container.jpa.messenger.sms.android.data.SentMessagePartDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidSMSDeliveryHandler {

    public static final Logger LOG = Logger.getLogger(AndroidSMSDeliveryHandler.class.getSimpleName());
    public static final String ENCODING = "utf-8";

    private Context context;
    private SentMessagePartDAO sentMessagePartDAO;
    private PersistentDeviceDAO persistentDeviceDAO;

    private BroadcastReceiver sentBroadcastReceiver;
    private BroadcastReceiver deliveredBroadcastReceiver;
    private BroadcastReceiver smsBroadcastReceiver;

    private boolean started = false;

    public AndroidSMSDeliveryHandler(
            Context context,
            final JPAContainer container,
            final SentMessagePartDAO sentMessagePartDAO,
            final PersistentDeviceDAO persistentDeviceDAO
    ) {
        this.context = context;
        this.sentMessagePartDAO = sentMessagePartDAO;

        this.smsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if( bundle != null ) {
                    Object[] pdus = (Object[])bundle.get(AndroidSMSConstants.ATTRIBUTE_PDUS);
                    for( Object pdu : pdus ) {
                        byte[] pduData = (byte[])pdu;
                        SmsMessage message = SmsMessage.createFromPdu(pduData);
                        String phoneNumber = message.getOriginatingAddress();
                        String messageBody = message.getMessageBody();
                        try {
                            PersistentDevice persistentDevice = persistentDeviceDAO.findByNameAndType(phoneNumber, AndroidSMSConstants.DEVICE_TYPE_MOBILE);
                            if( persistentDevice == null ) {
                                persistentDevice = new PersistentDevice();
                                persistentDevice.setDeviceName(phoneNumber);
                                persistentDevice.setDeviceType(AndroidSMSConstants.DEVICE_TYPE_MOBILE);
                                persistentDevice.setLastUsed(new Date());
                                persistentDeviceDAO.create(persistentDevice);
                            }
                            ArrayList<PersistentMessagePart> parts = new ArrayList<PersistentMessagePart>(1);
                            PersistentMessagePart part = new PersistentMessagePart();
                            part.setSequenceNumber(0);
                            part.setContentType(ContentTypeUtils.toTextPlainMimeType(ENCODING));
                            part.setPayload(messageBody.getBytes(ENCODING));
                            parts.add(part);
                            container.handleInboundMessage(persistentDevice, parts);
                        } catch( Exception ex ) {
                            LOG.log(Level.WARNING, "unable to handle sms from "+phoneNumber+" : "+messageBody, ex);
                        }
                    }
                }
            }
        };

        this.sentBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    // extract the message part id
                    int messagePartId = intent.getIntExtra(AndroidSMSConstants.ATTRIBUTE_MESSAGE_PART_ID, 0);
                    SentMessagePart messagePart = sentMessagePartDAO.find(messagePartId);
                    if( messagePart != null ) {
                        messagePart.setSendResponse(getResultCode());
                        String errorCode = intent.getStringExtra(AndroidSMSConstants.ATTRIBUTE_ERROR_CODE);
                        messagePart.setSendErrorCode(errorCode);
                        sentMessagePartDAO.update(messagePart);
                    }
                    try {
                        checkMessageSent(messagePart.getMessageId());
                    } catch( ContainerDataException ex ) {
                        LOG.log(Level.WARNING, "unable to check message sent", ex);
                    }
                } catch( ContainerDataException ex ) {
                    LOG.log(Level.SEVERE, "unable to update message semt parts", ex);
                }
            }
        };

        this.deliveredBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    // extract the message part id
                    int messagePartId = intent.getIntExtra(AndroidSMSConstants.ATTRIBUTE_MESSAGE_PART_ID, 0);
                    SentMessagePart messagePart = sentMessagePartDAO.find(messagePartId);
                    if( messagePart != null ) {
                        messagePart.setDeliveryResponse(getResultCode());
                        String errorCode = intent.getStringExtra(AndroidSMSConstants.ATTRIBUTE_ERROR_CODE);
                        messagePart.setDeliveryErrorCode(errorCode);
                        sentMessagePartDAO.update(messagePart);
                    }
                    try {
                        checkMessageDelivered(messagePart.getMessageId());
                    } catch( ContainerDataException ex ) {
                        LOG.log(Level.WARNING, "unable to check message delivered", ex);
                    }
                } catch( ContainerDataException ex ) {
                    LOG.log(Level.SEVERE, "unable to update message delivered parts", ex);
                }

            }
        };
    }

    public void start() {
        if( !started ) {
            this.context.registerReceiver(
                    this.sentBroadcastReceiver,
                    new IntentFilter(AndroidSMSConstants.SENT_SMS_INTENT_NAME)
            );
            this.context.registerReceiver(
                    this.deliveredBroadcastReceiver,
                    new IntentFilter(AndroidSMSConstants.DELIVERED_SMS_INTENT_NAME)
            );
            this.context.registerReceiver(
                    this.smsBroadcastReceiver,
                    new IntentFilter(AndroidSMSConstants.RECEIVED_SMS_INTENT_NAME)
            );
            started = true;
        }
    }

    public void stop() {
        if( started ) {
            this.context.unregisterReceiver(this.sentBroadcastReceiver);
            this.context.unregisterReceiver(this.deliveredBroadcastReceiver);
            this.context.unregisterReceiver(this.smsBroadcastReceiver);
            started = false;
        }
    }

    public void checkMessageSent(int messageId) throws ContainerDataException {
        List<SentMessagePart> messageParts = this.sentMessagePartDAO.findByMessageId(messageId);
        boolean allSent = true;
        Integer result = null;
        for( SentMessagePart messagePart : messageParts ) {
            Integer sendResponse = messagePart.getSendResponse();
            if( sendResponse == null ) {
                allSent = false;
                break;
            } else {
                if( result == null || result == Activity.RESULT_OK ) {
                    result = sendResponse;
                }
            }
        }
        if( allSent ) {
            // TODO convert the result into a meaningful error message
            // TODO notify the container
            //this.container.
        }
    }

    public void checkMessageDelivered(int messageId) throws ContainerDataException {
        List<SentMessagePart> messageParts = this.sentMessagePartDAO.findByMessageId(messageId);
        boolean allDelivered = true;
        Integer result = null;
        for( SentMessagePart messagePart : messageParts ) {
            Integer deliveryResponse = messagePart.getDeliveryResponse();
            if( deliveryResponse == null ) {
                allDelivered = false;
                break;
            } else {
                if( result == null || result == Activity.RESULT_OK ) {
                    result = deliveryResponse;
                }
            }
        }
        if( allDelivered ) {
            // TODO convert the result into a meaningful error message
            // TODO notify the container
            //this.container.
        }

    }
}
