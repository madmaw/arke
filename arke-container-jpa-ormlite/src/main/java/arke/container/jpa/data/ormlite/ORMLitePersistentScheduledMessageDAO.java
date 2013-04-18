package arke.container.jpa.data.ormlite;

import arke.container.jpa.data.ContainerDataException;
import arke.container.jpa.data.PersistentScheduledMessage;
import arke.container.jpa.data.PersistentScheduledMessageDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ORMLitePersistentScheduledMessageDAO implements PersistentScheduledMessageDAO {

    private Dao<PersistentScheduledMessage, Integer> dao;

    public ORMLitePersistentScheduledMessageDAO(Dao<PersistentScheduledMessage, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(PersistentScheduledMessage message) throws ContainerDataException {
        try {
            this.dao.create(message);
            return message.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(PersistentScheduledMessage message) throws ContainerDataException {
        try {
            this.dao.update(message);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void updateCanceled(int id, boolean canceled) throws ContainerDataException {
        try {
            PersistentScheduledMessage message = this.dao.queryForId(id);
            if( message != null ) {
                message.setCanceled(canceled);
                this.dao.update(message);
            }
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public PersistentScheduledMessage findFirstUndelivered() throws ContainerDataException {
        try {
            QueryBuilder<PersistentScheduledMessage, Integer> query = dao.queryBuilder();
            query.where()
                    .isNull(PersistentScheduledMessage.FIELD_DELIVERY_TIME).and()
                    .eq(PersistentScheduledMessage.FIELD_CANCELED, false).and()
                    .isNull(PersistentScheduledMessage.FIELD_FAILURE_REASON);
            query.orderBy(PersistentScheduledMessage.FIELD_SCHEDULED_TIME, true);
            return this.dao.queryForFirst(query.prepare());
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<PersistentScheduledMessage> findUndeliveredScheduledBeforeTime(Date time) throws ContainerDataException {
        try {
            QueryBuilder<PersistentScheduledMessage, Integer> query = dao.queryBuilder();
            query.where()
                    .isNull(PersistentScheduledMessage.FIELD_DELIVERY_TIME).and()
                    .eq(PersistentScheduledMessage.FIELD_CANCELED, false).and()
                    .isNull(PersistentScheduledMessage.FIELD_FAILURE_REASON).and()
                    .lt(PersistentScheduledMessage.FIELD_SCHEDULED_TIME, time);
            query.orderBy(PersistentScheduledMessage.FIELD_SCHEDULED_TIME, true);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
