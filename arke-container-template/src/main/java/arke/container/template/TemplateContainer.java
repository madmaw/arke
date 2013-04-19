package arke.container.template;

import arke.*;

import java.util.Date;
import java.util.Map;

public interface TemplateContainer {

    void sendMessage(InboundMessage sourceMessage, String[] templateNames, Map<String, Object> toRender) throws ContainerException;

    void sendMessage(long targetUserId, String[] templateNames, Map<String, Object> toRender, boolean immediately) throws ContainerException;

    long scheduleMessage(ScheduledMessage message) throws ContainerException;

    void cancelScheduledMessage(long id) throws ContainerException;

    // user operations

    Device getPreferredDevice(long userId) throws ContainerException;

    User getUser(long userId) throws ContainerException;

    void attachUser(InboundMessage sourceMessage, long existingUserId) throws ContainerException;

    long createUser(InboundMessage sourceMessage) throws ContainerException;

}
