package arke.container.jpa.messenger.sms.android.data.ormlite;

import arke.ContainerDataException;
import arke.container.jpa.messenger.sms.android.data.SentMessagePart;
import arke.container.jpa.messenger.sms.android.data.SentMessagePartDAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ORMLiteSentMessagePartDAO implements SentMessagePartDAO {

    private Dao<SentMessagePart, Integer> dao;

    public ORMLiteSentMessagePartDAO(Dao<SentMessagePart, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public int create(SentMessagePart part) throws ContainerDataException {
        try {
            this.dao.create(part);
            return part.getId();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public void update(SentMessagePart part) throws ContainerDataException {
        try {
            this.dao.update(part);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public SentMessagePart find(int id) throws ContainerDataException {
        try {
            return this.dao.queryForId(id);
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }

    @Override
    public List<SentMessagePart> findByMessageId(int messageId) throws ContainerDataException {
        try {
            QueryBuilder<SentMessagePart, Integer> query = this.dao.queryBuilder();
            query.where().eq(SentMessagePart.FIELD_MESSAGE_ID, messageId);
            return query.query();
        } catch( SQLException ex ) {
            throw new ContainerDataException(ex);
        }
    }
}
