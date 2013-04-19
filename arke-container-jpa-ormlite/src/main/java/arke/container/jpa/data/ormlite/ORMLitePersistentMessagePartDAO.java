package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ORMLitePersistentMessagePartDAO implements PersistentMessagePartDAO {

    private Dao<PersistentMessagePart, Integer> persistentMessagePartDAO;
    private Dao<PersistentOutboundMessage, Integer> persistentOutboundMessageDAO;

    public ORMLitePersistentMessagePartDAO(
            Dao<PersistentMessagePart, Integer> persistentMessagePartDAO,
            Dao<PersistentOutboundMessage, Integer> persistentOutboundMessageDAO
    ) {
        this.persistentMessagePartDAO = persistentMessagePartDAO;
        this.persistentOutboundMessageDAO = persistentOutboundMessageDAO;
    }

    @Override
    public int create(PersistentMessagePart part) throws ContainerDataException {
        try {
            this.persistentMessagePartDAO.create(part);
            return part.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<PersistentMessagePart> findPendingOutboundByUserIdBefore(int userId, Date date) throws ContainerDataException {
        return findPendingOutboundBefore(userId, null, date);
    }

    @Override
    public List<PersistentMessagePart> findPendingOutboundByDeviceIdBefore(int deviceId, Date date) throws ContainerDataException {
        return findPendingOutboundBefore(null, deviceId, date);
    }

    public List<PersistentMessagePart> findPendingOutboundBefore(Integer userId, Integer deviceId, Date date) throws ContainerDataException {
        try {

            QueryBuilder<PersistentOutboundMessage, Integer> messageQuery = this.persistentOutboundMessageDAO.queryBuilder();

            // TODO raw query
            // just do it manually
            Where<PersistentOutboundMessage, Integer> messageWhere = messageQuery.where().le(PersistentOutboundMessage.FIELD_TIME_LODGED, date).and().eq(PersistentOutboundMessage.FIELD_FAILED_PERMANENTLY, false);
            if( userId != null ) {
                messageWhere.and().eq(PersistentOutboundMessage.FIELD_TARGET_USER_ID, userId);
            }
            if( deviceId != null ) {
                messageWhere.and().eq(PersistentOutboundMessage.FIELD_TARGET_DEVICE_ID, deviceId);
            }
            messageWhere.or(
                    messageWhere.isNull(PersistentOutboundMessage.FIELD_TIME_SENT),
                    messageWhere.isNotNull(PersistentOutboundMessage.FIELD_FAILURE_REASON)
            );
            messageQuery.orderBy(PersistentOutboundMessage.FIELD_TIME_LODGED, true);

            List<PersistentOutboundMessage> messages = messageQuery.query();

            ArrayList<PersistentMessagePart> results = new ArrayList<PersistentMessagePart>(messages.size());

            for( PersistentOutboundMessage message : messages ) {
                QueryBuilder<PersistentMessagePart, Integer> partQuery = this.persistentMessagePartDAO.queryBuilder();
                partQuery.where().eq(PersistentMessagePart.FIELD_OUTBOUND_MESSAGE_ID, message.getId());
                partQuery.orderBy(PersistentMessagePart.FIELD_SEQUENCE_NUMBER, true);
                List<PersistentMessagePart> parts = partQuery.query();
                results.addAll(parts);
            }

            return results;
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<PersistentMessagePart> findByInboundMessageId(int inboundMessageId) throws ContainerDataException {
        try {
            QueryBuilder<PersistentMessagePart, Integer> query = this.persistentMessagePartDAO.queryBuilder();
            query.where().eq(PersistentMessagePart.FIELD_INBOUND_MESSAGE_ID, inboundMessageId);
            query.orderBy(PersistentMessagePart.FIELD_SEQUENCE_NUMBER, true);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<PersistentMessagePart> findByScheduledMessageId(int scheduledMessageId) throws ContainerDataException {
        try {
            QueryBuilder<PersistentMessagePart, Integer> query = this.persistentMessagePartDAO.queryBuilder();
            query.where().eq(PersistentMessagePart.FIELD_SCHEDULED_MESSAGE_ID, scheduledMessageId);
            query.orderBy(PersistentMessagePart.FIELD_SEQUENCE_NUMBER, true);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
