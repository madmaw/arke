package arke.container.jpa;

import arke.container.jpa.data.PersistentDevice;

public interface DeviceBasedTimeZoneGuesser {
    String guessTimeZoneId(PersistentDevice device);
}
