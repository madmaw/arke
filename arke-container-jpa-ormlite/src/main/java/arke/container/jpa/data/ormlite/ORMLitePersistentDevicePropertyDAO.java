package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.PersistentDeviceProperty;
import arke.container.jpa.data.PersistentDevicePropertyDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ORMLitePersistentDevicePropertyDAO implements PersistentDevicePropertyDAO {

    private Dao<PersistentDeviceProperty, Integer> dao;

    public ORMLitePersistentDevicePropertyDAO(Dao<PersistentDeviceProperty, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public List<PersistentDeviceProperty> findByDeviceId(int deviceId) throws ContainerDataException {
        try {
            QueryBuilder<PersistentDeviceProperty, Integer> query = this.dao.queryBuilder();
            query.where().eq(PersistentDeviceProperty.FIELD_DEVICE_ID, deviceId);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public PersistentDeviceProperty findByDeviceIdAndKey(int deviceId, String key) throws ContainerDataException {
        try {
            QueryBuilder<PersistentDeviceProperty, Integer> query = this.dao.queryBuilder();
            query.where().eq(PersistentDeviceProperty.FIELD_DEVICE_ID, deviceId).and().eq(PersistentDeviceProperty.FIELD_KEY, key);
            return dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void create(PersistentDeviceProperty property) throws ContainerDataException {
        try {
            this.dao.create(property);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(PersistentDeviceProperty property) throws ContainerDataException {
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
}
