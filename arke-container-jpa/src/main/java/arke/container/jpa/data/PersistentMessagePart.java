package arke.container.jpa.data;

import arke.Message;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = PersistentMessagePart.TABLE_NAME)
public class PersistentMessagePart implements Message.Part {
    public static final String TABLE_NAME = "persistent_message_part";

    public static final String FIELD_ID = "persistent_message_part_id";
    public static final String FIELD_TYPE = "persistent_message_part_type";
    public static final String FIELD_CONTENT_TYPE = "persistent_message_part_content_type";
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
    @Column(name = FIELD_CONTENT_TYPE, nullable = false)
    private String contentType;
    // don't seem to able to specify this in JPA?!
    @DatabaseField(columnName = FIELD_PAYLOAD, dataType = DataType.BYTE_ARRAY)
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
