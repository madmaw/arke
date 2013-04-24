package arke.sample.rps.data.ormlite;

import arke.ContainerDataException;
import arke.sample.rps.data.Game;
import arke.sample.rps.data.GameDAO;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class ORMLiteGameDAO implements GameDAO {

    private Dao<Game, Integer> dao;

    public ORMLiteGameDAO(Dao<Game, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(Game game) throws ContainerDataException {
        try {
            dao.create(game);
            return game.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(Game game) throws ContainerDataException {
        try {
            dao.update(game);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public Game find(int id) throws ContainerDataException {
        try {
            return dao.queryForId(id);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
