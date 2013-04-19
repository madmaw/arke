package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.PersistentInboundMessage;
import arke.container.jpa.data.PersistentInboundMessageDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ORMLitePersistentInboundMessageDAO implements PersistentInboundMessageDAO {

    private Dao<PersistentInboundMessage, Integer> dao;

    public ORMLitePersistentInboundMessageDAO(Dao<PersistentInboundMessage, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(PersistentInboundMessage message) throws ContainerDataException {
        try {
            this.dao.create(message);
            return message.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(PersistentInboundMessage message) throws ContainerDataException {
        try {
            this.dao.update(message);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<PersistentInboundMessage> findUnhandled() throws ContainerDataException {
        // TODO this would be trivial to prepare (no parameters)
        try {
            QueryBuilder<PersistentInboundMessage, Integer> query = this.dao.queryBuilder();

            query.where().eq(PersistentInboundMessage.FIELD_HANDLED, false).and().isNull(PersistentInboundMessage.FIELD_FAILURE_REASON);
            query.orderBy(PersistentInboundMessage.FIELD_TIME_RECEIVED, true);

            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
