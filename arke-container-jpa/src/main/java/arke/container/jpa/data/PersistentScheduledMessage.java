package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = PersistentScheduledMessage.TABLE_NAME)
public class PersistentScheduledMessage {

    public static final String TABLE_NAME = "persistent_scheduled_message";

    public static final String FIELD_ID = "persistent_scheduled_message_id";
    public static final String FIELD_INSERTION_TIME = "persistent_scheduled_message_insertion_time";
    public static final String FIELD_SCHEDULED_TIME = "persistent_scheduled_message_scheduled_time";
    public static final String FIELD_DELIVERY_TIME = "persistent_scheduled_message_delivery_time";
    public static final String FIELD_CANCELED = "persistent_scheduled_message_canceled";
    public static final String FIELD_FAILURE_REASON = "persistent_scheduled_message_failure_reason";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_INSERTION_TIME, nullable = false)
    private Date insertionTime;

    @Column(name = FIELD_SCHEDULED_TIME, nullable = false)
    private Date scheduledTime;

    @Column(name = FIELD_DELIVERY_TIME)
    private Date deliveryTime;

    @Column(name = FIELD_CANCELED, nullable = false)
    private boolean canceled;

    @Column(name = FIELD_FAILURE_REASON)
    private String failureReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Date insertionTime) {
        this.insertionTime = insertionTime;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
