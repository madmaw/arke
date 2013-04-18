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
    public static final String FIELD_FAILURE_REASON = "persistent_outbound_message_failure_reason";

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
    @Column(name = FIELD_FAILURE_REASON)
    private String failureReason;

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
}
