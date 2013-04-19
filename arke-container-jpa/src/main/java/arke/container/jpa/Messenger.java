package arke.container.jpa;

import arke.ContainerException;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.List;

public interface Messenger {
    String sendMessage(PersistentDevice targetPersistentDevice, List<PersistentMessagePart> parts) throws ContainerException;
}
