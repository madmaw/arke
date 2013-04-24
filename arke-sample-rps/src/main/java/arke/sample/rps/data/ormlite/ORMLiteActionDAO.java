package arke.sample.rps.data.ormlite;

import arke.ContainerDataException;
import arke.sample.rps.data.Action;
import arke.sample.rps.data.ActionDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ORMLiteActionDAO implements ActionDAO {

    private Dao<Action, Integer> dao;

    public ORMLiteActionDAO(Dao<Action, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(Action action) throws ContainerDataException {
        try {
            dao.create(action);
            return action.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(Action action) throws ContainerDataException {
        try {
            dao.update(action);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public int createOrUpdate(Action action) throws ContainerDataException {
        try {
            dao.createOrUpdate(action);
            return action.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<Action> findForGameIdAndTurn(int gameId, int turnNumber) throws ContainerDataException {
        try {
            QueryBuilder<Action, Integer> query = this.dao.queryBuilder();
            query.where().eq(Action.FIELD_GAME_ID, gameId).and().eq(Action.FIELD_TURN, turnNumber);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public Action findForGameIdPlayerIdAndTurn(int gameId, int playerId, int turnNumber) throws ContainerDataException {
        try {
            QueryBuilder<Action, Integer> query = this.dao.queryBuilder();
            query.where().eq(Action.FIELD_GAME_ID, gameId).and().eq(Action.FIELD_PLAYER_ID, playerId).and().eq(Action.FIELD_TURN, turnNumber);
            return this.dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
