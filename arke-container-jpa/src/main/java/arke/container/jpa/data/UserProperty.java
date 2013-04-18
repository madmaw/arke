package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = UserProperty.TABLE_NAME)
public class UserProperty {

    public static final String TABLE_NAME = "user_property";

    public static final String FIELD_ID = "user_property_id";
    public static final String FIELD_USER_ID = "user_property_user_id";
    public static final String FIELD_KEY = "user_property_key";
    public static final String FIELD_VALUE = "user_property_value";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = FIELD_USER_ID, nullable = false)
    private Integer userId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
