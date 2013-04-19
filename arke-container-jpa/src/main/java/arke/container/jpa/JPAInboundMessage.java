package arke.container.jpa;

import arke.BaseMessage;
import arke.InboundMessage;
import arke.Message;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentInboundMessage;

import java.util.Date;
import java.util.List;

public class JPAInboundMessage extends BaseMessage implements InboundMessage {

    private PersistentInboundMessage persistentInboundMessage;
    private PersistentDevice sourcePersistentDevice;

    public JPAInboundMessage(PersistentInboundMessage persistentInboundMessage, PersistentDevice sourcePersistentDevice, List<Message.Part> parts) {
        super(parts);
        this.persistentInboundMessage = persistentInboundMessage;
        this.sourcePersistentDevice = sourcePersistentDevice;
    }

    @Override
    public Long getSourceUserId() {
        Integer ownerId = this.sourcePersistentDevice.getOwnerId();
        Long result;
        if( ownerId == null ) {
            result = null;
        } else {
            result = ownerId.longValue();
        }
        return result;
    }

    @Override
    public Date getTimeReceived() {
        return this.persistentInboundMessage.getTimeReceived();
    }

    public PersistentInboundMessage getPersistentInboundMessage() {
        return persistentInboundMessage;
    }

    public PersistentDevice getSourcePersistentDevice() {
        return sourcePersistentDevice;
    }
}
