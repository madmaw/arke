package arke.container.jpa.data.ormlite;

import arke.container.jpa.data.ContainerDataException;
import arke.container.jpa.data.User;
import arke.container.jpa.data.UserDAO;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class ORMLiteUserDAO implements UserDAO {

    private Dao<User, Integer> dao;

    public ORMLiteUserDAO(Dao<User, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(User user) throws ContainerDataException {
        try {
            this.dao.create(user);
            return user.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(User user) throws ContainerDataException {
        try {
            this.dao.update(user);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public User find(int userId) throws ContainerDataException {
        try {
            return this.dao.queryForId(userId);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
