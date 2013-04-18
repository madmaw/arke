package arke;

import java.util.Map;

public interface Container {

    /**
     * Respond to a message (even if there is no user associated with it)
     * @param sourceMessage the message to respond to
     * @param outboundMessage the message to respond with
     */
    void sendMessage(InboundMessage sourceMessage, Message outboundMessage) throws ContainerException;

    void sendMessage(OutboundMessage message, boolean immediately) throws ContainerException;

    long scheduleMessage(ScheduledMessage message) throws ContainerException;

    void cancelScheduledMessage(long id) throws ContainerException;

    // user operations

    Map<String, String> getResponseDeviceProperties(long userId) throws ContainerException;

    Map<String, String> getUserProperties(long userId) throws ContainerException;

    void attachUser(InboundMessage sourceMessage, long existingUserId) throws ContainerException;

    long createUser(InboundMessage sourceMessage) throws ContainerException;

    void setUserProperty(long userId, String key, String value) throws ContainerException;

    void removeUserProperty(long userId, String key) throws ContainerException;

    void blockUser(long userId, String reasonBlocked) throws ContainerException;
}
