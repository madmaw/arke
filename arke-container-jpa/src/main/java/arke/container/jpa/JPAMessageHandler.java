package arke.container.jpa;

import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.List;

public interface JPAMessageHandler {

    void handleInboundMessage(PersistentDevice from, List<PersistentMessagePart> parts) throws Exception;
}
