package arke;

import java.util.Date;
import java.util.Map;

public interface InboundMessage extends Message {

    Long getSourceUserId();

    Date getTimeReceived();

}
