package arke.container.jpa.messenger.sms.android.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = SentMessage.TABLE_NAME)
public class SentMessage {

    public static final String TABLE_NAME = "sent_message";

    public static final String FIELD_ID = "sent_message_id";
    public static final String FIELD_DESCRIPTION = "sent_message_description";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_DESCRIPTION)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
