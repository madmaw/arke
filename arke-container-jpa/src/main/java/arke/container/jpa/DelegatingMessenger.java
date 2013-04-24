package arke.container.jpa;

import arke.ContainerException;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.List;
import java.util.Map;

public class DelegatingMessenger implements Messenger {

    private Map<String, Messenger> messengerMap;
    private Messenger defaultMessenger;

    public DelegatingMessenger(Map<String, Messenger> messengerMap, Messenger defaultMessenger) {
        this.messengerMap = messengerMap;
        this.defaultMessenger = defaultMessenger;
    }

    @Override
    public String sendMessage(PersistentDevice targetPersistentDevice, List<PersistentMessagePart> parts) throws ContainerException {
        Messenger messenger = messengerMap.get(targetPersistentDevice.getDeviceType());
        if( messenger == null ) {
            messenger = defaultMessenger;
        }
        if( messenger == null ) {
            throw new ContainerException("unknown messenger type "+targetPersistentDevice.getDeviceType());
        } else {
            return messenger.sendMessage(targetPersistentDevice, parts);
        }
    }
}
