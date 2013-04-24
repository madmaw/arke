package arke.sample.rps.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = Player.TABLE_NAME)
public class Player {
    public static final String TABLE_NAME = "rps_player";

    public static final String FIELD_ID = "rps_player_id";
    public static final String FIELD_USER_ID = "rps_player_user_id";
    public static final String FIELD_NAME = "rps_player_name";
    public static final String FIELD_CURRENT_GAME_ID = "rps_player_current_game_id";
    public static final String FIELD_GAMES_PLAYED = "rps_player_games_played";
    public static final String FIELD_GAMES_WON = "rps_player_games_won";
    public static final String FIELD_INACTIVE = "rps_player_inactive";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_USER_ID)
    private Long userId;

    @Column(name = FIELD_NAME, nullable = false, unique = true)
    private String name;

    @Column(name = FIELD_CURRENT_GAME_ID)
    private Integer currentGameId;

    @Column(name = FIELD_GAMES_PLAYED, nullable = false)
    private int gamesPlayed;

    @Column(name = FIELD_GAMES_WON, nullable = false)
    private int gamesWon;

    @Column(name = FIELD_INACTIVE, nullable = false)
    private boolean inactive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(Integer currentGameId) {
        this.currentGameId = currentGameId;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }
}
