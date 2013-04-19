package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = PersistentUser.TABLE_NAME)
public class PersistentUser {

    public static final String TABLE_NAME = "persistent_user";

    public static final String FIELD_ID = "persistent_user_id";
    public static final String FIELD_BLOCKED = "persistent_user_blocked";
    public static final String FIELD_BLOCK_REASON = "persistent_user_block_reason";
    public static final String FIELD_TIME_ZONE_ID = "persistent_user_time_zone_id";


    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_BLOCKED, nullable = false)
    private boolean blocked;

    @Column(name = FIELD_BLOCK_REASON)
    private String blockReason;

    @Column(name = FIELD_TIME_ZONE_ID)
    private String timeZoneId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }
}
