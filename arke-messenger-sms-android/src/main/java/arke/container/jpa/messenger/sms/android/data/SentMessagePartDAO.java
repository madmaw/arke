package arke.container.jpa.messenger.sms.android.data;

import arke.ContainerDataException;

import java.util.List;

public interface SentMessagePartDAO {

    int create(SentMessagePart part) throws ContainerDataException;

    void update(SentMessagePart part) throws ContainerDataException;

    SentMessagePart find(int id) throws ContainerDataException;

    List<SentMessagePart> findByMessageId(int messageId) throws ContainerDataException;
}
