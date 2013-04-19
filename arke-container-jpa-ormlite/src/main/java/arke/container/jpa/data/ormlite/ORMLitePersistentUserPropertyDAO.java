package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.PersistentUserProperty;
import arke.container.jpa.data.PersistentUserPropertyDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ORMLitePersistentUserPropertyDAO implements PersistentUserPropertyDAO {

    private Dao<PersistentUserProperty, Integer> dao;

    public ORMLitePersistentUserPropertyDAO(Dao<PersistentUserProperty, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public List<PersistentUserProperty> findByUserId(int userId) throws ContainerDataException {
        try {
            QueryBuilder<PersistentUserProperty, Integer> query = dao.queryBuilder();
            query.where().eq(PersistentUserProperty.FIELD_USER_ID, userId);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public PersistentUserProperty findByUserIdAndKey(int userId, String key) throws ContainerDataException {
        try {
            QueryBuilder<PersistentUserProperty, Integer> query = dao.queryBuilder();
            query.where().eq(PersistentUserProperty.FIELD_USER_ID, userId).and().eq(PersistentUserProperty.FIELD_KEY, key);
            return dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void create(PersistentUserProperty property) throws ContainerDataException {
        try {
            this.dao.create(property);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(PersistentUserProperty property) throws ContainerDataException {
        try {
            this.dao.update(property);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void delete(int id) throws ContainerDataException {
        try {
            this.dao.deleteById(id);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void delete(int userId, String key) throws ContainerDataException {
        try {
            DeleteBuilder<PersistentUserProperty, Integer> query = this.dao.deleteBuilder();
            query.where().eq(PersistentUserProperty.FIELD_USER_ID, userId).and().eq(PersistentUserProperty.FIELD_KEY, key);
            query.delete();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
