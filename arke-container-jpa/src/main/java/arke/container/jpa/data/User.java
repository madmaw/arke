package arke.container.jpa.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME = "arke_user";

    public static final String FIELD_ID = "arke_user_id";
    public static final String FIELD_BLOCKED = "arke_user_blocked";
    public static final String FIELD_BLOCK_REASON = "arke_user_block_reason";


    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_BLOCKED, nullable = false)
    private boolean blocked;

    @Column(name = FIELD_BLOCK_REASON)
    private String blockReason;

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
}
