package arke;

import java.util.Map;
import java.util.TimeZone;

public interface User {

    Map<String, String> getProperties() throws ContainerException;

    void setProperty(String key, String value) throws ContainerException;

    void removeProperty(String key) throws ContainerException;

    void block(String reasonBlocked) throws ContainerException;

    TimeZone getTimeZone() throws ContainerException;

    void setTimeZone(TimeZone timeZone) throws  ContainerException;

}
