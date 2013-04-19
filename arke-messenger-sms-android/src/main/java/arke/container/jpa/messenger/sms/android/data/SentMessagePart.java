package arke.container.jpa.messenger.sms.android.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = SentMessagePart.TABLE_NAME)
public class SentMessagePart {
    public static final String TABLE_NAME = "sent_message_part";

    public static final String FIELD_ID = "sent_message_part_id";
    public static final String FIELD_MESSAGE_ID = "sent_message_part_message_id";
    public static final String FIELD_SEND_RESPONSE = "sent_message_part_send_response";
    public static final String FIELD_SEND_ERROR_CODE = "sent_message_part_send_error_code";
    public static final String FIELD_DELIVERY_RESPONSE = "sent_message_part_delivery_response";
    public static final String FIELD_DELIVERY_ERROR_CODE = "sent_message_part_delivery_error_code";
    public static final String FIELD_DESCRIPTION = "sent_message_part_description";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_MESSAGE_ID, nullable = false)
    private Integer messageId;

    @Column(name = FIELD_SEND_RESPONSE)
    private Integer sendResponse;

    @Column(name = FIELD_SEND_ERROR_CODE)
    private String sendErrorCode;

    @Column(name = FIELD_DELIVERY_RESPONSE)
    private Integer deliveryResponse;

    @Column(name = FIELD_DELIVERY_ERROR_CODE)
    private String deliveryErrorCode;

    @Column(name = FIELD_DESCRIPTION)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getSendResponse() {
        return sendResponse;
    }

    public void setSendResponse(Integer sendResponse) {
        this.sendResponse = sendResponse;
    }

    public String getSendErrorCode() {
        return sendErrorCode;
    }

    public void setSendErrorCode(String sendErrorCode) {
        this.sendErrorCode = sendErrorCode;
    }

    public Integer getDeliveryResponse() {
        return deliveryResponse;
    }

    public void setDeliveryResponse(Integer deliveryResponse) {
        this.deliveryResponse = deliveryResponse;
    }

    public String getDeliveryErrorCode() {
        return deliveryErrorCode;
    }

    public void setDeliveryErrorCode(String deliveryErrorCode) {
        this.deliveryErrorCode = deliveryErrorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
