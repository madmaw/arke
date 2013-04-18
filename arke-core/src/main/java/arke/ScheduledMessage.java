package arke;

import java.util.Date;
import java.util.List;

public final class ScheduledMessage extends BaseMessage {

    private Date scheduledTime;

    public ScheduledMessage(Date scheduledTime, List<Part> parts) {
        super(parts);
        this.scheduledTime = scheduledTime;
    }

    public Date getScheduledTime() {
        return this.scheduledTime;
    }
}
