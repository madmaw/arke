package arke.container.jpa.data.ormlite;

import arke.container.jpa.data.ContainerDataException;
import arke.container.jpa.data.Device;
import arke.container.jpa.data.DeviceDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class ORMLiteDeviceDAO implements DeviceDAO {

    private Dao<Device, Integer> dao;

    public ORMLiteDeviceDAO(Dao<Device, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(Device device) throws ContainerDataException {
        try {
            this.dao.create(device);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
        return device.getId();
    }

    @Override
    public void update(Device device) throws ContainerDataException {
        try {
            this.dao.update(device);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public int createOrUpdate(Device device) throws ContainerDataException {
        try {
            this.dao.createOrUpdate(device);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
        return device.getId();
    }

    @Override
    public Device find(int id) throws ContainerDataException {
        try {
            return this.dao.queryForId(id);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public Device findMostRecentForUserId(int userId) throws ContainerDataException {
        try {
            QueryBuilder<Device, Integer> query = this.dao.queryBuilder();
            query.where().eq(Device.FIELD_OWNER_ID, userId);
            return this.dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }

    }
}
