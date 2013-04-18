package arke.container.jpa.data;

import arke.Message;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = PersistentMessagePart.TABLE_NAME)
public class PersistentMessagePart implements Message.Part {
    public static final String TABLE_NAME = "persistent_message_part";

    public static final String FIELD_ID = "persistent_message_part_id";
    public static final String FIELD_TYPE = "persistent_message_part_type";
    public static final String FIELD_MIME_TYPE = "persistent_message_part_mime_type";
    public static final String FIELD_PAYLOAD = "persistent_message_part_payload";
    public static final String FIELD_INBOUND_MESSAGE_ID = "persistent_message_part_inbound_message_id";
    public static final String FIELD_OUTBOUND_MESSAGE_ID = "persistent_message_part_outbound_message_id";
    public static final String FIELD_SCHEDULED_MESSAGE_ID = "persistent_message_part_scheduled_message_id";
    public static final String FIELD_SEQUENCE_NUMBER = "persistent_message_part_sequence_number";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = FIELD_TYPE, nullable = true)
    private String type;
    @Column(name = FIELD_MIME_TYPE, nullable = false)
    private String mimeType;
    @Column(name = FIELD_PAYLOAD)
    private byte[] payload;
    @Column(name = FIELD_INBOUND_MESSAGE_ID)
    private Integer inboundMessageId;
    @Column(name = FIELD_OUTBOUND_MESSAGE_ID)
    private Integer outboundMessageId;
    @Column(name = FIELD_SCHEDULED_MESSAGE_ID)
    private Integer scheduledMessageId;
    @Column(name = FIELD_SEQUENCE_NUMBER, nullable = false)
    private int sequenceNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Integer getInboundMessageId() {
        return inboundMessageId;
    }

    public void setInboundMessageId(Integer inboundMessageId) {
        this.inboundMessageId = inboundMessageId;
    }

    public Integer getOutboundMessageId() {
        return outboundMessageId;
    }

    public void setOutboundMessageId(Integer outboundMessageId) {
        this.outboundMessageId = outboundMessageId;
    }

    public Integer getScheduledMessageId() {
        return scheduledMessageId;
    }

    public void setScheduledMessageId(Integer scheduledMessageId) {
        this.scheduledMessageId = scheduledMessageId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
