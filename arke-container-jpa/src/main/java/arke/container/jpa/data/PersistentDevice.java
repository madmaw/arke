package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = PersistentDevice.TABLE_NAME)
public class PersistentDevice {

    public static final String TABLE_NAME = "persistent_device";

    public static final String FIELD_ID = "persistent_device_id";
    public static final String FIELD_OWNER_ID = "persistent_device_owner_id";
    public static final String FIELD_TYPE = "persistent_device_type";
    public static final String FIELD_NAME = "persistent_device_name";
    public static final String FIELD_INACTIVE = "persistent_device_inactive";
    public static final String FIELD_LAST_USED = "persistent_device_last_used";


    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = FIELD_OWNER_ID)
    private Integer ownerId;
    @Column(name = FIELD_TYPE, nullable = false)
    private String deviceType;
    @Column(name = FIELD_NAME, nullable = false)
    private String deviceName;
    @Column(name = FIELD_INACTIVE, nullable = false)
    private boolean inactive;
    @Column(name = FIELD_LAST_USED)
    private Date lastUsed;

    public PersistentDevice() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
