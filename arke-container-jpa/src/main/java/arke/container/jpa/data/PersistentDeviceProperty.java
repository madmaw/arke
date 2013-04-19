package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = PersistentDeviceProperty.TABLE_NAME)
public class PersistentDeviceProperty {

    public static final String TABLE_NAME = "persistent_device_property";

    public static final String FIELD_ID = "persistent_device_property_id";
    public static final String FIELD_DEVICE_ID = "persistent_device_property_device_id";
    public static final String FIELD_KEY = "persistent_device_property_key";
    public static final String FIELD_VALUE = "persistent_device_property_value";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_DEVICE_ID, nullable = false)
    private Integer deviceId;

    @Column(name = FIELD_KEY, nullable = false)
    private String key;

    @Column(name = FIELD_VALUE)
    private String value;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
