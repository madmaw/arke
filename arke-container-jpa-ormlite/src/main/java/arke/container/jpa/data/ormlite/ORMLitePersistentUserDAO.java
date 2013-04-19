package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.PersistentUser;
import arke.container.jpa.data.PersistentUserDAO;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class ORMLitePersistentUserDAO implements PersistentUserDAO {

    private Dao<PersistentUser, Integer> dao;

    public ORMLitePersistentUserDAO(Dao<PersistentUser, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(PersistentUser persistentUser) throws ContainerDataException {
        try {
            this.dao.create(persistentUser);
            return persistentUser.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(PersistentUser persistentUser) throws ContainerDataException {
        try {
            this.dao.update(persistentUser);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public PersistentUser find(int userId) throws ContainerDataException {
        try {
            return this.dao.queryForId(userId);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
