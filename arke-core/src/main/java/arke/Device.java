package arke;

import java.util.Map;

public interface Device {

    Map<String, String> getProperties(long userId) throws ContainerException;



}
