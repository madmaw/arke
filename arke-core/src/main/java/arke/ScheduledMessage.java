package arke;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ScheduledMessage extends BaseMessage {

    private Date scheduledTime;

    public ScheduledMessage(Date scheduledTime) {
        this(scheduledTime, new ArrayList<Part>());
    }

    public ScheduledMessage(Date scheduledTime, List<Part> parts) {
        super(parts);
        this.scheduledTime = scheduledTime;
    }

    public Date getScheduledTime() {
        return this.scheduledTime;
    }
}
