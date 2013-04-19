package arke.container.jpa.messenger.sms.android;

public interface AndroidSMSConstants {
    public static final String SENT_SMS_INTENT_NAME = "arke_sms_sent";
    public static final String DELIVERED_SMS_INTENT_NAME = "arke_sms_delivered";
    public static final String RECEIVED_SMS_INTENT_NAME = "android.provider.Telephony.SMS_RECEIVED";

    public static final String ATTRIBUTE_MESSAGE_PART_ID = "arke_message_part_id";
    public static final String ATTRIBUTE_ERROR_CODE = "errorCode";
    public static final String ATTRIBUTE_PDUS = "pdus";

    public static final String DEVICE_TYPE_MOBILE = "mobile";

}
