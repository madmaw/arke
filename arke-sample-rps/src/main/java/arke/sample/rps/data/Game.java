package arke.sample.rps.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = Game.TABLE_NAME)
public class Game {

    public static final String TABLE_NAME = "rps_game";

    public static final String FIELD_ID = "rps_game_id";
    public static final String FIELD_DATE_STARTED = "rps_game_date_started";
    public static final String FIELD_TURN_NUMBER = "rps_game_turn_number";
    public static final String FIELD_WINNER_ID = "rps_game_winner_id";
    public static final String FIELD_SCHEDULED_TIMEOUT_MESSAGE_ID = "rps_game_scheduled_timeout_message_id";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_DATE_STARTED, nullable = false)
    private Date dateStarted;

    @Column(name = FIELD_TURN_NUMBER, nullable = false)
    private int turnNumber;

    @Column(name = FIELD_WINNER_ID)
    private Integer winnerId;

    @Column(name = FIELD_SCHEDULED_TIMEOUT_MESSAGE_ID)
    private Long scheduledTimeoutMessageId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public Long getScheduledTimeoutMessageId() {
        return scheduledTimeoutMessageId;
    }

    public void setScheduledTimeoutMessageId(Long scheduledTimeoutMessageId) {
        this.scheduledTimeoutMessageId = scheduledTimeoutMessageId;
    }
}
