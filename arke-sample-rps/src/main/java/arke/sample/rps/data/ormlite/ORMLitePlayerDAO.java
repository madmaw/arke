package arke.sample.rps.data.ormlite;

import arke.ContainerDataException;
import arke.sample.rps.data.Player;
import arke.sample.rps.data.PlayerDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ORMLitePlayerDAO implements PlayerDAO {

    private Dao<Player, Integer> dao;

    public ORMLitePlayerDAO(Dao<Player, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(Player player) throws ContainerDataException {
        try {
            this.dao.create(player);
            return player.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(Player player) throws ContainerDataException {
        try {
            this.dao.update(player);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public Player findByName(String name) throws ContainerDataException {
        try {
            QueryBuilder<Player, Integer> query = this.dao.queryBuilder();
            query.where().eq(Player.FIELD_NAME, name);
            return dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public Player findByUserId(long userId) throws ContainerDataException {
        try {
            QueryBuilder<Player, Integer> query = this.dao.queryBuilder();
            query.where().eq(Player.FIELD_USER_ID, userId);
            return dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<Player> findByCurrentGameId(int gameId) throws ContainerDataException {
        try {
            QueryBuilder<Player, Integer> query = this.dao.queryBuilder();
            query.where().eq(Player.FIELD_CURRENT_GAME_ID, gameId);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public Player find(int id) throws ContainerDataException {
        try {
            return dao.queryForId(id);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<Player> findAvailable(int max) throws ContainerDataException {
        try {
            QueryBuilder<Player, Integer> query = this.dao.queryBuilder();
            query.where().eq(Player.FIELD_INACTIVE, false).and().isNull(Player.FIELD_CURRENT_GAME_ID);
            // TODO maybe exclude timeout players?
            // return different players each time (at random)
            query.orderByRaw("RANDOM()");
            query.limit((long)max);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
