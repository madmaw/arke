package arke.container.jpa.messenger.sms.android.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.messenger.sms.android.data.SentMessage;
import arke.container.jpa.messenger.sms.android.data.SentMessageDAO;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class ORMLiteSentMessageDAO implements SentMessageDAO {

    private Dao<SentMessage, Integer> dao;

    public ORMLiteSentMessageDAO(Dao<SentMessage, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(SentMessage message) throws ContainerDataException {
        try {
            this.dao.create(message);
            return message.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
