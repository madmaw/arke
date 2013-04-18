package arke;

import java.util.List;

public final class OutboundMessage extends BaseMessage {

    private long toUserId;

    public OutboundMessage(long toUserId, List<Part> parts) {
        super(parts);
        this.toUserId = toUserId;
    }

    public long getToUserId() {
        return this.toUserId;
    }
}
