package arke.container.template;

import arke.*;

import java.util.Date;
import java.util.Map;

public abstract class AbstractTemplateContainerAdapter implements TemplateContainer {

    protected Container container;

    public AbstractTemplateContainerAdapter(Container container) {
        this.container = container;
    }

    @Override
    public void cancelScheduledMessage(long id) throws ContainerException {
        this.container.cancelScheduledMessage(id);
    }
    @Override
    public void attachUser(InboundMessage sourceMessage, long existingUserId) throws ContainerException {
        this.container.attachUser(sourceMessage, existingUserId);
    }

    @Override
    public long createUser(InboundMessage sourceMessage) throws ContainerException {
        return this.container.createUser(sourceMessage);
    }

    @Override
    public Device getPreferredDevice(long userId) throws ContainerException {
        return this.container.getPreferredDevice(userId);
    }

    @Override
    public User getUser(long userId) throws ContainerException {
        return this.container.getUser(userId);
    }
}
