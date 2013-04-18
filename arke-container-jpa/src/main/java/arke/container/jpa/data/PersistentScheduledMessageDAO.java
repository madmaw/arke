package arke.container.jpa.data;

import java.util.Date;
import java.util.List;

public interface PersistentScheduledMessageDAO {

    int create(PersistentScheduledMessage message) throws ContainerDataException;

    void update(PersistentScheduledMessage message) throws ContainerDataException;

    void updateCanceled(int id, boolean canceled) throws ContainerDataException;

    PersistentScheduledMessage findFirstUndelivered() throws ContainerDataException;

    List<PersistentScheduledMessage> findUndeliveredScheduledBeforeTime(Date time) throws ContainerDataException;
}
