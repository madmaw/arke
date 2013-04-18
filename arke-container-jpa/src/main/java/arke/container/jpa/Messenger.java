package arke.container.jpa;

import arke.container.jpa.data.Device;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.List;

public interface Messenger {
    void sendMessage(Device targetDevice, List<PersistentMessagePart> parts);
}
