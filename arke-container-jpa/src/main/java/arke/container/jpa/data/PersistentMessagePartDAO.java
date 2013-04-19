package arke.container.jpa.data;

import arke.ContainerDataException;

import java.util.Date;
import java.util.List;

public interface PersistentMessagePartDAO {

    int create(PersistentMessagePart part) throws ContainerDataException;

    List<PersistentMessagePart> findPendingOutboundByUserIdBefore(int userId, Date date) throws ContainerDataException;

    List<PersistentMessagePart> findPendingOutboundByDeviceIdBefore(int deviceId, Date date) throws ContainerDataException;

    List<PersistentMessagePart> findByInboundMessageId(int inboundMessageId) throws ContainerDataException;

    List<PersistentMessagePart> findByScheduledMessageId(int scheduledMessageId) throws ContainerDataException;
}
