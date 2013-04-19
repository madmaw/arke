package arke.container.jpa.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.data.PersistentOutboundMessage;
import arke.container.jpa.data.PersistentOutboundMessageDAO;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

public class ORMLitePersistentOutboundMessageDAO implements PersistentOutboundMessageDAO {

    private Dao<PersistentOutboundMessage, Integer> dao;

    public ORMLitePersistentOutboundMessageDAO(Dao<PersistentOutboundMessage, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(PersistentOutboundMessage message) throws ContainerDataException {
        try {
            this.dao.create(message);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
        return message.getId();
    }

    @Override
    public void update(PersistentOutboundMessage message) throws ContainerDataException {
        try {
            this.dao.update(message);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void updateTimeSent(int messageId, Date timeSent) throws ContainerDataException {
        try {
            PersistentOutboundMessage message = this.dao.queryForId(messageId);
            if( message != null ) {
                message.setTimeSent(timeSent);
                this.dao.update(message);
            }
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }

    }
}
