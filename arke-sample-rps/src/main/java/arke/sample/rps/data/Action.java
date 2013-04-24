package arke.sample.rps.data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

public class Action {

    public static enum Type {
        ROCK(true),
        PAPER(true),
        SCISSORS(true),
        STOP(false),
        TIMEOUT(false);

        private boolean move;

        Type(boolean move) {
            this.move = move;
        }

        public boolean isMove() {
            return this.move;
        }

        public int compare(Type type) {
            int result;
            if( this == STOP || this == TIMEOUT ) {
                result = -1;
            } else if( this == type ) {
                result = 0;
            } else {
                if( type == STOP || type == TIMEOUT ) {
                    result = 1;
                } else if( this == SCISSORS ) {
                    if( type == PAPER ) {
                        result = 1;
                    } else { // ROCK
                        result = -1;
                    }
                } else if( this == PAPER ) {
                    if( type == ROCK ) {
                        result = 1;
                    } else { // SCISSORS
                        result = -1;
                    }
                } else { // this == ROCK
                    if( type == SCISSORS ) {
                        result = 1;
                    } else { // PAPER
                        result = -1;
                    }
                }
            }
            return result;
        }
    }

    public static final String TABLE_NAME = "rps_action";

    public static final String FIELD_ID = "rps_action_id";
    public static final String FIELD_TYPE = "rps_action_type";
    public static final String FIELD_TIME = "rps_action_time";
    public static final String FIELD_PLAYER_ID = "rps_action_player_id";
    public static final String FIELD_GAME_ID = "rps_action_game_id";
    public static final String FIELD_TURN = "rps_action_turn";

    @Column(name = FIELD_ID)
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = FIELD_TYPE, nullable = false)
    private Type type;

    @Column(name = FIELD_TIME, nullable = false)
    private Date time;

    @Column(name = FIELD_PLAYER_ID, nullable = false)
    private int playerId;

    @Column(name = FIELD_GAME_ID, nullable = false)
    private int gameId;

    @Column(name = FIELD_TURN, nullable = false)
    private int turn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
