package arke.container.jpa.data;

import arke.ContainerDataException;

import java.util.List;

public interface PersistentInboundMessageDAO {
    int create(PersistentInboundMessage message) throws ContainerDataException;

    void update(PersistentInboundMessage message) throws ContainerDataException;

    List<PersistentInboundMessage> findUnhandled() throws ContainerDataException;
}
