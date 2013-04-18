package arke.container.jpa.data;

import java.util.Date;

public interface PersistentOutboundMessageDAO {

    int create(PersistentOutboundMessage message) throws ContainerDataException;

    void update(PersistentOutboundMessage message) throws ContainerDataException;

    void updateTimeSent(int messageId, Date timeSent) throws ContainerDataException;
}
