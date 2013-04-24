package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = PersistentOutboundMessage.TABLE_NAME)
public class PersistentOutboundMessage {

    public static final String TABLE_NAME = "persistent_outbound_message";

    public static final String FIELD_ID = "persistent_outbound_message_id";
    public static final String FIELD_TARGET_USER_ID = "persistent_outbound_message_target_user_id";
    public static final String FIELD_TARGET_DEVICE_ID = "persistent_outbound_message_target_device_id";
    public static final String FIELD_TIME_LODGED = "persistent_outbound_message_time_lodged";
    public static final String FIELD_TIME_SENT = "persistent_outbound_message_time_sent";
    public static final String FIELD_TIME_TRANSMITTED = "persistent_outbound_message_time_transmitted";
    public static final String FIELD_TIME_RECEIVED = "persistent_outbound_message_time_received";
    public static final String FIELD_FAILURE_REASON = "persistent_outbound_message_failure_reason";
    public static final String FIELD_FAILED_PERMANENTLY = "persistent_outbound_message_failed_permanently";
    public static final String FIELD_RECEIPT = "persistent_outbound_message_receipt";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = FIELD_TARGET_USER_ID)
    private Integer targetUserId;
    @Column(name = FIELD_TARGET_DEVICE_ID)
    private Integer targetDeviceId;
    @Column(name = FIELD_TIME_LODGED, nullable = false)
    private Date timeLodged;
    @Column(name = FIELD_TIME_SENT)
    private Date timeSent;
    @Column(name = FIELD_TIME_TRANSMITTED)
    private Date timeTransmitted;
    @Column(name = FIELD_TIME_RECEIVED)
    private Date timeReceived;
    @Column(name = FIELD_FAILURE_REASON)
    private String failureReason;
    @Column(name = FIELD_FAILED_PERMANENTLY)
    private boolean failedPermanently;
    @Column(name = FIELD_RECEIPT)
    private String receipt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Integer getTargetDeviceId() {
        return targetDeviceId;
    }

    public void setTargetDeviceId(Integer targetDeviceId) {
        this.targetDeviceId = targetDeviceId;
    }

    public Date getTimeLodged() {
        return timeLodged;
    }

    public void setTimeLodged(Date timeLodged) {
        this.timeLodged = timeLodged;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Date getTimeTransmitted() {
        return timeTransmitted;
    }

    public void setTimeTransmitted(Date timeTransmitted) {
        this.timeTransmitted = timeTransmitted;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    public boolean isFailedPermanently() {
        return failedPermanently;
    }

    public void setFailedPermanently(boolean failedPermanently) {
        this.failedPermanently = failedPermanently;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
