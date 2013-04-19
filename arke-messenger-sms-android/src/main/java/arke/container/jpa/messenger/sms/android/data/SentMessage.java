package arke.container.jpa.messenger.sms.android.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = SentMessage.TABLE_NAME)
public class SentMessage {

    public static final String TABLE_NAME = "sent_message";

    public static final String FIELD_ID = "sent_message_id";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
