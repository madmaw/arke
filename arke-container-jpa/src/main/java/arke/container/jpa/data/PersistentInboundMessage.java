package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = PersistentInboundMessage.TABLE_NAME)
public class PersistentInboundMessage {

    public static final String TABLE_NAME = "persistent_inbound_message";

    public static final String FIELD_ID = "persistent_inbound_message_id";
    public static final String FIELD_DEVICE_ID = "persistent_inbound_message_device_id";
    public static final String FIELD_TIME_RECEIVED = "persistent_inbound_message_time_received";
    public static final String FIELD_HANDLED = "persistent_inbound_message_handled";
    public static final String FIELD_FAILURE_REASON = "persistent_inbound_message_failure_reason";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = FIELD_DEVICE_ID, nullable = false)
    private Integer deviceId;
    @Column(name = FIELD_TIME_RECEIVED, nullable = false)
    private Date timeReceived;
    @Column(name = FIELD_HANDLED, nullable = false)
    private boolean handled;
    @Column(name = FIELD_FAILURE_REASON)
    private String failureReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
