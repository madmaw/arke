package arke.container.jpa.messenger.sms.android.data;

import arke.ContainerDataException;

public interface SentMessageDAO {

    int create(SentMessage message) throws ContainerDataException;

}
