package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = Device.TABLE_NAME)
public class Device {

    public static final String TABLE_NAME = "device";

    public static final String FIELD_ID = "device_id";
    public static final String FIELD_OWNER_ID = "device_owner_id";
    public static final String FIELD_TYPE = "device_type";
    public static final String FIELD_NAME = "device_name";
    public static final String FIELD_INACTIVE = "device_inactive";
    public static final String FIELD_LAST_USED = "device_last_used";


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

    public Device() {

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
