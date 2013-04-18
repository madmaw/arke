package arke.container.jpa;

import arke.container.jpa.data.Device;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.List;

public interface JPAMessageHandler {

    void handleInboundMessage(Device from, List<PersistentMessagePart> parts) throws Exception;
}
