package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentDeviceDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class ORMLitePersistentDeviceDAO implements PersistentDeviceDAO {

    private Dao<PersistentDevice, Integer> dao;

    public ORMLitePersistentDeviceDAO(Dao<PersistentDevice, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(PersistentDevice persistentDevice) throws ContainerDataException {
        try {
            this.dao.create(persistentDevice);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
        return persistentDevice.getId();
    }

    @Override
    public void update(PersistentDevice persistentDevice) throws ContainerDataException {
        try {
            this.dao.update(persistentDevice);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public int createOrUpdate(PersistentDevice persistentDevice) throws ContainerDataException {
        try {
            this.dao.createOrUpdate(persistentDevice);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
        return persistentDevice.getId();
    }

    @Override
    public PersistentDevice find(int id) throws ContainerDataException {
        try {
            return this.dao.queryForId(id);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public PersistentDevice findMostRecentForUserId(int userId) throws ContainerDataException {
        try {
            QueryBuilder<PersistentDevice, Integer> query = this.dao.queryBuilder();
            query.where().eq(PersistentDevice.FIELD_OWNER_ID, userId);
            return this.dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public PersistentDevice findByNameAndType(String name, String type) throws ContainerDataException {
        try {
            QueryBuilder<PersistentDevice, Integer> query = this.dao.queryBuilder();
            query.where().eq(PersistentDevice.FIELD_NAME, name).and().eq(PersistentDevice.FIELD_TYPE, type);
            return this.dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
